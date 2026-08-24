package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.util.ID;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface BuilderTypeRegistry {
   <T> void of(ResourceKey<Registry<T>> registry, Consumer<BuilderTypeRegistry.Callback<T>> callback);

   default <T> void addDefault(ResourceKey<Registry<T>> registry, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
      this.of(registry, reg -> reg.addDefault(builderType, factory));
   }

   public interface Callback<T> {
      void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);

      void add(ResourceLocation type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory);

      @Deprecated(
         forRemoval = true
      )
      default void add(String type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
         this.add(ID.kjs(type), builderType, factory);
      }
   }
}
