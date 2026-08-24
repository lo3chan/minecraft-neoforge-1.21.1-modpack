package dev.corgitaco.enhancedcelestials2shaders.render;

import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import dev.corgitaco.enhancedcelestials2shaders.api.LunarEventUtils;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public final class LunarColorRenderer {
   private static final LunarColorRenderer INSTANCE = new LunarColorRenderer();
   private static final float GLOW_PERIOD_SECONDS = 4.0F;
   private static final float MIN_GLOW = 0.7F;
   private static final float MAX_GLOW = 1.0F;
   private static final float MAX_FRAME_DT_SECONDS = 0.1F;
   private final float[] skyColor = new float[]{1.0F, 1.0F, 1.0F};
   private final float[] moonColor = new float[]{1.0F, 1.0F, 1.0F};
   private final float[] glowColor = new float[]{1.0F, 1.0F, 1.0F};
   private float glowIntensity = 1.0F;
   private float blend = 0.0F;
   private boolean active = false;
   private float glowPhase = 0.0F;
   private float glowPulse = 1.0F;
   private long lastFrameNanos = 0L;
   private static final Vector3f WHITE = new Vector3f(1.0F, 1.0F, 1.0F);

   public static LunarColorRenderer getInstance() {
      return INSTANCE;
   }

   private LunarColorRenderer() {
   }

   public void update(LunarForecast forecast) {
      float dt = this.advanceFrameClock();
      if (forecast == null) {
         this.setDefault();
         this.updatePulse(dt);
      } else {
         LunarEvent lastEvent = (LunarEvent)forecast.lastLunarEventHolder().value();
         LunarEvent currentEvent = (LunarEvent)forecast.currentLunarEventOrDefault().value();
         float eventBlend = LunarEventUtils.clampBlend(forecast.getBlend());
         Vector3f lastSky = lastEvent.getSkyLightColor().orElse(WHITE);
         Vector3f currentSky = currentEvent.getSkyLightColor().orElse(WHITE);
         lerpInto(lastSky, currentSky, eventBlend, this.skyColor);
         Vector3f lastMoon = lastEvent.getMoonTextureColor().orElse(WHITE);
         Vector3f currentMoon = currentEvent.getMoonTextureColor().orElse(WHITE);
         lerpInto(lastMoon, currentMoon, eventBlend, this.moonColor);
         Vector3f lastGlow = resolveGlowColor(lastEvent, lastSky);
         Vector3f currentGlow = resolveGlowColor(currentEvent, currentSky);
         lerpInto(lastGlow, currentGlow, eventBlend, this.glowColor);
         this.glowIntensity = Mth.lerp(eventBlend, lastEvent.getGlowIntensity(), currentEvent.getGlowIntensity());
         this.blend = eventBlend;
         this.active = this.blend > 0.01F;
         this.updatePulse(dt);
      }
   }

   public float[] getSkyColor() {
      return LunarEventUtils.copyArray(this.skyColor);
   }

   public float[] getMoonColor() {
      return LunarEventUtils.copyArray(this.moonColor);
   }

   public float[] getGlowColor() {
      return LunarEventUtils.copyArray(this.glowColor);
   }

   public float getBlend() {
      return this.blend;
   }

   public float getGlowIntensity() {
      return this.glowIntensity;
   }

   public float getGlowPulse() {
      return this.glowPulse;
   }

   public boolean isRenderingActive() {
      return this.active;
   }

   public void reset() {
      this.setDefault();
      this.glowPhase = 0.0F;
      this.glowPulse = 1.0F;
      this.lastFrameNanos = 0L;
   }

   private void setDefault() {
      this.skyColor[0] = 1.0F;
      this.skyColor[1] = 1.0F;
      this.skyColor[2] = 1.0F;
      this.moonColor[0] = 1.0F;
      this.moonColor[1] = 1.0F;
      this.moonColor[2] = 1.0F;
      this.glowColor[0] = 1.0F;
      this.glowColor[1] = 1.0F;
      this.glowColor[2] = 1.0F;
      this.glowIntensity = 1.0F;
      this.blend = 0.0F;
      this.active = false;
   }

   private static Vector3f resolveGlowColor(LunarEvent event, Vector3f sky) {
      return event.getGlowColor().orElseGet(() -> new Vector3f(sky).mul(0.3F));
   }

   private static void lerpInto(Vector3f from, Vector3f to, float blend, float[] dest) {
      dest[0] = Mth.lerp(blend, from.x(), to.x());
      dest[1] = Mth.lerp(blend, from.y(), to.y());
      dest[2] = Mth.lerp(blend, from.z(), to.z());
   }

   private float advanceFrameClock() {
      long now = System.nanoTime();
      float dt;
      if (this.lastFrameNanos == 0L) {
         dt = 0.016666668F;
      } else {
         dt = Math.min(0.1F, (float)(now - this.lastFrameNanos) / 1.0E9F);
      }

      this.lastFrameNanos = now;
      return dt;
   }

   private void updatePulse(float dt) {
      if (!this.active) {
         this.glowPulse = 1.0F;
      } else {
         float radiansPerSecond = 1.5707964F;
         this.glowPhase += radiansPerSecond * dt;
         if (this.glowPhase > 6.283185307179586) {
            this.glowPhase -= 6.2831855F;
         }

         float pulse = (float)(Math.sin(this.glowPhase) * 0.5 + 0.5);
         this.glowPulse = Mth.lerp(pulse, 0.7F, 1.0F);
      }
   }

   public String getDebugInfo() {
      return String.format("Blend: %.2f | Active: %b | Sky: [%.2f, %.2f, %.2f]", this.blend, this.active, this.skyColor[0], this.skyColor[1], this.skyColor[2]);
   }
}
