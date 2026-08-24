package fuzs.puzzleslib.impl.core.resources;

import fuzs.puzzleslib.impl.PuzzlesLib;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;

public class ForwardingResourceManagerReloadListener extends ForwardingReloadListener<ResourceManagerReloadListener> implements ResourceManagerReloadListener {
   public ForwardingResourceManagerReloadListener(ResourceLocation identifier, Supplier<Collection<ResourceManagerReloadListener>> supplier) {
      super(identifier, supplier);
   }

   @Override
   public CompletableFuture<Void> reload(
      PreparationBarrier preparationBarrier,
      ResourceManager resourceManager,
      ProfilerFiller preparationsProfiler,
      ProfilerFiller reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      return super.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      for (ResourceManagerReloadListener reloadListener : this.reloadListeners()) {
         try {
            reloadListener.onResourceManagerReload(resourceManager);
         } catch (Exception var5) {
            PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), var5);
         }
      }
   }
}
