package net.mehvahdjukaar.moonlight.core.integration;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.ShaderRenderingPipeline;

public class IrisCompat {
   public static boolean isIrisShaderStuffActive() {
      return Iris.getPipelineManager().getPipelineNullable() instanceof ShaderRenderingPipeline s && s.shouldOverrideShaders();
   }
}
