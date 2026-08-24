package dev.latvian.mods.kubejs.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Undefined;
import java.lang.runtime.SwitchBootstraps;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;

public interface TimeJS {
   static TemporalAmount wrapTemporalAmount(Context cx, Object o) {
      Object var2 = o;
      byte var3 = 0;

      while (true) {
         Object var10000;
         switch (SwitchBootstraps.typeSwitch<"typeSwitch",TemporalAmount,Number,Undefined,Scriptable,CharSequence>(var2, var3)) {
            case -1:
               throw new KubeRuntimeException("Cannot convert null to temporal amount!").source(SourceLine.of(cx));
            case 0:
               TemporalAmount d = (TemporalAmount)var2;
               var10000 = d;
               break;
            case 1:
               Number n = (Number)var2;
               Duration var12 = Duration.ofMillis(n.longValue());
               var10000 = var12;
               break;
            case 2:
               Undefined undefined = (Undefined)var2;
               throw new KubeRuntimeException("Cannot convert undefined to temporal amount!").source(SourceLine.of(cx));
            case 3:
               Scriptable s = (Scriptable)var2;
               if (Undefined.isUndefined(s)) {
                  throw new KubeRuntimeException("Cannot convert undefined to temporal amount!").source(SourceLine.of(cx));
               }

               var3 = 4;
               continue;
            case 4:
               CharSequence cs = (CharSequence)var2;

               TemporalAmount var4;
               try {
                  var4 = readTemporalAmount(new StringReader(cs.toString()));
               } catch (CommandSyntaxException var11) {
                  throw new KubeRuntimeException("Failed to parse temporal amount: %s".formatted(cs), var11).source(SourceLine.of(cx));
               }

               var10000 = var4;
               break;
            default:
               throw new KubeRuntimeException("Don't know how to parse temporal amount from %s".formatted(o)).source(SourceLine.of(cx));
         }

         return (TemporalAmount)var10000;
      }
   }

   private static TemporalAmount readTemporalAmount(StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (!reader.canRead()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(reader);
      } else {
         double totalNanos = 0.0;

         double ticks;
         for (ticks = 0.0 / 0.0; reader.canRead(); reader.skipWhitespace()) {
            double amount = reader.readDouble();
            reader.skipWhitespace();
            String var7 = readTemporalUnit(reader);
            switch (var7) {
               case "t":
                  if (Double.isNaN(ticks)) {
                     ticks = 0.0;
                  }

                  ticks += amount;
                  break;
               case "ns":
                  totalNanos += amount;
                  break;
               case "ms":
                  totalNanos += amount * 1000000.0;
                  break;
               case "s":
                  totalNanos += amount * 1.0E9;
                  break;
               case "m":
                  totalNanos += amount * 60.0 * 1.0E9;
                  break;
               case "h":
                  totalNanos += amount * 3600.0 * 1.0E9;
                  break;
               case "d":
                  totalNanos += amount * 86400.0 * 1.0E9;
                  break;
               case "w":
                  totalNanos += amount * 604800.0 * 1.0E9;
                  break;
               case "M":
                  totalNanos += amount * 2629746.0 * 1.0E9;
                  break;
               case "y":
                  totalNanos += amount * 3.1556952E7 * 1.0E9;
                  break;
               default:
                  throw new IllegalStateException("Unexpected temporal unit!");
            }
         }

         return (TemporalAmount)(!Double.isNaN(ticks) ? TickDuration.of((long)(ticks + totalNanos / 5.0E7)) : Duration.ofNanos((long)totalNanos));
      }
   }

   private static String readTemporalUnit(StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, "<time unit>");
      } else {
         if (reader.canRead(2)) {
            if (reader.peek() == 'm' && reader.peek(1) == 's') {
               reader.skip();
               reader.skip();
               return "ms";
            }

            if (reader.peek() == 'n' && reader.peek(1) == 's') {
               reader.skip();
               reader.skip();
               return "ns";
            }
         }
         return switch (reader.read()) {
            case 'M' -> "M";
            case 'd' -> "d";
            case 'h' -> "h";
            case 'm' -> "m";
            case 's' -> "s";
            case 't' -> "t";
            case 'w' -> "w";
            case 'y' -> "y";
            default -> throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, "<time unit>");
         };
      }
   }

   static Duration wrapDuration(Context cx, Object o) {
      TemporalAmount t = wrapTemporalAmount(cx, o);

      return switch (t) {
         case Duration d -> d;
         case TickDuration(long var13) -> Duration.ofMillis(var13 * 50L);
         default -> {
            Duration d = Duration.ZERO;

            for (TemporalUnit unit : t.getUnits()) {
               d = d.plus(t.get(unit), unit);
            }

            yield d;
         }
      };
   }

   static DataResult<Duration> readDuration(String s) {
      try {
         StringReader reader = new StringReader(s);
         reader.skipWhitespace();
         TemporalAmount temporalAmount = readTemporalAmount(reader);

         return DataResult.success(switch (temporalAmount) {
            case Duration d -> d;
            case TickDuration(long var14) -> Duration.ofMillis(var14 * 50L);
            default -> {
               Duration d = Duration.ZERO;

               for (TemporalUnit unit : temporalAmount.getUnits()) {
                  d = d.plus(temporalAmount.get(unit), unit);
               }

               yield d;
            }
         });
      } catch (CommandSyntaxException var13) {
         return DataResult.error(() -> "Error parsing %s from string: %s".formatted(s, var13));
      }
   }

   static void appendTimestamp(StringBuilder builder, Calendar calendar) {
      int h = calendar.get(11);
      int m = calendar.get(12);
      int s = calendar.get(13);
      if (h < 10) {
         builder.append('0');
      }

      builder.append(h);
      builder.append(':');
      if (m < 10) {
         builder.append('0');
      }

      builder.append(m);
      builder.append(':');
      if (s < 10) {
         builder.append('0');
      }

      builder.append(s);
   }

   static String msToString(long ms) {
      return ms < 1000L ? ms + " ms" : "%.3f".formatted((float)ms / 1000.0F) + " s";
   }
}
