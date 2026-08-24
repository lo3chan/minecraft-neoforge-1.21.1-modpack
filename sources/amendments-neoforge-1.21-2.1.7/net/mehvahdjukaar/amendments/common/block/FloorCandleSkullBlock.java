package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.mehvahdjukaar.moonlight.api.block.IRecolorable;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class FloorCandleSkullBlock extends AbstractCandleSkullBlock implements IRecolorable {
   public static final MapCodec<FloorCandleSkullBlock> CODEC = simpleCodec(FloorCandleSkullBlock::new);
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

   public FloorCandleSkullBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, 0)).setValue(LIT, false));
   }

   protected MapCodec<? extends FloorCandleSkullBlock> codec() {
      return CODEC;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{ROTATION});
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(ROTATION, rotation.rotate((Integer)state.getValue(ROTATION), 16));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return (BlockState)state.setValue(ROTATION, mirror.mirror((Integer)state.getValue(ROTATION), 16));
   }

   public boolean tryRecolor(Level level, BlockPos blockPos, BlockState blockState, @Nullable DyeColor dyeColor) {
      if (level.getBlockEntity(blockPos) instanceof CandleSkullBlockTile tile) {
         BlockState c = tile.getCandle();
         if (!c.isAir()) {
            Block otherCandle = BlocksColorAPI.changeColor(c.getBlock(), dyeColor);
            if (otherCandle != null && !c.is(otherCandle)) {
               tile.setCandle(otherCandle.withPropertiesOf(c));
               tile.setChanged();
               return true;
            }
         }
      }

      return false;
   }

   public boolean isDefaultColor(Level level, BlockPos blockPos, BlockState blockState) {
      if (level.getBlockEntity(blockPos) instanceof CandleSkullBlockTile tile) {
         BlockState c = tile.getCandle();
         return BlocksColorAPI.isDefaultColor(c.getBlock());
      } else {
         return false;
      }
   }
}
