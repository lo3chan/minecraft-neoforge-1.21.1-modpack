package dev.corgitaco.enhancedcelestials2shaders.render;

public final class LunarEffectCompute {
   public static final float GLOW_THRESHOLD = 0.3F;
   public static final float MIN_GLOW_ALPHA = 0.01F;

   private LunarEffectCompute() {
   }

   public static float[] tintRGBA(float r, float g, float b, float intensity) {
      float colorBlend = Math.min(1.0F, intensity);
      return new float[]{1.0F + (r - 1.0F) * colorBlend, 1.0F + (g - 1.0F) * colorBlend, 1.0F + (b - 1.0F) * colorBlend, 1.0F};
   }

   public static float[] glowRGBA(float intensity, float glowR, float glowG, float glowB, float glowIntensityMul) {
      float alpha = (intensity - 0.3F) * 0.15F * glowIntensityMul;
      return new float[]{glowR, glowG, glowB, alpha};
   }
}
