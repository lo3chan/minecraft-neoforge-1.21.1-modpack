package io.github.razordevs.deep_aether.block.behavior;

import io.github.razordevs.deep_aether.init.DAItems;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface GoldenVines {
   VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   BooleanProperty BERRIES = BlockStateProperties.BERRIES;

   static InteractionResult use(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
      if ((Boolean)state.getValue(BERRIES)) {
         Block.popResource(level, pos, new ItemStack((ItemLike)DAItems.GOLDEN_BERRIES.get(), 1));
         float f = Mth.randomBetween(level.random, 0.8F, 1.2F);
         level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, f);
         BlockState blockstate = (BlockState)state.setValue(BERRIES, Boolean.FALSE);
         level.setBlock(pos, blockstate, 2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(entity, blockstate));
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   static ToIntFunction<BlockState> emission(int emission) {
      return state -> state.getValue(BlockStateProperties.BERRIES) ? emission : 0;
   }
}
