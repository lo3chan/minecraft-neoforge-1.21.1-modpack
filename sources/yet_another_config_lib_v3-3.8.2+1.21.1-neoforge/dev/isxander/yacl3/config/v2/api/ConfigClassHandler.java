package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.impl.ConfigClassHandlerImpl;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public interface ConfigClassHandler<T> {
   T instance();

   T defaults();

   Class<T> configClass();

   ConfigField<?>[] fields();

   ResourceLocation id();

   YetAnotherConfigLib generateGui();

   boolean supportsAutoGen();

   boolean load();

   void save();

   @Deprecated
   ConfigSerializer<T> serializer();

   static <T> ConfigClassHandler.Builder<T> createBuilder(Class<T> configClass) {
      return new ConfigClassHandlerImpl.BuilderImpl<>(configClass);
   }

   public interface Builder<T> {
      ConfigClassHandler.Builder<T> id(ResourceLocation var1);

      ConfigClassHandler.Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> var1);

      ConfigClassHandler<T> build();
   }
}
