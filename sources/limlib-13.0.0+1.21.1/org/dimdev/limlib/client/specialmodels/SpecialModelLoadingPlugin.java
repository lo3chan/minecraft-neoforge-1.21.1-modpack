package org.dimdev.limlib.client.specialmodels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.limlib.client.specialmodels.mixin.MultiPartBakedModelAccessor;
import org.dimdev.limlib.client.specialmodels.mixin.WeightedBakedModelAccessor;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.jetbrains.annotations.Nullable;

public final class SpecialModelLoadingPlugin {
   private static final Logger LOGGER = LogManager.getLogger("LimLib Special Models");
   private static final String MODELS_PATH = "models";
   private static final String MODEL_PATH_SUFFIX = ".json";
   private static final String SPECIAL_MODELS_KEY = "specialmodels";
   private static Function<BakedModel, BakedModel> wrappedModelGetter = model -> null;

   public static void init(LimLibClientSided<?> sided) {
      wrappedModelGetter = sided::getWrappedBakedModel;
      sided.registerSpecialModelLoadingPlugin();
   }

   public static void prepareModelLoading() {
      SpecialModelRenderTypes.clearSpecialModelRenderTypes();
   }

   public static CompletableFuture<Set<ResourceLocation>> loadSpecialModelIds(ResourceManager resourceManager, Executor executor) {
      return CompletableFuture.supplyAsync(() -> scanSpecialModelIds(resourceManager), executor);
   }

   public static Set<ResourceLocation> scanSpecialModelIds(ResourceManager resourceManager) {
      Set<ResourceLocation> specialModelIds = new LinkedHashSet<>();
      Map<ResourceLocation, Resource> modelResources = resourceManager.listResources("models", id -> id.getPath().endsWith(".json"));

      for (Entry<ResourceLocation, Resource> entry : modelResources.entrySet()) {
         try (BufferedReader reader = entry.getValue().openAsReader()) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root.isJsonObject()) {
               JsonObject rootObject = root.getAsJsonObject();
               JsonObject specialModels = GsonHelper.getAsJsonObject(rootObject, "specialmodels", null);
               if (specialModels != null) {
                  for (Entry<String, JsonElement> specialModel : specialModels.entrySet()) {
                     specialModelIds.add(ResourceLocation.parse(GsonHelper.convertToString(specialModel.getValue(), "specialmodels")));
                  }
               }
            }
         } catch (Exception var13) {
            LOGGER.warn("[specialmodels] failed to scan model resource {} for specialmodels", entry.getKey(), var13);
         }
      }

      return Set.copyOf(specialModelIds);
   }

   public static void applySpecialModelParts(BakedModel model, Map<ResourceLocation, ResourceLocation> specialModels, ModelBaker baker, ModelState settings) {
      if (!specialModels.isEmpty() && model instanceof SpecialModelPartsHolder specialModelPartsHolder) {
         specialModelPartsHolder.corners$setSpecialModelParts(bakeSpecialModelParts(specialModels, baker, settings));
      }
   }

   public static List<SpecialModelLoadingPlugin.SpecialModelPart> getSpecialModelParts(BakedModel model, BlockState state, long seed) {
      BakedModel wrappedModel = getWrappedModel(model);
      if (model instanceof SpecialModelPartsHolder specialModelPartsHolder
         && !(model instanceof MultiPartBakedModel)
         && !(model instanceof WeightedBakedModel)
         && wrappedModel == null) {
         return specialModelPartsHolder.corners$getSpecialModelParts();
      } else {
         List<SpecialModelLoadingPlugin.SpecialModelPart> parts = new ArrayList<>();
         collectSpecialModelParts(model, state, seed, parts, newIdentitySet());
         return parts;
      }
   }

   private static void collectSpecialModelParts(
      BakedModel model, BlockState state, long seed, List<SpecialModelLoadingPlugin.SpecialModelPart> parts, Set<BakedModel> seen
   ) {
      if (seen.add(model)) {
         if (model instanceof SpecialModelPartsHolder specialModelPartsHolder) {
            parts.addAll(specialModelPartsHolder.corners$getSpecialModelParts());
         }

         if (model instanceof MultiPartBakedModel multiPartModel) {
            long childSeed = RandomSource.create(seed).nextLong();

            for (Pair<Predicate<BlockState>, BakedModel> selector : ((MultiPartBakedModelAccessor)multiPartModel).corners$getSelectors()) {
               if (((Predicate)selector.getLeft()).test(state)) {
                  collectSpecialModelParts((BakedModel)selector.getRight(), state, childSeed, parts, seen);
               }
            }
         } else if (model instanceof WeightedBakedModel weightedModel) {
            collectWeightedModelParts(weightedModel, state, seed, parts, seen);
         } else {
            BakedModel wrappedModel = getWrappedModel(model);
            if (wrappedModel != null && wrappedModel != model) {
               collectSpecialModelParts(wrappedModel, state, seed, parts, seen);
            }
         }
      }
   }

   @Nullable
   private static BakedModel getWrappedModel(BakedModel model) {
      return wrappedModelGetter.apply(model);
   }

   private static List<SpecialModelLoadingPlugin.SpecialModelPart> bakeSpecialModelParts(
      Map<ResourceLocation, ResourceLocation> specialModels, ModelBaker baker, ModelState settings
   ) {
      List<SpecialModelLoadingPlugin.SpecialModelPart> parts = new ArrayList<>(specialModels.size());

      for (Entry<ResourceLocation, ResourceLocation> entry : specialModels.entrySet()) {
         RenderType renderType = SpecialModelRenderTypes.getOrCreateSpecialModelRenderType(entry.getKey());
         if (renderType != null) {
            BakedModel specialModel = baker.bake(entry.getValue(), settings);
            if (specialModel != null) {
               parts.add(new SpecialModelLoadingPlugin.SpecialModelPart(entry.getKey(), renderType, specialModel));
            }
         }
      }

      return List.copyOf(parts);
   }

   private static void collectWeightedModelParts(
      WeightedBakedModel weightedModel, BlockState state, long seed, List<SpecialModelLoadingPlugin.SpecialModelPart> parts, Set<BakedModel> seen
   ) {
      WeightedBakedModelAccessor accessor = (WeightedBakedModelAccessor)weightedModel;
      List<Wrapper<BakedModel>> weightedModels = accessor.corners$getList();
      int totalWeight = accessor.corners$getTotalWeight();
      if (!weightedModels.isEmpty() && totalWeight > 0) {
         RandomSource random = RandomSource.create(seed);
         int weightIndex = Math.abs((int)random.nextLong()) % totalWeight;
         WeightedRandom.getWeightedItem(weightedModels, weightIndex)
            .<BakedModel>map(Wrapper::data)
            .ifPresent(model -> collectSpecialModelParts(model, state, seed, parts, seen));
      }
   }

   private static Set<BakedModel> newIdentitySet() {
      return Collections.newSetFromMap(new IdentityHashMap<>());
   }

   private SpecialModelLoadingPlugin() {
   }

   public record SpecialModelPart(ResourceLocation rendererId, RenderType renderType, BakedModel model) {
   }
}
