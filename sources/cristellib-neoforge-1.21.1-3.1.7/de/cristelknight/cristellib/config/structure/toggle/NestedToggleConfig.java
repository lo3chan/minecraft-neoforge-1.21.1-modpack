package de.cristelknight.cristellib.config.structure.toggle;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Map;

public record NestedToggleConfig(Map<String, NestedToggleConfig.Entry> entries) {
   public static final Codec<NestedToggleConfig> CODEC = Codec.unboundedMap(Codec.STRING, NestedToggleConfig.Entry.CODEC)
      .xmap(NestedToggleConfig::new, NestedToggleConfig::entries);
   public static final Codec<Map<String, NestedToggleConfig>> TOGGLE_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

   public record Entry(Boolean value, NestedToggleConfig nested) {
      public static final Codec<NestedToggleConfig.Entry> CODEC = Codec.either(Codec.BOOL, Codec.lazyInitialized(() -> NestedToggleConfig.CODEC))
         .xmap(
            either -> (NestedToggleConfig.Entry)either.map(NestedToggleConfig.Entry::ofBoolean, NestedToggleConfig.Entry::ofNested),
            entry -> entry.isBoolean() ? Either.left(entry.value) : Either.right(entry.nested)
         );

      public boolean isBoolean() {
         return this.value != null;
      }

      public static NestedToggleConfig.Entry ofBoolean(Boolean value) {
         return new NestedToggleConfig.Entry(value, null);
      }

      public static NestedToggleConfig.Entry ofNested(NestedToggleConfig nested) {
         return new NestedToggleConfig.Entry(null, nested);
      }
   }
}
