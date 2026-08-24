package dev.latvian.mods.kubejs.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum Tristate implements StringRepresentable {
   FALSE("false"),
   TRUE("true"),
   DEFAULT("default");

   public static final Tristate[] VALUES = values();
   public static final Codec<Tristate> CODEC = Codec.either(Codec.BOOL, Codec.unit("default"))
      .xmap(
         either -> (Tristate)either.map(b -> b ? TRUE : FALSE, s -> s.equalsIgnoreCase("true") ? TRUE : (s.equalsIgnoreCase("false") ? FALSE : DEFAULT)),
         t -> t == DEFAULT ? Either.right("default") : Either.left(t == TRUE)
      );
   public static final StreamCodec<ByteBuf, Tristate> STREAM_CODEC = ByteBufCodecs.idMapper(i -> VALUES[i], Enum::ordinal);
   public final String name;

   public static Tristate wrap(Object from) {
      return switch (from) {
         case null -> DEFAULT;
         case Tristate t -> t;
         case Boolean b -> b ? TRUE : FALSE;
         default -> {
            String var5 = from.toString().toLowerCase(Locale.ROOT);
            switch (var5) {
               case "true":
                  yield TRUE;
                  break;
               case "false":
                  yield FALSE;
                  break;
               default:
                  yield DEFAULT;
            }
         }
      };
   }

   private Tristate(String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean test(boolean enabled) {
      return this == DEFAULT || this == TRUE == enabled;
   }

   public boolean test(BooleanSupplier enabled) {
      return this == DEFAULT || this == TRUE == enabled.getAsBoolean();
   }
}
