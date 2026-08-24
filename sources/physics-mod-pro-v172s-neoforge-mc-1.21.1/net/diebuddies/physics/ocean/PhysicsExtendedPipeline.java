package net.diebuddies.physics.ocean;

import net.minecraft.client.renderer.ShaderInstance;

public interface PhysicsExtendedPipeline {
   ShaderInstance physicsmod$getOceanShader();

   ShaderInstance physicsmod$getOceanShadowShader();

   ShaderInstance physicsmod$getLiquidShader();

   ShaderInstance physicsmod$getLiquidShadowShader();

   boolean physicsmod$renderOceanShadow();

   boolean physicsmod$renderLiquidShadow();
}
