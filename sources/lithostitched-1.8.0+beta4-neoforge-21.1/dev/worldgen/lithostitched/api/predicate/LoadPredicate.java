package dev.worldgen.lithostitched.api.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.impl.predicate.AllOfPredicate;
import dev.worldgen.lithostitched.impl.predicate.AnyOfPredicate;
import dev.worldgen.lithostitched.impl.predicate.LoaderPredicate;
import dev.worldgen.lithostitched.impl.predicate.ModLoadedPredicate;
import dev.worldgen.lithostitched.impl.predicate.NotPredicate;
import dev.worldgen.lithostitched.impl.predicate.PackFormatPredicate;
import dev.worldgen.lithostitched.impl.predicate.TruePredicate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.InclusiveRange;

public interface LoadPredicate {
   Codec<LoadPredicate> CODEC = LithostitchedBuiltInRegistries.LOAD_PREDICATE_TYPE.byNameCodec().dispatch(LoadPredicate::codec, Function.identity());
   MapCodec<Optional<LoadPredicate>> FIELD_CODEC = CODEC.lenientOptionalFieldOf("predicate");

   boolean test();

   MapCodec<? extends LoadPredicate> codec();

   static LoadPredicate allOf(LoadPredicate... predicates) {
      return new AllOfPredicate(List.of(predicates));
   }

   static LoadPredicate anyOf(LoadPredicate... predicates) {
      return new AnyOfPredicate(List.of(predicates));
   }

   static LoadPredicate isFabric() {
      return new LoaderPredicate("fabric");
   }

   static LoadPredicate isNeoforge() {
      return new LoaderPredicate("neoforge");
   }

   static LoadPredicate modLoaded(String modId) {
      return new ModLoadedPredicate(modId);
   }

   static LoadPredicate not(LoadPredicate predicate) {
      return new NotPredicate(predicate);
   }

   static LoadPredicate packFormat(InclusiveRange<Integer> supportedFormats) {
      return new PackFormatPredicate(supportedFormats);
   }

   static LoadPredicate alwaysFalse() {
      return not(alwaysTrue());
   }

   static LoadPredicate alwaysTrue() {
      return new TruePredicate();
   }
}
