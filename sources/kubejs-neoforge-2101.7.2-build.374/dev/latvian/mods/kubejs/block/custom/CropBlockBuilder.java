package dev.latvian.mods.kubejs.block.custom;

import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.block.SeedItemBuilder;
import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags.Items;
import org.jetbrains.annotations.Nullable;

@ReturnsSelf
public class CropBlockBuilder extends BlockBuilder {
   public static final ResourceLocation[] CROP_BLOCK_TAGS = new ResourceLocation[]{BlockTags.CROPS.location()};
   public static final ResourceLocation[] CROP_ITEM_TAGS = new ResourceLocation[]{Items.SEEDS.location()};
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/crop");
   public transient int age = 7;
   protected transient List<VoxelShape> shapeByAge = Collections.nCopies(8, Shapes.block());
   public transient ToDoubleFunction<RandomTickCallback> growSpeedCallback = null;
   public transient ToIntFunction<RandomTickCallback> fertilizerCallback = null;
   public transient CropBlockBuilder.SurviveCallback surviveCallback = null;
   public transient List<Pair<Holder<Item>, NumberProvider>> outputs;
   public transient boolean noSeeds;

   public CropBlockBuilder(ResourceLocation id) {
      super(id);
      this.renderType = BlockRenderType.CUTOUT;
      this.noCollision = true;
      this.itemBuilder = new SeedItemBuilder(this.newID("", "_seeds"));
      ((SeedItemBuilder)this.itemBuilder).blockBuilder = this;
      this.hardness = 0.0F;
      this.resistance = 0.0F;
      this.outputs = new ArrayList<>();
      this.noSeeds = false;
      this.notSolid = true;
      this.soundType(SoundType.CROP);
      this.mapColor(MapColor.PLANT);

      for (int a = 0; a <= this.age; a++) {
         this.textures.put(String.valueOf(a), this.newID("block/", "/" + a).toString());
      }

      this.tagBlock(CROP_BLOCK_TAGS);
      this.tagItem(CROP_ITEM_TAGS);
   }

   @Override
   public BlockBuilder noItem() {
      this.itemBuilder = null;
      return this;
   }

   @Info("Remove seed drops from the loot table, does not prevent seed item from creating.")
   public CropBlockBuilder noSeeds() {
      this.noSeeds = true;
      return this;
   }

   @Info("Add a crop output with exactly one output.")
   public CropBlockBuilder crop(Holder<Item> output) {
      this.crop(output, ConstantValue.exactly(1.0F));
      return this;
   }

   @Info("Add a crop output with a specific amount.")
   public CropBlockBuilder crop(Holder<Item> output, NumberProvider chance) {
      this.outputs.add(new Pair(output, chance));
      return this;
   }

   @Info("Set the age of the crop. Note that the box will be the same for all ages (A full block size).")
   public CropBlockBuilder age(int age) {
      this.age(age, builder -> {});
      return this;
   }

   @Info("Set the age of the crop and the shape of the crop at that age.")
   public CropBlockBuilder age(int age, Consumer<CropBlockBuilder.ShapeBuilder> builder) {
      this.age = age;
      CropBlockBuilder.ShapeBuilder shapes = new CropBlockBuilder.ShapeBuilder(age);
      builder.accept(shapes);
      this.shapeByAge = shapes.getShapes();
      this.textures.clear();

      for (int i = 0; i <= age; i++) {
         this.textures.put(String.valueOf(i), this.newID("block/", "/" + i).toString());
      }

      return this;
   }

   public CropBlockBuilder farmersCanPlant() {
      this.tagItem(new ResourceLocation[]{ResourceLocation.withDefaultNamespace("villager_plantable_seeds")});
      return this;
   }

   public CropBlockBuilder bonemeal(ToIntFunction<RandomTickCallback> bonemealCallback) {
      this.fertilizerCallback = bonemealCallback;
      return this;
   }

   public CropBlockBuilder survive(CropBlockBuilder.SurviveCallback surviveCallback) {
      this.surviveCallback = surviveCallback;
      return this;
   }

   public CropBlockBuilder growTick(ToDoubleFunction<RandomTickCallback> growSpeedCallback) {
      this.growSpeedCallback = growSpeedCallback;
      return this;
   }

