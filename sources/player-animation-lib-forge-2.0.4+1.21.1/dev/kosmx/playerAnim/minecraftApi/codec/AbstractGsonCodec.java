package dev.kosmx.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dev.kosmx.playerAnim.api.IPlayable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGsonCodec<T extends IPlayable> implements AnimationCodec<T> {
   protected abstract Gson getGson();

   protected abstract Type getListedTypeToken();

   @NotNull
   @Override
   public Collection<T> decode(@NotNull InputStream buffer) throws IOException {
      Gson gson = this.getGson();

      try {
         return (Collection<T>)gson.fromJson(new InputStreamReader(buffer), this.getListedTypeToken());
      } catch (JsonParseException var4) {
         throw new IOException(var4);
      }
   }

   @NotNull
   @Override
   public String getExtension() {
      return "json";
   }
}
