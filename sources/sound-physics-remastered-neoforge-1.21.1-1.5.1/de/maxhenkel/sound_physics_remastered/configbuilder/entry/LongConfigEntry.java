package de.maxhenkel.sound_physics_remastered.configbuilder.entry;

import de.maxhenkel.sound_physics_remastered.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LongConfigEntry extends AbstractRangedConfigEntry<Long> {
   public LongConfigEntry(
      CommentedPropertyConfig config, ValueSerializer<Long> serializer, String[] comments, String key, Long def, @Nullable Long min, @Nullable Long max
   ) {
      super(config, serializer, comments, key, def, min, max);
      this.reload();
   }

   @Nonnull
   Long minimumPossibleValue() {
      return -9223372036854775808L;
   }

   @Nonnull
   Long maximumPossibleValue() {
      return 9223372036854775807L;
   }

   Long fixValue(Long value) {
      return Math.max(Math.min(value, this.max), this.min);
   }
}
