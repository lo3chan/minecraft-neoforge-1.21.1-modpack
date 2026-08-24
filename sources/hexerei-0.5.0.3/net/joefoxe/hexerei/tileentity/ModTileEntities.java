package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTileEntities {
   public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "hexerei");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MixingCauldronTile>> MIXING_CAULDRON_TILE = TILE_ENTITIES.register(
      "mixing_cauldron_entity", () -> Builder.of(MixingCauldronTile::new, new Block[]{(Block)ModBlocks.MIXING_CAULDRON.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CofferTile>> COFFER_TILE = TILE_ENTITIES.register(
      "coffer_entity", () -> Builder.of(CofferTile::new, new Block[]{(Block)ModBlocks.COFFER.get(), (Block)ModBlocks.ENTANGLED_COFFER.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CourierPackageTile>> COURIER_PACKAGE_TILE = TILE_ENTITIES.register(
      "courier_package_tile", () -> Builder.of(CourierPackageTile::new, new Block[]{(Block)ModBlocks.COURIER_PACKAGE.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CourierLetterTile>> COURIER_LETTER_TILE = TILE_ENTITIES.register(
      "courier_letter_tile", () -> Builder.of(CourierLetterTile::new, new Block[]{(Block)ModBlocks.COURIER_LETTER.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbJarTile>> HERB_JAR_TILE = TILE_ENTITIES.register(
      "herb_jar_entity", () -> Builder.of(HerbJarTile::new, new Block[]{(Block)ModBlocks.HERB_JAR.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalBallTile>> CRYSTAL_BALL_TILE = TILE_ENTITIES.register(
      "crystal_ball_entity", () -> Builder.of(CrystalBallTile::new, new Block[]{(Block)ModBlocks.CRYSTAL_BALL.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BookOfShadowsAltarTile>> BOOK_OF_SHADOWS_ALTAR_TILE = TILE_ENTITIES.register(
      "book_of_shadows_altar_entity",
      () -> Builder.of(
            BookOfShadowsAltarTile::new,
            new Block[]{(Block)ModBlocks.BOOK_OF_SHADOWS_ALTAR.get(), (Block)ModBlocks.WILLOW_ALTAR.get(), (Block)ModBlocks.WITCH_HAZEL_ALTAR.get()}
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BroomStandTile>> BROOM_STAND_TILE = TILE_ENTITIES.register(
      "broom_stand_entity",
      () -> Builder.of(
            BroomStandTile::new,
            new Block[]{
               (Block)ModBlocks.MAHOGANY_BROOM_STAND.get(),
               (Block)ModBlocks.MAHOGANY_BROOM_STAND_WALL.get(),
               (Block)ModBlocks.WILLOW_BROOM_STAND.get(),
               (Block)ModBlocks.WILLOW_BROOM_STAND_WALL.get(),
               (Block)ModBlocks.WITCH_HAZEL_BROOM_STAND.get(),
               (Block)ModBlocks.WITCH_HAZEL_BROOM_STAND_WALL.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OwlCourierDepotTile>> OWL_COURIER_DEPOT_TILE = TILE_ENTITIES.register(
      "owl_courier_depot_entity",
      () -> Builder.of(
            OwlCourierDepotTile::new,
            new Block[]{
               (Block)ModBlocks.MAHOGANY_COURIER_DEPOT.get(),
               (Block)ModBlocks.MAHOGANY_COURIER_DEPOT_WALL.get(),
               (Block)ModBlocks.WILLOW_COURIER_DEPOT.get(),
               (Block)ModBlocks.WILLOW_COURIER_DEPOT_WALL.get(),
               (Block)ModBlocks.WITCH_HAZEL_COURIER_DEPOT.get(),
               (Block)ModBlocks.WITCH_HAZEL_COURIER_DEPOT_WALL.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CandleTile>> CANDLE_TILE = TILE_ENTITIES.register(
      "candle_entity", () -> Builder.of(CandleTile::new, new Block[]{(Block)ModBlocks.CANDLE.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CandleDipperTile>> CANDLE_DIPPER_TILE = TILE_ENTITIES.register(
      "candle_dipper_entity", () -> Builder.of(CandleDipperTile::new, new Block[]{(Block)ModBlocks.CANDLE_DIPPER.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DryingRackTile>> DRYING_RACK_TILE = TILE_ENTITIES.register(
      "drying_rack_entity",
      () -> Builder.of(
            DryingRackTile::new,
            new Block[]{
               (Block)ModBlocks.HERB_DRYING_RACK.get(),
               (Block)ModBlocks.MAHOGANY_DRYING_RACK.get(),
               (Block)ModBlocks.WILLOW_DRYING_RACK.get(),
               (Block)ModBlocks.WITCH_HAZEL_DRYING_RACK.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PestleAndMortarTile>> PESTLE_AND_MORTAR_TILE = TILE_ENTITIES.register(
      "pestle_and_mortar_entity", () -> Builder.of(PestleAndMortarTile::new, new Block[]{(Block)ModBlocks.PESTLE_AND_MORTAR.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SageBurningPlateTile>> SAGE_BURNING_PLATE_TILE = TILE_ENTITIES.register(
      "sage_burning_plate_entity", () -> Builder.of(SageBurningPlateTile::new, new Block[]{(Block)ModBlocks.SAGE_BURNING_PLATE.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModChestBlockEntity>> CHEST_TILE = TILE_ENTITIES.register(
      "chest_entity",
      () -> Builder.of(
            ModChestBlockEntity::new,
            new Block[]{(Block)ModBlocks.WILLOW_CHEST.get(), (Block)ModBlocks.WITCH_HAZEL_CHEST.get(), (Block)ModBlocks.MAHOGANY_CHEST.get()}
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModSignBlockEntity>> SIGN_TILE = TILE_ENTITIES.register(
      "sign_entity",
      () -> Builder.of(
            ModSignBlockEntity::new,
            new Block[]{
               (Block)ModBlocks.WILLOW_SIGN.get(),
               (Block)ModBlocks.WITCH_HAZEL_SIGN.get(),
               (Block)ModBlocks.MAHOGANY_SIGN.get(),
               (Block)ModBlocks.WILLOW_WALL_SIGN.get(),
               (Block)ModBlocks.WITCH_HAZEL_WALL_SIGN.get(),
               (Block)ModBlocks.MAHOGANY_WALL_SIGN.get(),
               (Block)ModBlocks.POLISHED_WILLOW_SIGN.get(),
               (Block)ModBlocks.POLISHED_WITCH_HAZEL_SIGN.get(),
               (Block)ModBlocks.POLISHED_MAHOGANY_SIGN.get(),
               (Block)ModBlocks.POLISHED_WILLOW_WALL_SIGN.get(),
               (Block)ModBlocks.POLISHED_WITCH_HAZEL_WALL_SIGN.get(),
               (Block)ModBlocks.POLISHED_MAHOGANY_WALL_SIGN.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModHangingSignBlockEntity>> HANGING_SIGN_TILE = TILE_ENTITIES.register(
      "hanging_sign_entity",
      () -> Builder.of(
            ModHangingSignBlockEntity::new,
            new Block[]{
               (Block)ModBlocks.MAHOGANY_HANGING_SIGN.get(),
               (Block)ModBlocks.MAHOGANY_WALL_HANGING_SIGN.get(),
               (Block)ModBlocks.WILLOW_HANGING_SIGN.get(),
               (Block)ModBlocks.WILLOW_WALL_HANGING_SIGN.get(),
               (Block)ModBlocks.WITCH_HAZEL_HANGING_SIGN.get(),
               (Block)ModBlocks.WITCH_HAZEL_WALL_HANGING_SIGN.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CuttingCrystalTile>> CUTTING_CRYSTAL_TILE = TILE_ENTITIES.register(
      "cutting_crystal_entity", () -> Builder.of(CuttingCrystalTile::new, new Block[]{(Block)ModBlocks.CUTTING_CRYSTAL.get()}).build(null)
   );

   public static void register(IEventBus eventBus) {
      TILE_ENTITIES.register(eventBus);
   }
}
