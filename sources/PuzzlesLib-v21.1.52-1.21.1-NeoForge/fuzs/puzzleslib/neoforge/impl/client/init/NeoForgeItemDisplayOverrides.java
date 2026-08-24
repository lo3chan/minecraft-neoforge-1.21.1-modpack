package fuzs.puzzleslib.neoforge.impl.client.init;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.impl.client.init.ItemDisplayOverridesImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

public final class NeoForgeItemDisplayOverrides extends ItemDisplayOverridesImpl<NeoForgeItemDisplayOverrides.BakedModelKey> {
   @Override
   public void register(ModelResourceLocation itemModel, ModelResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts) {
      Objects.requireNonNull(itemModelOverride, "item model override is null");
      this.register(itemModel, modelResolver -> modelResolver.getModel(itemModelOverride), itemDisplayContexts);
   }

   @Override
   public void register(ModelResourceLocation itemModel, ResourceLocation itemModelOverride, ItemDisplayContext... itemDisplayContexts) {
      Objects.requireNonNull(itemModelOverride, "item model override is null");
      this.register(itemModel, modelResolver -> modelResolver.getModel(ModelResourceLocation.standalone(itemModelOverride)), itemDisplayContexts);
   }

   protected NeoForgeItemDisplayOverrides.BakedModelKey createOverrideModelKey(ModelResourceLocation modelResourceLocation, BakedModel itemModel) {
      return new NeoForgeItemDisplayOverrides.BakedModelKey(modelResourceLocation, itemModel);
   }

   @Override
   protected void registerEventHandlers() {
      NeoForgeModContainerHelper.getOptionalModEventBus("puzzleslib")
         .ifPresent(
            eventBus -> eventBus.addListener(
               evt -> {
                  final BakedModel missingModel = (BakedModel)evt.getModels().get(ModelBakery.MISSING_MODEL_VARIANT);
                  Objects.requireNonNull(missingModel, "missing model is null");
                  Map<NeoForgeItemDisplayOverrides.BakedModelKey, Map<ItemDisplayContext, BakedModel>> overrideModels = this.computeOverrideModels(
                     new ItemDisplayOverridesImpl.BakedModelResolver() {
                        @Override
                        public BakedModel getModel(ModelResourceLocation modelResourceLocation) {
                           return evt.getModels().getOrDefault(modelResourceLocation, missingModel);
                        }

                        @Override
                        public BakedModel getModel(ResourceLocation resourceLocation) {
                           return evt.getModels().getOrDefault(ModelResourceLocation.standalone(resourceLocation), missingModel);
                        }
                     }, missingModel
                  );

                  for (final Entry<NeoForgeItemDisplayOverrides.BakedModelKey, Map<ItemDisplayContext, BakedModel>> entry : overrideModels.entrySet()) {
                     evt.getModels()
                        .put(
                           entry.getKey().modelResourceLocation(),
                           new BakedModelWrapper<BakedModel>(entry.getKey().bakedModel()) {
                              public BakedModel applyTransform(ItemDisplayContext itemDisplayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
                                 return entry.getValue()
                                    .getOrDefault(itemDisplayContext, this.originalModel)
                                    .applyTransform(itemDisplayContext, poseStack, applyLeftHandTransform);
                              }
                           }
                        );
                  }
               }
            )
         );
   }

   protected record BakedModelKey(ModelResourceLocation modelResourceLocation, BakedModel bakedModel) {
   }
}
