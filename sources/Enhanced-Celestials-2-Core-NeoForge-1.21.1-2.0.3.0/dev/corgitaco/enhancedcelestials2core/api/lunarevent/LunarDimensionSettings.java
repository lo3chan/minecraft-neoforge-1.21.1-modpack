package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import net.minecraft.resources.ResourceKey;

public record LunarDimensionSettings(
   ResourceKey<LunarEvent> defaultEvent,
   long trackedPastEventsMaxCount,
   long dayLength,
   long yearLengthInDays,
   long minDaysBetweenEvents,
   long maxDaysBetweenEvents,
   boolean requiresClearSkies,
   long nightStartTime
) {
   public static final Codec<LunarDimensionSettings> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            ResourceKey.codec(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).fieldOf("default").forGetter(LunarDimensionSettings::defaultEvent),
            Codec.LONG.fieldOf("tracked_past_events_max_count").forGetter(LunarDimensionSettings::trackedPastEventsMaxCount),
            Codec.LONG.fieldOf("day_length").forGetter(LunarDimensionSettings::dayLength),
            Codec.LONG.fieldOf("year_length_in_days").forGetter(LunarDimensionSettings::yearLengthInDays),
            Codec.LONG.fieldOf("min_days_between_all_events").forGetter(LunarDimensionSettings::minDaysBetweenEvents),
            Codec.LONG.fieldOf("max_days_between_all_events").orElse(98L).forGetter(LunarDimensionSettings::maxDaysBetweenEvents),
            Codec.BOOL.fieldOf("requires_clear_skies").orElse(true).forGetter(LunarDimensionSettings::requiresClearSkies),
            Codec.LONG.fieldOf("night_start_time").orElse(12500L).forGetter(LunarDimensionSettings::nightStartTime)
         )
         .apply(builder, LunarDimensionSettings::new)
   );
}
