package net.diebuddies.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.opengl.FBO;
import net.diebuddies.opengl.Texture;
import net.diebuddies.opengl.VAO;
import org.joml.Vector2f;

public class GaussianDepthBlurEffect {
   private GaussianDepthBlurShader shader;
   private FBO firstBlur;
   private FBO image;
   private double[] gaussianKernel;
   private Vector2f vertical = new Vector2f(0.0F, 1.0F);
   private Vector2f horizontal = new Vector2f(1.0F, 0.0F);

   public GaussianDepthBlurEffect(double sigma, int kernelSize) {
      this.gaussianKernel = new double[kernelSize / 2 + 1];
      double sum = 0.0;
      double eulerPart = 1.0 / (6.283185307179586 * sigma * sigma);

      for (int i = 0; i < this.gaussianKernel.length; i++) {
         this.gaussianKernel[i] = eulerPart * Math.exp(-(i * i / (2.0 * sigma * sigma)));
         sum += this.gaussianKernel[i] * (i != 0 ? 2 : 1);
      }

      for (int i = 0; i < this.gaussianKernel.length; i++) {
         this.gaussianKernel[i] = this.gaussianKernel[i] / sum;
      }

      this.shader = new GaussianDepthBlurShader(this.gaussianKernel.length);
   }

   public void render(VAO emptyVAO) {
      RenderSystem.disableDepthTest();
      this.firstBlur.bind();
      this.shader.bind();
      this.shader.uploadTexelSize(new Vector2f(1.0F / this.image.getTexture().getWidth(), 1.0F / this.image.getTexture().getWidth()));
      this.shader.uploadKernel(this.gaussianKernel);
      this.shader.uploadOffset(this.vertical);
      this.shader.uploadImage(this.image.getTexture().getID());
      emptyVAO.renderEmptyTriangle();
      this.image.bind();
      this.shader.uploadOffset(this.horizontal);
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
