package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMTileEntityRegistry {
   public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "alexsmobs");
   public static final Supplier<BlockEntityType<TileEntityLeafcutterAnthill>> LEAFCUTTER_ANTHILL = DEF_REG.register(
      "leafcutter_anthill_te", () -> type(TileEntityLeafcutterAnthill::new, AMBlockRegistry.LEAFCUTTER_ANTHILL.get())
   );
   public static final Supplier<BlockEntityType<TileEntityCapsid>> CAPSID = DEF_REG.register(
      "capsid_te", () -> type(TileEntityCapsid::new, AMBlockRegistry.CAPSID.get())
   );
   public static final Supplier<BlockEntityType<TileEntityVoidWormBeak>> VOID_WORM_BEAK = DEF_REG.register(
      "void_worm_beak_te", () -> type(TileEntityVoidWormBeak::new, AMBlockRegistry.VOID_WORM_BEAK.get())
   );
   public static final Supplier<BlockEntityType<TileEntityTerrapinEgg>> TERRAPIN_EGG = DEF_REG.register(
      "terrapin_egg_te", () -> type(TileEntityTerrapinEgg::new, AMBlockRegistry.TERRAPIN_EGG.get())
   );
   public static final Supplier<BlockEntityType<TileEntityTransmutationTable>> TRANSMUTATION_TABLE = DEF_REG.register(
      "transmutation_table", () -> type(TileEntityTransmutationTable::new, AMBlockRegistry.TRANSMUTATION_TABLE.get())
   );
   public static final Supplier<BlockEntityType<TileEntitySculkBoomer>> SCULK_BOOMER = DEF_REG.register(
      "sculk_boomer", () -> type(TileEntitySculkBoomer::new, AMBlockRegistry.SCULK_BOOMER.get())
   );
   public static final Supplier<BlockEntityType<TileEntityEndPirateDoor>> END_PIRATE_DOOR = null;
   public static final Supplier<BlockEntityType<TileEntityEndPirateAnchor>> END_PIRATE_ANCHOR = null;
   public static final Supplier<BlockEntityType<TileEntityEndPirateAnchorWinch>> END_PIRATE_ANCHOR_WINCH = null;
   public static final Supplier<BlockEntityType<TileEntityEndPirateShipWheel>> END_PIRATE_SHIP_WHEEL = null;
   public static final Supplier<BlockEntityType<TileEntityEndPirateFlag>> END_PIRATE_FLAG = null;

   private static <T extends BlockEntity> BlockEntityType<T> type(BlockEntitySupplier<T> factory, Block block) {
      return Builder.of(factory, new Block[]{block}).build(null);
   }
}
