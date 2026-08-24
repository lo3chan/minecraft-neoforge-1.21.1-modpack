package com.mcwfences.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoubleGate extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<DoubleGate.FenceGateState> GATESTATE = EnumProperty.create("fencepart", DoubleGate.FenceGateState.class);
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   protected static final VoxelShape EE = box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
   protected static final VoxelShape NN = box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
   protected static final VoxelShape EE_COLLISION = box(0.0, 0.0, 7.0, 16.0, 24.0, 9.0);
   protected static final VoxelShape NN_COLLISION = box(7.0, 0.0, 0.0, 9.0, 24.0, 16.0);

   public DoubleGate(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                  .setValue(GATESTATE, DoubleGate.FenceGateState.TOP))
               .setValue(OPEN, false))
            .setValue(HINGE, DoorHingeSide.LEFT)
      );
   }

   private BlockState updateFenceGateState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      DoubleGate.FenceGateState newState = above ? DoubleGate.FenceGateState.BOTTOM : DoubleGate.FenceGateState.TOP;
      return (BlockState)state.setValue(GATESTATE, newState);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return this.updateFenceGateState(state, level, pos);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      if ((Boolean)state.getValue(OPEN)) {
         return Shapes.empty();
      } else {
         return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? NN_COLLISION : EE_COLLISION;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      BlockState baseState = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()))
         .setValue(HINGE, this.getHinge(context));
      return this.updateFenceGateState(baseState, level, pos);
   }

   private DoorHingeSide getHinge(BlockPlaceContext context) {
      BlockPos blockpos = context.getClickedPos();
      Direction direction = context.getHorizontalDirection();
      int j = direction.getStepX();
      int k = direction.getStepZ();
      Vec3 vector3d = context.getClickLocation();
      double d0 = vector3d.x - blockpos.getX();
      double d1 = vector3d.z - blockpos.getZ();
      return j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      if (!level.isClientSide) {
         ItemStack itemstack = player.getItemInHand(handIn);
         if (itemstack.getItem() == this.asItem()) {
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.8F);
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         this.fenceGate(level, pos, !(Boolean)state.getValue(OPEN), (Direction)state.getValue(FACING));
         boolean currentState = (Boolean)state.getValue(OPEN);
         boolean newState = !currentState;
         state = (BlockState)state.setValue(OPEN, newState);
         level.setBlockAndUpdate(pos, state);
         level.playSound(null, pos, newState ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return ItemInteractionResult.SUCCESS;
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return ((Direction)state.getValue(FACING)).getAxis() == Axis.X ? NN : EE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, GATESTATE, OPEN, HINGE});
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      Block block = level.getBlockState(pos).getBlock();
      Block below = level.getBlockState(pos.below(1)).getBlock();
      if (block == this && below == block) {
         DoorHingeSide hinge = (DoorHingeSide)level.getBlockState(pos.below(1)).getValue(HINGE);
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(HINGE, hinge));
      }
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   private void fenceGate(Level world, BlockPos pos, boolean bool, Direction dir) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() == this && (Boolean)state.getValue(OPEN) != bool && ((Direction)state.getValue(FACING)).equals(dir)) {
         world.setBlockAndUpdate(pos, (BlockState)state.setValue(OPEN, bool));

         for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
               for (int z = -1; z <= 1; z++) {
                  BlockPos newPos = pos.offset(x, y, z);
                  this.fenceGate(world, newPos, bool, dir);
               }
            }
         }
      }
   }

   public static enum FenceGateState implements StringRepresentable {
      BOTTOM("bottom"),
      TOP("top");

      private final String name;

      private FenceGateState(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
