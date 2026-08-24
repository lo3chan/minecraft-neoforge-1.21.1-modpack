package dev.kosmx.playerAnim.core.data.gson;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Deprecated(
   forRemoval = true
)
public class AnimationSerializing {
   @Deprecated(
      forRemoval = true
   )
   public static List<KeyframeAnimation> deserializeAnimation(Reader stream) {
      return (List<KeyframeAnimation>)AnimationJson.GSON.fromJson(stream, AnimationJson.getListedTypeToken());
   }

   @Deprecated(
      forRemoval = true
   )
   public static List<KeyframeAnimation> deserializeAnimation(InputStream stream) throws IOException {
      List var2;
      try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
         var2 = deserializeAnimation(reader);
      }

      return var2;
   }

   public static String serializeAnimation(KeyframeAnimation animation) {
      return AnimationJson.GSON.toJson(animation, KeyframeAnimation.class);
   }

   public static Writer writeAnimation(KeyframeAnimation animation, Writer writer) throws IOException {
      writer.write(serializeAnimation(animation));
      return writer;
   }
}
