package vazkii.patchouli.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation.Serializer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IVariable;

public final class SerializationUtil {
   public static final IVariable.Serializer VARIABLE_SERIALIZER = new IVariable.Serializer();
   public static final Gson RAW_GSON = new GsonBuilder()
      .registerTypeAdapter(ResourceLocation.class, new Serializer())
      .registerTypeAdapter(IVariable.class, VARIABLE_SERIALIZER)
      .create();
   public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

   private SerializationUtil() {
   }

   public static ResourceLocation getAsResourceLocation(JsonObject object, String key, @Nullable ResourceLocation fallback) {
      return object.has(key) ? ResourceLocation.tryParse(GsonHelper.convertToString(object.get(key), key)) : fallback;
   }

   @Nullable
   public static <T extends Enum<T>> T getAsEnum(JsonObject object, String key, Class<T> clz, @Nullable T fallback) {
      if (object.has(key)) {
         String str = GsonHelper.convertToString(object.get(key), key).toUpperCase(Locale.ROOT);
         return Enum.valueOf(clz, str);
      } else {
         return fallback;
      }
   }
}
