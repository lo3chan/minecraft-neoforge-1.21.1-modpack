package dev.worldgen.lithostitched.mixin.common.mnbs;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import dev.worldgen.lithostitched.duck.mnbs.MNBSPLDuck;
import dev.worldgen.lithostitched.util.CodecExtender;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({MultiNoiseBiomeSourceParameterList.class})
public abstract class MNBSPLMixin implements MNBSPLDuck {
   @Shadow
   @Mutable
   @Final
   private ParameterList<Holder<Biome>> parameters;
   @Unique
   private Optional<Holder<Biome>> lithostitched$MigrationBiome;

   @Override
   public void lithostitched$setParameters(ParameterList<Holder<Biome>> parameters) {
      this.parameters = parameters;
   }

   @Override
   public void lithostitched$setMigrationBiome(Optional<Holder<Biome>> biome) {
      this.lithostitched$MigrationBiome = biome;
   }

   @Override
   public Optional<Holder<Biome>> lithostitched$getMigrationBiome() {
      return this.lithostitched$MigrationBiome;
   }

   @Redirect(
      method = {"<clinit>"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
      )
   )
   private static Codec<MultiNoiseBiomeSourceParameterList> wrapCodec(
      Function<Instance<MultiNoiseBiomeSourceParameterList>, ? extends App<Mu<MultiNoiseBiomeSourceParameterList>, MultiNoiseBiomeSourceParameterList>> builder
   ) {
      return CodecExtender.extend(
         RecordCodecBuilder.create(builder),
         (instance, wrapper) -> instance.group(
               wrapper,
               ParameterList.codec(Biome.CODEC.fieldOf("biome")).optionalFieldOf("lithostitched:biomes").forGetter(mnbspl -> Optional.of(mnbspl.parameters())),
               Biome.CODEC.optionalFieldOf("lithostitched:migration_biome").forGetter(mnbspl -> ((MNBSPLDuck)mnbspl).lithostitched$getMigrationBiome())
            )
            .apply(instance, (mnbspl, parameters, biome) -> {
               MNBSPLDuck duck = (MNBSPLDuck)mnbspl;
               parameters.ifPresent(duck::lithostitched$setParameters);
               duck.lithostitched$setMigrationBiome(biome);
               return mnbspl;
            })
      );
   }
}
