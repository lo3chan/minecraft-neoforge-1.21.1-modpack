package net.blay09.mods.balm.common.proxy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.proxy.ModProxy;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModProxyImpl<T> implements ModProxy<T> {
   private final Logger logger = LoggerFactory.getLogger(ModProxyImpl.class);
   private final Function<String, Optional<String>> modVersionProvider;
   private final List<ModProxyImpl.ModEntry<T>> proxies = new ArrayList<>();
   @Nullable
   private final ResourceLocation identifier;
   @Nullable
   private Function<List<T>, T> multiplexer;
   @Nullable
   private T fallback;

   public ModProxyImpl(Function<String, Optional<String>> modVersionProvider) {
      this(modVersionProvider, null);
   }

   public ModProxyImpl(Function<String, Optional<String>> modVersionProvider, @Nullable ResourceLocation identifier) {
      this.modVersionProvider = modVersionProvider;
      this.identifier = identifier;
   }

   @Override
   public ModProxy<T> with(String modId, String clazzName) {
      return this.with(modId, null, clazzName);
   }

   @Override
   public ModProxy<T> with(String modId, @Nullable String versionRange, String clazzName) {
      this.proxies.add(new ModProxyImpl.ModEntry<>(modId, versionRange != null ? VersionRange.parse(versionRange.trim()) : null, clazzName, () -> {
         try {
            return (T)Class.forName(clazzName).getConstructor().newInstance();
         } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException var2x) {
            throw new RuntimeException("Failed to instantiate mod proxy " + clazzName, var2x);
         } catch (NoSuchMethodException var3x) {
            throw new RuntimeException("Failed to instantiate mod proxy, missing no-arg constructor in " + clazzName, var3x);
         }
      }));
      return this;
   }

   @Override
   public ModProxy<T> withMultiplexer(Function<List<T>, T> multiplexer) {
      this.multiplexer = multiplexer;
      return this;
   }

   @Override
   public ModProxy<T> withFallback(T fallback) {
      this.fallback = fallback;
      return this;
   }

   @Override
   public T build() {
      List<ModProxyImpl.ModEntry<T>> applicableProxies = this.proxies.stream().filter(this::isApplicable).toList();
      if (this.multiplexer != null && applicableProxies.size() > 1) {
         ArrayList<T> effectiveProxies = new ArrayList<>();

         for (ModProxyImpl.ModEntry<T> applicableProxy : applicableProxies) {
            try {
               effectiveProxies.add(applicableProxy.proxy.get());
            } catch (Exception var6) {
               this.logger.error("Failed to instantiate proxy", var6);
            }
         }

         if (effectiveProxies.size() > 1) {
            T proxy = this.multiplexer.apply(effectiveProxies);
            this.logger.info("Mod proxy {} resolved as {}", this.identifier != null ? this.identifier : "<unnamed>", proxy);
            return proxy;
         } else {
            T proxy = (T)effectiveProxies.getFirst();
            this.logger.info("Mod proxy {} resolved as {}", this.identifier != null ? this.identifier : "<unnamed>", proxy);
            return proxy;
         }
      } else {
         for (ModProxyImpl.ModEntry<T> applicableProxy : applicableProxies) {
            try {
               T proxy = applicableProxy.proxy.get();
               this.logger.info("Mod proxy {} resolved as {}", this.identifier != null ? this.identifier : "<unnamed>", proxy);
               return proxy;
            } catch (Exception var7) {
               this.logger.error("Failed to instantiate proxy {}", this.identifier != null ? this.identifier : "<unnamed>", var7);
            }
         }

         if (this.fallback != null) {
            this.logger.info("Mod proxy {} resolved as {}", this.identifier != null ? this.identifier : "<unnamed>", this.fallback);
         } else {
            this.logger.warn("No applicable proxy found for {}", this.identifier != null ? this.identifier : "<unnamed>");
         }

         return this.fallback;
      }
   }

   private boolean isApplicable(ModProxyImpl.ModEntry<T> proxy) {
      Optional<String> modVersion = this.modVersionProvider.apply(proxy.modId);
      return modVersion.isPresent() && (proxy.versionRange == null || proxy.versionRange.contains(modVersion.get()));
   }

   @Override
   public Supplier<T> buildLazily() {
      return new Supplier<T>() {
         private T instance;

         @Override
         public T get() {
            if (this.instance == null) {
               this.instance = (T)ModProxyImpl.this.build();
            }

            return this.instance;
         }
      };
   }

   public record ModEntry<T>(String modId, @Nullable VersionRange versionRange, String clazzName, Supplier<T> proxy) {
   }
}
