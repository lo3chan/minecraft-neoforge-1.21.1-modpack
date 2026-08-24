package net.joefoxe.hexerei.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class PotionFluid extends FlowingFluid {
   public FluidType getFluidType() {
      return (FluidType)ModFluidTypes.POTION_FLUID_TYPE.get();
   }

   protected boolean canConvertToSource(Level level) {
      return false;
   }

   protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
   }

   protected int getSlopeFindDistance(LevelReader level) {
      return 0;
   }

   protected int getDropOff(LevelReader level) {
      return 0;
   }

   public Fluid getFlowing() {
      return this;
   }

   public Fluid getSource() {
      return this;
   }

   public Item getBucket() {
      return Items.AIR;
   }

   protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
      return false;
   }

   public int getTickDelay(LevelReader level) {
      return 0;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   protected BlockState createLegacyBlock(FluidState state) {
      return Blocks.AIR.defaultBlockState();
   }

   public boolean isSource(FluidState p_207193_1_) {
      return true;
   }

   public int getAmount(FluidState p_207192_1_) {
      return 0;
   }

   public static FluidStack of(int amount, PotionContents potion) {
      FluidStack fluidStack = new FluidStack((Fluid)ModFluids.POTION.get(), amount);
      addPotionToFluidStack(fluidStack, potion);
      return fluidStack;
   }

   public static FluidStack addPotionToFluidStack(FluidStack fs, PotionContents potion) {
      if (potion != null && potion != PotionContents.EMPTY) {
         fs.set(DataComponents.POTION_CONTENTS, potion);
      }

      return fs;
   }

   public static <V> ResourceLocation getKeyOrThrow(Potion value) {
      ResourceLocation key = BuiltInRegistries.POTION.getKey(value);
      if (key == null) {
         throw new IllegalArgumentException("Could not get key for value " + value + "!");
      } else {
         return key;
      }
   }

   public static enum BottleType implements StringRepresentable {
      REGULAR,
      SPLASH,
      LINGERING;

      public static final EnumCodec<PotionFluid.BottleType> CODEC = StringRepresentable.fromEnum(PotionFluid.BottleType::values);

      public static PotionFluid.BottleType byId(int id) {
         PotionFluid.BottleType[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }

      public String getSerializedName() {
         return switch (this) {
            case REGULAR -> "REGULAR";
            case SPLASH -> "SPLASH";
            case LINGERING -> "LINGERING";
         };
      }
   }
}
