package net.bobophones.bobolib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HorizontalDirectionBlock extends Block {
   public static final DirectionProperty facing = HorizontalDirectionalBlock.FACING;

   public HorizontalDirectionBlock(Properties props) {
      super(props);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(facing, Direction.NORTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{facing});
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)super.getStateForPlacement(ctx).setValue(facing, ctx.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(facing, rot.rotate((Direction)state.getValue(facing)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(facing)));
   }
}
