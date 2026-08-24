package net.mcreator.undeadrevamp.block;

import net.mcreator.undeadrevamp.procedures.AltaractiveUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AltaractiveBlock extends Block {
   public AltaractiveBlock() {
      super(
         Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.STONE)
            .strength(1.0F, 10.0F)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .randomTicks()
            .isRedstoneConductor((bs, br, bp) -> false)
      );
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.or(box(0.0, 0.0, 0.0, 18.0, 4.0, 18.0), new VoxelShape[]{box(1.0, 4.0, 1.0, 17.0, 8.0, 17.0), box(-3.0, 8.0, -3.0, 21.0, 16.0, 21.0)});
   }

   public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.randomTick(blockstate, world, pos, random);
      AltaractiveUpdateTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
   }
}
