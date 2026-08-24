package dev.latvian.mods.kubejs.util;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import io.netty.buffer.ByteBuf;
import java.util.regex.Pattern;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public interface RegExpKJS {
   Codec<Pattern> CODEC = KubeJSCodecs.stringResolverCodec(RegExpKJS::toRegExpString, RegExpKJS::wrap);
   StreamCodec<ByteBuf, Pattern> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(RegExpKJS::wrap, RegExpKJS::toRegExpString);

   @Nullable
   static Pattern wrap(Object o) {
      if (o instanceof CharSequence || o instanceof NativeRegExp) {
         return ofString(o.toString());
      } else {
         return o instanceof Pattern pattern ? pattern : null;
      }
   }

   static int getFlags(String f) {
      int flags = 0;

      for (int i = 0; i < f.length(); i++) {
         switch (f.charAt(i)) {
            case 'U':
               flags |= 256;
               break;
            case 'd':
               flags |= 1;
               break;
            case 'i':
               flags |= 2;
               break;
            case 'm':
               flags |= 8;
               break;
            case 's':
               flags |= 32;
               break;
            case 'u':
               flags |= 64;
               break;
            case 'x':
               flags |= 4;
         }
      }

      return flags;
   }

   static boolean isValidFlag(char c) {
      return c == 'd' || c == 'i' || c == 'x' || c == 'm' || c == 's' || c == 'u' || c == 'U';
   }

   @Nullable
   static Pattern ofString(String string) {
      return string.length() >= 3 && string.charAt(0) == '/' ? read(new StringReader(string)) : null;
   }

   static String toRegExpString(Pattern pattern) {
      StringBuilder sb = new StringBuilder("/");
      sb.append(pattern.pattern());
      sb.append('/');
      int flags = pattern.flags();
      if ((flags & 1) != 0) {
         sb.append('d');
      }

      if ((flags & 2) != 0) {
         sb.append('i');
      }

      if ((flags & 4) != 0) {
         sb.append('x');
      }

      if ((flags & 8) != 0) {
         sb.append('m');
      }

      if ((flags & 32) != 0) {
         sb.append('s');
      }

      if ((flags & 64) != 0) {
         sb.append('u');
      }

      if ((flags & 256) != 0) {
         sb.append('U');
      }

      return sb.toString();
   }

   static Pattern read(StringReader reader) {
      if (reader.canRead() && reader.peek() == '/') {
         reader.skip();
         StringBuilder pattern = new StringBuilder();

         while (reader.canRead()) {
            char c = reader.read();
            if (c == '\\' && reader.canRead() && reader.peek() == '/') {
               reader.skip();
               pattern.append('/');
            } else {
               if (c == '/') {
                  break;
               }

               pattern.append(c);
            }
         }

         StringBuilder flags = new StringBuilder(0);

         while (reader.canRead() && isValidFlag(reader.peek())) {
            flags.append(reader.read());
         }

         return Pattern.compile(pattern.toString(), getFlags(flags.toString()));
      } else {
         throw new IllegalArgumentException("RegExp must start with /");
      }
   }

   static DataResult<Pattern> tryRead(StringReader reader) {
      try {
         return DataResult.success(read(reader));
      } catch (IllegalArgumentException var2) {
         return DataResult.error(() -> "Failed to parse regex from string: " + var2);
      }
   }
}
