package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import org.joml.Vector2f;

public class BoxDepthBlurEffect {
   private BoxDepthBlurShader shader = new BoxDepthBlurShader();
   private FBO firstBlur;
   private FBO image;

   public void render(VAO emptyVAO) {
      RenderSystem.disableDepthTest();
      this.firstBlur.bind();
      this.shader.bind();
      float m22 = RenderSystem.getProjectionMatrix().m22();
      float m32 = RenderSystem.getProjectionMatrix().m32();
      float near = 2.0F * m32 / (2.0F * m22 - 2.0F);
      float far = (m22 - 1.0F) * near / (m22 + 1.0F);
      this.shader.uploadTexelSize(new Vector2f(1.0F / this.image.getTexture().getWidth(), 1.0F / this.image.getTexture().getWidth()));
      this.shader.uploadNearAndFar(near, far);
      this.shader.uploadImage(this.image.getTexture().getID());
      emptyVAO.renderEmptyTriangle();
      this.image.bind();
      this.shader.uploadImage(this.firstBlur.getTexture().getID());
      emptyVAO.renderEmptyTriangle();
      RenderSystem.enableDepthTest();
   }

   public void setImage(FBO image) {
      if (this.firstBlur == null
         || this.firstBlur.getTexture().getWidth() != image.getTexture().getWidth()
         || this.firstBlur.getTexture().getHeight() != image.getTexture().getHeight()) {
         if (this.firstBlur != null) {
            this.firstBlur.destroy();
         }

         this.firstBlur = new FBO();
         this.firstBlur.attachColorBuffer(Texture.createTexture(image.getTexture().getWidth(), image.getTexture().getHeight(), 33326, 6403, 5126));
      }

      this.image = image;
   }

   public void destroy() {
      this.shader.destroy();
      if (this.firstBlur != null) {
         this.firstBlur.destroy();
      }
   }

   public void resize(int width, int height) {
   }
}
