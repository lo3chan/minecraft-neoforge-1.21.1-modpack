package vectorwing.farmersdelight.common.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SafetyNetBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(0.0, 7.0, 0.0, 16.0, 9.0, 16.0);

   public SafetyNetBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
      if (entity.isSuppressingBounce()) {
         super.fallOn(level, state, pos, entity, fallDistance);
      } else {
         entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
      }
   }

   public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
      if (entity.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(level, entity);
      } else {
         this.bounceEntity(entity);
      }
   }

   private void bounceEntity(Entity entity) {
      Vec3 vec3d = entity.getDeltaMovement();
      if (vec3d.y < 0.0) {
         double entityWeightOffset = entity instanceof LivingEntity ? 0.6 : 0.8;
         entity.setDeltaMovement(vec3d.x, -vec3d.y * entityWeightOffset, vec3d.z);
      }
   }
}
