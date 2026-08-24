package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.RottenSoilPriRazrushieniiBlokaIghrokomProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;

public class RottenSoilBlock extends Block {
   public RottenSoilBlock() {
      super(Properties.of().sound(SoundType.GRAVEL).strength(0.6F, 0.5F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      RottenSoilPriRazrushieniiBlokaIghrokomProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
      return retval;
   }
}
