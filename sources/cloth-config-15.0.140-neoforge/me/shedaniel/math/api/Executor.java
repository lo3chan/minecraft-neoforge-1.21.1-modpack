package me.shedaniel.math.api;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.api.distmarker.Dist;

public class Executor {
   private Executor() {
   }

   public static void run(Supplier<Runnable> runnableSupplier) {
      runnableSupplier.get().run();
   }

   public static void runIf(Supplier<Boolean> predicate, Supplier<Runnable> runnableSupplier) {
      if (predicate.get()) {
         runnableSupplier.get().run();
      }
   }

   public static void runIfEnv(Dist env, Supplier<Runnable> runnableSupplier) {
      if (FabricLoader.getInstance().getEnvironmentType() == env) {
         runnableSupplier.get().run();
      }
   }

   public static <T> T call(Supplier<Callable<T>> runnableSupplier) {
      try {
         return runnableSupplier.get().call();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }

   public static <T> Optional<T> callIf(Supplier<Boolean> predicate, Supplier<Callable<T>> runnableSupplier) {
      if (predicate.get()) {
         try {
            return Optional.of(runnableSupplier.get().call());
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return Optional.empty();
      }
   }

   public static <T> Optional<T> callIfEnv(Dist env, Supplier<Callable<T>> runnableSupplier) {
      if (FabricLoader.getInstance().getEnvironmentType() == env) {
         try {
            return Optional.of(runnableSupplier.get().call());
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return Optional.empty();
      }
   }
}
