package com.seibel.distanthorizons.neoforge.mixins.server;

import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.core.util.objects.RunOnThisThreadExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Util.class})
public class MixinUtilBackgroundThread {
   @Inject(
      method = {"backgroundExecutor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void overrideUtil$backgroundExecutor(CallbackInfoReturnable<ExecutorService> ci) {
      if (BatchGenerationEnvironment_neoforge.isThisDhWorldGenThread()) {
         ci.setReturnValue(new RunOnThisThreadExecutorService());
      }
   }

   @Inject(
      method = {"wrapThreadWithTaskName(Ljava/lang/String;Ljava/lang/Runnable;)Ljava/lang/Runnable;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void overrideUtil$wrapThreadWithTaskName(String string, Runnable r, CallbackInfoReturnable<Runnable> ci) {
      if (BatchGenerationEnvironment_neoforge.isThisDhWorldGenThread()) {
         ci.setReturnValue(r);
      }
   }

   @Inject(
      method = {"wrapThreadWithTaskName(Ljava/lang/String;Ljava/util/function/Supplier;)Ljava/util/function/Supplier;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void overrideUtil$wrapThreadWithTaskNameForSupplier(String string, Supplier<?> r, CallbackInfoReturnable<Supplier<?>> ci) {
      if (BatchGenerationEnvironment_neoforge.isThisDhWorldGenThread()) {
         ci.setReturnValue(r);
      }
   }
}
