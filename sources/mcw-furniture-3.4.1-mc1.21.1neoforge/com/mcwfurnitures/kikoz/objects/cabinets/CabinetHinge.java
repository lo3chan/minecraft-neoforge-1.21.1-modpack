package com.mcwfurnitures.kikoz.objects.cabinets;

import com.mcwfurnitures.kikoz.objects.counters.CupboardCounter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CabinetHinge extends CupboardCounter implements EntityBlock {
   protected static final VoxelShape EAS = Shapes.or(Block.box(5.0, 0.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape SOU = Shapes.or(Block.box(0.0, 0.0, 5.0, 16.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape WES = Shapes.or(Block.box(0.0, 0.0, 0.0, 11.0, 16.0, 16.0), new VoxelShape[0]);
   protected static final VoxelShape NOR = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 11.0), new VoxelShape[0]);

   public CabinetHinge(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HINGE, DoorHingeSide.LEFT)
      );
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter blockReader, BlockPos pos, CollisionContext selectionContext) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return NOR;
         case SOUTH:
            return SOU;
         case WEST:
            return WES;
         case EAST:
         default:
            return EAS;
      }
   }
}
