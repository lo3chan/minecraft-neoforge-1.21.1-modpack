package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.FilteredResManager;
import net.mehvahdjukaar.moonlight.core.misc.ReloadInstanceWrapper;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldLoader.InitConfig;
import net.minecraft.server.WorldLoader.ResultFactory;
import net.minecraft.server.WorldLoader.WorldDataSupplier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WorldLoader.class})
public abstract class WorldLoaderMixin {
   @Inject(
      method = {"load"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/RegistryLayer;createRegistryAccess()Lnet/minecraft/core/LayeredRegistryAccess;",
         shift = Shift.BEFORE
      )}
   )
   private static void moonlight$serverDynamicPackEarlyReload(
      InitConfig initConfig,
      WorldDataSupplier worldDataSupplier,
      ResultFactory resultFactory,
      Executor backgroundExecutor,
      Executor gameExecutor,
      CallbackInfoReturnable<CompletableFuture> cir,
      @Local CloseableResourceManager manager
   ) {
      if (!(manager instanceof FilteredResManager) && manager.getResource(Moonlight.res("moonlight/token.json")).isPresent() && !PlatHelper.isInitializing()) {
         ReloadInstanceWrapper.executeEarlyReloadBlocking(PackType.SERVER_DATA, manager, IProgressTracker.createTree(1));
      }
   }
}
