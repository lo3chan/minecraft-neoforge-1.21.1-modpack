package net.blay09.mods.balm.neoforge.client.rendering;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.client.rendering.BalmModels;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.mixin.ModelBakeryAccessor;
import net.blay09.mods.balm.neoforge.ModBusEventRegisters;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.ModelBakery.TextureGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted;
import net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.model.SimpleModelState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.slf4j.Logger;

public record NeoForgeBalmModels(NamespaceResolver namespaceResolver) implements BalmModels {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<NeoForgeBalmModels.DeferredModel> modelsToBake = Collections.synchronizedList(new ArrayList<>());
   private static TextureGetter textureGetter;
   private static ModelBakery modelBakery;

   public static void onBakeModels(ModelBakery modelBakery, TextureGetter textureGetter) {
      NeoForgeBalmModels.modelBakery = modelBakery;
      NeoForgeBalmModels.textureGetter = textureGetter;
      synchronized (modelsToBake) {
         for (NeoForgeBalmModels.DeferredModel deferredModel : modelsToBake) {
            deferredModel.resolveAndSet(modelBakery, modelBakery.getBakedTopLevelModels(), textureGetter);
         }
      }
   }

   @Override
   public DeferredObject<BakedModel> loadModel(ResourceLocation identifier) {
      NeoForgeBalmModels.DeferredModel deferredModel = new NeoForgeBalmModels.DeferredModel(new ModelResourceLocation(identifier, "standalone")) {
         @Override
         public BakedModel resolve(ModelBakery bakery, Map<ModelResourceLocation, BakedModel> modelRegistry, TextureGetter textureGetter) {
            return modelRegistry.get(this.getModelResourceLocation());
         }
      };
      this.getActiveRegistrations().additionalModels.add(deferredModel);
      return deferredModel;
   }

   @Override
   public DeferredObject<BakedModel> bakeModel(final ModelResourceLocation identifier, final UnbakedModel model) {
      NeoForgeBalmModels.DeferredModel deferredModel = new NeoForgeBalmModels.DeferredModel(identifier) {
         @Override
         public BakedModel resolve(ModelBakery bakery, Map<ModelResourceLocation, BakedModel> modelRegistry, TextureGetter textureGetter) {
            ModelBaker baker = NeoForgeBalmModels.this.createBaker(identifier, textureGetter);
            return model.bake(baker, baker.getModelTextureGetter(), NeoForgeBalmModels.this.getModelState(Transformation.identity()));
         }
      };
      modelsToBake.add(deferredModel);
      return deferredModel;
   }

   @Override
   public DeferredObject<BakedModel> retexture(final ModelResourceLocation identifier, final Map<String, String> textureMap) {
      NeoForgeBalmModels.DeferredModel deferredModel = new NeoForgeBalmModels.DeferredModel(identifier) {
         @Override
         public BakedModel resolve(ModelBakery bakery, Map<ModelResourceLocation, BakedModel> modelRegistry, TextureGetter textureGetter) {
            UnbakedModel model = NeoForgeBalmModels.this.retexture(bakery, identifier, textureMap);
            ModelBaker baker = NeoForgeBalmModels.this.createBaker(identifier, textureGetter);
            return model.bake(baker, baker.getModelTextureGetter(), NeoForgeBalmModels.this.getModelState(Transformation.identity()));
         }
      };
      modelsToBake.add(deferredModel);
      return deferredModel;
   }

   @Override
   public DeferredObject<BakedModel> loadDynamicModel(
      final ModelResourceLocation identifier,
      final Set<ModelResourceLocation> models,
      @Nullable Function<BlockState, ModelResourceLocation> modelFunction,
      @Nullable final Function<BlockState, Map<String, String>> textureMapFunction,
      @Nullable final BiConsumer<BlockState, Matrix4f> transformFunction,
      final List<RenderType> renderTypes
   ) {
      final Function<BlockState, ModelResourceLocation> effectiveModelFunction = modelFunction != null ? modelFunction : it -> identifier;
      NeoForgeBalmModels.DeferredModel deferredModel = new NeoForgeBalmModels.DeferredModel(identifier) {
         @Override
         public BakedModel resolve(ModelBakery bakery, Map<ModelResourceLocation, BakedModel> modelRegistry, TextureGetter textureGetter) {
            HashMap<ModelResourceLocation, UnbakedModel> unbakedModels = new HashMap<>();

            for (ModelResourceLocation modelId : models) {
               unbakedModels.put(modelId, ((ModelBakeryAccessor)bakery).callGetModel(modelId.id()));
            }

            return new NeoForgeCachedDynamicModel(
               bakery, unbakedModels, effectiveModelFunction, null, textureMapFunction, transformFunction, renderTypes, identifier, textureGetter
            );
         }
      };
      modelsToBake.add(deferredModel);
      return deferredModel;
   }

