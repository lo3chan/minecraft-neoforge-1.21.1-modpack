package net.mehvahdjukaar.moonlight.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.misc.FilteredResManager;
import net.mehvahdjukaar.moonlight.core.misc.ReloadInstanceWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ReloadableResourceManager.class})
public abstract class ReloadableClientResourcesMixin {
   @Shadow
   @Final
   public PackType type;
   @Shadow
   private CloseableResourceManager resources;

   @Shadow
   public abstract Stream<PackResources> listPacks();

   @WrapOperation(
      method = {"createReload"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"
      )}
   )
   private ReloadInstance moonlight$clientDynamicPackEarlyReload(
      ResourceManager resourceManager,
      List<PreparableReloadListener> listeners,
      Executor backgroundExecutor,
      Executor gameExecutor,
      CompletableFuture<Unit> alsoWaitedFor,
      boolean profiled,
      Operation<ReloadInstance> original
   ) {
      return !(this.resources instanceof FilteredResManager)
            && this.resources.getResource(ResourceLocation.parse("moonlight:moonlight/token.json")).isPresent()
            && !PlatHelper.isInitializing()
         ? ReloadInstanceWrapper.wrap(
            () -> (ReloadInstance)original.call(
               new Object[]{resourceManager, List.copyOf(listeners), backgroundExecutor, gameExecutor, alsoWaitedFor, profiled}
            ),
            this.type,
            this.resources,
            backgroundExecutor,
            gameExecutor
         )
         : (ReloadInstance)original.call(new Object[]{resourceManager, listeners, backgroundExecutor, gameExecutor, alsoWaitedFor, profiled});
   }
}
