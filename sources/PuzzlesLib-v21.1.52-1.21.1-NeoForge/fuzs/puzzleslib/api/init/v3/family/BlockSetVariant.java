package fuzs.puzzleslib.api.init.v3.family;

import fuzs.puzzleslib.impl.init.VanillaBlockSetVariant;
import fuzs.puzzleslib.impl.init.boat.TypedBoat;
import fuzs.puzzleslib.impl.init.boat.TypedBoatItem;
import fuzs.puzzleslib.impl.init.boat.TypedChestBoat;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.data.BlockFamily.Builder;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public interface BlockSetVariant extends StringRepresentable {
   BlockSetVariant CHISELED = new VanillaBlockSetVariant.Direct(Variant.CHISELED, Builder::chiseled);
   BlockSetVariant CRACKED = new VanillaBlockSetVariant.Direct(Variant.CRACKED, Builder::cracked);
   BlockSetVariant POLISHED = new VanillaBlockSetVariant.Direct(Variant.POLISHED, Builder::polished);
   BlockSetVariant CUT = new VanillaBlockSetVariant.Direct(Variant.CUT, Builder::cut);
   BlockSetVariant MOSAIC = new VanillaBlockSetVariant.Direct(Variant.MOSAIC, Builder::mosaic);
   BlockSetVariant STAIRS = new VanillaBlockSetVariant(Variant.STAIRS, Builder::stairs) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("stairs"),
                  properties -> new StairBlock(((Block)context.getBaseBlock().value()).defaultBlockState(), properties),
                  () -> Properties.ofLegacyCopy((BlockBehaviour)context.getBaseBlock().value())
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant SLAB = new VanillaBlockSetVariant(Variant.SLAB, Builder::slab) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(context.getNameWithSuffix("slab"), SlabBlock::new, () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()))
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant WALL = new VanillaBlockSetVariant(Variant.WALL, Builder::wall) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("wall"),
                  WallBlock::new,
                  () -> Properties.ofLegacyCopy((BlockBehaviour)context.getBaseBlock().value()).forceSolidOn()
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant FENCE = new VanillaBlockSetVariant(Variant.FENCE, Builder::fence) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(context.getNameWithSuffix("fence"), FenceBlock::new, () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()))
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant FENCE_GATE = new VanillaBlockSetVariant(Variant.FENCE_GATE, Builder::fenceGate) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("fence_gate"),
                  properties -> new FenceGateBlock(context.getWoodType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).forceSolidOn()
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant DOOR = new VanillaBlockSetVariant(Variant.DOOR, Builder::door) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("door"),
                  properties -> new DoorBlock(context.getBlockSetType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).noOcclusion().pushReaction(PushReaction.DESTROY)
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this), DoubleHighBlockItem::new));
      }
   };
   BlockSetVariant TRAPDOOR = new VanillaBlockSetVariant(Variant.TRAPDOOR, Builder::trapdoor) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("trapdoor"),
                  properties -> new TrapDoorBlock(context.getBlockSetType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).noOcclusion().isValidSpawn(Blocks::never)
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant BUTTON = new VanillaBlockSetVariant(Variant.BUTTON, Builder::button) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("button"),
                  properties -> new ButtonBlock(context.getBlockSetType(), 30, properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).noCollission().pushReaction(PushReaction.DESTROY)
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant PRESSURE_PLATE = new VanillaBlockSetVariant(Variant.PRESSURE_PLATE, Builder::pressurePlate) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("pressure_plate"),
                  properties -> new PressurePlateBlock(context.getBlockSetType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).forceSolidOn().noCollission().pushReaction(PushReaction.DESTROY)
               )
         );
         context.registerItem(this, context.getRegistries().registerBlockItem(context.getBlock(this)));
      }
   };
   BlockSetVariant SIGN = new StandaloneBlockSetVariant(Variant.SIGN) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("sign"),
                  properties -> new StandingSignBlock(context.getWoodType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).forceSolidOn().noCollission()
               )
         );
         Holder<Block> signHolder = context.getBlock(this);
         context.registerBlock(
            WALL_SIGN,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("wall_sign"),
                  properties -> new WallSignBlock(context.getWoodType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value())
                     .dropsLike((Block)signHolder.value())
                     .forceSolidOn()
                     .noCollission()
               )
         );
         context.registerItem(
            this,
            context.getRegistries()
               .registerBlockItem(
                  signHolder,
                  (block, properties) -> new SignItem(properties, block, (Block)context.getBlock(WALL_SIGN).value()),
                  () -> new net.minecraft.world.item.Item.Properties().stacksTo(16)
               )
         );
      }
   };
   BlockSetVariant WALL_SIGN = new StandaloneBlockSetVariant(Variant.WALL_SIGN) {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         throw new UnsupportedOperationException();
      }
   };
   BlockSetVariant HANGING_SIGN = new StandaloneBlockSetVariant("hanging_sign") {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerBlock(
            this,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("hanging_sign"),
                  properties -> new CeilingHangingSignBlock(context.getWoodType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value()).forceSolidOn().noCollission()
               )
         );
         Holder<Block> hangingSignHolder = context.getBlock(this);
         context.registerBlock(
            WALL_HANGING_SIGN,
            context.getRegistries()
               .registerBlock(
                  context.getNameWithSuffix("wall_hanging_sign"),
                  properties -> new WallHangingSignBlock(context.getWoodType(), properties),
                  () -> Properties.ofFullCopy((BlockBehaviour)context.getBaseBlock().value())
                     .dropsLike((Block)hangingSignHolder.value())
                     .forceSolidOn()
                     .noCollission()
               )
         );
         context.registerItem(
            this,
            context.getRegistries()
               .registerBlockItem(
                  hangingSignHolder,
                  (block, properties) -> new HangingSignItem(block, (Block)context.getBlock(WALL_HANGING_SIGN).value(), properties),
                  () -> new net.minecraft.world.item.Item.Properties().stacksTo(16)
               )
         );
      }
   };
   BlockSetVariant WALL_HANGING_SIGN = new StandaloneBlockSetVariant("wall_hanging_sign") {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         throw new UnsupportedOperationException();
      }
   };
   BlockSetVariant BOAT = new StandaloneBlockSetVariant("boat") {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerEntityType(
            this,
            context.getRegistries()
               .registerEntityType(
                  context.getNameWithSuffix("boat"),
                  () -> net.minecraft.world.entity.EntityType.Builder.of(
                        (entityType, level) -> new TypedBoat(entityType, level, () -> (Item)context.getItem(this).value()), MobCategory.MISC
                     )
                     .sized(1.375F, 0.5625F)
                     .eyeHeight(0.5625F)
                     .clientTrackingRange(10)
               )
         );
         context.registerItem(
            this,
            context.getRegistries()
               .registerItem(
                  context.getNameWithSuffix("boat"),
                  properties -> new TypedBoatItem((EntityType<? extends Boat>)context.getEntityType(this).value(), properties),
                  () -> new net.minecraft.world.item.Item.Properties().stacksTo(1)
               )
         );
      }
   };
   BlockSetVariant CHEST_BOAT = new StandaloneBlockSetVariant("chest_boat") {
      @Override
      public void generateFor(BlockSetFamily.Context context) {
         context.registerEntityType(
            this,
            context.getRegistries()
               .registerEntityType(
                  context.getNameWithSuffix("chest_boat"),
                  () -> net.minecraft.world.entity.EntityType.Builder.of(
                        (entityType, level) -> new TypedChestBoat(entityType, level, () -> (Item)context.getItem(this).value()), MobCategory.MISC
                     )
                     .sized(1.375F, 0.5625F)
                     .eyeHeight(0.5625F)
                     .clientTrackingRange(10)
               )
         );
         context.registerItem(
            this,
            context.getRegistries()
               .registerItem(
                  context.getNameWithSuffix("chest_boat"),
                  properties -> new TypedBoatItem((EntityType<? extends Boat>)context.getEntityType(this).value(), properties),
                  () -> new net.minecraft.world.item.Item.Properties().stacksTo(1)
               )
         );
      }
   };

   void generateFor(BlockSetFamily.Context var1);

   @Nullable
   Variant toVanilla();
}
