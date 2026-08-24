package dev.worldgen.lithostitched.mixin.common.mnbs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.duck.mnbs.MNBSDuck;
import dev.worldgen.lithostitched.duck.mnbs.MNBSPLDuck;
import dev.worldgen.lithostitched.util.CodecExtender;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({MultiNoiseBiomeSource.class})
public abstract class MNBSMixin implements MNBSDuck {
   @Shadow
   @Mutable
   @Final
   private Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

   @Override
   public Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> lithostitched$getEntries() {
      return this.parameters;
   }

   @Override
   public void lithostitched$setEntries(Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> entries) {
      this.parameters = entries;
   }

   @Redirect(
      method = {"<clinit>"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/serialization/MapCodec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
      )
   )
   private static MapCodec<MultiNoiseBiomeSource> wrapCodec(
      MapCodec<Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>> original,
      Function<? super Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>, ? extends MultiNoiseBiomeSource> to,
      Function<? super MultiNoiseBiomeSource, ? extends Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>> from
   ) {
      return CodecExtender.extend(
         original.xmap(to, from),
         (instance, wrapper) -> instance.group(wrapper, RegistryOps.retrieveGetter(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST))
            .apply(instance, (mnbs, lookup) -> {
               MNBSDuck duck = (MNBSDuck)mnbs;
               Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> biomeEntries = duck.lithostitched$getEntries();
               if (biomeEntries.left().isPresent()) {
                  List<Pair<ParameterPoint, Holder<Biome>>> rawEntries = ((ParameterList)biomeEntries.left().get()).values();
                  Optional<Reference<MultiNoiseBiomeSourceParameterList>> overworldPreset = lookup.get(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
                  if (overworldPreset.isEmpty() || !overworldPreset.get().isBound()) {
                     return mnbs;
                  }

                  Optional<Holder<Biome>> migrationBiome = ((MNBSPLDuck)overworldPreset.get().value()).lithostitched$getMigrationBiome();
                  if (migrationBiome.isPresent() && ((Holder)((Pair)rawEntries.getLast()).getSecond()).is(migrationBiome.get())) {
                     duck.lithostitched$setEntries(Either.right(overworldPreset.get()));
                  }
               }

               return mnbs;
            })
      );
   }
}
