package net.mcreator.borninchaosv.block;

import net.mcreator.borninchaosv.procedures.CobwebCoverPriObnovlieniiTikaProcedure;
import net.mcreator.borninchaosv.procedures.CobwebCoverPriStolknovieniiSushchnostiSBlokomProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CobwebCoverBlock extends Block {
   public CobwebCoverBlock() {
      super(
         Properties.of()
            .ignitedByLava()
            .sound(SoundType.COBWEB)
            .strength(0.2F, 0.0F)
            .noCollission()
            .friction(0.4F)
            .speedFactor(0.7F)
            .noOcclusion()
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
      return box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   }

   public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 5;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
      return 1;
   }

   public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
      super.onPlace(blockstate, world, pos, oldState, moving);
      world.scheduleTick(pos, this, 2);
   }

   public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
      super.tick(blockstate, world, pos, random);
      CobwebCoverPriObnovlieniiTikaProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
      world.scheduleTick(pos, this, 2);
   }

   public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
      super.entityInside(blockstate, world, pos, entity);
      CobwebCoverPriStolknovieniiSushchnostiSBlokomProcedure.execute(entity);
   }
}
