package vectorwing.farmersdelight.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.block.entity.BasketBlockEntity;
import vectorwing.farmersdelight.common.block.entity.CabinetBlockEntity;
import vectorwing.farmersdelight.common.block.entity.CanvasSignBlockEntity;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.block.entity.HangingCanvasSignBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;

public class ModBlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "farmersdelight");
   public static final Supplier<BlockEntityType<StoveBlockEntity>> STOVE = TILES.register(
      "stove", () -> Builder.of(StoveBlockEntity::new, new Block[]{ModBlocks.STOVE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<CookingPotBlockEntity>> COOKING_POT = TILES.register(
      "cooking_pot", () -> Builder.of(CookingPotBlockEntity::new, new Block[]{ModBlocks.COOKING_POT.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasketBlockEntity>> BASKET = TILES.register(
      "basket", () -> Builder.of(BasketBlockEntity::new, new Block[]{ModBlocks.WOODEN_BASKET.get(), ModBlocks.BAMBOO_BASKET.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<CuttingBoardBlockEntity>> CUTTING_BOARD = TILES.register(
      "cutting_board", () -> Builder.of(CuttingBoardBlockEntity::new, new Block[]{ModBlocks.CUTTING_BOARD.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<SkilletBlockEntity>> SKILLET = TILES.register(
      "skillet", () -> Builder.of(SkilletBlockEntity::new, new Block[]{ModBlocks.SKILLET.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<CabinetBlockEntity>> CABINET = TILES.register(
      "cabinet",
      () -> Builder.of(
            CabinetBlockEntity::new,
            new Block[]{
               ModBlocks.OAK_CABINET.get(),
               ModBlocks.BIRCH_CABINET.get(),
               ModBlocks.SPRUCE_CABINET.get(),
               ModBlocks.JUNGLE_CABINET.get(),
               ModBlocks.ACACIA_CABINET.get(),
               ModBlocks.DARK_OAK_CABINET.get(),
               ModBlocks.MANGROVE_CABINET.get(),
               ModBlocks.BAMBOO_CABINET.get(),
               ModBlocks.CHERRY_CABINET.get(),
               ModBlocks.CRIMSON_CABINET.get(),
               ModBlocks.WARPED_CABINET.get()
            }
         )
         .build(null)
   );
   public static final Supplier<BlockEntityType<CanvasSignBlockEntity>> CANVAS_SIGN = TILES.register(
      "canvas_sign",
      () -> Builder.of(
            CanvasSignBlockEntity::new,
            new Block[]{
               ModBlocks.CANVAS_SIGN.get(),
               ModBlocks.WHITE_CANVAS_SIGN.get(),
               ModBlocks.ORANGE_CANVAS_SIGN.get(),
               ModBlocks.MAGENTA_CANVAS_SIGN.get(),
               ModBlocks.LIGHT_BLUE_CANVAS_SIGN.get(),
               ModBlocks.YELLOW_CANVAS_SIGN.get(),
               ModBlocks.LIME_CANVAS_SIGN.get(),
               ModBlocks.PINK_CANVAS_SIGN.get(),
               ModBlocks.GRAY_CANVAS_SIGN.get(),
               ModBlocks.LIGHT_GRAY_CANVAS_SIGN.get(),
               ModBlocks.CYAN_CANVAS_SIGN.get(),
               ModBlocks.PURPLE_CANVAS_SIGN.get(),
               ModBlocks.BLUE_CANVAS_SIGN.get(),
               ModBlocks.BROWN_CANVAS_SIGN.get(),
               ModBlocks.GREEN_CANVAS_SIGN.get(),
               ModBlocks.RED_CANVAS_SIGN.get(),
               ModBlocks.BLACK_CANVAS_SIGN.get(),
               ModBlocks.CANVAS_WALL_SIGN.get(),
               ModBlocks.WHITE_CANVAS_WALL_SIGN.get(),
               ModBlocks.ORANGE_CANVAS_WALL_SIGN.get(),
               ModBlocks.MAGENTA_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIGHT_BLUE_CANVAS_WALL_SIGN.get(),
               ModBlocks.YELLOW_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIME_CANVAS_WALL_SIGN.get(),
               ModBlocks.PINK_CANVAS_WALL_SIGN.get(),
               ModBlocks.GRAY_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIGHT_GRAY_CANVAS_WALL_SIGN.get(),
               ModBlocks.CYAN_CANVAS_WALL_SIGN.get(),
               ModBlocks.PURPLE_CANVAS_WALL_SIGN.get(),
               ModBlocks.BLUE_CANVAS_WALL_SIGN.get(),
               ModBlocks.BROWN_CANVAS_WALL_SIGN.get(),
               ModBlocks.GREEN_CANVAS_WALL_SIGN.get(),
               ModBlocks.RED_CANVAS_WALL_SIGN.get(),
               ModBlocks.BLACK_CANVAS_WALL_SIGN.get()
            }
         )
         .build(null)
   );
   public static final Supplier<BlockEntityType<HangingCanvasSignBlockEntity>> HANGING_CANVAS_SIGN = TILES.register(
      "hanging_canvas_sign",
      () -> Builder.of(
            HangingCanvasSignBlockEntity::new,
            new Block[]{
               ModBlocks.HANGING_CANVAS_SIGN.get(),
               ModBlocks.WHITE_HANGING_CANVAS_SIGN.get(),
               ModBlocks.ORANGE_HANGING_CANVAS_SIGN.get(),
               ModBlocks.MAGENTA_HANGING_CANVAS_SIGN.get(),
               ModBlocks.LIGHT_BLUE_HANGING_CANVAS_SIGN.get(),
               ModBlocks.YELLOW_HANGING_CANVAS_SIGN.get(),
               ModBlocks.LIME_HANGING_CANVAS_SIGN.get(),
               ModBlocks.PINK_HANGING_CANVAS_SIGN.get(),
               ModBlocks.GRAY_HANGING_CANVAS_SIGN.get(),
               ModBlocks.LIGHT_GRAY_HANGING_CANVAS_SIGN.get(),
               ModBlocks.CYAN_HANGING_CANVAS_SIGN.get(),
               ModBlocks.PURPLE_HANGING_CANVAS_SIGN.get(),
               ModBlocks.BLUE_HANGING_CANVAS_SIGN.get(),
               ModBlocks.BROWN_HANGING_CANVAS_SIGN.get(),
               ModBlocks.GREEN_HANGING_CANVAS_SIGN.get(),
               ModBlocks.RED_HANGING_CANVAS_SIGN.get(),
               ModBlocks.BLACK_HANGING_CANVAS_SIGN.get(),
               ModBlocks.HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.WHITE_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.ORANGE_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.MAGENTA_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIGHT_BLUE_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.YELLOW_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIME_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.PINK_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.GRAY_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.LIGHT_GRAY_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.CYAN_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.PURPLE_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.BLUE_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.BROWN_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.GREEN_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.RED_HANGING_CANVAS_WALL_SIGN.get(),
               ModBlocks.BLACK_HANGING_CANVAS_WALL_SIGN.get()
            }
         )
         .build(null)
   );
}
