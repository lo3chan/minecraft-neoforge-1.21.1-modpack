package dev.corgitaco.enhancedcelestials2core.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2core.client.ECWorldRenderer;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class WorldRendererMixin {
   @Inject(
      method = {"renderSky"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;getMoonPhase()I"
      )}
   )
   private void changeMoonColor(
      Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci
   ) {
      ECWorldRenderer.changeMoonColor(partialTick);
   }

   @WrapOperation(
      method = {"renderSky"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
         ordinal = 1
      )}
   )
   private void bindCustomMoonTexture(int moonTextureId, ResourceLocation moonLocation, Operation<Void> original) {
      ClientLevel level = Minecraft.getInstance().level;
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(level);
      if (lunarForecastWorldData.isEmpty()) {
         original.call(new Object[]{moonTextureId, moonLocation});
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         RenderSystem.setShaderTexture(moonTextureId, data.currentLunarEvent().getMoonTextureLocation());
      }
   }

   @ModifyExpressionValue(
      method = {"renderSky"},
      at = {@At(
         value = "CONSTANT",
         args = {"floatValue=20.0"}
      )}
   )
   private float getSuperMoonSize(float original) {
      ClientLevel level = Minecraft.getInstance().level;
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(level);
      if (lunarForecastWorldData.isEmpty()) {
         return original;
      } else {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         float naturalSize = Mth.clampedLerp(
            data.lastLunarEvent().getMoonSize(), ((LunarEvent)data.currentLunarEventOrDefault().value()).getMoonSize(), data.getBlend()
         );
         float rainLevel = level.getRainLevel(1.0F);
         return Mth.clampedLerp(naturalSize, data.currentLunarEvent().getMoonSize(), rainLevel);
      }
   }
}
