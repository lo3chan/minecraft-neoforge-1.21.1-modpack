package io.github.razordevs.deep_aether.fluids;

import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.init.DAFluids;
import io.github.razordevs.deep_aether.init.DAItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidType;

public abstract class PoisonFluid extends FlowingFluid {
   public FluidType getFluidType() {
      return (FluidType)DAFluidTypes.POISON_FLUID_TYPE.value();
   }

   protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
      this.fizz(pLevel, pPos);
   }

   public Fluid getFlowing() {
      return (Fluid)DAFluids.POISON_FLOWING.get();
   }

   public Fluid getSource() {
      return (Fluid)DAFluids.POISON_FLUID.get();
   }

   protected boolean canConvertToSource(Level pLevel) {
      return false;
   }

   public boolean isSame(Fluid pFluid) {
      return pFluid == DAFluids.POISON_FLUID.get() || pFluid == DAFluids.POISON_FLOWING.get();
   }

   private void fizz(LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.levelEvent(1501, blockPos, 0);
   }

   protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
      if (direction == Direction.DOWN) {
         FluidState fluidstate = levelAccessor.getFluidState(blockPos);
         if (this.is(DATags.Fluids.POISON) && fluidstate.is(FluidTags.LAVA)) {
            if (blockState.getBlock() instanceof LiquidBlock) {
               levelAccessor.setBlock(
                  blockPos, EventHooks.fireFluidPlaceBlockEvent(levelAccessor, blockPos, blockPos, Blocks.CRYING_OBSIDIAN.defaultBlockState()), 3
               );
            }

            this.fizz(levelAccessor, blockPos);
            return;
         }

         if (this.is(DATags.Fluids.POISON) && fluidstate.is(FluidTags.WATER)) {
            if (blockState.getBlock() instanceof LiquidBlock) {
               levelAccessor.setBlock(
                  blockPos, EventHooks.fireFluidPlaceBlockEvent(levelAccessor, blockPos, blockPos, ((Block)DABlocks.AERSMOG.get()).defaultBlockState()), 3
               );
            }

            this.fizz(levelAccessor, blockPos);
            return;
         }
      }

      super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
   }

   public int getAmount(FluidState fluidState) {
      return (Integer)fluidState.getValue(LEVEL);
   }

   public boolean isSource(FluidState fluidState) {
      return false;
   }

   protected int getSlopeFindDistance(LevelReader pLevel) {
      return 4;
   }

   public Item getBucket() {
      return (Item)DAItems.PLACEABLE_POISON_BUCKET.get();
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL);
   }

   public int getDropOff(LevelReader pLevel) {
      return 1;
   }

   public int getTickDelay(LevelReader pLevel) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection) {
      return pFluidState.getHeight(pBlockReader, pPos) >= 0.44444445F && pFluid.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public BlockState createLegacyBlock(FluidState pState) {
      return (BlockState)((LiquidBlock)DABlocks.POISON_BLOCK.get()).defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
   }

   public static class Flowing extends PoisonFluid {
      protected void createFluidStateDefinition(Builder<Fluid, FluidState> fluidStateBuilder) {
         super.createFluidStateDefinition(fluidStateBuilder);
         fluidStateBuilder.add(new Property[]{LEVEL});
      }
   }

   public static class Source extends PoisonFluid {
      @Override
      public int getAmount(FluidState fluidState) {
         return 8;
      }

      @Override
      public boolean isSource(FluidState fluidState) {
         return true;
      }
   }
}
