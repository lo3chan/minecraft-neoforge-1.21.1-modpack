package dev.shadowsoffire.placebo.util.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

public class RuntimeDatagenHelpers {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   public static <T> JsonElement toJson(T object, Codec<T> codec) {
      return (JsonElement)codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow();
   }

   @Deprecated
   public static <T extends CodecProvider<T>> JsonElement toJson(T object) {
      return toJson(object, (Codec<T>)object.getCodec());
   }

   public static <T> void write(T object, Codec<T> codec, String type, ResourceLocation key) {
      write(toJson(object, codec), type, key);
   }

   @Deprecated
   public static <T extends CodecProvider<T>> void write(T object, String type, ResourceLocation key) {
      write(toJson(object), type, key);
   }

   public static void write(JsonElement json, String type, ResourceLocation key) {
      File file = new File(FMLPaths.GAMEDIR.get().toFile(), "datagen/" + key.getNamespace() + "/" + type + "/" + key.getPath() + ".json");
      file.getParentFile().mkdirs();

      try {
         try (FileWriter writer = new FileWriter(file)) {
            JsonWriter jWriter = new JsonWriter(writer);
            jWriter.setIndent("    ");
            GSON.toJson(json, jWriter);
         }
      } catch (IOException var9) {
         throw new IllegalStateException(var9);
      }
   }
}
