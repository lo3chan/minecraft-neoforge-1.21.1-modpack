package io.github.razordevs.deep_aether.block.building;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CarvedSquashBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<CarvedSquashBlock> CODEC = simpleCodec(CarvedSquashBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   public CarvedSquashBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, placeContext.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
