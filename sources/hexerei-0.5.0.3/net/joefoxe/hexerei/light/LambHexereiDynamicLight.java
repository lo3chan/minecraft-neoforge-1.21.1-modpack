package net.joefoxe.hexerei.light;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;

public interface LambHexereiDynamicLight {
   double getDynamicLightXH();

   double getDynamicLightYH();

   double getDynamicLightZH();

   Level getDynamicLightWorldH();

   default boolean isDynamicLightEnabledH() {
      return LightManager.containsLightSource(this);
   }

   void resetDynamicLightH();

   default void setHexereiDynamicLightEnabled(boolean enabled) {
      this.resetDynamicLightH();
      if (enabled) {
         LightManager.addLightSource(this);
      } else {
         LightManager.removeLightSource(this);
      }
   }

   int getLuminanceH();

   void dynamicLightTickH();

   boolean shouldUpdateDynamicLightH();

   boolean lambdynlights$updateDynamicLightH(LevelRenderer var1);

   void lambdynlights$scheduleTrackedChunksRebuildH(LevelRenderer var1);
}
