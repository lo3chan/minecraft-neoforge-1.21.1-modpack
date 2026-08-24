package net.joefoxe.hexerei.block.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectingTable extends Block implements SimpleWaterloggedBlock {
   VoxelShape TOP = Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
   VoxelShape CORNER = Block.box(12.0, 0.0, 1.0, 15.0, 12.0, 4.0);
   VoxelShape CORNER_90 = Block.box(12.0, 0.0, 12.0, 15.0, 12.0, 15.0);
   VoxelShape CORNER_180 = Block.box(1.0, 0.0, 12.0, 4.0, 12.0, 15.0);
   VoxelShape CORNER_270 = Block.box(1.0, 0.0, 1.0, 4.0, 12.0, 4.0);
   VoxelShape INSIDE_CORNER = Stream.of(
         Block.box(12.0, 0.0, 1.0, 15.0, 12.0, 4.0), Block.box(11.0, 6.0, 1.0, 16.0, 9.0, 4.0), Block.box(12.0, 6.0, 0.0, 15.0, 9.0, 5.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape INSIDE_CORNER_90 = Stream.of(
         Block.box(12.0, 0.0, 12.0, 15.0, 12.0, 15.0), Block.box(12.0, 6.0, 11.0, 15.0, 9.0, 16.0), Block.box(11.0, 6.0, 12.0, 16.0, 9.0, 15.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape INSIDE_CORNER_180 = Stream.of(
         Block.box(1.0, 0.0, 12.0, 4.0, 12.0, 15.0), Block.box(0.0, 6.0, 12.0, 5.0, 9.0, 15.0), Block.box(1.0, 6.0, 11.0, 4.0, 9.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape INSIDE_CORNER_270 = Stream.of(
         Block.box(1.0, 0.0, 1.0, 4.0, 12.0, 4.0), Block.box(1.0, 6.0, 0.0, 4.0, 9.0, 5.0), Block.box(0.0, 6.0, 1.0, 5.0, 9.0, 4.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   VoxelShape END = Block.box(0.0, 6.0, 1.0, 16.0, 9.0, 4.0);
   VoxelShape END_90 = Block.box(12.0, 6.0, 0.0, 15.0, 9.0, 16.0);
   VoxelShape END_180 = Block.box(0.0, 6.0, 12.0, 16.0, 9.0, 15.0);
   VoxelShape END_270 = Block.box(1.0, 6.0, 0.0, 4.0, 9.0, 16.0);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static BooleanProperty WEST = BooleanProperty.create("west");
   public static BooleanProperty EAST = BooleanProperty.create("east");
   public static final EnumProperty<ConnectingTable.North> NORTH = EnumProperty.create("north", ConnectingTable.North.class);
   public static final EnumProperty<ConnectingTable.South> SOUTH = EnumProperty.create("south", ConnectingTable.South.class);

   public BlockState rotate(BlockState pState, Rotation pRot) {
      boolean east = (Boolean)pState.getValue(EAST);
      boolean west = (Boolean)pState.getValue(WEST);
      ConnectingTable.North northState = (ConnectingTable.North)pState.getValue(NORTH);
      ConnectingTable.South southState = (ConnectingTable.South)pState.getValue(SOUTH);
      boolean north = northState == ConnectingTable.North.ALL
         || northState == ConnectingTable.North.JUST_NORTH
         || northState == ConnectingTable.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingTable.North.NORTH_AND_NORTH_WEST;
      boolean north_east = northState == ConnectingTable.North.ALL
         || northState == ConnectingTable.North.JUST_NORTH_EAST
         || northState == ConnectingTable.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST;
      boolean north_west = northState == ConnectingTable.North.ALL
         || northState == ConnectingTable.North.JUST_NORTH_WEST
         || northState == ConnectingTable.North.NORTH_AND_NORTH_WEST
         || northState == ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST;
      boolean south = southState == ConnectingTable.South.ALL
         || southState == ConnectingTable.South.JUST_SOUTH
         || southState == ConnectingTable.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingTable.South.SOUTH_AND_SOUTH_WEST;
      boolean south_east = southState == ConnectingTable.South.ALL
         || southState == ConnectingTable.South.JUST_SOUTH_EAST
         || southState == ConnectingTable.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST;
      boolean south_west = southState == ConnectingTable.South.ALL
         || southState == ConnectingTable.South.JUST_SOUTH_WEST
         || southState == ConnectingTable.South.SOUTH_AND_SOUTH_WEST
         || southState == ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST;
      switch (pRot) {
         case NONE:
            return pState;
         case CLOCKWISE_90:
            ConnectingTable.North northTempxx = ConnectingTable.North.NONE;
            ConnectingTable.South southTempxx = ConnectingTable.South.NONE;
            if (south_east && east && north_east) {
               southTempxx = ConnectingTable.South.ALL;
            } else if (!south_east && east && north_east) {
               southTempxx = ConnectingTable.South.SOUTH_AND_SOUTH_EAST;
            } else if (south_east && east && !north_east) {
               southTempxx = ConnectingTable.South.SOUTH_AND_SOUTH_WEST;
            } else if (south_east && !east && north_east) {
               southTempxx = ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!south_east && east && !north_east) {
               southTempxx = ConnectingTable.South.JUST_SOUTH;
            } else if (!south_east && !east && north_east) {
               southTempxx = ConnectingTable.South.JUST_SOUTH_EAST;
            } else if (south_east && !east && !north_east) {
               southTempxx = ConnectingTable.South.JUST_SOUTH_WEST;
            }

            if (south_west && west && north_west) {
               northTempxx = ConnectingTable.North.ALL;
            } else if (!south_west && west && north_west) {
               northTempxx = ConnectingTable.North.NORTH_AND_NORTH_EAST;
            } else if (south_west && west && !north_west) {
               northTempxx = ConnectingTable.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && !west && north_west) {
               northTempxx = ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && west && !north_west) {
               northTempxx = ConnectingTable.North.JUST_NORTH;
            } else if (!south_west && !west && north_west) {
               northTempxx = ConnectingTable.North.JUST_NORTH_EAST;
            } else if (south_west && !west && !north_west) {
               northTempxx = ConnectingTable.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, north)).setValue(WEST, south)).setValue(NORTH, northTempxx))
               .setValue(SOUTH, southTempxx);
         case CLOCKWISE_180:
            ConnectingTable.North northTempx = ConnectingTable.North.NONE;
            ConnectingTable.South southTempx = ConnectingTable.South.NONE;
            if (north && north_east && north_west) {
               southTempx = ConnectingTable.South.ALL;
            } else if (north && north_west && !north_east) {
               southTempx = ConnectingTable.South.SOUTH_AND_SOUTH_EAST;
            } else if (north && north_east && !north_west) {
               southTempx = ConnectingTable.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && north_east && !north) {
               southTempx = ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && !north_east && north) {
               southTempx = ConnectingTable.South.JUST_SOUTH;
            } else if (north_west && !north_east && !north) {
               southTempx = ConnectingTable.South.JUST_SOUTH_EAST;
            } else if (!north_west && north_east && !north) {
               southTempx = ConnectingTable.South.JUST_SOUTH_WEST;
            }

            if (south && south_east && south_west) {
               northTempx = ConnectingTable.North.ALL;
            } else if (south && south_west && !south_east) {
               northTempx = ConnectingTable.North.NORTH_AND_NORTH_EAST;
            } else if (south && south_east && !south_west) {
               northTempx = ConnectingTable.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && south_east && !south) {
               northTempx = ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && !south_east && south) {
               northTempx = ConnectingTable.North.JUST_NORTH;
            } else if (south_west && !south_east && !south) {
               northTempx = ConnectingTable.North.JUST_NORTH_EAST;
            } else if (!south_west && south_east && !south) {
               northTempx = ConnectingTable.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, west)).setValue(WEST, east)).setValue(NORTH, northTempx))
               .setValue(SOUTH, southTempx);
         case COUNTERCLOCKWISE_90:
            ConnectingTable.North northTemp = ConnectingTable.North.NONE;
            ConnectingTable.South southTemp = ConnectingTable.South.NONE;
            if (north_west && west && south_west) {
               southTemp = ConnectingTable.South.ALL;
            } else if (!north_west && west && south_west) {
               southTemp = ConnectingTable.South.SOUTH_AND_SOUTH_EAST;
            } else if (north_west && west && !south_west) {
               southTemp = ConnectingTable.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && !west && south_west) {
               southTemp = ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && west && !south_west) {
               southTemp = ConnectingTable.South.JUST_SOUTH;
            } else if (!north_west && !west && south_west) {
               southTemp = ConnectingTable.South.JUST_SOUTH_EAST;
            } else if (north_west && !west && !south_west) {
               southTemp = ConnectingTable.South.JUST_SOUTH_WEST;
            }

            if (north_east && east && south_east) {
               northTemp = ConnectingTable.North.ALL;
            } else if (!north_east && east && south_east) {
               northTemp = ConnectingTable.North.NORTH_AND_NORTH_EAST;
            } else if (north_east && east && !south_east) {
               northTemp = ConnectingTable.North.NORTH_AND_NORTH_WEST;
            } else if (north_east && !east && south_east) {
               northTemp = ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!north_east && east && !south_east) {
               northTemp = ConnectingTable.North.JUST_NORTH;
            } else if (!north_east && !east && south_east) {
               northTemp = ConnectingTable.North.JUST_NORTH_EAST;
            } else if (north_east && !east && !south_east) {
               northTemp = ConnectingTable.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, south)).setValue(WEST, north)).setValue(NORTH, northTemp))
               .setValue(SOUTH, southTemp);
         default:
            return pState;
      }
   }

   public ConnectingTable(Properties pProperties) {
      super(pProperties.noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.defaultBlockState().setValue(WEST, false)).setValue(EAST, false))
                  .setValue(NORTH, ConnectingTable.North.NONE))
               .setValue(SOUTH, ConnectingTable.South.NONE))
            .setValue(WATERLOGGED, false)
      );
   }

   public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      boolean west = (Boolean)state.getValue(WEST);
      boolean east = (Boolean)state.getValue(EAST);
      ConnectingTable.North north = (ConnectingTable.North)state.getValue(NORTH);
      ConnectingTable.South south = (ConnectingTable.South)state.getValue(SOUTH);
      List<VoxelShape> list = new ArrayList<>();
      list.add(this.TOP);
      if (north.equals(ConnectingTable.North.NONE)
         || north.equals(ConnectingTable.North.JUST_NORTH_WEST)
         || north.equals(ConnectingTable.North.JUST_NORTH_EAST)
         || north.equals(ConnectingTable.North.NORTH_EAST_AND_NORTH_WEST)) {
         list.add(this.END);
      }

      if (!east) {
         list.add(this.END_90);
      }

      if (south.equals(ConnectingTable.South.NONE)
         || south.equals(ConnectingTable.South.JUST_SOUTH_WEST)
         || south.equals(ConnectingTable.South.JUST_SOUTH_EAST)
         || south.equals(ConnectingTable.South.SOUTH_EAST_AND_SOUTH_WEST)) {
         list.add(this.END_180);
      }

      if (!west) {
         list.add(this.END_270);
      }

      if (!east
         && north != ConnectingTable.North.JUST_NORTH
         && north != ConnectingTable.North.NORTH_AND_NORTH_EAST
         && north != ConnectingTable.North.NORTH_AND_NORTH_WEST
         && north != ConnectingTable.North.ALL) {
         list.add(this.CORNER);
      }

      if (!east
         && south != ConnectingTable.South.JUST_SOUTH
         && south != ConnectingTable.South.SOUTH_AND_SOUTH_EAST
         && south != ConnectingTable.South.SOUTH_AND_SOUTH_WEST
         && south != ConnectingTable.South.ALL) {
         list.add(this.CORNER_90);
      }

      if (!west
         && north != ConnectingTable.North.JUST_NORTH
         && north != ConnectingTable.North.NORTH_AND_NORTH_EAST
         && north != ConnectingTable.North.NORTH_AND_NORTH_WEST
         && north != ConnectingTable.North.ALL) {
         list.add(this.CORNER_270);
      }

      if (!west
         && south != ConnectingTable.South.JUST_SOUTH
         && south != ConnectingTable.South.SOUTH_AND_SOUTH_EAST
         && south != ConnectingTable.South.SOUTH_AND_SOUTH_WEST
         && south != ConnectingTable.South.ALL) {
         list.add(this.CORNER_180);
      }

      if (west && (north == ConnectingTable.North.JUST_NORTH || north == ConnectingTable.North.NORTH_AND_NORTH_EAST)) {
         list.add(this.INSIDE_CORNER_270);
      }

      if (west && (south == ConnectingTable.South.JUST_SOUTH || south == ConnectingTable.South.SOUTH_AND_SOUTH_EAST)) {
         list.add(this.INSIDE_CORNER_180);
      }

      if (east && (north == ConnectingTable.North.JUST_NORTH || north == ConnectingTable.North.NORTH_AND_NORTH_WEST)) {
         list.add(this.INSIDE_CORNER);
      }

      if (east && (south == ConnectingTable.South.JUST_SOUTH || south == ConnectingTable.South.SOUTH_AND_SOUTH_WEST)) {
         list.add(this.INSIDE_CORNER_90);
      }

      return list.stream().reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
   }

   protected BlockState updateCorners(BlockGetter world, BlockPos pos, BlockState state) {
      BlockState bs_north = world.getBlockState(pos.north());
      BlockState bs_north_east = world.getBlockState(pos.north().east());
      BlockState bs_north_west = world.getBlockState(pos.north().west());
      BlockState bs_east = world.getBlockState(pos.east());
      BlockState bs_south = world.getBlockState(pos.south());
      BlockState bs_south_east = world.getBlockState(pos.south().east());
      BlockState bs_south_west = world.getBlockState(pos.south().west());
      BlockState bs_west = world.getBlockState(pos.west());
      ConnectingTable.North north = ConnectingTable.North.NONE;
      ConnectingTable.South south = ConnectingTable.South.NONE;
      if (bs_north.getBlock() == this) {
         north = ConnectingTable.North.JUST_NORTH;
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingTable.North.NORTH_AND_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingTable.North.NORTH_AND_NORTH_EAST;
         }

         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() == this) {
            north = ConnectingTable.North.ALL;
         }
      } else {
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingTable.North.JUST_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingTable.North.JUST_NORTH_EAST;
         }
      }

      if (bs_south.getBlock() == this) {
         south = ConnectingTable.South.JUST_SOUTH;
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingTable.South.SOUTH_AND_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingTable.South.SOUTH_AND_SOUTH_EAST;
         }

         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() == this) {
            south = ConnectingTable.South.ALL;
         }
      } else {
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingTable.South.JUST_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingTable.South.JUST_SOUTH_EAST;
         }
      }

      boolean east = bs_east.getBlock() == this;
      boolean west = bs_west.getBlock() == this;
      return (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, north)).setValue(EAST, east)).setValue(SOUTH, south))
         .setValue(WEST, west);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockGetter iblockreader = context.getLevel();
      BlockPos blockpos = context.getClickedPos();
      return this.updateCorners(iblockreader, blockpos, super.getStateForPlacement(context));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WEST, EAST, NORTH, SOUTH, WATERLOGGED});
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if (state.hasProperty(WATERLOGGED) && (Boolean)state.getValue(WATERLOGGED)) {
         world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
      }

      return this.updateCorners(world, pos, state);
   }

   public static enum North implements StringRepresentable {
      JUST_NORTH,
      NORTH_AND_NORTH_WEST,
      NORTH_AND_NORTH_EAST,
      JUST_NORTH_WEST,
      JUST_NORTH_EAST,
      NORTH_EAST_AND_NORTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_NORTH -> "north";
            case NORTH_AND_NORTH_WEST -> "north_and_north_west";
            case NORTH_AND_NORTH_EAST -> "north_and_north_east";
            case JUST_NORTH_WEST -> "north_west";
            case JUST_NORTH_EAST -> "north_east";
            case NORTH_EAST_AND_NORTH_WEST -> "north_east_and_north_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }

   public static enum South implements StringRepresentable {
      JUST_SOUTH,
      SOUTH_AND_SOUTH_WEST,
      SOUTH_AND_SOUTH_EAST,
      JUST_SOUTH_WEST,
      JUST_SOUTH_EAST,
      SOUTH_EAST_AND_SOUTH_WEST,
      ALL,
      NONE;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return switch (this) {
            case JUST_SOUTH -> "south";
            case SOUTH_AND_SOUTH_WEST -> "south_and_south_west";
            case SOUTH_AND_SOUTH_EAST -> "south_and_south_east";
            case JUST_SOUTH_WEST -> "south_west";
            case JUST_SOUTH_EAST -> "south_east";
            case SOUTH_EAST_AND_SOUTH_WEST -> "south_east_and_south_west";
            case ALL -> "all";
            case NONE -> "none";
         };
      }
   }
}
