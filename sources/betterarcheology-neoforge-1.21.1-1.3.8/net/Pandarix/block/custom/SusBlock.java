package net.Pandarix.block.custom;

import net.Pandarix.block.entity.SusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class SusBlock extends BrushableBlock {
   public SusBlock(Block baseBlock, Properties settings, SoundEvent brushingSound, SoundEvent brushingCompleteSound) {
      super(baseBlock, brushingSound, brushingCompleteSound, settings);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SusBlockEntity(pos, state);
   }
}
