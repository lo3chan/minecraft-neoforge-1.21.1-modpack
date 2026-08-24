package net.mehvahdjukaar.amendments.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class SoftFluidIngredient {
   private final SoftFluidStack fluid;
   public static final Codec<SoftFluidIngredient> CODEC = SoftFluidStack.CODEC
      .validate(stack -> stack.getCount() != 1 ? DataResult.error(() -> "SoftFluidIngredient must have count 1") : DataResult.success(stack))
      .xmap(SoftFluidIngredient::containing, SoftFluidIngredient::createStack);
   public static final StreamCodec<RegistryFriendlyByteBuf, SoftFluidIngredient> STREAM_CODEC = SoftFluidStack.STREAM_CODEC
      .map(SoftFluidIngredient::containing, SoftFluidIngredient::createStack);

   private SoftFluidIngredient(SoftFluidStack fluid) {
      this.fluid = fluid;
   }

   public static SoftFluidIngredient containing(SoftFluidStack result) {
      return new SoftFluidIngredient(result.copyWithCount(1));
   }

   public boolean matches(SoftFluidStack other) {
      return this.fluid.isSameFluidSameComponents(other);
   }

   public boolean isEmpty() {
      return this.fluid.isEmpty();
   }

   public SoftFluidStack createStack() {
      return this.fluid.copyWithCount(1);
   }
}
