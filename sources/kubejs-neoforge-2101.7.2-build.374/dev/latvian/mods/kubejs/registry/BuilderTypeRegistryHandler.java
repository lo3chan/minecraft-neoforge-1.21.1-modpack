package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record BuilderTypeRegistryHandler(Map<ResourceKey<?>, BuilderTypeRegistryHandler.Info<?>> map) implements BuilderTypeRegistry, ServerRegistryRegistry {
   public static final Lazy<Map<ResourceKey<?>, BuilderTypeRegistryHandler.Info<?>>> INFO = Lazy.identityMap(map -> {
      BuilderTypeRegistryHandler handler = new BuilderTypeRegistryHandler(map);
      KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerBuilderTypes);
      KubeJSPlugins.forEachPlugin(handler, KubeJSPlugin::registerServerRegistries);
   });

   public static <T> BuilderTypeRegistryHandler.Info<T> info(ResourceKey<Registry<T>> key) {
      return (BuilderTypeRegistryHandler.Info<T>)INFO.get().get(key);
   }

   @Override
   public <T> void of(ResourceKey<Registry<T>> registry, Consumer<BuilderTypeRegistry.Callback<T>> callback) {
      callback.accept(
         new BuilderTypeRegistryHandler.RegConsumer<>(
            (BuilderTypeRegistryHandler.Info<T>)this.map.computeIfAbsent(registry, k -> new BuilderTypeRegistryHandler.Info())
         )
      );
   }

   @Override
   public <T> void register(ResourceKey<Registry<T>> registry, Codec<T> directCodec, TypeInfo typeInfo) {
      BuilderTypeRegistryHandler.Info<?> info = this.map.computeIfAbsent(registry, k -> new BuilderTypeRegistryHandler.Info());
      info.directCodec = directCodec;
      info.typeInfo = typeInfo == null ? TypeInfo.NONE : typeInfo;
   }

   public static class Info<T> {
      private BuilderType<T> defaultType;
      private Map<ResourceLocation, BuilderType<T>> types;
      private Map<String, BuilderType<T>> fallbackLookup;
      private Codec<T> directCodec;
      private TypeInfo typeInfo;

      @Nullable
      public BuilderType<T> defaultType() {
         return this.defaultType;
      }

      public List<BuilderType<T>> types() {
         return this.types == null ? List.of() : List.copyOf(this.types.values());
      }

      @Nullable
      public BuilderType<T> namedType(ResourceLocation name) {
         BuilderType<T> t = this.types == null ? null : this.types.get(name);
         return t != null ? t : (this.fallbackLookup == null ? null : this.fallbackLookup.get(name.getPath()));
      }

      @Nullable
      public Codec<T> directCodec() {
         return this.directCodec;
      }

      @Nullable
      public TypeInfo typeInfo() {
         return this.typeInfo;
      }
   }

   private record RegConsumer<T>(BuilderTypeRegistryHandler.Info<T> info) implements BuilderTypeRegistry.Callback<T> {
      private static final ResourceLocation DEFAULT = KubeJS.id("default");

      @Override
      public void addDefault(Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
         if (this.info.defaultType != null) {
            ConsoleJS.STARTUP
               .warn(
                  "Previous default type '"
                     + this.info.defaultType.builderClass().getName()
                     + "' for registry '"
                     + this.info
                     + "' replaced with '"
                     + builderType.getName()
                     + "'!"
               );
         }

         this.info.defaultType = new BuilderType<>(DEFAULT, builderType, factory);
      }

      @Override
      public void add(ResourceLocation type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
         if (this.info.types == null) {
            this.info.types = new LinkedHashMap<>();
         }

         if (this.info.fallbackLookup == null) {
            this.info.fallbackLookup = new HashMap<>();
         }

         BuilderType<T> prev = this.info.types.get(type);
         if (prev != null) {
            ConsoleJS.STARTUP
               .warn(
                  "Previous '"
                     + type
                     + "' type '"
                     + prev.builderClass().getName()
                     + "' for registry '"
                     + this.info
                     + "' replaced with '"
                     + builderType.getName()
                     + "'!"
               );
         }

         BuilderType<T> builderTypeDef = new BuilderType<>(type, builderType, factory);
         this.info.types.put(type, builderTypeDef);
         this.info.fallbackLookup.put(type.getPath(), builderTypeDef);
      }
   }
}
