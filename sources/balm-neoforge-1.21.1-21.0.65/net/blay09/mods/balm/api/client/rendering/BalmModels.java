package net.blay09.mods.balm.api.client.rendering;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.mixin.ModelBakeryAccessor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public interface BalmModels {
   @Deprecated
   DeferredObject<BakedModel> loadModel(ResourceLocation var1);

   DeferredObject<BakedModel> bakeModel(ModelResourceLocation var1, UnbakedModel var2);

   default DeferredObject<BakedModel> loadDynamicModel(
      ModelResourceLocation identifier,
      Set<ModelResourceLocation> models,
      @Nullable Function<BlockState, ModelResourceLocation> modelFunction,
      @Nullable Function<BlockState, Map<String, String>> textureMapFunction,
      @Nullable BiConsumer<BlockState, Matrix4f> transformFunction
   ) {
      return this.loadDynamicModel(identifier, models, modelFunction, textureMapFunction, transformFunction, Collections.emptyList());
   }

   DeferredObject<BakedModel> loadDynamicModel(
      ModelResourceLocation var1,
      Set<ModelResourceLocation> var2,
      @Nullable Function<BlockState, ModelResourceLocation> var3,
      @Nullable Function<BlockState, Map<String, String>> var4,
      @Nullable BiConsumer<BlockState, Matrix4f> var5,
      List<RenderType> var6
   );

   DeferredObject<BakedModel> retexture(ModelResourceLocation var1, Map<String, String> var2);

   void overrideModel(Supplier<Block> var1, Supplier<BakedModel> var2);

   ModelState getModelState(Transformation var1);

   UnbakedModel getUnbakedModelOrMissing(ResourceLocation var1);

   UnbakedModel getUnbakedMissingModel();

   default UnbakedModel retexture(ModelBakery bakery, ModelResourceLocation identifier, Map<String, String> textureMap) {
      Map<String, Either<Material, String>> replacedTexturesMapped = new HashMap<>();

      for (Entry<String, String> entry : textureMap.entrySet()) {
         replacedTexturesMapped.put(entry.getKey(), Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse(entry.getValue()))));
      }

      BlockModel blockModel = new BlockModel(
         identifier.id(), Collections.emptyList(), replacedTexturesMapped, false, GuiLight.FRONT, ItemTransforms.NO_TRANSFORMS, Collections.emptyList()
      );
      blockModel.resolveParents(it -> ((ModelBakeryAccessor)bakery).callGetModel(it));
      return blockModel;
   }

   ModelBaker createBaker(ModelResourceLocation var1, TextureGetter var2);

   @Deprecated
   BalmModels scoped(String var1);
}
