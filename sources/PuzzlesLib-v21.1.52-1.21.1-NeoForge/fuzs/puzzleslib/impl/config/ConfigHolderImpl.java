package fuzs.puzzleslib.impl.config;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigDataHolder;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.core.Freezable;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class ConfigHolderImpl implements ConfigHolder.Builder, Freezable {
   private final String modId;
   private Map<Class<?>, ConfigDataHolderImpl<?>> configsByClass = new IdentityHashMap<>();

   protected ConfigHolderImpl(String modId) {
      this.modId = modId;
   }

   private static <T extends ConfigCore> Supplier<T> construct(Class<T> clazz) {
      return () -> {
         try {
            return (T)(ConfigCore)MethodHandles.publicLookup().findConstructor(clazz, MethodType.methodType(void.class)).invoke();
         } catch (Throwable var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   @Override
   public <T extends ConfigCore> ConfigDataHolder<T> getHolder(Class<T> clazz) {
      ConfigDataHolderImpl<?> holder = this.configsByClass.get(clazz);
      Objects.requireNonNull(holder, "No config holder available for type " + clazz);
      return (ConfigDataHolder<T>)holder;
   }

   @Override
   public <T extends ConfigCore> ConfigHolder.Builder client(Class<T> clazz) {
      this.isWritableOrThrow();
      Supplier<T> supplier = ModLoaderEnvironment.INSTANCE.isClient() ? construct(clazz) : () -> null;
      if (this.configsByClass.put(clazz, this.client(supplier)) != null) {
         throw new IllegalStateException("Duplicate registration for client config of type " + clazz);
      } else {
         return this;
      }
   }

   @Override
   public <T extends ConfigCore> ConfigHolder.Builder common(Class<T> clazz) {
      this.isWritableOrThrow();
      if (this.configsByClass.put(clazz, this.common(construct(clazz))) != null) {
         throw new IllegalStateException("Duplicate registration for common config of type " + clazz);
      } else {
         return this;
      }
   }

   @Override
   public <T extends ConfigCore> ConfigHolder.Builder server(Class<T> clazz) {
      this.isWritableOrThrow();
      if (this.configsByClass.put(clazz, this.server(construct(clazz))) != null) {
         throw new IllegalStateException("Duplicate registration for server config of type " + clazz);
      } else {
         return this;
      }
   }

   protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> var1);

   protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> var1);

   protected abstract <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> var1);

   @Override
   public <T extends ConfigCore> ConfigHolder.Builder setFileName(Class<T> clazz, UnaryOperator<String> fileNameFactory) {
      ((ConfigDataHolderImpl)this.<T>getHolder(clazz)).setFileNameFactory(fileNameFactory);
      return this;
   }

   @Override
   public final void freeze() {
      this.isWritableOrThrow();
      this.configsByClass = ImmutableMap.copyOf(this.configsByClass);

      for (ConfigDataHolderImpl<?> holder : this.configsByClass.values()) {
         if (holder.config != null) {
            this.bake(holder, this.modId);
         }
      }

      ProxyImpl.get().registerConfigurationScreen(this.modId, new String[0]);
   }

   @Override
   public final boolean isFrozen() {
      return this.configsByClass instanceof ImmutableMap;
   }

   protected abstract void bake(ConfigDataHolderImpl<?> var1, String var2);
}
