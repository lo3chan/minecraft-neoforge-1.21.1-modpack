package com.mcwfurnitures.kikoz.objects;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Desk extends FurnitureObjectNonFaceable {
   public static final DirectionProperty FACING_TWO_DIRECTIONAL = DirectionProperty.create("facing", new Direction[]{Direction.NORTH, Direction.EAST});
   public static final EnumProperty<Desk.ConnectionStatus> CONNECTION = EnumProperty.create(
      "connection",
      Desk.ConnectionStatus.class,
      new Desk.ConnectionStatus[]{Desk.ConnectionStatus.SINGLE, Desk.ConnectionStatus.MIDDLE, Desk.ConnectionStatus.LEFT, Desk.ConnectionStatus.RIGHT}
   );
   protected static final VoxelShape NORTH_BASE = Shapes.or(
      Block.box(0.1, 14.0, 0.1, 16.1, 16.0, 16.1), new VoxelShape[]{Block.box(0.0, 0.0, 13.0, 16.0, 14.0, 15.0), Block.box(0.0, 0.0, 1.0, 16.0, 14.0, 3.0)}
   );
   protected static final VoxelShape NORTH_MIDDLE = Shapes.or(Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape NORTH_RIGHT = Shapes.or(Block.box(0.0, 0.0, 1.0, 16.0, 14.0, 3.0), Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape NORTH_LEFT = Shapes.or(Block.box(0.0, 0.0, 13.0, 16.0, 14.0, 15.0), Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 16.0));
   protected static final VoxelShape EAST_BASE = Shapes.or(
      Block.box(0.1, 14.0, 0.1, 16.1, 16.0, 16.1), new VoxelShape[]{Block.box(1.1, 0.0, 0.0, 3.1, 14.0, 16.1), Block.box(13.1, 0.0, 0.0, 15.1, 14.0, 16.1)}
   );
   protected static final VoxelShape EAST_MIDDLE = Shapes.or(Block.box(0.1, 14.0, 0.1, 16.1, 16.0, 16.1), new VoxelShape[0]);
   protected static final VoxelShape EAST_RIGHT = Shapes.or(Block.box(0.1, 14.0, 0.0, 16.1, 16.0, 16.1), Block.box(13.1, 0.0, 0.0, 15.0, 14.0, 16.0));
   protected static final VoxelShape EAST_LEFT = Shapes.or(Block.box(0.1, 14.0, 0.1, 16.1, 16.0, 16.1), Block.box(1.1, 0.0, 0.0, 3.1, 14.0, 16.1));

   public Desk(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING_TWO_DIRECTIONAL, Direction.NORTH))
            .setValue(CONNECTION, Desk.ConnectionStatus.SINGLE)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      Desk.ConnectionStatus connectionStatus = (Desk.ConnectionStatus)state.getValue(CONNECTION);
      switch ((Direction)state.getValue(FACING_TWO_DIRECTIONAL)) {
         case NORTH:
            if (connectionStatus == Desk.ConnectionStatus.MIDDLE) {
               return NORTH_MIDDLE;
            } else if (connectionStatus == Desk.ConnectionStatus.LEFT) {
               return NORTH_LEFT;
            } else {
               if (connectionStatus == Desk.ConnectionStatus.RIGHT) {
                  return NORTH_RIGHT;
               }

               return NORTH_BASE;
            }
         case EAST:
         default:
            if (connectionStatus == Desk.ConnectionStatus.MIDDLE) {
               return EAST_MIDDLE;
            } else if (connectionStatus == Desk.ConnectionStatus.LEFT) {
               return EAST_LEFT;
            } else {
               return connectionStatus == Desk.ConnectionStatus.RIGHT ? EAST_RIGHT : EAST_BASE;
            }
      }
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      Direction originalDirection = (Direction)state.getValue(FACING_TWO_DIRECTIONAL);
      Direction rotatedDirection = rot.rotate(originalDirection);
      if (originalDirection != Direction.NORTH && originalDirection != Direction.SOUTH) {
         rotatedDirection = Direction.EAST;
      } else {
         rotatedDirection = Direction.NORTH;
      }

      return (BlockState)state.setValue(FACING_TWO_DIRECTIONAL, rotatedDirection);
   }

   public BlockState mirror(BlockState state, Mirror mir) {
      Direction originalDirection = (Direction)state.getValue(FACING_TWO_DIRECTIONAL);
      if (mir != Mirror.FRONT_BACK) {
         originalDirection.getOpposite();
      }

      Direction rotatedDirection;
      if (originalDirection != Direction.NORTH && originalDirection != Direction.SOUTH) {
         rotatedDirection = Direction.EAST;
      } else {
         rotatedDirection = Direction.NORTH;
      }

      return (BlockState)state.setValue(FACING_TWO_DIRECTIONAL, rotatedDirection);
   }

   private BlockState TableState(BlockState state, LevelAccessor level, BlockPos pos) {
      boolean north = level.getBlockState(pos.north()).getBlock() == this;
      boolean east = level.getBlockState(pos.east()).getBlock() == this;
      boolean south = level.getBlockState(pos.south()).getBlock() == this;
      boolean west = level.getBlockState(pos.west()).getBlock() == this;
      Desk.ConnectionStatus connection = this.getConnectionStatus((Direction)state.getValue(FACING_TWO_DIRECTIONAL), north, east, south, west);
      return (BlockState)state.setValue(CONNECTION, connection);
   }

   private Desk.ConnectionStatus getConnectionStatus(Direction facing, boolean north, boolean east, boolean south, boolean west) {
      if (facing == Direction.NORTH) {
         if (north && south && !east && !west) {
            return Desk.ConnectionStatus.MIDDLE;
         }

         if (!north && south && !east && !west) {
            return Desk.ConnectionStatus.RIGHT;
         }

         if (north && !south && !east && !west) {
            return Desk.ConnectionStatus.LEFT;
         }
      }

      if (facing == Direction.EAST) {
         if (!north && !south && east && west) {
            return Desk.ConnectionStatus.MIDDLE;
         }

         if (!north && !south && east && !west) {
            return Desk.ConnectionStatus.LEFT;
         }

         if (!north && !south && !east && west) {
            return Desk.ConnectionStatus.RIGHT;
         }
      }

      return Desk.ConnectionStatus.SINGLE;
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState statetwo, boolean bool) {
      Direction currentFacing = (Direction)state.getValue(FACING_TWO_DIRECTIONAL);
      BlockState defaultState = (BlockState)this.defaultBlockState().setValue(FACING_TWO_DIRECTIONAL, currentFacing);
      level.setBlock(pos, this.TableState(defaultState, level, pos), 2);
   }

   @Override
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING_TWO_DIRECTIONAL, CONNECTION});
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity livent, ItemStack stack) {
      this.TableState(state, level, pos);
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
      return (BlockState)this.TableState(state, level, pos).setValue(FACING_TWO_DIRECTIONAL, (Direction)state.getValue(FACING_TWO_DIRECTIONAL));
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction playerFacing = context.getHorizontalDirection();

      return (BlockState)this.TableState(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos())
         .setValue(FACING_TWO_DIRECTIONAL, switch (playerFacing) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.NORTH;
            default -> Direction.NORTH;
         });
   }

   public void placeAt(Level level, BlockPos pos, int number) {
      level.setBlock(pos, this.defaultBlockState(), number);
   }

   public static enum ConnectionStatus implements StringRepresentable {
      SINGLE("single"),
      MIDDLE("middle"),
      LEFT("left"),
      RIGHT("right");

      private final String name;

      private ConnectionStatus(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
