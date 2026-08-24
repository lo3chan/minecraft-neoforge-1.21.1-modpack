package com.aetherteam.aether.block.construction;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AerogelWallBlock extends WallBlock {
   private static final Map<Direction, EnumProperty<WallSide>> WALL_SIDES_BY_DIRECTION = new EnumMap<>(Direction.class);

   public AerogelWallBlock(Properties properties) {
      super(properties);
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
      if (adjacentBlockState.is(this)) {
         if (side.getAxis().isHorizontal()) {
            WallSide ourHeight = (WallSide)state.getValue((Property)WALL_SIDES_BY_DIRECTION.get(side));
            WallSide theirHeight = (WallSide)adjacentBlockState.getValue((Property)WALL_SIDES_BY_DIRECTION.get(side.getOpposite()));
            return ourHeight.ordinal() <= theirHeight.ordinal();
         } else {
            return !(Boolean)state.getValue(UP) || (Boolean)adjacentBlockState.getValue(UP);
         }
      } else {
         return super.skipRendering(state, adjacentBlockState, side);
      }
   }

   static {
      WALL_SIDES_BY_DIRECTION.put(Direction.NORTH, NORTH_WALL);
      WALL_SIDES_BY_DIRECTION.put(Direction.SOUTH, SOUTH_WALL);
      WALL_SIDES_BY_DIRECTION.put(Direction.EAST, EAST_WALL);
      WALL_SIDES_BY_DIRECTION.put(Direction.WEST, WEST_WALL);
   }
}
