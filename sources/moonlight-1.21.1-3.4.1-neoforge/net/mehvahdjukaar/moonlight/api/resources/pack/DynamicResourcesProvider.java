package net.mehvahdjukaar.moonlight.api.resources.pack;

import com.google.common.base.Stopwatch;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.TextHelper;
import net.mehvahdjukaar.moonlight.core.CommonConfigs;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Metadata;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class DynamicResourcesProvider implements SimplePackProvider {
   private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
   private final ResourceLocation name;
   private final PackLocationInfo locationInfo;
   private final PackType packType;
   protected final IEditablePackResources packResources;
   protected final PackGenerationStrategy generationStrategy;
   private volatile boolean needsRegeneration = true;

   public DynamicResourcesProvider(ResourceLocation name, PackType packType, PackGenerationStrategy generationPolicy) {
      this.name = name;
      this.packType = packType;
      this.generationStrategy = generationPolicy;
      this.locationInfo = new PackLocationInfo(
         name.toString(), Component.translatable(TextHelper.getReadableName(name.toString())), PackSource.BUILT_IN, Optional.empty()
      );
      this.packResources = generationPolicy.createPackResources(this.locationInfo, packType);
      this.packResources.addNamespaces(this.gatherSupportedNamespaces().toArray(new String[0]));
      this.packResources.addNamespaces(name.getNamespace());
   }

   public boolean canUseExternalResourcePacks() {
      return false;
   }

   public final IEditablePackResources getPackResources() {
      return this.packResources;
   }

   public final ResourceLocation getName() {
      return this.name;
   }

   public final PackLocationInfo getLocationInfo() {
      return this.locationInfo;
   }

   public final PackType getPackType() {
      return this.packType;
   }

   public PackSelectionConfig createSelectionConfig() {
      return new PackSelectionConfig(true, Position.TOP, false);
   }

   @Override
   public String toString() {
      return "Dynamic " + this.getPackType() + " Resources Provider [" + this.name + "]";
   }

   public final void prepare() {
      this.needsRegeneration = this.needsToRegenerate();
   }

   public boolean needsToRegenerate() {
      if (this.generationStrategy.needsRegeneration(this.packType)) {
         return this.packResources.clearAllResources();
      } else {
         boolean shouldRegenDueToInvalid = !this.packResources.initializeIfValid();
         if (shouldRegenDueToInvalid) {
            Moonlight.LOGGER.info("Cache for {} at {} is invalid or absent, will regenerate", this, this.packResources);
         }

         return shouldRegenDueToInvalid;
      }
   }

   public void reload(ResourceManager manager, IProgressTracker reporter) {
      if (this.needsRegeneration) {
         this.needsRegeneration = false;

         try {
            Moonlight.LOGGER.info("Regenerating {}, requested by strategy {}", this, this.generationStrategy);
            Stopwatch watch = Stopwatch.createStarted();
            this.runGenerationPipeline(manager, reporter);
            Moonlight.LOGGER.info("Generated runtime {} for pack {} in {}", this.getPackType(), this.packResources.packId(), watch);
         } catch (Exception var10) {
            Moonlight.LOGGER.error("An error occurred while trying to generate dynamic assets for {}", this, var10);
         } finally {
            this.packResources.commitChanges();
            if (this.generateDebugResources() && this.packResources instanceof IDebugDumpable d) {
               this.getExecutorService().execute(() -> d.dumpToDisk(Paths.get("debug", "generated_resource_pack")));
            }
         }
      } else {
         Moonlight.LOGGER.info("Skipping regeneration for {} (cache up-to-date)", this);
      }
   }

   private void runGenerationPipeline(ResourceManager manager, IProgressTracker progressTracker) {
      List<ResourceGenTask> genTasks = new ArrayList<>();

      try {
         this.regenerateDynamicAssets(genTasks::add);
      } catch (Exception var10) {
         Moonlight.LOGGER.error("Failed to add tasks to dynamic resource gen: ", var10);
      }

      int totalTasks = genTasks.size();
      IProgressTracker.Task reporter = progressTracker.subtask(totalTasks);
      List<CompletableFuture<ResourceSink>> futures = genTasks.stream().map(task -> CompletableFuture.<ResourceSink>supplyAsync(() -> {
         ResourceSink sink = new ResourceSink(this.name.getNamespace(), this.packResources.packId());
         task.accept(manager, sink);
         return sink;
      }, this.getExecutorService()).handle((sink, ex) -> {
         reporter.step();
         if (ex != null) {
            Moonlight.LOGGER.error("Resource Gen Task failed", ex);
            return null;
         } else {
            return (ResourceSink)sink;
         }
      })).toList();
      List<ResourceSink> successful = futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList();
      if (successful.isEmpty()) {
         Moonlight.LOGGER.warn("No resource sinks produced; all tasks failed or none were scheduled.");
      } else {
         try {
            ResourceSink.acceptSinks(this.packResources, successful);
         } catch (Exception var9) {
            Moonlight.LOGGER.error("Failed to accept generated resource sinks", var9);
         }
      }
   }

   protected Executor getExecutorService() {
      return (Executor)(CommonConfigs.MULTI_THREADED_GENERATION.get() ? EXECUTOR_SERVICE : Runnable::run);
   }

   protected boolean generateDebugResources() {
      return PlatHelper.isDev();
   }

   protected abstract Collection<String> gatherSupportedNamespaces();

   public void addSupportedNamespaces(String... namespace) {
      this.packResources.addNamespaces(namespace);
   }

   protected abstract void regenerateDynamicAssets(Consumer<ResourceGenTask> var1);

   @Override
   public Pack createPack() {
      final IEditablePackResources resources = this.packResources;
      return Pack.readMetaAndCreate(this.getLocationInfo(), new ResourcesSupplier() {
         public PackResources openPrimary(PackLocationInfo location) {
            return resources;
         }

         public PackResources openFull(PackLocationInfo location, Metadata metadata) {
            return resources;
         }
      }, this.getPackType(), this.createSelectionConfig());
   }
}
