package net.astralya.hexalia.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class RusticOvenBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   public RusticOvenBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      if (!entity.fireImmune() && !entity.isSteppingCarefully() && entity instanceof LivingEntity) {
         entity.hurt(level.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(level, pos, state, entity);
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      double x = pos.getX() + 0.5;
      double y = pos.getY();
      double z = pos.getZ() + 0.5;
      if (random.nextDouble() < 0.1) {
         level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      }

      Direction direction = (Direction)state.getValue(FACING);
      Axis axis = direction.getAxis();
      double offset = 0.52;
      double spread = random.nextDouble() * 0.6 - 0.3;
      double particleX = axis == Axis.X ? direction.getStepX() * offset : spread;
      double particleY = random.nextDouble() * 10.0 / 16.0;
      double particleZ = axis == Axis.Z ? direction.getStepZ() * offset : spread;
      level.addParticle(ParticleTypes.SMOKE, x + particleX, y + particleY, z + particleZ, 0.0, 0.0, 0.0);
      level.addParticle(ParticleTypes.FLAME, x + particleX, y + particleY, z + particleZ, 0.0, 0.0, 0.0);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected BlockState rotate(BlockState state, Rotation direction) {
      return (BlockState)state.setValue(FACING, direction.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
