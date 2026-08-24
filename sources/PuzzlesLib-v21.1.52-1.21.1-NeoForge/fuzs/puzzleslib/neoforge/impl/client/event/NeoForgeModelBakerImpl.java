package fuzs.puzzleslib.neoforge.impl.client.event;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.client.event.ModelLoadingHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.BakedCacheKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import org.jetbrains.annotations.Nullable;

public record NeoForgeModelBakerImpl(
   Map<BakedCacheKey, BakedModel> bakedCache,
   Function<ResourceLocation, UnbakedModel> unbakedModelGetter,
   Function<ModelResourceLocation, UnbakedModel> unbakedTopLevelGetter,
   Function<Material, TextureAtlasSprite> modelTextureGetter,
   Supplier<BakedModel> missingModel
) implements ModelBaker {
   public static NeoForgeModelBakerImpl create(ModifyBakingResult evt, BakedModel missingModel) {
      return create(evt, missingModel, Collections.emptyMap());
   }

   public static NeoForgeModelBakerImpl create(ModifyBakingResult evt, BakedModel missingModel, Map<ModelResourceLocation, UnbakedModel> models) {
      return new NeoForgeModelBakerImpl(new HashMap<>(), resourceLocation -> {
         if (!models.isEmpty()) {
            ModelResourceLocation modelResourceLocation = ModelResourceLocation.standalone(resourceLocation);
            if (models.containsKey(modelResourceLocation)) {
               return models.get(modelResourceLocation);
            }
         }

         return evt.getModelBakery().getModel(resourceLocation);
      }, ModelLoadingHelper.getUnbakedTopLevelModel(evt.getModelBakery()), evt.getTextureGetter(), () -> missingModel);
   }

   public UnbakedModel getModel(ResourceLocation resourceLocation) {
      return this.unbakedModelGetter.apply(resourceLocation);
   }

   @Nullable
   public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState) {
      return this.bake(resourceLocation, modelState, this.getModelTextureGetter());
   }

   @Nullable
   public UnbakedModel getTopLevelModel(ModelResourceLocation modelLocation) {
      return this.unbakedTopLevelGetter.apply(modelLocation);
   }

   @Nullable
   public BakedModel bake(ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
      return this.bake(this.getModel(resourceLocation), resourceLocation, modelState, modelTextureGetter);
   }

   @Nullable
   public BakedModel bakeUncached(UnbakedModel unbakedModel, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter) {
      return unbakedModel instanceof BlockModel blockModel && blockModel.getRootModel() == ModelBakery.GENERATION_MARKER
         ? ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(modelTextureGetter, blockModel).bake(this, blockModel, modelTextureGetter, modelState, false)
         : unbakedModel.bake(this, modelTextureGetter, modelState);
   }

   public BakedModel bake(UnbakedModel unbakedModel, ResourceLocation resourceLocation) {
      return this.bake(unbakedModel, resourceLocation, BlockModelRotation.X0_Y0, this.getModelTextureGetter());
   }

   private BakedModel bake(
      UnbakedModel unbakedModel, ResourceLocation resourceLocation, ModelState modelState, Function<Material, TextureAtlasSprite> modelTextureGetter
   ) {
      BakedCacheKey key = new BakedCacheKey(resourceLocation, modelState.getRotation(), modelState.isUvLocked());
      BakedModel bakedModel = this.bakedCache.get(key);
      if (bakedModel == null) {
         try {
            bakedModel = this.bakeUncached(unbakedModel, modelState, modelTextureGetter);
         } catch (Exception var8) {
            PuzzlesLib.LOGGER.warn("Unable to bake model: '{}': {}", resourceLocation, var8);
            bakedModel = this.missingModel.get();
         }

         this.bakedCache.put(key, bakedModel);
      }

      return bakedModel;
   }

   public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
      return this.modelTextureGetter;
   }
}