   @Override
   public void overrideModel(Supplier<Block> block, Supplier<BakedModel> model) {
      this.getActiveRegistrations().overrides.add(Pair.of(block, model));
   }

   @Override
   public ModelState getModelState(Transformation transformation) {
      return new SimpleModelState(transformation);
   }

   @Override
   public UnbakedModel getUnbakedModelOrMissing(ResourceLocation location) {
      return ((ModelBakeryAccessor)modelBakery).callGetModel(location);
   }

   @Override
   public UnbakedModel getUnbakedMissingModel() {
      return ((ModelBakeryAccessor)modelBakery).callGetModel(ModelBakery.MISSING_MODEL_LOCATION);
   }

   private NeoForgeBalmModels.Registrations getActiveRegistrations() {
      return ModBusEventRegisters.getRegistrations(this.namespaceResolver.getDefaultNamespace(), NeoForgeBalmModels.Registrations.class);
   }

   @Override
   public ModelBaker createBaker(ModelResourceLocation location, TextureGetter textureGetter) {
      try {
         Class<?> clazz = Class.forName("net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl");
         Constructor<?> constructor = clazz.getDeclaredConstructor(ModelBakery.class, TextureGetter.class, ModelResourceLocation.class);
         constructor.setAccessible(true);
         return (ModelBaker)constructor.newInstance(modelBakery, textureGetter, location);
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var5) {
         throw new RuntimeException("Balm failed to create model baker", var5);
      }
   }

   @Override
   public BalmModels scoped(String modId) {
      return new NeoForgeBalmModels(new StaticNamespaceResolver(modId));
   }

   private abstract static class DeferredModel extends DeferredObject<BakedModel> {
      private final ModelResourceLocation modelResourceLocation;

      public DeferredModel(ModelResourceLocation modelResourceLocation) {
         super(modelResourceLocation.id());
         this.modelResourceLocation = modelResourceLocation;
      }

      public void resolveAndSet(ModelBakery modelBakery, Map<ModelResourceLocation, BakedModel> modelRegistry, TextureGetter textureGetter) {
         try {
            this.set(this.resolve(modelBakery, modelRegistry, textureGetter));
         } catch (Exception var5) {
            NeoForgeBalmModels.LOGGER.warn("Unable to bake model: '{}':", this.getIdentifier(), var5);
            this.set((BakedModel)modelBakery.getBakedTopLevelModels().get(ModelBakery.MISSING_MODEL_LOCATION));
         }
      }

      public abstract BakedModel resolve(ModelBakery var1, Map<ModelResourceLocation, BakedModel> var2, TextureGetter var3);

      public ModelResourceLocation getModelResourceLocation() {
         return this.modelResourceLocation;
      }
   }

   public static class Registrations {
      public final List<NeoForgeBalmModels.DeferredModel> additionalModels = new ArrayList<>();
      public final List<Pair<Supplier<Block>, Supplier<BakedModel>>> overrides = new ArrayList<>();

      @SubscribeEvent
      public void onRegisterAdditionalModels(RegisterAdditional event) {
         this.additionalModels.forEach(it -> event.register(it.getModelResourceLocation()));
      }

      @SubscribeEvent
      public void onModelBakingCompleted(ModifyBakingResult event) {
         for (Pair<Supplier<Block>, Supplier<BakedModel>> override : this.overrides) {
            Block block = (Block)((Supplier)override.getFirst()).get();
            BakedModel bakedModel = (BakedModel)((Supplier)override.getSecond()).get();
            block.getStateDefinition().getPossibleStates().forEach(state -> {
               ModelResourceLocation modelLocation = BlockModelShaper.stateToModelLocation(state);
               event.getModels().put(modelLocation, bakedModel);
            });
         }
      }

      @SubscribeEvent
      public void onModelBakingCompleted(BakingCompleted event) {
         for (NeoForgeBalmModels.DeferredModel deferredModel : this.additionalModels) {
            deferredModel.resolveAndSet(event.getModelBakery(), event.getModels(), NeoForgeBalmModels.textureGetter);
         }
      }
   }
}
