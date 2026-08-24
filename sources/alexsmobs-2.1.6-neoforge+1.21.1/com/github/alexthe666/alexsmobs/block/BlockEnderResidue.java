package com.github.alexthe666.alexsmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;

public class BlockEnderResidue extends TransparentBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   public static final BooleanProperty SLOW_DECAY = BooleanProperty.create("slow_decay");

   public BlockEnderResidue() {
      super(
         Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .noOcclusion()
            .hasPostProcess((i, j, k) -> true)
            .emissiveRendering((i, j, k) -> true)
            .lightLevel(i -> 3)
            .strength(0.2F)
            .sound(SoundType.AMETHYST)
            .randomTicks()
            .noOcclusion()
      );
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(SLOW_DECAY, false));
   }

   public void randomTick(BlockState p_53588_, ServerLevel p_53589_, BlockPos p_53590_, RandomSource p_53591_) {
      this.tick(p_53588_, p_53589_, p_53590_, p_53591_);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (random.nextInt(state.getValue(SLOW_DECAY) ? 15 : 5) == 0) {
         MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

         for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(pos, direction);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (blockstate.is(this) && !this.incrementAge(blockstate, level, blockpos$mutableblockpos)) {
               level.scheduleTick(blockpos$mutableblockpos, this, Mth.nextInt(random, 20, 40));
            }
         }

         this.incrementAge(state, level, pos);
      } else {
         level.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));
      }
   }

   private boolean incrementAge(BlockState state, Level level, BlockPos pos) {
      int i = (Integer)state.getValue(AGE);
      if (i < 3) {
         level.setBlock(pos, (BlockState)state.setValue(AGE, i + 1), 2);
         return false;
      } else {
         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
         return true;
      }
   }

   public void neighborChanged(BlockState p_53579_, Level p_53580_, BlockPos p_53581_, Block p_53582_, BlockPos p_53583_, boolean p_53584_) {
      super.neighborChanged(p_53579_, p_53580_, p_53581_, p_53582_, p_53583_, p_53584_);
   }

   private boolean fewerNeigboursThan(BlockGetter p_53566_, BlockPos p_53567_, int p_53568_) {
      int i = 0;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for (Direction direction : Direction.values()) {
         blockpos$mutableblockpos.setWithOffset(p_53567_, direction);
         if (p_53566_.getBlockState(blockpos$mutableblockpos).is(this)) {
            if (++i >= p_53568_) {
               return false;
            }
         }
      }

      return true;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> p_53586_) {
      p_53586_.add(new Property[]{AGE, SLOW_DECAY});
   }

   public ItemStack getCloneItemStack(BlockGetter p_53570_, BlockPos p_53571_, BlockState p_53572_) {
      return ItemStack.EMPTY;
   }
}
