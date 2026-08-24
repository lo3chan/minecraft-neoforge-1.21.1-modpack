package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity.CatalystListener;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;

public class SculkBudBlockEntity extends BlockEntity {
   private final CatalystListener sculkListener;

   public SculkBudBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AlchemancyBlockEntities.SCULK_BUD.get(), pos, blockState);
      this.sculkListener = new CatalystListener(blockState, new BlockPositionSource(pos));
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, SculkBudBlockEntity bud) {
      bud.sculkListener.getSculkSpreader().updateCursors(level, pos, level.getRandom(), true);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.sculkListener.getSculkSpreader().load(tag);
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      this.sculkListener.getSculkSpreader().save(tag);
      super.saveAdditional(tag, registries);
   }

   public void addCursor(int charge, BlockPos pos) {
      this.sculkListener.getSculkSpreader().addCursors(pos, charge);
   }
}
