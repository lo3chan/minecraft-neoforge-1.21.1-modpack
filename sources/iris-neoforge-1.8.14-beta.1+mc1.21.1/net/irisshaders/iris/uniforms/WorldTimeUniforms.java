package net.irisshaders.iris.uniforms;

import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.DimensionId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class WorldTimeUniforms {
   private WorldTimeUniforms() {
   }

   public static void addWorldTimeUniforms(UniformHolder uniforms) {
      uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "worldTime", WorldTimeUniforms::getWorldDayTime)
         .uniform1i(UniformUpdateFrequency.PER_TICK, "worldDay", WorldTimeUniforms::getWorldDay)
         .uniform1i(UniformUpdateFrequency.PER_TICK, "moonPhase", () -> getWorld().getMoonPhase());
   }

   static int getWorldDayTime() {
      long timeOfDay = getWorld().getDayTime();
      if (Iris.getCurrentDimension() != DimensionId.END && Iris.getCurrentDimension() != DimensionId.NETHER) {
         long dayTime = getWorld().dimensionType().fixedTime().orElse(timeOfDay % 24000L);
         return (int)dayTime;
      } else {
         return (int)(timeOfDay % 24000L);
      }
   }

   private static int getWorldDay() {
      long timeOfDay = getWorld().getDayTime();
      long day = timeOfDay / 24000L;
      return (int)day;
   }

   private static ClientLevel getWorld() {
      return Objects.requireNonNull(Minecraft.getInstance().level);
   }
}
