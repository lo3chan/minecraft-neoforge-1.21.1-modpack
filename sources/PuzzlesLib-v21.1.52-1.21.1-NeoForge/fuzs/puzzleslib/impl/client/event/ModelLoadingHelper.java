package fuzs.puzzleslib.impl.client.event;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

public final class ModelLoadingHelper {
   private static WeakReference<BlockStateModelLoader> modelLoaderReference = new WeakReference<>(null);

   private ModelLoadingHelper() {
   }

   public static void setModelLoader(BlockStateModelLoader modelLoader) {
      modelLoaderReference = new WeakReference<>(modelLoader);
   }

   public static Function<ModelResourceLocation, UnbakedModel> getUnbakedTopLevelModel(ModelBakery modelBakery) {
      return modelResourceLocation -> getUnbakedTopLevelModel(modelBakery, modelResourceLocation);
   }

   private static UnbakedModel getUnbakedTopLevelModel(ModelBakery modelBakery, ModelResourceLocation modelResourceLocation) {
      UnbakedModel unbakedModel = (UnbakedModel)modelBakery.topLevelModels.get(modelResourceLocation);
      BlockStateModelLoader modelLoader = modelLoaderReference.get();
      if (unbakedModel == null && modelLoader != null) {
         unbakedModel = loadUnbakedBlockStateModel(modelBakery, modelLoader, modelResourceLocation);
      } else if (unbakedModel != null) {
         unbakedModel.resolveParents(modelBakery::getModel);
      }

      return unbakedModel == null ? (UnbakedModel)modelBakery.topLevelModels.get(ModelBakery.MISSING_MODEL_VARIANT) : unbakedModel;
   }

   @Nullable
   public static UnbakedModel loadUnbakedBlockStateModel(
      ModelBakery modelBakery, BlockStateModelLoader blockStateModelLoader, ModelResourceLocation modelResourceLocation
   ) {
      return BuiltInRegistries.BLOCK.getOptional(modelResourceLocation.id()).map(block -> {
         blockStateModelLoader.loadBlockStateDefinitions(modelResourceLocation.id(), block.getStateDefinition());
         UnbakedModel unbakedModel = (UnbakedModel)modelBakery.topLevelModels.get(modelResourceLocation);
         if (unbakedModel != null) {
            unbakedModel.resolveParents(modelBakery::getModel);
         }

         return unbakedModel;
      }).orElse(null);
   }
}
