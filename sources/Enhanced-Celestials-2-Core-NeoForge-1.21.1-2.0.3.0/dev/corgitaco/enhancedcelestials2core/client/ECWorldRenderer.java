package dev.corgitaco.enhancedcelestials2core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ECWorldRenderer {
   public static void changeMoonColor(float partialTicks) {
      ClientLevel level = Minecraft.getInstance().level;
      EnhancedCelestials.lunarForecastWorldData(level).ifPresent(data -> {
         float[] vanillaShaderColor = RenderSystem.getShaderColor();
         Vector3f vanillaColor = new Vector3f(vanillaShaderColor[0], vanillaShaderColor[1], vanillaShaderColor[2]);
         Vector3f lastGLColor = ((LunarEvent)data.lastLunarEventHolder().value()).getMoonTextureColor().orElse(vanillaColor);
         Vector3f currentGLColor = ((LunarEvent)data.currentLunarEventOrDefault().value()).getMoonTextureColor().orElse(vanillaColor);
         Vector3f rainTargetGLColor = ((LunarEvent)data.currentLunarEventHolder().value()).getMoonTextureColor().orElse(vanillaColor);
         float blend = data.getBlend();
         float rainLevel = level.getRainLevel(partialTicks);
         float r = Mth.clampedLerp(Mth.clampedLerp(lastGLColor.x(), currentGLColor.x(), blend), rainTargetGLColor.x(), rainLevel);
         float g = Mth.clampedLerp(Mth.clampedLerp(lastGLColor.y(), currentGLColor.y(), blend), rainTargetGLColor.y(), rainLevel);
         float b = Mth.clampedLerp(Mth.clampedLerp(lastGLColor.z(), currentGLColor.z(), blend), rainTargetGLColor.z(), rainLevel);
         RenderSystem.setShaderColor(r, g, b, 1.0F - rainLevel);
      });
   }

   public static void eventLightMap(Vector3f skyVector, float partialTicks) {
      ClientLevel level = Minecraft.getInstance().level;
      EnhancedCelestials.lunarForecastWorldData(level).ifPresent(data -> {
         LunarEvent lastEvent = data.lastLunarEvent();
         LunarEvent currentEvent = (LunarEvent)data.currentLunarEventOrDefault().value();
         LunarEvent rainTargetEvent = data.currentLunarEvent();
         Vector3f vanillaColor = new Vector3f(skyVector.x(), skyVector.y(), skyVector.z());
         Vector3f glSkyLightColor = lastEvent.getSkyLightColor().orElse(vanillaColor);
         Vector3f targetColor = new Vector3f(glSkyLightColor.x(), glSkyLightColor.y(), glSkyLightColor.z());
         float skyDarken = (level.getSkyDarken(1.0F) - 0.2F) / 0.8F;
         float eventBlend = data.getBlend() - skyDarken;
         targetColor.lerp((Vector3fc)currentEvent.getSkyLightColor().orElse(vanillaColor), eventBlend);
         float rainLevel = level.getRainLevel(partialTicks);
         targetColor.lerp((Vector3fc)rainTargetEvent.getSkyLightColor().orElse(vanillaColor), rainLevel);
         float skyBlend = 1.0F - skyDarken - rainLevel;
         skyVector.lerp(targetColor, skyBlend);
      });
   }
}
