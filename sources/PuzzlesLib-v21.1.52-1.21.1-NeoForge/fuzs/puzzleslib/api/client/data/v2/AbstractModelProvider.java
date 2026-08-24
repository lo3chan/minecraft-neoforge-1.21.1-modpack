package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.BlockModelGenerators.BlockFamilyProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.data.models.model.ModelTemplate.JsonFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class AbstractModelProvider implements DataProvider {
   public static final Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> VARIANT_WOOD_ITEM_PROVIDERS = ImmutableMap.builder()
      .put(
         BlockSetVariant.BOAT,
         (BiConsumer<ItemModelGenerators, Item>)(itemModelGenerators, item) -> itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM)
      )
      .put(
         BlockSetVariant.CHEST_BOAT,
         (BiConsumer<ItemModelGenerators, Item>)(itemModelGenerators, item) -> itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM)
      )
      .build();
   public static final String BLOCK_PATH = "block";
   public static final String ITEM_PATH = "item";
   public static final ModelTemplate SPAWN_EGG = ModelTemplates.createItem("template_spawn_egg", new TextureSlot[0]);
   private final String modId;
   private final PathProvider blockStatePathProvider;
   private final PathProvider modelPathProvider;
   private final Set<Object> skipValidation = Sets.newHashSet();

   public AbstractModelProvider(DataProviderContext context) {
      this(context.getModId(), context.getPackOutput());
   }

   public AbstractModelProvider(String modId, PackOutput packOutput) {
      this.modId = modId;
      this.blockStatePathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "blockstates");
      this.modelPathProvider = packOutput.createPathProvider(Target.RESOURCE_PACK, "models");
   }

   public static Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> createVariantWoodBlockProviders(
      BlockSetFamily blockSetFamily, Block strippedBlock
   ) {
      return ImmutableMap.builder().put(BlockSetVariant.HANGING_SIGN, (BiConsumer<BlockModelGenerators, Block>)(blockModelGenerators, block) -> {
         Reference<Block> wallHangingSign = blockSetFamily.getBlock(BlockSetVariant.WALL_HANGING_SIGN);
         Objects.requireNonNull(wallHangingSign, "wall hanging sign is null");
         blockModelGenerators.createHangingSign(strippedBlock, block, (Block)wallHangingSign.value());
      }).build();
   }

   public void addBlockModels(BlockModelGenerators builder) {
   }

   public void addItemModels(ItemModelGenerators builder) {
   }

   public void generateForBlocks(
      BlockModelGenerators blockModelGenerators, BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> variants
   ) {
      this.generateForBlocks(blockModelGenerators, blockSetFamily, variants, TexturedModel.CUBE.get((Block)blockSetFamily.getBaseBlock().value()));
   }

   public void generateForBlocks(
      BlockModelGenerators blockModelGenerators,
      BlockSetFamily blockSetFamily,
      Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> variants,
      TexturedModel texturedModel
   ) {
      BlockFamily blockFamily = blockSetFamily.getBlockFamily();
      if (blockFamily.shouldGenerateModel()) {
         Objects.requireNonNull(blockModelGenerators);
         BlockFamilyProvider familyProvider = new BlockFamilyProvider(blockModelGenerators, texturedModel.getMapping());
         familyProvider.fullBlock = texturedModel.getTemplate().getDefaultModelLocation(blockFamily.getBaseBlock());
         familyProvider.generateFor(blockFamily);
         blockSetFamily.getBlockVariants().forEach((variant, holder) -> {
            BiConsumer<BlockModelGenerators, Block> modelProvider = variants.get(variant);
            if (modelProvider != null) {
               modelProvider.accept(blockModelGenerators, (Block)holder.value());
            }
         });
      }
   }

   public void generateForItems(
      ItemModelGenerators itemModelGenerators, BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> variants
   ) {
      BlockFamily blockFamily = blockSetFamily.getBlockFamily();
      if (blockFamily.shouldGenerateModel()) {
         blockSetFamily.getItemVariants().forEach((variant, holder) -> {
            BiConsumer<ItemModelGenerators, Item> modelProvider = variants.get(variant);
            if (modelProvider != null) {
               modelProvider.accept(itemModelGenerators, (Item)holder.value());
            }
         });
      }
   }

   protected boolean skipValidation() {
      return false;
   }

   protected void skipBlock(Block block) {
      this.skipValidation.add(block);
   }

   protected void skipItem(Item item) {
      this.skipValidation.add(item);
   }

   public CompletableFuture<?> run(CachedOutput output) {
      Map<Block, BlockStateGenerator> generators = Maps.newHashMap();
      Consumer<BlockStateGenerator> blockStateOutput = generator -> {
         Block block = generator.getBlock();
         BlockStateGenerator blockstategenerator = generators.put(block, generator);
         if (blockstategenerator != null) {
            throw new IllegalStateException("Duplicate block state definition for " + block);
         }
      };
      Map<ResourceLocation, Supplier<JsonElement>> models = Maps.newHashMap();
      Set<Item> skippedAutoModels = Sets.newHashSet();
      BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, supplier) -> {
         if (models.put(resourceLocation, supplier) != null) {
            throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
         }
      };
      this.addBlockModels(new BlockModelGenerators(blockStateOutput, modelOutput, skippedAutoModels::add));
      this.addItemModels(new ItemModelGenerators(modelOutput));
      List<Block> missingBlocks;
      if (!this.skipValidation()) {
         missingBlocks = BuiltInRegistries.BLOCK
            .entrySet()
            .stream()
            .filter(entry -> ((ResourceKey)entry.getKey()).location().getNamespace().equals(this.modId) && !generators.containsKey(entry.getValue()))
            .map(Entry::getValue)
            .filter(Predicate.not(this.skipValidation::contains))
            .toList();
      } else {
         missingBlocks = Collections.emptyList();
      }

      if (!missingBlocks.isEmpty()) {
         throw new IllegalStateException("Missing block state definitions for " + missingBlocks);
      } else {
         BuiltInRegistries.BLOCK.entrySet().forEach(entry -> {
            Item item = (Item)Item.BY_BLOCK.get(entry.getValue());
            if (item != null) {
               if (!((ResourceKey)entry.getKey()).location().getNamespace().equals(this.modId) || skippedAutoModels.contains(item)) {
                  return;
               }

               ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
               if (!models.containsKey(resourcelocation)) {
                  models.put(resourcelocation, new DelegatedModel(ModelLocationUtils.getModelLocation((Block)entry.getValue())));
               }
            }
         });
         List<Item> missingItems;
         if (!this.skipValidation()) {
            missingItems = BuiltInRegistries.ITEM
               .entrySet()
               .stream()
               .filter(
                  entry -> ((ResourceKey)entry.getKey()).location().getNamespace().equals(this.modId)
                     && !models.containsKey(decorateItemModelLocation(((ResourceKey)entry.getKey()).location()))
               )
               .map(Entry::getValue)
               .filter(Predicate.not(this.skipValidation::contains))
               .toList();
         } else {
            missingItems = Collections.emptyList();
         }

         if (!missingItems.isEmpty()) {
            throw new IllegalStateException("Missing item models for " + missingItems);
         } else {
            return CompletableFuture.allOf(
               saveCollection(output, generators, block -> this.blockStatePathProvider.json(block.builtInRegistryHolder().key().location())),
               saveCollection(output, models, this.modelPathProvider::json)
            );
         }
      }
   }

   public final String getName() {
      return "Model Definitions";
   }

   private static <T> CompletableFuture<?> saveCollection(CachedOutput output, Map<T, ? extends Supplier<JsonElement>> map, Function<T, Path> pathExtractor) {
      return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
         Path path = pathExtractor.apply(entry.getKey());
         JsonElement jsonElement = entry.getValue().get();
         return DataProvider.saveStable(output, jsonElement, path);
      }).toArray(CompletableFuture[]::new));
   }

   public static ResourceLocation getModelLocation(Block block) {
      return decorateBlockModelLocation(getLocation(block));
   }

   public static ResourceLocation decorateBlockModelLocation(ResourceLocation resourceLocation) {
      return resourceLocation.withPrefix("block/");
   }

   public static ResourceLocation getLocation(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   public static String getName(Block block) {
      return getLocation(block).getPath();
   }

   public static ResourceLocation getModelLocation(Item item) {
      return decorateItemModelLocation(getLocation(item));
   }

   public static ResourceLocation decorateItemModelLocation(ResourceLocation resourceLocation) {
      return resourceLocation.withPrefix("item/");
   }

   public static ResourceLocation getLocation(Item item) {
      return BuiltInRegistries.ITEM.getKey(item);
   }

   public static String getName(Item item) {
      return getLocation(item).getPath();
   }

   public static ResourceLocation stripUntil(ResourceLocation resourceLocation, String s) {
      String path = resourceLocation.getPath();
      if (path.contains(s)) {
         path = path.substring(path.lastIndexOf(s) + 1);
         return ResourceLocationHelper.fromNamespaceAndPath(resourceLocation.getNamespace(), path);
      } else {
         return resourceLocation;
      }
   }

   public static ModelTemplate createBlockModelTemplate(ResourceLocation blockModelLocation, TextureSlot... requiredSlots) {
      return createBlockModelTemplate(blockModelLocation, "", requiredSlots);
   }

   public static ModelTemplate createBlockModelTemplate(ResourceLocation blockModelLocation, String suffix, TextureSlot... requiredSlots) {
      return new ModelTemplate(Optional.of(decorateBlockModelLocation(blockModelLocation)), Optional.of(suffix), requiredSlots);
   }

   public static ModelTemplate createItemModelTemplate(ResourceLocation itemModelLocation, TextureSlot... requiredSlots) {
      return createItemModelTemplate(itemModelLocation, "", requiredSlots);
   }

   public static ModelTemplate createItemModelTemplate(ResourceLocation itemModelLocation, String suffix, TextureSlot... requiredSlots) {
      return new ModelTemplate(Optional.of(decorateItemModelLocation(itemModelLocation)), Optional.of(suffix), requiredSlots);
   }

   public static ResourceLocation generateFlatItem(
      ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput
   ) {
      return modelTemplate.create(decorateItemModelLocation(resourceLocation), TextureMapping.layer0(decorateItemModelLocation(resourceLocation)), modelOutput);
   }

   public static ResourceLocation generateFlatItem(
      Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, JsonFactory factory
   ) {
      return modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), modelOutput, factory);
   }
}
