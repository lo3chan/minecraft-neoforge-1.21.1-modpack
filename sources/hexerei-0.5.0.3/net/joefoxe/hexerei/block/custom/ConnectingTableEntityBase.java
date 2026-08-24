package net.joefoxe.hexerei.block.custom;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ConnectingTableEntityBase extends ConnectingTable implements EntityBlock {
   public ConnectingTableEntityBase(Properties pProperties) {
      super(pProperties);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return null;
   }

   public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
      super.triggerEvent(pState, pLevel, pPos, pId, pParam);
      BlockEntity blockentity = pLevel.getBlockEntity(pPos);
      return blockentity == null ? false : blockentity.triggerEvent(pId, pParam);
   }
}
