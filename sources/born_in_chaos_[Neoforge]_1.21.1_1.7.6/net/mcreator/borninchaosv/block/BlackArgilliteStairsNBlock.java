package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.BlackArgilliteNKoghdaBlokRazrushienIghrokomProcedure;
import net.mcreator.borninchaosv.procedures.BlackArgilliteNKoghdaIghrokNachinaietUnichtozhatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;

public class BlackArgilliteStairsNBlock extends StairBlock {
   public BlackArgilliteStairsNBlock() {
      super(
         Blocks.AIR.defaultBlockState(),
         Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.DEEPSLATE).strength(25.0F, 1000.0F).requiresCorrectToolForDrops()
      );
   }

   public float getExplosionResistance() {
      return 1000.0F;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public boolean onDestroyedByPlayer(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid) {
      boolean retval = super.onDestroyedByPlayer(blockstate, world, pos, entity, willHarvest, fluid);
      BlackArgilliteNKoghdaBlokRazrushienIghrokomProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
      return retval;
   }

   public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
      super.attack(blockstate, world, pos, entity);
      BlackArgilliteNKoghdaIghrokNachinaietUnichtozhatProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), entity);
   }
}
