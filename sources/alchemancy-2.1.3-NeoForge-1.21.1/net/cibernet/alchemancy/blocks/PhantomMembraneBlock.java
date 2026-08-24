package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.entity.CustomFallingBlock;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PhantomMembraneBlock extends ColoredFallingBlock {
   public PhantomMembraneBlock(Properties properties) {
      super(new ColorRGBA(8282747), properties);
   }

   public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity fallingBlock) {
      if (!fallingBlock.isSilent()) {
         level.levelEvent(2008, pos, 0);
         level.levelEvent(2001, pos, getId(this.defaultBlockState()));
         fallingBlock.playSound((SoundEvent)AlchemancySoundEvents.PHANTOM_MEMBRANE_POP.value());
      }
   }

   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (isFree(level.getBlockState(pos.above())) && pos.getY() <= level.getMaxBuildHeight()) {
         CustomFallingBlock fallingblockentity = CustomFallingBlock.fall(level, pos, state, this.asItem().getDefaultInstance());
         fallingblockentity.setGravity(-0.0125F);
         fallingblockentity.setHasCollision(true);
         fallingblockentity.dropItem = false;
         this.falling(fallingblockentity);
      }
   }
}
