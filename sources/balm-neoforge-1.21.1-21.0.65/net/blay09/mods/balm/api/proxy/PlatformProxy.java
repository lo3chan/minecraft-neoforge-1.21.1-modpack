package net.blay09.mods.balm.api.proxy;

public interface PlatformProxy<T> {
   PlatformProxy<T> with(String var1, String var2);

   T build();

   default PlatformProxy<T> withFabric(String clazzName) {
      return this.with("fabric", clazzName);
   }

   default PlatformProxy<T> withForge(String clazzName) {
      return this.with("forge", clazzName);
   }

   default PlatformProxy<T> withNeoForge(String clazzName) {
      return this.with("neoforge", clazzName);
   }
}
