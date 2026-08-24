package me.lucko.spark.lib.adventure.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import me.lucko.spark.lib.adventure.internal.properties.AdventureProperties;
import org.jetbrains.annotations.NotNull;

public final class Services {
   private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());

   private Services() {
   }

   @NotNull
   public static <P> Optional<P> service(@NotNull final Class<P> type) {
      ServiceLoader<P> loader = Services0.loader(type);
      Iterator<P> it = loader.iterator();

      while (it.hasNext()) {
         P instance;
         try {
            instance = it.next();
         } catch (Throwable var5) {
            if (!SERVICE_LOAD_FAILURES_ARE_FATAL) {
               continue;
            }

            throw new IllegalStateException("Encountered an exception loading service " + type, var5);
         }

         if (it.hasNext()) {
            throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
         }

         return Optional.of(instance);
      }

      return Optional.empty();
   }

   @NotNull
   public static <P> Optional<P> serviceWithFallback(@NotNull final Class<P> type) {
      ServiceLoader<P> loader = Services0.loader(type);
      Iterator<P> it = loader.iterator();
      P firstFallback = null;

      while (it.hasNext()) {
         P instance;
         try {
            instance = it.next();
         } catch (Throwable var6) {
            if (!SERVICE_LOAD_FAILURES_ARE_FATAL) {
               continue;
            }

            throw new IllegalStateException("Encountered an exception loading service " + type, var6);
         }

         if (!(instance instanceof Services.Fallback)) {
            return Optional.of(instance);
         }

         if (firstFallback == null) {
            firstFallback = instance;
         }
      }

      return Optional.ofNullable(firstFallback);
   }

   public static <P> Set<P> services(final Class<? extends P> clazz) {
      ServiceLoader<? extends P> loader = Services0.loader(clazz);
      Set<P> providers = new HashSet<>();

      for (P instance : loader) {
         try {
            ;
         } catch (ServiceConfigurationError var6) {
            if (!SERVICE_LOAD_FAILURES_ARE_FATAL) {
               continue;
            }

            throw new IllegalStateException("Encountered an exception loading a provider for " + clazz + ": ", var6);
         }

         providers.add(instance);
      }

      return Collections.unmodifiableSet(providers);
   }

   public interface Fallback {
   }
}
