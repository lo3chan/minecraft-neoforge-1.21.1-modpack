package dev.kosmx.playerAnim.minecraftApi.codec;

import com.google.gson.Gson;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.GeckoLibSerializer;
import java.lang.reflect.Type;
import org.jetbrains.annotations.NotNull;

@Deprecated(
   forRemoval = false
)
public class LegacyGeckoJsonCodec extends AbstractGsonCodec<KeyframeAnimation> {
   public static final LegacyGeckoJsonCodec INSTANCE = new LegacyGeckoJsonCodec();

   @Override
   protected Gson getGson() {
      return GeckoLibSerializer.GSON;
   }

   @Override
   protected Type getListedTypeToken() {
      return GeckoLibSerializer.getListedTypeToken();
   }

   @NotNull
   @Override
   public String getFormatName() {
      return "gecko_legacy";
   }
}