   @Override
   public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallback> randomTickCallback) {
      KubeJS.LOGGER.warn("randomTick is overridden by growTick to return grow speed, use it instead.");
      return this;
   }

   @Nullable
   @Override
   public LootTable generateLootTable(KubeDataGenerator generator) {
      Frozen registries = generator.getRegistries().access();
      net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder mature = LootItemBlockStatePropertyCondition.hasBlockStateProperties(
            this.get()
         )
         .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, this.age));
      net.minecraft.world.level.storage.loot.LootTable.Builder builder = LootTable.lootTable();

      for (Pair<Holder<Item>, NumberProvider> output : this.outputs) {
         if (((Holder)output.getFirst()).isBound()) {
            Builder<?> cropItem = (Builder<?>)LootItem.lootTableItem((ItemLike)((Holder)output.getFirst()).value())
               .apply(SetItemCountFunction.setCount((NumberProvider)output.getSecond()))
               .when(mature);
            builder.withPool(LootPool.lootPool().add(cropItem));
         }
      }

      if (this.itemBuilder != null && !this.noSeeds) {
         net.minecraft.world.level.storage.loot.LootPool.Builder pool = LootPool.lootPool()
            .add(
               ((Builder)LootItem.lootTableItem((ItemLike)this.itemBuilder.get()).when(mature))
                  .otherwise(LootItem.lootTableItem((ItemLike)this.itemBuilder.get()))
            );
         builder.withPool(pool);
      }

      return builder.build();
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      for (int i = 0; i <= this.age; i++) {
         bs.simpleVariant("age=" + i, this.parentModel == null ? this.id.withPath("block/" + this.id.getPath() + "/" + i) : this.parentModel);
      }
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      for (int i = 0; i <= this.age; i++) {
         int fi = i;
         generator.blockModel(this.newID("", "/" + i), m -> {
            m.parent(MODEL);
            m.texture("crop", this.textures.get(String.valueOf(fi)));
         });
      }
   }

   @Override
   protected void generateItemModel(ModelGenerator m) {
      m.parent(KubeAssetGenerator.GENERATED_ITEM_MODEL);
      m.texture("layer0", this.itemBuilder.baseTexture);
   }

   public Block createObject() {
      return new BasicCropBlockJS(this);
   }

   public static class ShapeBuilder {
      private final List<VoxelShape> shapes;

      public ShapeBuilder(int age) {
         this.shapes = new ArrayList<>(Collections.nCopies(age + 1, Shapes.block()));
      }

      @Info("Describe the shape of the crop at a specific age.\nmin/max coordinates are double values between 0 and 16.\n")
      public CropBlockBuilder.ShapeBuilder shape(int age, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
         this.shapes.set(age, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
         return this;
      }

      @Info("Makes the block to have a box like wheat for each stage.")
      public CropBlockBuilder.ShapeBuilder wheat() {
         this.shapes.clear();

         for (int i = 0; i < 8; i++) {
            this.shapes.add(Block.box(0.0, 0.0, 0.0, 16.0, 2 + i * 2, 16.0));
         }

         return this;
      }

      @Info("Makes the block to have a box like carrot for each stage.")
      public CropBlockBuilder.ShapeBuilder carrot() {
         this.shapes.clear();

         for (int i = 0; i < 8; i++) {
            this.shapes.add(Block.box(0.0, 0.0, 0.0, 16.0, 2 + i, 16.0));
         }

         return this;
      }

      @Info("Makes the block to have a box like beetroot for each stage.")
      public CropBlockBuilder.ShapeBuilder beetroot() {
         this.shapes.clear();

         for (int i = 0; i < 4; i++) {
            this.shapes.add(Block.box(0.0, 0.0, 0.0, 16.0, 2 + i * 2, 16.0));
         }

         return this;
      }

      @Info("Makes the block to have a box like potato for each stage.")
      public CropBlockBuilder.ShapeBuilder potato() {
         return this.carrot();
      }

      public List<VoxelShape> getShapes() {
         return List.copyOf(this.shapes);
      }
   }

   @FunctionalInterface
   public interface SurviveCallback {
      boolean survive(BlockState state, LevelReader reader, BlockPos pos);
   }
}
