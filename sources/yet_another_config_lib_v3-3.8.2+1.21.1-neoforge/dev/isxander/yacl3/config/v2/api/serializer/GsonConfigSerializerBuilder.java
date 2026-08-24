package dev.isxander.yacl3.config.v2.api.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public interface GsonConfigSerializerBuilder<T> {
   static <T> GsonConfigSerializerBuilder<T> create(ConfigClassHandler<T> config) {
      return new GsonConfigSerializer.Builder<>(config);
   }

   GsonConfigSerializerBuilder<T> setPath(Path var1);

   GsonConfigSerializerBuilder<T> overrideGsonBuilder(GsonBuilder var1);

   GsonConfigSerializerBuilder<T> overrideGsonBuilder(Gson var1);

   GsonConfigSerializerBuilder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> var1);

   GsonConfigSerializerBuilder<T> setJson5(boolean var1);

   ConfigSerializer<T> build();
}
