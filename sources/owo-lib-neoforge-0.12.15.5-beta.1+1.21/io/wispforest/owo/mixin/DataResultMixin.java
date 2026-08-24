package io.wispforest.owo.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import io.wispforest.owo.Owo;
import io.wispforest.owo.util.StackTraceSupplier;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {DataResult.class},
   remap = false
)
public interface DataResultMixin {
   @Inject(
      method = {"error(Ljava/util/function/Supplier;)Lcom/mojang/serialization/DataResult;", "error(Ljava/util/function/Supplier;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", "error(Ljava/util/function/Supplier;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/serialization/DataResult;", "error(Ljava/util/function/Supplier;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/serialization/DataResult;"},
      at = {@At("HEAD")},
      remap = false
   )
   private static <R> void wrapMessageWithStacktrace(
      CallbackInfoReturnable<Optional<Error<R>>> cir, @Local(argsOnly = true) LocalRef<Supplier<String>> messageSupplier
   ) {
      if (Owo.DEBUG) {
         Supplier<String> ogSupplier = (Supplier<String>)messageSupplier.get();
         Class<? extends Supplier> ogClass = (Class<? extends Supplier>)ogSupplier.getClass();
         if (!(ogSupplier instanceof StackTraceSupplier)) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (ogClass.isSynthetic()) {
               try {
                  for (Field field : ogClass.getDeclaredFields()) {
                     if (Throwable.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        if (field.get(ogSupplier) instanceof Throwable e) {
                           stackTrace = (StackTraceElement[])e.getStackTrace().clone();
                        }
                        break;
                     }
                  }
               } catch (IllegalAccessException | IllegalArgumentException var11) {
               }
            }

            messageSupplier.set(new StackTraceSupplier(stackTrace, ogSupplier));
         }
      }
   }

   @Mixin(
      value = {Error.class},
      remap = false
   )
   public abstract static class DataResultErrorMixin<R> {
      @Shadow(
         remap = false
      )
      public abstract Supplier<String> messageSupplier();

      @Inject(
         method = {"getOrThrow", "getPartialOrThrow"},
         at = {@At("HEAD")},
         remap = false
      )
      private <E extends Throwable> void addStackTraceToException(
         CallbackInfoReturnable<R> cir, @Local(argsOnly = true) LocalRef<Function<String, E>> exceptionSupplier
      ) {
         Function<String, E> funcToWrap = (Function<String, E>)exceptionSupplier.get();
         exceptionSupplier.set((Function<String, Throwable>)s -> {
            E exception = funcToWrap.apply(s);
            if (this.messageSupplier() instanceof StackTraceSupplier stackTraceSupplier) {
               exception.setStackTrace(stackTraceSupplier.stackTrace());
            }

            return exception;
         });
      }
   }
}
