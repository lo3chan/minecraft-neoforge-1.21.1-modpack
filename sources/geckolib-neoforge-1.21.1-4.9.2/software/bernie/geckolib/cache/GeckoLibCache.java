package software.bernie.geckolib.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.FileLoader;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.BakedAnimationsAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.CompoundException;

public final class GeckoLibCache {
   private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");
   private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
   private static Map<ResourceLocation, BakedGeoModel> MODELS = Collections.emptyMap();

   public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
      return ANIMATIONS;
   }

   public static Map<ResourceLocation, BakedGeoModel> getBakedModels() {
      return MODELS;
   }

   public static void registerReloadListener() {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.getResourceManager() instanceof ReloadableResourceManager resourceManager) {
         resourceManager.registerReloadListener(GeckoLibCache::reload);
      }
   }

   public static CompletableFuture<Void> reload(
      PreparationBarrier stage,
      ResourceManager resourceManager,
      ProfilerFiller preparationsProfiler,
      ProfilerFiller reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap();
      Map<ResourceLocation, BakedGeoModel> models = new Object2ObjectOpenHashMap();
      return CompletableFuture.allOf(
            loadAnimations(backgroundExecutor, resourceManager, animations::put), loadModels(backgroundExecutor, resourceManager, models::put)
         )
         .<Void>thenCompose(stage::wait)
         .thenAcceptAsync(empty -> {
            ANIMATIONS = animations;
            MODELS = models;
         }, gameExecutor);
   }

   private static CompletableFuture<Void> loadAnimations(
      Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedAnimations> elementConsumer
   ) {
      return CompletableFuture.runAsync(() -> BakedAnimationsAdapter.COMPRESSION_CACHE = new ConcurrentHashMap<>(), backgroundExecutor)
         .thenRunAsync(() -> loadResources(backgroundExecutor, resourceManager, "animations", resource -> {
            try {
               return FileLoader.loadAnimationsFile(resource, resourceManager);
            } catch (CompoundException var3) {
               var3.withMessage(resource.toString() + ": Error loading animation file").printStackTrace();
               return new BakedAnimations(new Object2ObjectOpenHashMap());
            } catch (Exception var4) {
               throw GeckoLibConstants.exception(resource, "Error loading animation file", var4);
            }
         }, elementConsumer))
         .thenRunAsync(() -> BakedAnimationsAdapter.COMPRESSION_CACHE = null);
   }

   private static CompletableFuture<Void> loadModels(
      Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedGeoModel> elementConsumer
   ) {
      return loadResources(
         backgroundExecutor,
         resourceManager,
         "geo",
         resource -> {
            try {
               Model model = FileLoader.loadModelFile(resource, resourceManager);
               switch (model.formatVersion()) {
                  case V_1_12_0:
                     break;
                  case V_1_14_0:
                     GeckoLibConstants.LOGGER.warn("Unsupported geometry json version: 1.14.0 for model {}. This model may not appear as expected", resource);
                     break;
                  case V_1_21_0:
                     GeckoLibConstants.LOGGER
                        .warn(
                           "Unsupported geometry json version: 1.21.0 for model {}. Supported versions: 1.12.0. Remove any rotated face UVs and re-export the model to fix",
                           resource
                        );
                     break;
                  case null:
                  default:
                     GeckoLibConstants.LOGGER.warn("Unsupported geometry json version for model {}. Supported versions: 1.12.0", resource);
               }

               return BakedModelFactory.getForNamespace(resource.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
            } catch (Exception var5) {
               throw GeckoLibConstants.exception(resource, "Error loading model file", var5);
            }
         },
         elementConsumer
      );
   }

   private static <T> CompletableFuture<Void> loadResources(
      Executor executor, ResourceManager resourceManager, String path, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map
   ) {
      return CompletableFuture.<Map>supplyAsync(() -> resourceManager.listResources(path, fileName -> fileName.toString().endsWith(".json")), executor)
         .thenApplyAsync(resources -> {
            Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap();

            for (ResourceLocation resource : resources.keySet()) {
               tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
            }

            return tasks;
         }, executor)
         .thenAcceptAsync(tasks -> {
            for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
               if (!EXCLUDED_NAMESPACES.contains(entry.getKey().getNamespace().toLowerCase(Locale.ROOT))) {
                  map.accept(entry.getKey(), entry.getValue().join());
               }
            }
         }, executor);
   }

   public static synchronized void registerNamespaceExclusion(String namespace) {
      EXCLUDED_NAMESPACES.add(namespace);
   }
}
