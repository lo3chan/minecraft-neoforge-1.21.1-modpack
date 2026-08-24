package dev.kosmx.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationJson;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EmotecraftGsonCodec extends AbstractGsonCodec<KeyframeAnimation> {
   public static final EmotecraftGsonCodec INSTANCE = new EmotecraftGsonCodec();

   @Override
   protected Gson getGson() {
      return AnimationJson.GSON;
   }

   @Override
   protected Type getListedTypeToken() {
      return AnimationJson.getListedTypeToken();
   }

   @NotNull
   @Override
   public String getFormatName() {
      return "emotecraft";
   }

   public void encode(@NotNull OutputStream output, @NotNull ResourceLocation location, @NotNull KeyframeAnimation animation) throws IOException {
      try {
         try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(this.getGson().toJson(animation));
         }
      } catch (JsonSyntaxException var9) {
         throw new IOException(var9);
      }
   }
}
