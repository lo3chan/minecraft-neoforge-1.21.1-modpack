package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.util.DensityFunctionInjectorHelper;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.levelgen.DensityFunction;

public record WrapDensityFunctionModifier(
   Optional<LoadPredicate> predicate, int priority, Holder<DensityFunction> targetFunction, Holder<DensityFunction> wrapperFunction
) implements WorldgenModifier {
   public static final MapCodec<WrapDensityFunctionModifier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
            PRIORITY_DEFAULT_CODEC.forGetter(WrapDensityFunctionModifier::priority),
            LithostitchedCodecs.DF_REFERENCE.fieldOf("target_function").forGetter(WrapDensityFunctionModifier::targetFunction),
            LithostitchedCodecs.DF_REFERENCE.fieldOf("wrapper_function").forGetter(WrapDensityFunctionModifier::wrapperFunction)
         )
         .apply(instance, WrapDensityFunctionModifier::new)
   );

   public static WorldgenModifier create(
      Optional<LoadPredicate> predicate, int priority, Holder<DensityFunction> targetFunction, Holder<DensityFunction> wrapperFunction
   ) {
      return new WrapDensityFunctionModifier(predicate, priority, targetFunction, wrapperFunction);
   }

   @Override
   public void apply(RegistryAccess registries) {
      if (this.targetFunction instanceof Reference<DensityFunction> reference) {
         HolderReferenceAccessor<DensityFunction> accessor = (HolderReferenceAccessor<DensityFunction>)reference;
         accessor.setValue(DensityFunctionInjectorHelper.wrap((DensityFunction)this.targetFunction.value(), (DensityFunction)this.wrapperFunction.value()));
      }
   }

   @Override
   public MapCodec<? extends WorldgenModifier> codec() {
      return CODEC;
   }
}
