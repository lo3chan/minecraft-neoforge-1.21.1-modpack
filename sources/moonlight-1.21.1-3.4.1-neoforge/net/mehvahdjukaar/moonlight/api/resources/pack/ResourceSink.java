package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.misc.ThrowingSupplier;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.RecipeTemplate;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ResourceSink {
   private final String packNamespace;
   private final String packId;
   private final Map<ResourceLocation, byte[]> resources = new HashMap<>();
   private final Map<TagKey<?>, SimpleTagBuilder> tags = new HashMap<>();

   public ResourceSink(String packNamespace, String packId) {
      this.packNamespace = packNamespace;
      this.packId = packId;
   }

   protected void addBytes(ResourceLocation id, byte[] bytes) {
      this.resources.put(id, (byte[])Preconditions.checkNotNull(bytes));
   }

   public void addResource(StaticResource resource) {
      this.addBytes(resource.location, resource.data);
   }

   private void addJson(ResourceLocation path, JsonElement json) {
      try {
         this.addBytes(path, RPUtils.serializeJson(json).getBytes());
      } catch (IOException var4) {
         Moonlight.LOGGER.error("Failed to write JSON {} to resource pack.", path, var4);
      }
   }

   public void addJson(ResourceLocation location, JsonElement json, ResType resType) {
      this.addJson(resType.getPath(location), json);
   }

   public void addBytes(ResourceLocation location, byte[] bytes, ResType resType) {
      this.addBytes(resType.getPath(location), bytes);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addAndCloseTexture(ResourceLocation path, TextureImage image) {
      this.addAndCloseTexture(path, image, true);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addAndCloseTexture(ResourceLocation path, TextureImage image, boolean isOnAtlas) {
      try {
         TextureImage e = image;

         try {
            this.addTexture(path, image, isOnAtlas);
         } catch (Throwable var8) {
            if (image != null) {
               try {
                  e.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (image != null) {
            image.close();
         }
      } catch (Exception var9) {
         Moonlight.LOGGER.warn("Failed to add image {} to resource pack {}.", path, this, var9);
      }
   }

   public void addAndCloseTexture(ResourceLocation path, Supplier<TextureImage> image) {
      try (TextureImage img = image.get()) {
         this.addTexture(path, img);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTexture(ResourceLocation path, TextureImage image, boolean onAtlas) {
      this.addTexture(path, image);
   }

   public void addTexture(ResourceLocation path, TextureImage texture) {
      try {
         NativeImage image = texture.getImage();
         if (!texture.isAllocated()) {
            Moonlight.crashIfInDev("Tried to save a non allocated texture image at " + path + " \nDid you close it too early?");
         } else {
            this.addBytes(path, image.asByteArray(), ResType.TEXTURES);
            if (texture.getMcMeta() != null) {
               this.addJson(path, texture.getMcMeta().toJson(), ResType.MCMETA);
            }
         }
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void markNotClearable(ResourceLocation path) {
   }

   public void addBlockModel(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.BLOCK_MODELS);
   }

   public void addItemModel(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.ITEM_MODELS);
   }

   public void addBlockState(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.BLOCKSTATES);
   }

   public void addLang(ResourceLocation langName, JsonElement language) {
      this.addJson(langName, language, ResType.LANG);
   }

   public void addLang(ResourceLocation langName, LangBuilder builder) {
      this.addJson(langName, builder.build(), ResType.LANG);
   }

   public void addTag(SimpleTagBuilder builder, ResourceKey<? extends Registry<?>> reg) {
      TagKey key = TagKey.create(reg, builder.getId());
      this.tags.merge(key, builder, (a, b) -> {
         a.merge(b);
         return (SimpleTagBuilder)a;
      });
   }

   public void addSimpleBlockLootTable(Block block) {
      this.addLootTable(block, createSingleItemTable(block).setParamSet(LootContextParamSets.BLOCK));
   }

   public void addLootTable(Block block, Builder table) {
      this.addLootTable(block.getLootTable().location(), table.build());
   }

   public void addLootTable(ResourceLocation id, LootTable table) {
      this.addJson(id, (JsonElement)LootDataType.TABLE.codec().encodeStart(JsonOps.INSTANCE, table).getOrThrow(), ResType.LOOT_TABLES);
   }

   protected static Builder createSingleItemTable(ItemLike itemLike) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(itemLike)).unwrap());
   }

   public void addRecipe(RecipeHolder<?> holder) {
      this.addRecipe(holder.value(), holder.id());
   }

   public void addRecipe(Recipe<?> recipe, ResourceLocation id) {
      this.addRecipeNoAdvancement(recipe, id);
   }

   public void addRecipeNoAdvancement(Recipe<?> recipe, ResourceLocation id) {
      this.addJson(id, RPUtils.writeRecipe(recipe), ResType.RECIPES);
   }

   public void addResourceIfNotPresent(ResourceManager manager, StaticResource resource) {
      if (!this.alreadyHasAssetAtLocation(manager, resource.location)) {
         this.addResource(resource);
      }
   }

   public void addJsonUnlessPresent(ResourceManager manager, ResourceLocation path, ThrowingSupplier<JsonElement> jsonSupplier) {
      if (!this.alreadyHasAssetAtLocation(manager, path)) {
         try {
            this.addJson(path, jsonSupplier.get());
         } catch (Exception var5) {
            Moonlight.LOGGER.error("Failed to write JSON {} to resource pack.", path, var5);
         }
      }
   }

   public boolean alreadyHasTextureAtLocation(ResourceManager manager, ResourceLocation res) {
      return this.alreadyHasAssetAtLocation(manager, res, ResType.TEXTURES);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTextureIfNotPresent(ResourceManager manager, String relativePath, Supplier<TextureImage> textureSupplier) {
      this.addTextureIfNotPresent(manager, relativePath, textureSupplier, true);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTextureIfNotPresent(ResourceManager manager, String relativePath, Supplier<TextureImage> textureSupplier, boolean isOnAtlas) {
      this.addTextureIfNotPresent(
         manager,
         relativePath.contains(":") ? ResourceLocation.parse(relativePath) : ResourceLocation.fromNamespaceAndPath(this.packNamespace, relativePath),
         textureSupplier
      );
   }

   public void addTextureIfNotPresent(ResourceManager manager, ResourceLocation res, Supplier<TextureImage> textureSupplier) {
      if (!this.alreadyHasTextureAtLocation(manager, res)) {
         try (TextureImage textureImage = textureSupplier.get()) {
            this.addTexture(res, textureImage);
         } catch (Exception var9) {
            Moonlight.LOGGER.error("Failed to generate texture {}: {}", res, var9);
         }
      }
   }

   public void addTextureUnlessPresent(ResourceManager manager, ResourceLocation res, ThrowingSupplier<TextureImage> textureSupplier) {
      if (!this.alreadyHasTextureAtLocation(manager, res)) {
         try (TextureImage textureImage = textureSupplier.get()) {
            this.addTexture(res, textureImage);
         } catch (Exception var9) {
            Moonlight.LOGGER.error("Failed to generate texture {}: {}", res, var9);
         }
      }
   }

   public boolean alreadyHasAssetAtLocation(ResourceManager manager, ResourceLocation res, ResType type) {
      return this.alreadyHasAssetAtLocation(manager, type.getPath(res));
   }

   public boolean alreadyHasAssetAtLocation(ResourceManager manager, ResourceLocation res) {
      Optional<Resource> resource = manager.getResource(res);
      return resource.filter(value -> !value.sourcePackId().equals(this.packId)).isPresent();
   }

   public void addSimilarJsonResource(ResourceManager manager, StaticResource resource, String keyword, String replaceWith) throws NoSuchElementException {
      this.addSimilarJsonResource(manager, resource, s -> s.replace(keyword, replaceWith));
   }

   public void addSimilarJsonResource(ResourceManager manager, StaticResource resource, Function<String, String> textTransform) throws NoSuchElementException {
      this.addSimilarJsonResource(manager, resource, textTransform, textTransform);
   }

   public void addSimilarJsonResource(
      ResourceManager manager, StaticResource resource, Function<String, String> textTransform, Function<String, String> pathTransform
   ) throws NoSuchElementException {
      ResourceLocation fullPath = resource.location;
      StringBuilder builder = new StringBuilder();
      String[] partial = fullPath.getPath().split("/");

      for (int i = 0; i < partial.length; i++) {
         if (i != 0) {
            builder.append("/");
         }

         if (i == partial.length - 1) {
            builder.append(pathTransform.apply(partial[i]));
         } else {
            builder.append(partial[i]);
         }
      }

      ResourceLocation newRes = ResourceLocation.fromNamespaceAndPath(this.packNamespace, builder.toString());
      if (!this.alreadyHasAssetAtLocation(manager, newRes)) {
         String fullText = resource.asString();
         fullText = textTransform.apply(fullText);
         this.addBytes(newRes, fullText.getBytes());
      }
   }

   public void copyResource(ResourceManager manager, ResourceLocation from, ResourceLocation to, boolean lenient) {
      Optional<Resource> resource = manager.getResource(from);
      if (resource.isPresent()) {
         StaticResource s = StaticResource.of(resource.get(), from);
         this.addBytes(to, s.data);
      } else {
         if (!lenient) {
            throw new NoSuchElementException("Resource " + from + " not found for copying to " + to);
         }

         Moonlight.LOGGER.info("Resource {} not found for copying to {}", from, to);
      }
   }

   public <T extends BlockType> void addBlockTypeSwapRecipe(
      ResourceManager manager, ResourceLocation originalRecipeId, T originalMat, T destinationMat, ResourceLocation baseID
   ) {
      StaticResource originalResource = StaticResource.getOrThrow(manager, ResType.RECIPES.getPath(originalRecipeId));
      JsonObject originalJson = originalResource.toJson();
      Recipe<?> originalRecipe = RPUtils.readRecipe(originalJson);
      RecipeHolder<?> newRecipe = RecipeTemplate.makeSimilarRecipe(originalRecipe, originalMat, destinationMat, baseID);
      JsonElement newJson = RPUtils.writeRecipe((T)newRecipe.value());
      if (newJson instanceof JsonObject jo) {
         for (Entry<String, JsonElement> e : originalJson.entrySet()) {
            String key = e.getKey();
            if (key.contains("condition") && !jo.has(key)) {
               jo.add(key, e.getValue());
            }
         }
      }

      if (!this.alreadyHasAssetAtLocation(manager, newRecipe.id())) {
         this.addJson(newRecipe.id(), newJson, ResType.RECIPES);
      }
   }

   public void appendModelOverride(ResourceManager manager, ResourceLocation modelRes, Consumer<RPUtils.OverrideAppender> modelConsumer) {
      JsonElement json = RPUtils.makeModelOverride(manager, modelRes, modelConsumer);
      this.addItemModel(modelRes, json);
   }

   public static void acceptSinks(IEditablePackResources pack, Collection<ResourceSink> sinks) {
      Multimap<TagKey<?>, SimpleTagBuilder> tags = HashMultimap.create();

      for (ResourceSink sink : sinks) {
         for (Entry<ResourceLocation, byte[]> r : sink.resources.entrySet()) {
            pack.addResource(r.getKey(), r.getValue());
         }

         for (Entry<TagKey<?>, SimpleTagBuilder> e : sink.tags.entrySet()) {
            tags.put(e.getKey(), e.getValue());
         }
      }

      ResourceSink dummy = new ResourceSink("dummy", "dummy");

      for (TagKey<?> key : tags.keySet()) {
         Iterator<SimpleTagBuilder> it = tags.get(key).iterator();
         if (it.hasNext()) {
            SimpleTagBuilder builder = it.next();

            while (it.hasNext()) {
               builder.merge(it.next());
            }

            ResourceLocation tagId = builder.getId();
            ResourceLocation loc = ResType.TAGS.getPath(tagId.withPath(key.registry().location().getPath() + "/" + tagId.getPath()));
            dummy.addJson(loc, builder.serializeToJson(), ResType.GENERIC);
         }
      }

      dummy.resources.forEach(pack::addResource);
   }

   public void appendItemToEnchantment(ResourceManager manager, ResourceKey<Enchantment> ench, Item... items) {
      ResourceLocation id = ench.location();

      try {
         try (InputStream model = manager.getResourceOrThrow(ResType.ENCHANTMENTS.getPath(id)).open()) {
            JsonObject json = RPUtils.deserializeJson(model);
            JsonElement supportedItems = json.get("supported_items");
            SimpleTagBuilder tb = SimpleTagBuilder.of(id.withPrefix("enchantable/"));
            String tagName = tb.getTagString();
            if (supportedItems instanceof JsonArray) {
               for (JsonElement a : (JsonArray)supportedItems) {
                  if (a.isJsonPrimitive()) {
                     String asString = a.getAsString();
                     if (!tagName.equals(asString)) {
                        tb.add(asString);
                     }
                  }
               }
            } else if (supportedItems.isJsonPrimitive()) {
               String asString = supportedItems.getAsString();
               if (!tagName.equals(asString)) {
                  tb.add(asString);
               }
            }

            for (Item item : items) {
               tb.addEntry(item);
            }

            json.addProperty("supported_items", tagName);
            this.addJson(id, json, ResType.ENCHANTMENTS);
            this.addTag(tb, Registries.ITEM);
         }
      } catch (Exception var16) {
         throw new RuntimeException(var16);
      }
   }
}
