package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.Shader;
import org.joml.Matrix4f;

public class EmptyTextureShader extends Shader {
   public static final String VERTEX_SHADER = "/assets/physicsmod/shaders/core/empty.vsh";
   public static final String FRAGMENT_SHADER = "/assets/physicsmod/shaders/core/empty.fsh";

   public EmptyTextureShader() {
      super("/assets/physicsmod/shaders/core/empty.vsh", "/assets/physicsmod/shaders/core/empty.fsh");
   }

   public void uploadTexture(int textureID) {
      this.setUniform1(this.getUniformLocation("diffuseMap"), 0);
      RenderSystem.setShaderTexture(0, textureID);
      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(textureID);
   }

   public void uploadInvProjectionMatrix(Matrix4f invProjectionMatrix) {
      this.uploadMatrix(this.getUniformLocation("invProjectionMatrix"), invProjectionMatrix);
   }

   public void uploadInvViewMatrix(Matrix4f invViewMatrix) {
      this.uploadMatrix(this.getUniformLocation("invViewMatrix"), invViewMatrix);
   }
}
