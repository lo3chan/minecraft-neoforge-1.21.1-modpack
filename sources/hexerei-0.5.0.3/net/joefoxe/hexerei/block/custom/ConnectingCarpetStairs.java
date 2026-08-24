package net.joefoxe.hexerei.block.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.CTDyable;
import net.joefoxe.hexerei.block.connected.Waxed;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbility;

public class ConnectingCarpetStairs extends CarpetBlock implements Waxed, CTDyable {
   public static final BooleanProperty RIGHT = BooleanProperty.create("right");
   public static final BooleanProperty LEFT = BooleanProperty.create("left");
   public static BooleanProperty WEST = BooleanProperty.create("west");
   public static BooleanProperty EAST = BooleanProperty.create("east");
   public static final EnumProperty<ConnectingCarpetStairs.North> NORTH = EnumProperty.create("north", ConnectingCarpetStairs.North.class);
   public static final EnumProperty<ConnectingCarpetStairs.South> SOUTH = EnumProperty.create("south", ConnectingCarpetStairs.South.class);
   public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
   public Block parentBlock;
   protected static final VoxelShape VOXEL_SHAPE = Stream.of(
         Block.box(8.0, 0.0, 2.0, 16.0, 1.0, 14.0),
         Block.box(7.0, -7.0, 2.0, 8.0, 0.0, 14.0),
         Block.box(-1.0, -16.0, 2.0, 0.0, -8.0, 14.0),
         Block.box(0.0, -8.0, 2.0, 8.0, -7.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE90 = Stream.of(
         Block.box(0.0, 0.0, 2.0, 8.0, 1.0, 14.0),
         Block.box(8.0, -7.0, 2.0, 9.0, 0.0, 14.0),
         Block.box(16.0, -16.0, 2.0, 17.0, -8.0, 14.0),
         Block.box(8.0, -8.0, 2.0, 16.0, -7.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE180 = Stream.of(
         Block.box(2.0, 0.0, 8.0, 14.0, 1.0, 16.0),
         Block.box(2.0, -7.0, 7.0, 14.0, 0.0, 8.0),
         Block.box(2.0, -16.0, -1.0, 14.0, -8.0, 0.0),
         Block.box(2.0, -8.0, 0.0, 14.0, -7.0, 8.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE270 = Stream.of(
         Block.box(2.0, 0.0, 0.0, 14.0, 1.0, 8.0),
         Block.box(2.0, -7.0, 8.0, 14.0, 0.0, 9.0),
         Block.box(2.0, -16.0, 16.0, 14.0, -8.0, 17.0),
         Block.box(2.0, -8.0, 8.0, 14.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_LEFT = Stream.of(
         Block.box(8.0, 0.0, 2.0, 16.0, 1.0, 16.0),
         Block.box(7.0, -7.0, 2.0, 8.0, 0.0, 16.0),
         Block.box(-1.0, -16.0, 2.0, 0.0, -8.0, 16.0),
         Block.box(0.0, -8.0, 2.0, 8.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE90_LEFT = Stream.of(
         Block.box(0.0, 0.0, 2.0, 8.0, 1.0, 16.0),
         Block.box(8.0, -7.0, 2.0, 9.0, 0.0, 16.0),
         Block.box(16.0, -16.0, 2.0, 17.0, -8.0, 16.0),
         Block.box(8.0, -8.0, 2.0, 16.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE180_LEFT = Stream.of(
         Block.box(2.0, 0.0, 8.0, 16.0, 1.0, 16.0),
         Block.box(2.0, -7.0, 7.0, 16.0, 0.0, 8.0),
         Block.box(2.0, -16.0, -1.0, 16.0, -8.0, 0.0),
         Block.box(2.0, -8.0, 0.0, 16.0, -7.0, 8.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE270_LEFT = Stream.of(
         Block.box(2.0, 0.0, 0.0, 16.0, 1.0, 8.0),
         Block.box(2.0, -7.0, 8.0, 16.0, 0.0, 9.0),
         Block.box(2.0, -16.0, 16.0, 16.0, -8.0, 17.0),
         Block.box(2.0, -8.0, 8.0, 16.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_RIGHT = Stream.of(
         Block.box(8.0, 0.0, 0.0, 16.0, 1.0, 14.0),
         Block.box(7.0, -7.0, 0.0, 8.0, 0.0, 14.0),
         Block.box(-1.0, -16.0, 0.0, 0.0, -8.0, 14.0),
         Block.box(0.0, -8.0, 0.0, 8.0, -7.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE90_RIGHT = Stream.of(
         Block.box(0.0, 0.0, 0.0, 8.0, 1.0, 14.0),
         Block.box(8.0, -7.0, 0.0, 9.0, 0.0, 14.0),
         Block.box(16.0, -16.0, 0.0, 17.0, -8.0, 14.0),
         Block.box(8.0, -8.0, 0.0, 16.0, -7.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE180_RIGHT = Stream.of(
         Block.box(0.0, 0.0, 8.0, 14.0, 1.0, 16.0),
         Block.box(0.0, -7.0, 7.0, 14.0, 0.0, 8.0),
         Block.box(0.0, -16.0, -1.0, 14.0, -8.0, 0.0),
         Block.box(0.0, -8.0, 0.0, 14.0, -7.0, 8.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE270_RIGHT = Stream.of(
         Block.box(0.0, 0.0, 0.0, 14.0, 1.0, 8.0),
         Block.box(0.0, -7.0, 8.0, 14.0, 0.0, 9.0),
         Block.box(0.0, -16.0, 16.0, 14.0, -8.0, 17.0),
         Block.box(0.0, -8.0, 8.0, 14.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_FULL = Stream.of(
         Block.box(8.0, 0.0, 0.0, 16.0, 1.0, 16.0),
         Block.box(7.0, -7.0, 0.0, 8.0, 0.0, 16.0),
         Block.box(-1.0, -16.0, 0.0, 0.0, -8.0, 16.0),
         Block.box(0.0, -8.0, 0.0, 8.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE90_FULL = Stream.of(
         Block.box(0.0, 0.0, 0.0, 8.0, 1.0, 16.0),
         Block.box(8.0, -7.0, 0.0, 9.0, 0.0, 16.0),
         Block.box(16.0, -16.0, 0.0, 17.0, -8.0, 16.0),
         Block.box(8.0, -8.0, 0.0, 16.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE180_FULL = Stream.of(
         Block.box(0.0, 0.0, 8.0, 16.0, 1.0, 16.0),
         Block.box(0.0, -7.0, 7.0, 16.0, 0.0, 8.0),
         Block.box(0.0, -16.0, -1.0, 16.0, -8.0, 0.0),
         Block.box(0.0, -8.0, 0.0, 16.0, -7.0, 8.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE270_FULL = Stream.of(
         Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 8.0),
         Block.box(0.0, -7.0, 8.0, 16.0, 0.0, 9.0),
         Block.box(0.0, -16.0, 16.0, 16.0, -8.0, 17.0),
         Block.box(0.0, -8.0, 8.0, 16.0, -7.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_THICK_N = Stream.of(
         Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 11.0),
         Block.box(0.0, -8.0, 8.0, 16.0, 0.0, 11.0),
         Block.box(0.0, -8.0, 11.0, 16.0, -5.0, 19.0),
         Block.box(0.0, -16.0, 16.0, 16.0, -8.0, 19.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_THICK_E = Stream.of(
         Block.box(5.0, 0.0, 0.0, 16.0, 3.0, 16.0),
         Block.box(5.0, -8.0, 0.0, 8.0, 0.0, 16.0),
         Block.box(-3.0, -8.0, 0.0, 5.0, -5.0, 16.0),
         Block.box(-3.0, -16.0, 0.0, 0.0, -8.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_THICK_S = Stream.of(
         Block.box(0.0, 0.0, 5.0, 16.0, 3.0, 16.0),
         Block.box(0.0, -8.0, 5.0, 16.0, 0.0, 8.0),
         Block.box(0.0, -8.0, -3.0, 16.0, -5.0, 5.0),
         Block.box(0.0, -16.0, -3.0, 16.0, -8.0, 0.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape VOXEL_SHAPE_THICK_W = Stream.of(
         Block.box(0.0, 0.0, 0.0, 11.0, 3.0, 16.0),
         Block.box(8.0, -8.0, 0.0, 11.0, 0.0, 16.0),
         Block.box(11.0, -8.0, 0.0, 19.0, -5.0, 16.0),
         Block.box(16.0, -16.0, 0.0, 19.0, -8.0, 16.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   @Override
   public DyeColor getDyeColor(BlockState blockState) {
      return blockState.hasProperty(COLOR) ? (DyeColor)blockState.getValue(COLOR) : DyeColor.WHITE;
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      boolean east = (Boolean)pState.getValue(EAST);
      boolean west = (Boolean)pState.getValue(WEST);
      ConnectingCarpetStairs.North northState = (ConnectingCarpetStairs.North)pState.getValue(NORTH);
      ConnectingCarpetStairs.South southState = (ConnectingCarpetStairs.South)pState.getValue(SOUTH);
      boolean north = northState == ConnectingCarpetStairs.North.ALL
         || northState == ConnectingCarpetStairs.North.JUST_NORTH
         || northState == ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST;
      boolean north_east = northState == ConnectingCarpetStairs.North.ALL
         || northState == ConnectingCarpetStairs.North.JUST_NORTH_EAST
         || northState == ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST
         || northState == ConnectingCarpetStairs.North.NORTH_EAST_AND_NORTH_WEST;
      boolean north_west = northState == ConnectingCarpetStairs.North.ALL
         || northState == ConnectingCarpetStairs.North.JUST_NORTH_WEST
         || northState == ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST
         || northState == ConnectingCarpetStairs.North.NORTH_EAST_AND_NORTH_WEST;
      boolean south = southState == ConnectingCarpetStairs.South.ALL
         || southState == ConnectingCarpetStairs.South.JUST_SOUTH
         || southState == ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST;
      boolean south_east = southState == ConnectingCarpetStairs.South.ALL
         || southState == ConnectingCarpetStairs.South.JUST_SOUTH_EAST
         || southState == ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST
         || southState == ConnectingCarpetStairs.South.SOUTH_EAST_AND_SOUTH_WEST;
      boolean south_west = southState == ConnectingCarpetStairs.South.ALL
         || southState == ConnectingCarpetStairs.South.JUST_SOUTH_WEST
         || southState == ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST
         || southState == ConnectingCarpetStairs.South.SOUTH_EAST_AND_SOUTH_WEST;
      switch (pRot) {
         case NONE:
            return pState;
         case CLOCKWISE_90:
            ConnectingCarpetStairs.North northTempxx = ConnectingCarpetStairs.North.NONE;
            ConnectingCarpetStairs.South southTempxx = ConnectingCarpetStairs.South.NONE;
            if (south_east && east && north_east) {
               southTempxx = ConnectingCarpetStairs.South.ALL;
            } else if (!south_east && east && north_east) {
               southTempxx = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST;
            } else if (south_east && east) {
               southTempxx = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST;
            } else if (south_east && north_east) {
               southTempxx = ConnectingCarpetStairs.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!south_east && east) {
               southTempxx = ConnectingCarpetStairs.South.JUST_SOUTH;
            } else if (!south_east && north_east) {
               southTempxx = ConnectingCarpetStairs.South.JUST_SOUTH_EAST;
            } else if (south_east) {
               southTempxx = ConnectingCarpetStairs.South.JUST_SOUTH_WEST;
            }

            if (south_west && west && north_west) {
               northTempxx = ConnectingCarpetStairs.North.ALL;
            } else if (!south_west && west && north_west) {
               northTempxx = ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST;
            } else if (south_west && west) {
               northTempxx = ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && north_west) {
               northTempxx = ConnectingCarpetStairs.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && west) {
               northTempxx = ConnectingCarpetStairs.North.JUST_NORTH;
            } else if (!south_west && north_west) {
               northTempxx = ConnectingCarpetStairs.North.JUST_NORTH_EAST;
            } else if (south_west) {
               northTempxx = ConnectingCarpetStairs.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, north)).setValue(WEST, south)).setValue(NORTH, northTempxx))
               .setValue(SOUTH, southTempxx);
         case CLOCKWISE_180:
            ConnectingCarpetStairs.North northTempx = ConnectingCarpetStairs.North.NONE;
            ConnectingCarpetStairs.South southTempx = ConnectingCarpetStairs.South.NONE;
            if (north && north_east && north_west) {
               southTempx = ConnectingCarpetStairs.South.ALL;
            } else if (north && north_west) {
               southTempx = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST;
            } else if (north && north_east) {
               southTempx = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && north_east) {
               southTempx = ConnectingCarpetStairs.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && !north_east && north) {
               southTempx = ConnectingCarpetStairs.South.JUST_SOUTH;
            } else if (north_west) {
               southTempx = ConnectingCarpetStairs.South.JUST_SOUTH_EAST;
            } else if (north_east) {
               southTempx = ConnectingCarpetStairs.South.JUST_SOUTH_WEST;
            }

            if (south && south_east && south_west) {
               northTempx = ConnectingCarpetStairs.North.ALL;
            } else if (south && south_west) {
               northTempx = ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST;
            } else if (south && south_east) {
               northTempx = ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST;
            } else if (south_west && south_east) {
               northTempx = ConnectingCarpetStairs.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!south_west && !south_east && south) {
               northTempx = ConnectingCarpetStairs.North.JUST_NORTH;
            } else if (south_west) {
               northTempx = ConnectingCarpetStairs.North.JUST_NORTH_EAST;
            } else if (south_east) {
               northTempx = ConnectingCarpetStairs.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, west)).setValue(WEST, east)).setValue(NORTH, northTempx))
               .setValue(SOUTH, southTempx);
         case COUNTERCLOCKWISE_90:
            ConnectingCarpetStairs.North northTemp = ConnectingCarpetStairs.North.NONE;
            ConnectingCarpetStairs.South southTemp = ConnectingCarpetStairs.South.NONE;
            if (north_west && west && south_west) {
               southTemp = ConnectingCarpetStairs.South.ALL;
            } else if (!north_west && west && south_west) {
               southTemp = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST;
            } else if (north_west && west) {
               southTemp = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST;
            } else if (north_west && south_west) {
               southTemp = ConnectingCarpetStairs.South.SOUTH_EAST_AND_SOUTH_WEST;
            } else if (!north_west && west) {
               southTemp = ConnectingCarpetStairs.South.JUST_SOUTH;
            } else if (!north_west && south_west) {
               southTemp = ConnectingCarpetStairs.South.JUST_SOUTH_EAST;
            } else if (north_west) {
               southTemp = ConnectingCarpetStairs.South.JUST_SOUTH_WEST;
            }

            if (north_east && east && south_east) {
               northTemp = ConnectingCarpetStairs.North.ALL;
            } else if (!north_east && east && south_east) {
               northTemp = ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST;
            } else if (north_east && east) {
               northTemp = ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST;
            } else if (north_east && south_east) {
               northTemp = ConnectingCarpetStairs.North.NORTH_EAST_AND_NORTH_WEST;
            } else if (!north_east && east) {
               northTemp = ConnectingCarpetStairs.North.JUST_NORTH;
            } else if (!north_east && south_east) {
               northTemp = ConnectingCarpetStairs.North.JUST_NORTH_EAST;
            } else if (north_east) {
               northTemp = ConnectingCarpetStairs.North.JUST_NORTH_WEST;
            }

            return (BlockState)((BlockState)((BlockState)((BlockState)pState.setValue(EAST, south)).setValue(WEST, north)).setValue(NORTH, northTemp))
               .setValue(SOUTH, southTemp);
         default:
            return pState;
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
      if (state.hasProperty(StairBlock.FACING)) {
         if (state.getValue(StairBlock.FACING) == Direction.NORTH) {
            return VOXEL_SHAPE_THICK_N;
         }

         if (state.getValue(StairBlock.FACING) == Direction.EAST) {
            return VOXEL_SHAPE_THICK_E;
         }

         if (state.getValue(StairBlock.FACING) == Direction.SOUTH) {
            return VOXEL_SHAPE_THICK_S;
         }

         if (state.getValue(StairBlock.FACING) == Direction.WEST) {
            return VOXEL_SHAPE_THICK_W;
         }
      }

      return VOXEL_SHAPE_THICK_N;
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (state.hasProperty(StairBlock.FACING)) {
         if (state.getValue(StairBlock.FACING) == Direction.NORTH) {
            boolean left = (Boolean)state.getValue(LEFT);
            boolean right = (Boolean)state.getValue(RIGHT);
            if (left && !right) {
               return VOXEL_SHAPE270_RIGHT;
            }

            if (!left && right) {
               return VOXEL_SHAPE270_LEFT;
            }

            if (!left) {
               return VOXEL_SHAPE270;
            }

            return VOXEL_SHAPE270_FULL;
         }

         if (state.getValue(StairBlock.FACING) == Direction.EAST) {
            boolean leftx = (Boolean)state.getValue(LEFT);
            boolean rightx = (Boolean)state.getValue(RIGHT);
            if (leftx && !rightx) {
               return VOXEL_SHAPE_RIGHT;
            }

            if (!leftx && rightx) {
               return VOXEL_SHAPE_LEFT;
            }

            if (!leftx) {
               return VOXEL_SHAPE;
            }

            return VOXEL_SHAPE_FULL;
         }

         if (state.getValue(StairBlock.FACING) == Direction.SOUTH) {
            boolean leftxx = (Boolean)state.getValue(LEFT);
            boolean rightxx = (Boolean)state.getValue(RIGHT);
            if (leftxx && !rightxx) {
               return VOXEL_SHAPE180_LEFT;
            }

            if (!leftxx && rightxx) {
               return VOXEL_SHAPE180_RIGHT;
            }

            if (!leftxx) {
               return VOXEL_SHAPE180;
            }

            return VOXEL_SHAPE180_FULL;
         }

         if (state.getValue(StairBlock.FACING) == Direction.WEST) {
            boolean leftxxx = (Boolean)state.getValue(LEFT);
            boolean rightxxx = (Boolean)state.getValue(RIGHT);
            if (leftxxx && !rightxxx) {
               return VOXEL_SHAPE90_LEFT;
            }

            if (!leftxxx && rightxxx) {
               return VOXEL_SHAPE90_RIGHT;
            }

            if (!leftxxx) {
               return VOXEL_SHAPE90;
            }

            return VOXEL_SHAPE90_FULL;
         }
      }

      return VOXEL_SHAPE;
   }

   public static boolean checkLeft(BlockState stateIn, BlockPos currentPos, LevelAccessor worldIn) {
      if (stateIn.hasProperty(StairBlock.FACING) && stateIn.hasProperty(COLOR)) {
         if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH) {
            return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.west()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.EAST) {
            return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.north()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH) {
            return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.east()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.WEST) {
            return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.south()).getValue(COLOR) == stateIn.getValue(COLOR);
         }
      }

      return false;
   }

   public List<ItemStack> getDrops(BlockState pState, net.minecraft.world.level.storage.loot.LootParams.Builder pParams) {
      List<ItemStack> drops = super.getDrops(pState, pParams);
      if (!pState.hasProperty(COLOR)) {
         return drops;
      } else {
         List<ItemStack> updated_drops = new ArrayList<>();

         for (ItemStack stack : drops) {
            if (stack.getItem() == ((ConnectingCarpetDyed)ModBlocks.INFUSED_FABRIC_CARPET.get()).asItem()
               || stack.getItem() == ((ConnectingCarpetDyed)ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get()).asItem()) {
               DyeColor color = (DyeColor)pState.getValue(COLOR);
               CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               tag.putString("color", color.getName());
               stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }

            updated_drops.add(stack);
         }

         return updated_drops;
      }
   }

   public boolean checkRight(BlockState stateIn, BlockPos currentPos, LevelAccessor worldIn) {
      if (stateIn.hasProperty(StairBlock.FACING)) {
         if (stateIn.getValue(StairBlock.FACING) == Direction.NORTH) {
            return worldIn.getBlockState(currentPos.east()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.east()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.east()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.EAST) {
            return worldIn.getBlockState(currentPos.south()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.south()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.south()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.SOUTH) {
            return worldIn.getBlockState(currentPos.west()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.west()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.west()).getValue(COLOR) == stateIn.getValue(COLOR);
         }

         if (stateIn.getValue(StairBlock.FACING) == Direction.WEST) {
            return worldIn.getBlockState(currentPos.north()).getBlock() == stateIn.getBlock()
               && worldIn.getBlockState(currentPos.north()).getValue(StairBlock.FACING) == stateIn.getValue(StairBlock.FACING)
               && worldIn.getBlockState(currentPos.north()).getValue(COLOR) == stateIn.getValue(COLOR);
         }
      }

      return false;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.getItem() instanceof DyeItem dyeItem) {
         DyeColor dyecolor = dyeItem.getDyeColor();
         if (this.getDyeColor(state) == dyecolor) {
            return ItemInteractionResult.FAIL;
         } else {
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }

            BlockState newBlockstate = (BlockState)((BlockState)((BlockState)((BlockState)level.getBlockState(pos)
                        .setValue(StairBlock.FACING, (Direction)level.getBlockState(pos.below()).getValue(StairBlock.FACING)))
                     .setValue(RIGHT, this.checkRight(state, pos, level)))
                  .setValue(LEFT, checkLeft(state, pos, level)))
               .setValue(COLOR, dyecolor);
            if (state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()) {
               Block.popResource(level, pos, new ItemStack(Items.GOLD_NUGGET));
               newBlockstate = (BlockState)((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get()).defaultBlockState().setValue(COLOR, dyecolor);
            }

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, newBlockstate));
            level.levelEvent(player, 3003, pos, 0);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      } else if (stack.getItem() == Items.GOLD_NUGGET) {
         if (state.getBlock() == ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()) {
            return ItemInteractionResult.FAIL;
         } else {
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
            }

            BlockState newBlockstate = ((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()).defaultBlockState();
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            level.setBlockAndUpdate(pos, newBlockstate);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, newBlockstate));
            level.levelEvent(player, 3004, pos, 0);
            level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   @Nullable
   public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getUnWaxed(state, context, itemAbility)
                  .setValue(StairBlock.FACING, (Direction)state.getValue(StairBlock.FACING)))
               .setValue(RIGHT, (Boolean)state.getValue(RIGHT)))
            .setValue(LEFT, (Boolean)state.getValue(LEFT)))
         .setValue(COLOR, (DyeColor)state.getValue(COLOR));
   }

   public ConnectingCarpetStairs(Properties pProperties) {
      super(pProperties.noOcclusion());
      this.parentBlock = this;
   }

   public ConnectingCarpetStairs(Properties pProperties, Block block) {
      super(pProperties.noOcclusion());
      this.parentBlock = block;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack stack = this.parentBlock.asItem().getDefaultInstance();
      DyeColor color = this.getDyeColor(state);
      if (color != DyeColor.WHITE) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         tag.putString("color", color.getName());
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      }

      return stack;
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
      ConnectingCarpetStairs.North north = ConnectingCarpetStairs.North.NONE;
      ConnectingCarpetStairs.South south = ConnectingCarpetStairs.South.NONE;
      if (bs_north.getBlock() == this) {
         north = ConnectingCarpetStairs.North.JUST_NORTH;
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingCarpetStairs.North.NORTH_AND_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpetStairs.North.NORTH_AND_NORTH_EAST;
         }

         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpetStairs.North.ALL;
         }
      } else {
         if (bs_north_west.getBlock() == this && bs_north_east.getBlock() != this) {
            north = ConnectingCarpetStairs.North.JUST_NORTH_WEST;
         }

         if (bs_north_west.getBlock() != this && bs_north_east.getBlock() == this) {
            north = ConnectingCarpetStairs.North.JUST_NORTH_EAST;
         }
      }

      if (bs_south.getBlock() == this) {
         south = ConnectingCarpetStairs.South.JUST_SOUTH;
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpetStairs.South.SOUTH_AND_SOUTH_EAST;
         }

         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpetStairs.South.ALL;
         }
      } else {
         if (bs_south_west.getBlock() == this && bs_south_east.getBlock() != this) {
            south = ConnectingCarpetStairs.South.JUST_SOUTH_WEST;
         }

         if (bs_south_west.getBlock() != this && bs_south_east.getBlock() == this) {
            south = ConnectingCarpetStairs.South.JUST_SOUTH_EAST;
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
      ItemStack stack = context.getItemInHand();
      BlockPos pos = context.getClickedPos();
      BlockState state = context.getLevel().getBlockState(context.getClickedPos());
      Level level = context.getLevel();
      if (level.getBlockState(pos.below()).getBlock() instanceof StairBlock && level.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM) {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         String colorName = tag.contains("color") ? tag.getString("color") : "";
         DyeColor color = DyeColor.byName(colorName, DyeColor.WHITE);
         return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState()
                     .setValue(StairBlock.FACING, (Direction)level.getBlockState(pos.below()).getValue(StairBlock.FACING)))
                  .setValue(RIGHT, this.checkRight(state, pos, level)))
               .setValue(LEFT, checkLeft(state, pos, level)))
            .setValue(COLOR, color);
      } else {
         return this.defaultBlockState();
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{StairBlock.FACING, RIGHT, LEFT, COLOR});
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      if (world.getBlockState(pos.below()).getBlock() instanceof StairBlock && world.getBlockState(pos.below()).getValue(StairBlock.HALF) == Half.BOTTOM) {
         return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState()
                     .setValue(StairBlock.FACING, (Direction)world.getBlockState(pos.below()).getValue(StairBlock.FACING)))
                  .setValue(RIGHT, this.checkRight(state, pos, world)))
               .setValue(LEFT, checkLeft(state, pos, world)))
            .setValue(COLOR, (DyeColor)state.getValue(COLOR));
      } else {
         return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : this.defaultBlockState();
      }
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
