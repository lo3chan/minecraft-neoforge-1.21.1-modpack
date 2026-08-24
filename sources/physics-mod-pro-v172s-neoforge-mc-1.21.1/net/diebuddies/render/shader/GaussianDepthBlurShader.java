package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.Replacement;
import net.diebuddies.opengl.Shader;
import org.joml.Vector2f;

public class GaussianDepthBlurShader extends Shader {
   public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/gaussian.vsh";
   public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/gaussian.fsh";
   public String[] kernel;
   public String[] textureOffset;

   public GaussianDepthBlurShader(int kernelSize) {
      super(
         "/assets/physicsmod/shaders/core/gaussian.vsh", "/assets/physicsmod/shaders/core/gaussian.fsh", new Replacement("#SIZE", Integer.toString(kernelSize))
      );
      this.kernel = new String[kernelSize];
      this.textureOffset = new String[kernelSize];

      for (int i = 0; i < kernelSize; i++) {
         this.kernel[i] = "kernel[" + Integer.toString(i) + "]";
         this.textureOffset[i] = "textureOffset[" + Integer.toString(i) + "]";
      }
   }

   public void uploadOffset(Vector2f offset) {
      this.setUniform2(this.getUniformLocation("offset"), offset);
   }

   public void uploadTexelSize(Vector2f texelSize) {
      this.setUniform2(this.getUniformLocation("texelSize"), texelSize);
   }

   public void uploadImage(int textureID) {
      this.setUniform1(this.getUniformLocation("imageMap"), 0);
      RenderSystem.setShaderTexture(0, textureID);
      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(textureID);
   }

   public void uploadKernel(double[] gaussianKernel) {
      for (int i = 0; i < gaussianKernel.length; i++) {
         this.setUniform1(this.getUniformLocation(this.kernel[i]), (float)gaussianKernel[i]);
      }
   }

   public void uploadTextureOffsets(float[] textureOffsets) {
      for (int i = 0; i < textureOffsets.length; i++) {
         this.setUniform1(this.getUniformLocation(this.textureOffset[i]), textureOffsets[i]);
      }
   }
}
