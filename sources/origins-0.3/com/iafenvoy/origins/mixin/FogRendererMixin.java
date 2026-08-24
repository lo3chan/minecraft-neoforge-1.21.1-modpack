package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCameraSubmersionPower;
import com.iafenvoy.origins.data.power.builtin.regular.NightVisionPower;
import com.iafenvoy.origins.data.power.builtin.regular.PhasingPower;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;

@OnlyIn(Dist.CLIENT)
@Mixin({FogRenderer.class})
public class FogRendererMixin {
   @ModifyVariable(
      method = {"setupColor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;",
         ordinal = 0
      ),
      ordinal = 0
   )
   private static FogType modifyCameraSubmersionTypeRender(FogType original, Camera camera) {
      return ModifyCameraSubmersionPower.tryReplace(camera.getEntity(), original).orElse(original);
   }

   @ModifyVariable(
      method = {"setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;",
         ordinal = 0
      ),
      ordinal = 0
   )
   private static FogType modifyCameraSubmersionTypeFog(
      FogType original, Camera camera, FogMode fogType, float viewDistance, boolean thickFog, float partialTicks
   ) {
      return ModifyCameraSubmersionPower.tryReplace(camera.getEntity(), original).orElse(original);
   }

   @ModifyVariable(
      method = {"setupColor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z",
         ordinal = 0,
         shift = Shift.AFTER
      ),
      ordinal = 2
   )
   private static float modifyFogDensityForPhasingBlindness(float original, Camera camera) {
      return camera.getEntity() instanceof LivingEntity living
            && PhasingPower.hasRenderMethod(living, PhasingPower.PhasingRenderType.BLINDNESS)
            && PhasingPower.getInWallBlockState(living) != null
         ? 0.0F
         : original;
   }

   @ModifyExpressionValue(
      method = {"setupColor"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z",
         ordinal = 0
      )}
   )
   private static boolean hasStatusEffectProxy(boolean original, @Local LivingEntity living) {
      return original || PowerHelper.get(living).anyActive(NightVisionPower.class, x -> true);
   }
}
