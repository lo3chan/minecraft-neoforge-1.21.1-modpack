package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.JavaWrapper;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import org.jetbrains.annotations.Nullable;

public record RegistryType<T>(ResourceKey<Registry<T>> key, Class<?> baseClass, TypeInfo type) {
   private static final Map<ResourceKey<?>, RegistryType<?>> KEY_MAP = new Reference2ObjectOpenHashMap();
   private static final Map<TypeInfo, RegistryType<?>> TYPE_MAP = new HashMap<>();
   private static final Map<Class<?>, List<RegistryType<?>>> CLASS_MAP = new Reference2ObjectOpenHashMap();

   public static synchronized <T> void register(ResourceKey<Registry<T>> key, TypeInfo type) {
      RegistryType<T> t = new RegistryType<>(key, type.asClass(), type);
      KEY_MAP.put(key, t);
      TYPE_MAP.put(type, t);
      CLASS_MAP.computeIfAbsent(t.baseClass, c -> new ArrayList<>(1)).add(t);
      if (DevProperties.get().logRegistryTypes) {
         KubeJS.LOGGER.info("Registered RegistryType '{}': {}", key.location(), type);
      }
   }

   @Nullable
   public static synchronized <T> RegistryType<T> ofKey(ResourceKey<? extends Registry<T>> key) {
      RegistryType.Scanner.startIfNotFrozen();
      return Cast.to(KEY_MAP.get(key));
   }

   @Nullable
   public static synchronized RegistryType<?> ofType(TypeInfo typeInfo) {
      RegistryType.Scanner.startIfNotFrozen();
      return TYPE_MAP.get(typeInfo);
   }

   @Nullable
   public static synchronized <T> RegistryType<T> ofClass(Class<T> type) {
      List<RegistryType<T>> regList = allOfClass(type);
      return regList != null && regList.size() == 1 ? (RegistryType)regList.getFirst() : null;
   }

   public static synchronized <T> List<RegistryType<T>> allOfClass(Class<T> type) {
      RegistryType.Scanner.startIfNotFrozen();
      return Cast.to(CLASS_MAP.getOrDefault(type, List.of()));
   }

   @Nullable
   public static synchronized RegistryType<?> lookup(TypeInfo target) {
      List<? extends RegistryType<?>> reg = allOfClass(target.asClass());
      if (reg.size() == 1) {
         return (RegistryType<?>)reg.getFirst();
      } else {
         if (!reg.isEmpty()) {
            for (RegistryType<?> regType : reg) {
               if (regType.type().equals(target)) {
                  return regType;
               }
            }
         }

         return null;
      }
   }

   @Override
   public String toString() {
      return this.key.location() + "=" + this.type;
   }

   public static class Scanner {
      private static final Lazy<Set<Class<?>>> VALID_TYPES = Lazy.of(() -> {
         Set<Class<?>> set = new HashSet<>();
         set.add(ResourceKey.class);
         set.add(Registry.class);
         Class<?> registrar = JavaWrapper.tryLoadClass("dev.architectury.registry.registries.Registrar");
         if (registrar != null) {
            set.add(registrar);
         }

         return set;
      });
      private static final Set<String> CLASSES_TO_SCAN = new HashSet<>();
      private static final Set<String> MODULES_TO_SKIP = Set.of("java.base", "neoforge", "fml_loader", "kubejs");
      private static final Set<String> NAMESPACES_TO_SKIP = Set.of("neoforge", "minecraft");
      private static boolean frozen = false;

      public static synchronized void init() {
         processClass(Stream.of(Registries.class, Keys.class));
      }

      private static synchronized void startIfNotFrozen() {
         if (!frozen) {
            frozen = true;
            long startTime = Util.getNanos();
            processClass(CLASSES_TO_SCAN.stream().map(JavaWrapper::tryLoadClass));
            CLASSES_TO_SCAN.clear();
            KubeJS.LOGGER.debug("Took {} ms to discover registry classes.", (int)((Util.getNanos() - startTime) / 1000000L));
         }
      }

      private static void processClass(Stream<Class<?>> classStream) {
         classStream.filter(Objects::nonNull)
            .map(Class::getDeclaredFields)
            .flatMap(Stream::of)
            .forEach(
               field -> {
                  try {
                     if (!((Set)VALID_TYPES.get()).contains(field.getType()) || !Modifier.isStatic(field.getModifiers())) {
                        return;
                     }

                     if (!Modifier.isPublic(field.getModifiers())) {
                        field.setAccessible(true);
                     }

                     Object value = field.get(null);
                     if (value instanceof ResourceKey<?> key) {
                        if (field.getGenericType() instanceof ParameterizedType t1 && t1.getActualTypeArguments()[0] instanceof ParameterizedType t2) {
                           processKey(key, t2, false);
                        }
                     } else if (value instanceof Registry<?> registry) {
                        if (field.getGenericType() instanceof ParameterizedType t1) {
                           processKey(registry.key(), t1, true);
                        }
                     } else if (field.getType().getName().equals("dev.architectury.registry.registries.Registrar")
                        && field.getGenericType() instanceof ParameterizedType t1) {
                        Method method = value.getClass().getDeclaredMethod("key");
                        processKey((ResourceKey)method.invoke(value), t1, true);
                     }
                  } catch (Exception var7) {
                     KubeJS.LOGGER
                        .error("Error while trying to get registry from field {} from class {}", new Object[]{field.getName(), field.getType().getName(), var7});
                  }
               }
            );
      }

      private static void processKey(ResourceKey key, ParameterizedType paramType, boolean checkIfContains) {
         if (!checkIfContains || RegistryType.ofKey(key) == null) {
            Type type = paramType.getActualTypeArguments()[0];
            TypeInfo typeInfo = TypeInfo.of(type);
            RegistryType.register(key, typeInfo);
         }
      }

      public static synchronized void scan(ResourceLocation registryName, ResourceLocation location) {
         if (!frozen) {
            if (registryName.equals(Registries.ROOT_REGISTRY_NAME)) {
               if (!NAMESPACES_TO_SKIP.contains(location.getNamespace())) {
                  long startTime = Util.getNanos();
                  StackTraceElement[] stack = Thread.currentThread().getStackTrace();

                  for (StackTraceElement stackTraceElement : stack) {
                     String moduleName = stackTraceElement.getModuleName();
                     if (moduleName == null || !MODULES_TO_SKIP.contains(moduleName)) {
                        String className = stackTraceElement.getClassName();
                        if (!CLASSES_TO_SCAN.contains(className)) {
                           CLASSES_TO_SCAN.add(className);
                        }
                     }
                  }

                  KubeJS.LOGGER.debug("Took {} ms to grab stacktrace classes.", (int)((Util.getNanos() - startTime) / 1000000L));
               }
            }
         }
      }
   }
}
