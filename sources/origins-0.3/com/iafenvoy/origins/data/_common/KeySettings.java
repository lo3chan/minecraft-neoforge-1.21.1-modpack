package com.iafenvoy.origins.data._common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;

public record KeySettings(String key, boolean continuous) {
   public static final Codec<KeySettings> BASE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.optionalFieldOf("key", "key.origins.primary_active").forGetter(KeySettings::key),
            Codec.BOOL.optionalFieldOf("continuous", false).forGetter(KeySettings::continuous)
         )
         .apply(instance, KeySettings::new)
   );
   public static final MapCodec<KeySettings> CODEC = BASE_CODEC.optionalFieldOf("key", new KeySettings("key.origins.primary_active", false));

   public boolean match(String key) {
      return Objects.equals(this.key, key);
   }
}
