package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoffinBlockAddedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (!world.isClientSide()) {
         BlockPos _bp = BlockPos.containing(x, y, z);
         BlockEntity _blockEntity = world.getBlockEntity(_bp);
         BlockState _bs = world.getBlockState(_bp);
         if (_blockEntity != null) {
            _blockEntity.getPersistentData().putDouble("open", 0.0);
         }

         if (world instanceof Level _level) {
            _level.sendBlockUpdated(_bp, _bs, _bs, 3);
         }
      }

      if (!world.isClientSide()) {
         BlockPos _bpx = BlockPos.containing(x, y, z);
         BlockEntity _blockEntityx = world.getBlockEntity(_bpx);
         BlockState _bsx = world.getBlockState(_bpx);
         if (_blockEntityx != null) {
            _blockEntityx.getPersistentData().putDouble("loot", 12.0);
         }

         if (world instanceof Level _level) {
            _level.sendBlockUpdated(_bpx, _bsx, _bsx, 3);
         }
      }

      if (!world.isClientSide()) {
         BlockPos _bpxx = BlockPos.containing(x, y, z);
         BlockEntity _blockEntityxx = world.getBlockEntity(_bpxx);
         BlockState _bsxx = world.getBlockState(_bpxx);
         if (_blockEntityxx != null) {
            _blockEntityxx.getPersistentData().putDouble("swicth", 0.0);
         }

         if (world instanceof Level _level) {
            _level.sendBlockUpdated(_bpxx, _bsxx, _bsxx, 3);
         }
      }
   }
}
