package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import net.mehvahdjukaar.moonlight.api.events.EarlyPackReloadEvent;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.pack.DynamicResourcesInternals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
public abstract class DynResourceGenerator<T extends DynamicResourcePack> {
   private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
   public final T dynamicPack;
   protected final String modId;
   private boolean hasBeenInitialized;

   protected DynResourceGenerator(T pack, String modId) {
      this.dynamicPack = pack;
      this.modId = modId;
      this.dynamicPack.addNamespaces(this.additionalNamespaces().toArray(new String[0]));
      this.dynamicPack.addNamespaces(new String[]{modId});
      this.dynamicPack.registerPack();
   }

   public final void register() {
      DynamicResourcesInternals.addGenerator(this);
   }

   public abstract Logger getLogger();

   public Collection<String> additionalNamespaces() {
      return List.of();
   }

   public T getPack() {
      return this.dynamicPack;
   }

   public String getModId() {
      return this.modId;
   }

   public boolean shouldClearOnReload() {
      return this.runsOnEveryReload();
   }

   public boolean runsOnEveryReload() {
      return true;
   }

   public boolean generateDebugResources() {
      return PlatHelper.isDev();
   }

   private void regenerateDynamicAssets(ResourceManager manager, IProgressTracker progressTracker) {
      ArrayList<ResourceGenTask> genTasks = new ArrayList<>();
      Stopwatch watch = Stopwatch.createStarted();

      try {
         this.regenerateDynamicAssets(manager);
      } catch (Exception var13) {
         this.getLogger().error("Legacy dynamic gen task failed: ", var13);
      }

      try {
         this.regenerateDynamicAssets(genTasks::add);
      } catch (Exception var12) {
         this.getLogger().error("Failed to add tasks to dynamic resource gen: ", var12);
      }

      int totalTasks = genTasks.size();
      IProgressTracker.Task reporter = progressTracker.subtask(totalTasks);
      List<CompletableFuture<ResourceSink>> futures = genTasks.stream().map(task -> CompletableFuture.supplyAsync(() -> {
         Object var5x;
         try {
            ResourceSink localSink = new ResourceSink(this.modId, this.dynamicPack.packId());
            task.accept(manager, localSink);
            return localSink;
         } catch (Exception var9) {
            this.getLogger().error("Resource Gen Task failed", var9);
            var5x = null;
         } finally {
            reporter.step();
         }

         return (ResourceSink)var5x;
      }, this.getExecutors())).toList();

      try {
         List<ResourceSink> list = futures.stream().map(CompletableFuture::join).toList();
         ResourceSink.acceptSinks(this.dynamicPack, list);
      } catch (Exception var11) {
         throw new RuntimeException("Task failed", var11);
      }

      boolean hasDebugGen = this.generateDebugResources();
      if (hasDebugGen) {
         DynamicResourcePack var10 = this.dynamicPack;
         if (var10 instanceof IDebugDumpable) {
            var10.dumpToDisk(Paths.get("debug", "generated_resource_pack"));
         }
      }

      this.getLogger()
         .info(
            "Generated runtime {} for pack {} ({}) in: {} ms{} (multithreaded)",
            this.dynamicPack.getPackType(),
            this.dynamicPack.packId(),
            this.modId,
            watch.elapsed().toMillis(),
            hasDebugGen ? " (debug resource dump on)" : ""
         );
   }

   @NotNull
   protected ResourceSink createLocalSink() {
      return new ResourceSink(this.modId, this.dynamicPack.packId());
   }

   @NotNull
   protected ExecutorService getExecutors() {
      return EXECUTOR_SERVICE;
   }

   @Deprecated(
      forRemoval = true
   )
   public void regenerateDynamicAssets(ResourceManager manager) {
   }

   public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
   }

   public final void onEarlyReload(EarlyPackReloadEvent event, IProgressTracker localReporter) {
      if (event.type() == this.dynamicPack.packType) {
         try {
            this.reloadResources(event.manager(), localReporter);
         } catch (Exception var4) {
            Moonlight.LOGGER.error("An error occurred while trying to generate dynamic assets for {}:", this.dynamicPack, var4);
         }
      }
   }

   protected final void reloadResources(ResourceManager manager, IProgressTracker reporter) {
      boolean wasFirstReload = false;
      if (!this.hasBeenInitialized) {
         wasFirstReload = true;
         this.hasBeenInitialized = true;
      }

      if (this.runsOnEveryReload() || wasFirstReload) {
         Moonlight.LOGGER.info("Generating runtime assets for pack {} ({})", this.dynamicPack.packId(), this.modId);
         this.regenerateDynamicAssets(manager, reporter);
      }
   }

   @Nullable
   protected abstract PackRepository getRepository();

   @Deprecated(
      forRemoval = true
   )
   public boolean alreadyHasAssetAtLocation(ResourceManager manager, ResourceLocation res, ResType type) {
      return this.alreadyHasAssetAtLocation(manager, type.getPath(res));
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean alreadyHasAssetAtLocation(ResourceManager manager, ResourceLocation res) {
      Optional<Resource> resource = manager.getResource(res);
      return resource.filter(value -> !value.sourcePackId().equals(this.dynamicPack.packId())).isPresent();
   }

   @Deprecated(
      forRemoval = true
   )
   public void addSimilarJsonResource(ResourceManager manager, StaticResource resource, String keyword, String replaceWith) throws NoSuchElementException {
      this.addSimilarJsonResource(manager, resource, s -> s.replace(keyword, replaceWith));
   }

   @Deprecated(
      forRemoval = true
   )
   public void addSimilarJsonResource(ResourceManager manager, StaticResource resource, Function<String, String> textTransform) throws NoSuchElementException {
      this.addSimilarJsonResource(manager, resource, textTransform, textTransform);
   }

   @Deprecated(
      forRemoval = true
   )
   public void addSimilarJsonResource(
      ResourceManager manager, StaticResource resource, Function<String, String> textTransform, Function<String, String> pathTransform
   ) throws NoSuchElementException {
      ResourceLocation fullPath = resource.location;
      StringBuilder builder = new StringBuilder();
      String[] partial = fullPath.getPath().split("/");

      for (int i = 0; i < partial.length; i++) {
         if (i != 0) {
            builder.append("/");
         }

         if (i == partial.length - 1) {
            builder.append(pathTransform.apply(partial[i]));
         } else {
            builder.append(partial[i]);
         }
      }

      ResourceLocation newRes = ResourceLocation.fromNamespaceAndPath(this.modId, builder.toString());
      if (!this.alreadyHasAssetAtLocation(manager, newRes)) {
         String fullText = resource.asString();
         fullText = textTransform.apply(fullText);
         this.dynamicPack.addResource(newRes, fullText.getBytes());
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public void addResourceIfNotPresent(ResourceManager manager, StaticResource resource) {
      if (!this.alreadyHasAssetAtLocation(manager, resource.location)) {
         this.dynamicPack.addResource(resource);
      }
   }
}
