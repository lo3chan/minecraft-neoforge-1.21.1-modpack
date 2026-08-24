package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.core.ReloadableServerResourcesKJS;
import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.core.TagManagerKJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess.RegistryEntry;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import net.minecraft.tags.TagManager.LoadResult;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TagManager.class})
public abstract class TagManagerMixin implements TagManagerKJS {
   @Unique
   private ReloadableServerResourcesKJS kjs$resources;

   @Inject(
      method = {"createLoader"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/tags/TagLoader;<init>(Ljava/util/function/Function;Ljava/lang/String;)V",
         shift = Shift.BY,
         by = 2
      )}
   )
   private <T> void kjs$saveRegistryToTagLoader(
      ResourceManager rm,
      Executor executor,
      RegistryEntry<T> reg,
      CallbackInfoReturnable<CompletableFuture<LoadResult<T>>> cir,
      @Local Registry<T> registry,
      @Local TagLoader<Holder<T>> loader
   ) {
      ((TagLoaderKJS)loader).kjs$init(this.kjs$resources, registry);
   }

   @Inject(
      method = {"reload"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/core/RegistryAccess;registries()Ljava/util/stream/Stream;"
      )}
   )
   private void kjs$reload(
      PreparationBarrier preparationBarrier,
      ResourceManager resourceManager,
      ProfilerFiller profilerFiller,
      ProfilerFiller profilerFiller2,
      Executor executor,
      Executor executor2,
      CallbackInfoReturnable<CompletableFuture<Void>> cir
   ) {
      KubeJSPlugins.forEachPlugin(KubeJSPlugin::clearCaches);
   }

   @Override
   public void kjs$setResources(ReloadableServerResourcesKJS resources) {
      this.kjs$resources = resources;
   }

   @Override
   public ReloadableServerResourcesKJS kjs$getResources() {
      return this.kjs$resources;
   }
}
