package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public record TickDuration(long ticks) implements TemporalAmount {
   public static final TickDuration ZERO = new TickDuration(0L);
   private static final List<TemporalUnit> UNITS = List.of(TickTemporalUnit.INSTANCE);
   public static final Codec<TickDuration> CODEC = Codec.LONG.xmap(TickDuration::of, TickDuration::ticks);
   public static final Codec<TickDuration> SECONDS_CODEC = Codec.DOUBLE.xmap(l -> of((long)(l * 20.0)), t -> t.ticks() / 20.0);
   public static final Codec<TickDuration> MINUTES_CODEC = Codec.DOUBLE.xmap(l -> of((long)(l * 1200.0)), t -> t.ticks() / 1200.0);
   public static final Codec<TickDuration> HOURS_CODEC = Codec.DOUBLE.xmap(l -> of((long)(l * 72000.0)), t -> t.ticks() / 72000.0);
   public static final TypeInfo TYPE_INFO = TypeInfo.of(TickDuration.class);

   public static TickDuration of(long ticks) {
      return ticks == 0L ? ZERO : new TickDuration(ticks);
   }

   public static TickDuration wrap(Context cx, Object from) {
      return switch (from) {
         case null -> ZERO;
         case TickDuration d -> d;
         case Number n -> of(n.longValue());
         case JsonPrimitive json -> of(json.getAsLong());
         default -> of(TimeJS.wrapDuration(cx, from).toMillis() / 50L);
      };
   }

   @Override
   public long get(TemporalUnit unit) {
      return unit == TickTemporalUnit.INSTANCE ? this.ticks : 0L;
   }

   @Override
   public List<TemporalUnit> getUnits() {
      return UNITS;
   }

   @Override
   public Temporal addTo(Temporal temporal) {
      return this.ticks != 0L ? temporal.plus(this.ticks, TickTemporalUnit.INSTANCE) : temporal;
   }

   @Override
   public Temporal subtractFrom(Temporal temporal) {
      return this.ticks != 0L ? temporal.minus(this.ticks, TickTemporalUnit.INSTANCE) : temporal;
   }

   @Override
   public String toString() {
      return this.ticks + " ticks";
   }

   public int intTicks() {
      return Math.clamp(this.ticks, -2147483648, 2147483647);
   }
}
