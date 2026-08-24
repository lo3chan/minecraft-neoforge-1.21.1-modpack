package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.CompatHandler;
import net.mehvahdjukaar.moonlight.core.MoonlightClient;
import net.mehvahdjukaar.moonlight.core.integration.ModernFixCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated(
   forRemoval = true
)
public abstract class DynamicResourcePack extends InMemoryPackResources {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final Position position;
   public final String mainNamespace;
   public final ResourceLocation resourcePackName;
   private boolean needsClearingNonStatic = false;
   boolean addToStatic = false;
   private static final boolean MODERN_FIX = CompatHandler.MODERNFIX && ModernFixCompat.areLazyResourcesOn();

   protected DynamicResourcePack(ResourceLocation name, PackType type) {
      this(name, type, Position.TOP, false);
   }

   protected DynamicResourcePack(ResourceLocation name, PackType type, Position position, boolean fixed, boolean hidden) {
      this(name, type, position, hidden);
   }

   protected DynamicResourcePack(ResourceLocation name, PackType type, Position position, boolean hidden) {
      super(makeInfo(name), type, hidden);
      this.position = position;
      this.mainNamespace = name.getNamespace();
      this.resourcePackName = name;
   }

   private static PackLocationInfo makeInfo(ResourceLocation name) {
      return new PackLocationInfo(name.toString(), Component.translatable(TextHelper.getReadableName(name.toString())), PackSource.BUILT_IN, Optional.empty());
   }

   public void markNotClearable(ResourceLocation texturePath) {
   }

   public void unMarkNotClearable(ResourceLocation staticResources) {
   }

   public ResourceLocation id() {
      return this.resourcePackName;
   }

   @Override
   public String toString() {
      return this.packId();
   }

   public Component getTitle() {
      return this.location().title();
   }

   public void registerPack() {
      PackType packType = this.getPackType();
      if (packType != PackType.CLIENT_RESOURCES || !MoonlightClient.maybeMergeLegacyPack(this)) {
         RegHelper.registerResourcePack(packType, () -> Pack.readMetaAndCreate(this.location(), new ResourcesSupplier() {
            public PackResources openPrimary(PackLocationInfo location) {
               return DynamicResourcePack.this;
            }

            public PackResources openFull(PackLocationInfo location, Metadata metadata) {
               return DynamicResourcePack.this;
            }
         }, packType, new PackSelectionConfig(true, Position.TOP, false)));
      }
   }

   public FileNotFoundException makeFileNotFoundException(String path) {
      return new FileNotFoundException(String.format("'%s' in ResourcePack '%s'", path, this.resourcePackName));
   }

   @Deprecated(
      forRemoval = true
   )
   @Override
   public void removeResource(ResourceLocation res) {
      synchronized (this) {
         this.searchTrie.remove(res);
         this.resources.remove(res);
      }
   }

   public final void clearNonStatic() {
   }

   public void clearAllContent() {
      synchronized (this) {
         this.searchTrie.clear();
         this.resources.clear();
         this.needsClearingNonStatic = true;
      }
   }

   private boolean modernFixHack(String s) {
      return s.startsWith("model") || s.startsWith("blockstate");
   }

   @Deprecated(
      forRemoval = true
   )
   public void addResource(StaticResource resource) {
      this.addResource(resource.location, resource.data);
   }

   @Deprecated(
      forRemoval = true
   )
   private void addJson(ResourceLocation path, JsonElement json) {
      try {
         this.addResource(path, RPUtils.serializeJson(json).getBytes());
      } catch (IOException var4) {
         LOGGER.error("Failed to write JSON {} to resource pack {}.", path, this.resourcePackName, var4);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void addJson(ResourceLocation location, JsonElement json, ResType resType) {
      this.addJson(resType.getPath(location), json);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addBytes(ResourceLocation location, byte[] bytes, ResType resType) {
      this.addResource(resType.getPath(location), bytes);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addTag(SimpleTagBuilder builder, ResourceKey<?> type) {
      ResourceLocation tagId = builder.getId();
      String tagPath = type.location().getPath();
      ResourceLocation loc = ResType.TAGS.getPath(tagId.withPath(tagPath + "/" + tagId.getPath()));
      if (this.resources.containsKey(loc)) {
         byte[] r = this.resources.get(loc);

         try (ByteArrayInputStream stream = new ByteArrayInputStream(r)) {
            JsonObject oldTag = RPUtils.deserializeJson(stream);
            builder.addFromJson(oldTag);
         } catch (Exception var12) {
         }
      }

      JsonElement json = builder.serializeToJson();
      this.addJson(loc, json, ResType.GENERIC);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addSimpleBlockLootTable(Block block) {
      this.addLootTable(block, createSingleItemTable(block).setParamSet(LootContextParamSets.BLOCK));
   }

   @Deprecated(
      forRemoval = true
   )
   public void addLootTable(Block block, Builder table) {
      this.addLootTable(block.getLootTable().location(), table.build());
   }

   @Deprecated(
      forRemoval = true
   )
   public void addLootTable(ResourceLocation id, LootTable table) {
      this.addJson(id, (JsonElement)LootDataType.TABLE.codec().encodeStart(JsonOps.INSTANCE, table).getOrThrow(), ResType.LOOT_TABLES);
   }

   protected static Builder createSingleItemTable(ItemLike itemLike) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(itemLike)).unwrap());
   }

   @Deprecated(
      forRemoval = true
   )
   public void addRecipe(RecipeHolder<?> holder) {
      this.addRecipe(holder.value(), holder.id());
   }

   public void addRecipe(Recipe<?> recipe, ResourceLocation id) {
      this.addRecipeNoAdvancement(recipe, id);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addRecipeNoAdvancement(Recipe<?> recipe, ResourceLocation id) {
      this.addJson(id, RPUtils.writeRecipe(recipe), ResType.RECIPES);
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
            this.addBytes(path, image.getImage().asByteArray(), ResType.TEXTURES);
            if (!isOnAtlas) {
               this.markNotClearable(ResType.TEXTURES.getPath(path));
            }

            if (image.getMcMeta() != null) {
               this.addJson(path, image.getMcMeta().toJson(), ResType.MCMETA);
            }
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
         LOGGER.warn("Failed to add image {} to resource pack {}.", path, this, var9);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void addBlockModel(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.BLOCK_MODELS);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addItemModel(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.ITEM_MODELS);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addBlockState(ResourceLocation modelLocation, JsonElement model) {
      this.addJson(modelLocation, model, ResType.BLOCKSTATES);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addLang(ResourceLocation langName, JsonElement language) {
      this.addJson(langName, language, ResType.LANG);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addLang(ResourceLocation langName, LangBuilder builder) {
      this.addJson(langName, builder.build(), ResType.LANG);
   }

   @Deprecated(
      forRemoval = true
   )
   public void setGenerateDebugResources(boolean generateDebugResources) {
   }

   @Deprecated(
      forRemoval = true
   )
   public void setClearOnReload(boolean canBeCleared) {
   }
}
