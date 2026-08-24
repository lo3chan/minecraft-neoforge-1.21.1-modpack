package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.INSESTProcedure;
import net.mcreator.borninchaosv.procedures.InfectedDiamondOrePriRazrushieniiBlokaOtVzryvaProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;

public class InfectedDiamondOreBlock extends Block {
   public InfectedDiamondOreBlock() {
      super(Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(3.0F).requiresCorrectToolForDrops());
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      INSESTProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
      return retval;
   }

   public void wasExploded(Level world, BlockPos pos, Explosion e) {
      super.wasExploded(world, pos, e);
      InfectedDiamondOrePriRazrushieniiBlokaOtVzryvaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
