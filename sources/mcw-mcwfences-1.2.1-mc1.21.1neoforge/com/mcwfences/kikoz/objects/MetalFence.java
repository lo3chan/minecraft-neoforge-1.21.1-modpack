package com.mcwfences.kikoz.objects;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MetalFence extends Block {
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty WEST = BooleanProperty.create("west");
   public static final EnumProperty<MetalFence.FencePart> PART = EnumProperty.create(
      "fencepart", MetalFence.FencePart.class, new MetalFence.FencePart[]{MetalFence.FencePart.TOP, MetalFence.FencePart.BOTTOM}
   );
   private static final VoxelShape POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape NORTH_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 6.0);
   private static final VoxelShape SOUTH_SHAPE = Block.box(6.0, 0.0, 10.0, 10.0, 16.0, 16.0);
   private static final VoxelShape EAST_SHAPE = Block.box(10.0, 0.0, 6.0, 16.0, 16.0, 10.0);
   private static final VoxelShape WEST_SHAPE = Block.box(0.0, 0.0, 6.0, 6.0, 16.0, 10.0);
   private static final VoxelShape POST_COLLISION = Block.box(6.0, 0.0, 6.0, 10.0, 24.0, 10.0);
   private static final VoxelShape NORTH_SHAPE_COLLISION = Block.box(6.0, 0.0, 0.0, 10.0, 24.0, 6.0);
   private static final VoxelShape SOUTH_SHAPE_COLLISION = Block.box(6.0, 0.0, 10.0, 10.0, 24.0, 16.0);
   private static final VoxelShape EAST_SHAPE_COLLISION = Block.box(10.0, 0.0, 6.0, 16.0, 24.0, 10.0);
   private static final VoxelShape WEST_SHAPE_COLLISION = Block.box(0.0, 0.0, 6.0, 6.0, 24.0, 10.0);
   private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();
   private static final Map<BlockState, VoxelShape> COLLISION_SHAPE_CACHE = new HashMap<>();

   public MetalFence(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false))
                     .setValue(SOUTH, false))
                  .setValue(EAST, false))
               .setValue(WEST, false))
            .setValue(PART, MetalFence.FencePart.TOP)
      );
   }

   private BlockState FenceState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      MetalFence.FencePart connection = this.getConnectionStatus(above, below);
      return (BlockState)state.setValue(PART, connection);
   }

   private MetalFence.FencePart getConnectionStatus(boolean above, boolean below) {
      return !above ? MetalFence.FencePart.TOP : MetalFence.FencePart.BOTTOM;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      if (!statetwo.is(state.getBlock())) {
         this.FenceState(state, level, pos);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      BlockState northState = level.getBlockState(pos.north());
      BlockState southState = level.getBlockState(pos.south());
      BlockState eastState = level.getBlockState(pos.east());
      BlockState westState = level.getBlockState(pos.west());
      boolean northConnects = this.canConnect(northState, northState.isFaceSturdy(level, pos.north(), Direction.SOUTH), Direction.SOUTH);
      boolean southConnects = this.canConnect(southState, southState.isFaceSturdy(level, pos.south(), Direction.NORTH), Direction.NORTH);
      boolean eastConnects = this.canConnect(eastState, eastState.isFaceSturdy(level, pos.east(), Direction.WEST), Direction.WEST);
      boolean westConnects = this.canConnect(westState, westState.isFaceSturdy(level, pos.west(), Direction.EAST), Direction.EAST);
      return this.FenceState(
         (BlockState)((BlockState)((BlockState)((BlockState)super.getStateForPlacement(context).setValue(NORTH, northConnects)).setValue(SOUTH, southConnects))
               .setValue(EAST, eastConnects))
            .setValue(WEST, westConnects),
         level,
         pos
      );
   }

   public void placeAt(Level level, BlockPos pos, int num) {
      level.setBlock(pos, this.defaultBlockState(), num);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE_CACHE.computeIfAbsent(state, this::computeShape);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
      return COLLISION_SHAPE_CACHE.computeIfAbsent(state, this::computeCollision);
   }

   private VoxelShape computeShape(BlockState state) {
      VoxelShape shape = POST;
      if ((Boolean)state.getValue(NORTH)) {
         shape = Shapes.or(shape, NORTH_SHAPE);
      }

      if ((Boolean)state.getValue(SOUTH)) {
         shape = Shapes.or(shape, SOUTH_SHAPE);
      }

      if ((Boolean)state.getValue(EAST)) {
         shape = Shapes.or(shape, EAST_SHAPE);
      }

      if ((Boolean)state.getValue(WEST)) {
         shape = Shapes.or(shape, WEST_SHAPE);
      }

      return shape;
   }

   private VoxelShape computeCollision(BlockState state) {
      VoxelShape shape = POST_COLLISION;
      if ((Boolean)state.getValue(NORTH)) {
         shape = Shapes.or(shape, NORTH_SHAPE_COLLISION);
      }

      if ((Boolean)state.getValue(SOUTH)) {
         shape = Shapes.or(shape, SOUTH_SHAPE_COLLISION);
      }

      if ((Boolean)state.getValue(EAST)) {
         shape = Shapes.or(shape, EAST_SHAPE_COLLISION);
      }

      if ((Boolean)state.getValue(WEST)) {
         shape = Shapes.or(shape, WEST_SHAPE_COLLISION);
      }

      return shape;
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (direction.getAxis().isHorizontal()) {
         BooleanProperty property = this.getProperty(direction);
         if (property != null) {
            boolean isNeighborSturdy = neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite());
            boolean connects = neighborState.isAir() ? false : this.canConnect(neighborState, isNeighborSturdy, direction);
            state = (BlockState)state.setValue(property, connects);
         }
      }

      boolean above = level.getBlockState(pos.above()).getBlock() == this;
      boolean below = level.getBlockState(pos.below()).getBlock() == this;
      MetalFence.FencePart connection = this.getConnectionStatus(above, below);
      return (BlockState)state.setValue(PART, connection);
   }

   public boolean canConnect(BlockState state, boolean bool, Direction dir) {
      Block block = state.getBlock();
      boolean flag = this.isSameFence(state);
      boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir);
      boolean flag2 = block instanceof WallBlock;
      boolean flag3 = block instanceof FenceHitbox;
      boolean flag4 = block instanceof DoubleGate;
      boolean flag5 = block instanceof FenceGateBlock;
      boolean flag6 = block instanceof FenceBlock;
      return !isExceptionForConnection(state) && bool || flag || flag1 || flag2 || flag3 || flag4 || flag5 || flag6;
   }

   private boolean isSameFence(BlockState state) {
      return state.is(BlockTags.FENCES) && state.is(BlockTags.WOODEN_FENCES) == this.defaultBlockState().is(BlockTags.WOODEN_FENCES);
   }

   private BooleanProperty getProperty(Direction direction) {
      return switch (direction) {
         case NORTH -> NORTH;
         case SOUTH -> SOUTH;
         case EAST -> EAST;
         case WEST -> WEST;
         default -> null;
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, SOUTH, EAST, WEST, PART});
   }

   public static enum FencePart implements StringRepresentable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      private FencePart(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
