package net.mehvahdjukaar.moonlight.core.mixins.platform;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.concurrent.CompletionStage;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.FilteredResManager;
import net.mehvahdjukaar.moonlight.core.misc.ReloadInstanceWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftServer.class})
public abstract class MinecraftServerMixin {
   @Inject(
      method = {"lambda$reloadResources$29"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
         shift = Shift.BEFORE
      )}
   )
   private void moonlight$serverDynamicPackEarlyReload(
      ImmutableList immutableList, CallbackInfoReturnable<CompletionStage> cir, @Local CloseableResourceManager manager
   ) {
      if (!(manager instanceof FilteredResManager) && manager.getResource(Moonlight.res("moonlight/token.json")).isPresent() && !PlatHelper.isInitializing()) {
         ReloadInstanceWrapper.executeEarlyReloadBlocking(PackType.SERVER_DATA, manager, IProgressTracker.createTree(1));
      }
   }
}
