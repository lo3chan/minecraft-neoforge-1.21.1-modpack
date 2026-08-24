package net.blay09.mods.balm.api.proxy;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.BalmEnvironment;

public class SidedProxy<T> {
   private final Supplier<BalmEnvironment> environmentResolver;
   private final String commonName;
   private final String clientName;
   private T proxy;

   public SidedProxy(Supplier<BalmEnvironment> environmentResolver, String commonName, String clientName) {
      this.environmentResolver = environmentResolver;
      this.commonName = commonName;
      this.clientName = clientName;
   }

   public Supplier<T> buildLazily() {
      return new Supplier<T>() {
         private T instance;

         @Override
         public T get() {
            if (this.instance == null) {
               this.instance = (T)SidedProxy.this.build();
            }

            return this.instance;
         }
      };
   }

   public T build() {
      String classNameForEnvironment = switch ((BalmEnvironment)this.environmentResolver.get()) {
         case CLIENT -> this.clientName;
         case SERVER -> this.commonName;
      };

      try {
         this.proxy = (T)Class.forName(classNameForEnvironment).getConstructor().newInstance();
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }

      return this.proxy;
   }

   @Deprecated(
      since = "1.22"
   )
   public T get() {
      if (this.proxy == null) {
         this.proxy = this.build();
      }

      return this.proxy;
   }
}
