package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.FelSoilKoghdaSushchnostKhoditPoBlokuProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class FelSoilBlock extends Block {
   public FelSoilBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.NETHERRACK)
            .strength(0.6F, 20.0F)
            .lightLevel(s -> 3)
            .requiresCorrectToolForDrops()
            .speedFactor(0.9F)
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering((bs, br, bp) -> true)
      );
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }

   public void stepOn(Level world, BlockPos pos, BlockState blockstate, Entity entity) {
      super.stepOn(world, pos, blockstate, entity);
      FelSoilKoghdaSushchnostKhoditPoBlokuProcedure.execute(world, entity);
   }
}
