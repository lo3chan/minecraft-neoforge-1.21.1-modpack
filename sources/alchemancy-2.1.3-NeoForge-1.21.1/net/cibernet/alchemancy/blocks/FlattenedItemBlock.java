package net.cibernet.alchemancy.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.client.render.ItemStackHolderCustomRender;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class FlattenedItemBlock extends BaseEntityBlock implements ItemStackHolderCustomRender, SimpleWaterloggedBlock {
   private static final MapCodec<FlattenedItemBlock> CODEC = simpleCodec(FlattenedItemBlock::new);
   private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
   private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
   private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public FlattenedItemBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(ROTATION, 0)).setValue(WATERLOGGED, false)
      );
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case UP -> UP_AABB;
         case DOWN -> DOWN_AABB;
         case WEST -> WEST_AABB;
         case EAST -> EAST_AABB;
         case NORTH -> NORTH_AABB;
         case SOUTH -> SOUTH_AABB;
         default -> throw new MatchException(null, null);
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, ROTATION, WATERLOGGED});
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pIsMoving) {
      Containers.dropContentsOnDestroy(state, newState, level, pos);
      super.onRemove(state, level, pos, newState, pIsMoving);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      return level.getBlockEntity(pos) instanceof ItemStackHolderBlockEntity blockEntity
         ? blockEntity.getItem().copy()
         : super.getCloneItemStack(state, target, level, pos, player);
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ItemStackHolderBlockEntity(pos, state);
   }

   protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      return canSupportCenter(level, pos.relative(facing), facing.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()))
            .setValue(ROTATION, context.getClickedFace().getAxis() == Axis.Y ? RotationSegment.convertToSegment(context.getRotation() + 180.0F) : 0))
         .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState pFacingState, LevelAccessor level, BlockPos pos, BlockPos pFacingPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, pFacingState, level, pos, pFacingPos);
   }

   protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   protected FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void render(
      ItemRenderer itemRenderer,
      ItemStackHolderBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      BlockState state = blockEntity.getBlockState();
      poseStack.translate(0.5F, 0.5F, 0.5F);
      Direction direction = (Direction)state.getValue(FACING);
      poseStack.translate(direction.getStepX() * 0.46875, direction.getStepY() * 0.46875, direction.getStepZ() * 0.46875);
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(direction.getAxis() == Axis.X ? 90.0F : 0.0F));
      poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(direction.getAxisDirection() == AxisDirection.NEGATIVE ? 180.0F : 0.0F));
      poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(direction.getAxis() == Axis.Y ? 90.0F : 0.0F));
      poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(RotationSegment.convertToDegrees((Integer)state.getValue(ROTATION))));
      itemRenderer.renderStatic(
         blockEntity.getItem(), ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 0
      );
   }
}
