package net.joefoxe.hexerei.fluid;

import javax.annotation.Nonnull;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

public class BloodFluid extends FlowingFluid {
   public Fluid getFlowing() {
      return (Fluid)ModFluids.BLOOD_FLOWING.get();
   }

   public Fluid getSource() {
      return (Fluid)ModFluids.BLOOD_FLUID.get();
   }

   protected boolean canConvertToSource(Level level) {
      return false;
   }

   protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
      BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
      Block.dropResources(state, worldIn, pos, tileentity);
   }

   public Item getBucket() {
      return (Item)ModItems.BLOOD_BUCKET.get();
   }

   protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
      return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   protected BlockState createLegacyBlock(FluidState state) {
      return (BlockState)((LiquidBlock)ModFluids.BLOOD_BLOCK.get()).defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
   }

   public boolean isSource(@Nonnull FluidState state) {
      return false;
   }

   public int getAmount(@Nonnull FluidState state) {
      return 0;
   }

   public boolean isSame(Fluid fluid) {
      return fluid == ModFluids.BLOOD_FLUID.get() || fluid == ModFluids.BLOOD_FLOWING.get();
   }

   public int getSlopeFindDistance(LevelReader level) {
      return 2;
   }

   public int getDropOff(LevelReader level) {
      return 2;
   }

   public int getTickDelay(LevelReader level) {
      return 5;
   }

   protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
      if (!state.isSource() && !(Boolean)state.getValue(FALLING)) {
         if (random.nextInt(64) == 0) {
            level.playSound(
               null,
               pos.getX() + 0.5,
               pos.getY() + 0.5,
               pos.getZ() + 0.5,
               SoundEvents.WATER_AMBIENT,
               SoundSource.BLOCKS,
               random.nextFloat() * 0.25F + 0.75F,
               random.nextFloat() + 0.5F
            );
         }
      } else if (random.nextInt(12) == 0 && !this.isSource(state)) {
         if (random.nextInt(3) == 0) {
            level.addParticle(
               (ParticleOptions)ModParticleTypes.BLOOD.get(),
               pos.getX() + random.nextDouble(),
               pos.getY() + random.nextDouble() * (((Integer)state.getValue(LEVEL)).intValue() / 8.0F),
               pos.getZ() + random.nextDouble(),
               -0.01 + random.nextDouble() * 0.02,
               -0.02 + random.nextDouble() * 0.02,
               -0.01 + random.nextDouble() * 0.02
            );
         }

         level.addParticle(
            (ParticleOptions)ModParticleTypes.BLOOD_BIT.get(),
            pos.getX() + random.nextDouble(),
            pos.getY() + random.nextDouble() * (((Integer)state.getValue(LEVEL)).intValue() / 8.0F),
            pos.getZ() + random.nextDouble(),
            -0.01 + random.nextDouble() * 0.02,
            -0.01 + random.nextDouble() * 0.02,
            -0.01 + random.nextDouble() * 0.02
         );
      } else if (random.nextInt(14) == 0 && this.isSource(state)) {
         if (random.nextInt(2) == 0) {
            level.addParticle(
               (ParticleOptions)ModParticleTypes.BLOOD.get(),
               pos.getX() + random.nextDouble(),
               pos.getY() + random.nextDouble(),
               pos.getZ() + random.nextDouble(),
               -0.01 + random.nextDouble() * 0.02,
               -0.01 + random.nextDouble() * 0.02,
               -0.01 + random.nextDouble() * 0.02
            );
         }

         level.addParticle(
            (ParticleOptions)ModParticleTypes.BLOOD_BIT.get(),
            pos.getX() + random.nextDouble(),
            pos.getY() + random.nextDouble(),
            pos.getZ() + random.nextDouble(),
            -0.01 + random.nextDouble() * 0.02,
            -0.01 + random.nextDouble() * 0.02,
            -0.01 + random.nextDouble() * 0.02
         );
      }

      super.animateTick(level, pos, state, random);
   }

   public FluidType getFluidType() {
      return (FluidType)ModFluidTypes.BLOOD_FLUID_TYPE.value();
   }

   public static class Flowing extends BloodFluid {
      protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(new Property[]{LEVEL});
      }

      @Override
      public BlockState createLegacyBlock(FluidState state) {
         return (BlockState)((LiquidBlock)ModFluids.BLOOD_BLOCK.get()).defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
      }

      @Override
      public int getAmount(FluidState state) {
         return (Integer)state.getValue(LEVEL);
      }

      @Override
      public boolean isSource(FluidState state) {
         return false;
      }
   }

   public static class Source extends BloodFluid {
      @Override
      public int getAmount(FluidState state) {
         return 8;
      }

      @Override
      public boolean isSource(FluidState state) {
         return true;
      }
   }
}
