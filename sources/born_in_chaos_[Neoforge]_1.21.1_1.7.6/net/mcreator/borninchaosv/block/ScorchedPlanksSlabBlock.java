package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.ScorchedLogKoghdaIghrokNachinaietUnichtozhatProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class ScorchedPlanksSlabBlock extends SlabBlock {
   public ScorchedPlanksSlabBlock() {
      super(Properties.of().ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(2.0F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 1;
   }

   public void attack(BlockState blockstate, Level world, BlockPos pos, Player entity) {
      super.attack(blockstate, world, pos, entity);
      ScorchedLogKoghdaIghrokNachinaietUnichtozhatProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
