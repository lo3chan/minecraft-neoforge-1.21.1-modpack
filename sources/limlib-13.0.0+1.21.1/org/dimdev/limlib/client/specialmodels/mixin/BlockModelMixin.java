package org.dimdev.limlib.client.specialmodels.mixin;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.client.specialmodels.SpecialModelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockModel.class})
public class BlockModelMixin implements SpecialModelSource {
   @Unique
   private Map<ResourceLocation, ResourceLocation> corners$specialModels = Map.of();

   @Override
   public Map<ResourceLocation, ResourceLocation> corners$getSpecialModels() {
      return this.corners$specialModels;
   }

   @Override
   public void corners$setSpecialModels(Map<ResourceLocation, ResourceLocation> specialModels) {
      this.corners$specialModels = specialModels.isEmpty() ? Map.of() : Map.copyOf(specialModels);
   }

   @Inject(
      method = {"getDependencies"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void corners$addSpecialModelDependencies(CallbackInfoReturnable<Collection<ResourceLocation>> cir) {
      if (!this.corners$specialModels.isEmpty()) {
         Set<ResourceLocation> dependencies = new LinkedHashSet<>((Collection<? extends ResourceLocation>)cir.getReturnValue());
         dependencies.addAll(this.corners$specialModels.values());
         cir.setReturnValue(Set.copyOf(dependencies));
      }
   }

   @Inject(
      method = {"bake"},
      at = {@At("RETURN")}
   )
   private void corners$applySpecialModelParts(
      ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, CallbackInfoReturnable<BakedModel> cir
   ) {
      SpecialModelLoadingPlugin.applySpecialModelParts((BakedModel)cir.getReturnValue(), this.corners$specialModels, baker, state);
   }
}
