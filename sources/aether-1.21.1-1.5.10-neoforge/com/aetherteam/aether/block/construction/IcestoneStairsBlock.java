package com.aetherteam.aether.block.construction;

import com.aetherteam.aether.blockentity.IcestoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class IcestoneStairsBlock extends StairBlock implements EntityBlock {
   public IcestoneStairsBlock(BlockState state, Properties properties) {
      super(state, properties);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new IcestoneBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> GameEventListener getListener(ServerLevel level, T blockEntity) {
      return blockEntity instanceof IcestoneBlockEntity icestoneBlockEntity ? icestoneBlockEntity.getListener() : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
      return null;
   }

   public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
      super.triggerEvent(state, level, pos, id, param);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return blockEntity != null && blockEntity.triggerEvent(id, param);
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
      return level.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
   }
}
