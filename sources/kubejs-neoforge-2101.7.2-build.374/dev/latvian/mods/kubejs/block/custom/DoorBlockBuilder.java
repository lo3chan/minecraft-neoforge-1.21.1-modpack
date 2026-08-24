package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.block.drop.BlockDrops;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

@ReturnsSelf
public class DoorBlockBuilder extends ShapedBlockBuilder {
   public static final ResourceLocation[] DOOR_TAGS = new ResourceLocation[]{BlockTags.DOORS.location()};
   public static final ResourceLocation[] WOODEN_DOOR_TAGS = new ResourceLocation[]{BlockTags.WOODEN_DOORS.location()};
   private static final Map<String, ResourceLocation> MODELS = Map.of(
      "top_right",
      ResourceLocation.withDefaultNamespace("block/door_top_right"),
      "top_right_open",
      ResourceLocation.withDefaultNamespace("block/door_top_right_open"),
      "top_left",
      ResourceLocation.withDefaultNamespace("block/door_top_left"),
      "top_left_open",
      ResourceLocation.withDefaultNamespace("block/door_top_left_open"),
      "bottom_right",
      ResourceLocation.withDefaultNamespace("block/door_bottom_right"),
      "bottom_right_open",
      ResourceLocation.withDefaultNamespace("block/door_bottom_right_open"),
      "bottom_left",
      ResourceLocation.withDefaultNamespace("block/door_bottom_left"),
      "bottom_left_open",
      ResourceLocation.withDefaultNamespace("block/door_bottom_left_open")
   );
   public transient BlockSetType behaviour;

   public DoorBlockBuilder(ResourceLocation i) {
      super(i);
      this.renderType(BlockRenderType.CUTOUT);
      this.noValidSpawns(true);
      this.notSolid();
      this.tagBoth(DOOR_TAGS);
      this.textures.put("top", this.newID("block/", "_top").toString());
      this.textures.put("bottom", this.newID("block/", "_bottom").toString());
      this.hardness(3.0F);
      this.behaviour = BlockSetType.OAK;
   }

   public DoorBlockBuilder behaviour(BlockSetType wt) {
      this.behaviour = wt;
      return this;
   }

   public DoorBlockBuilder wooden() {
      this.tagBoth(WOODEN_DOOR_TAGS);
      return this;
   }

   public Block createObject() {
      return new DoorBlock(this.behaviour, this.createProperties());
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      Map<DoubleBlockHalf, Map<DoorHingeSide, Map<Boolean, ResourceLocation>>> modelMap = Map.of(
         DoubleBlockHalf.UPPER,
         Map.of(
            DoorHingeSide.RIGHT,
            Map.of(Boolean.FALSE, this.newID("block/", "_top_right"), Boolean.TRUE, this.newID("block/", "_top_right_open")),
            DoorHingeSide.LEFT,
            Map.of(Boolean.FALSE, this.newID("block/", "_top_left"), Boolean.TRUE, this.newID("block/", "_top_left_open"))
         ),
         DoubleBlockHalf.LOWER,
         Map.of(
            DoorHingeSide.RIGHT,
            Map.of(Boolean.FALSE, this.newID("block/", "_bottom_right"), Boolean.TRUE, this.newID("block/", "_bottom_right_open")),
            DoorHingeSide.LEFT,
            Map.of(Boolean.FALSE, this.newID("block/", "_bottom_left"), Boolean.TRUE, this.newID("block/", "_bottom_left_open"))
         )
      );
      Map<Direction, Map<DoorHingeSide, Map<Boolean, Integer>>> rotationMap = Map.of(
         Direction.EAST,
         Map.of(DoorHingeSide.RIGHT, Map.of(Boolean.FALSE, 0, Boolean.TRUE, 270), DoorHingeSide.LEFT, Map.of(Boolean.FALSE, 0, Boolean.TRUE, 90)),
         Direction.NORTH,
         Map.of(DoorHingeSide.RIGHT, Map.of(Boolean.FALSE, 270, Boolean.TRUE, 180), DoorHingeSide.LEFT, Map.of(Boolean.FALSE, 270, Boolean.TRUE, 0)),
         Direction.SOUTH,
         Map.of(DoorHingeSide.RIGHT, Map.of(Boolean.FALSE, 90, Boolean.TRUE, 0), DoorHingeSide.LEFT, Map.of(Boolean.FALSE, 90, Boolean.TRUE, 180)),
         Direction.WEST,
         Map.of(DoorHingeSide.RIGHT, Map.of(Boolean.FALSE, 180, Boolean.TRUE, 90), DoorHingeSide.LEFT, Map.of(Boolean.FALSE, 180, Boolean.TRUE, 270))
      );
      DoubleBlockHalf[] halfValues = DoubleBlockHalf.values();
      List<Boolean> openValues = List.of(Boolean.TRUE, Boolean.FALSE);
      Collection<Direction> facingValues = BlockStateProperties.HORIZONTAL_FACING.getPossibleValues();
      DoorHingeSide[] hingeValues = DoorHingeSide.values();

      for (DoubleBlockHalf half : halfValues) {
         for (Boolean open : openValues) {
            for (Direction facing : facingValues) {
               for (DoorHingeSide hinge : hingeValues) {
                  bs.variant(
                     "facing=" + facing.getSerializedName() + ",half=" + half.getSerializedName() + ",hinge=" + hinge.getSerializedName() + ",open=" + open,
                     (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(modelMap.get(half).get(hinge).get(open))
                        .y(rotationMap.get(facing).get(hinge).get(open)))
                  );
               }
            }
         }
      }
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      String topTexture = this.textures.get("top");
      String bottomTexture = this.textures.get("bottom");

      for (Entry<String, ResourceLocation> entry : MODELS.entrySet()) {
         generator.blockModel(this.newID("", "_" + entry.getKey()), m -> {
            m.parent(entry.getValue());
            m.texture("top", topTexture);
            m.texture("bottom", bottomTexture);
         });
      }
   }

   @Override
   public LootTable generateLootTable(KubeDataGenerator generator) {
      BlockDrops blockDrops = this.drops == null ? BlockDrops.createDefault(this.get().asItem().getDefaultInstance()) : this.drops.get();
      if (blockDrops.items().length == 0) {
         return null;
      } else {
         net.minecraft.world.level.storage.loot.LootPool.Builder pool = new net.minecraft.world.level.storage.loot.LootPool.Builder();
         if (blockDrops.rolls() != null) {
            pool.setRolls(blockDrops.rolls());
         }

         pool.when(ExplosionCondition.survivesExplosion());

         for (ItemStack drop : blockDrops.items()) {
            Builder<? extends Builder<?>> item = LootItem.lootTableItem(drop.getItem());
            item.when(
               new net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition.Builder(this.get())
                  .setProperties(
                     net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                  )
            );
            if (drop.getCount() > 1) {
               item.apply(SetItemCountFunction.setCount(ConstantValue.exactly(drop.getCount())));
            }

            if (!drop.isComponentsPatchEmpty()) {
               item.apply(LootItemConditionalFunction.simpleBuilder(c -> new SetComponentsFunction(c, drop.getComponentsPatch())));
            }

            pool.add(item);
         }

         return new net.minecraft.world.level.storage.loot.LootTable.Builder().withPool(pool).build();
      }
   }

   @Override
   protected void generateItemModel(ModelGenerator m) {
      m.parent(KubeAssetGenerator.GENERATED_ITEM_MODEL);
      m.texture("layer0", this.id.withPath(ID.ITEM).toString());
   }
}
