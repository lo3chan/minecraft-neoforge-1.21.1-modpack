package com.anthonyhilyard.iceberg.services;

import com.anthonyhilyard.iceberg.Iceberg;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Services {
   protected static final ConcurrentHashMap<Class<?>, Supplier<?>> serviceCache = new ConcurrentHashMap<>();

   public static IPlatformHelper getPlatformHelper() {
      return (IPlatformHelper)serviceCache.computeIfAbsent(IPlatformHelper.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   public static IBufferSourceFactory getBufferSourceFactory() {
      return (IBufferSourceFactory)serviceCache.computeIfAbsent(IBufferSourceFactory.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   public static IIcebergConfigSpecBuilder getConfigSpecBuilder() {
      return (IIcebergConfigSpecBuilder)serviceCache.computeIfAbsent(IIcebergConfigSpecBuilder.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   public static IKeyMappingRegistrar getKeyMappingRegistrar() {
      return (IKeyMappingRegistrar)serviceCache.computeIfAbsent(IKeyMappingRegistrar.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   public static IReloadListenerRegistrar getReloadListenerRegistrar() {
      return (IReloadListenerRegistrar)serviceCache.computeIfAbsent(IReloadListenerRegistrar.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   public static IFontLookup getFontLookup() {
      return (IFontLookup)serviceCache.computeIfAbsent(IFontLookup.class, x -> createLazySupplier((Class<?>)x)).get();
   }

   protected static <T> T load(Class<T> clazz) {
      T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
      Iceberg.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
      return loadedService;
   }

   protected static <T> Supplier<T> createLazySupplier(Class<T> clazz) {
      return new Supplier<T>() {
         private volatile T instance;

         @Override
         public T get() {
            if (this.instance == null) {
               synchronized (this) {
                  if (this.instance == null) {
                     this.instance = Services.load(clazz);
                  }
               }
            }

            return this.instance;
         }
      };
   }
}
