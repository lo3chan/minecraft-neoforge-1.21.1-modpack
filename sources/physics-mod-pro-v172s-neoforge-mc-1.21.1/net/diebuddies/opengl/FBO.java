package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.lwjgl.opengl.GL32C;

public class FBO {
   private List<DrawBuffer> colorBuffers;
   private DrawBuffer depthStencilBuffer;
   private int id = this.createBuffer();

   public FBO() {
      this.colorBuffers = new ObjectArrayList();
   }

   public FBO(int width, int height, boolean depth) {
      this();
      if (depth) {
         this.attachColorBuffer(Texture.createTexture(width, height, 32856, 6408, 5121));
         this.attachDepthBuffer(Texture.createTexture(width, height, 33190, 6402, 5126));
      } else {
         this.attachColorBuffer(Texture.createTexture(width, height, 33321, 6403, 5121, Texture.FILTER_CREATE_LINEAR_TEXTURE));
      }

      this.checkError();
      unbind();
   }

   public FBO(int width, int height) {
      this(width, height, false);
   }

   private int createBuffer() {
      return GL32C.glGenFramebuffers();
   }

   public DrawBuffer attachColorBuffer(Texture texture) {
      return this.attachColorBuffer(texture, texture.getTextureType(), this.colorBuffers.size());
   }

   public DrawBuffer attachColorBuffer(Texture texture, int textureType, int count) {
      return this.attachColorBuffer(texture, textureType, count, 0);
   }

   public DrawBuffer attachColorBuffer(Texture texture, int textureType, int count, int mipLevel) {
      this.bind();
      int id = 36064 + count;
      GL32C.glFramebufferTexture2D(36160, id, textureType, texture.getID(), mipLevel);
      DrawBuffer drawBuffer;
      if (this.colorBuffers.size() < count) {
         drawBuffer = this.colorBuffers.get(count);
         drawBuffer.setTexture(texture);
      } else {
         drawBuffer = new DrawBuffer(id, texture);
         this.colorBuffers.add(drawBuffer);
      }

      return drawBuffer;
   }

   public DrawBuffer attachDepthBuffer(Texture texture) {
      return this.attachDepthBuffer(texture, 3553);
   }

   public DrawBuffer attachDepthBuffer(Texture texture, int textureType) {
      this.bind();
      if (textureType == 34067) {
         GL32C.glFramebufferTexture(36160, 36096, texture.getID(), 0);
      } else {
         GL32C.glFramebufferTexture2D(36160, 36096, textureType, texture.getID(), 0);
      }

      this.depthStencilBuffer = new DrawBuffer(36096, texture);
      return this.depthStencilBuffer;
   }

   public DrawBuffer attachMultisampleDepthBuffer(int width, int height, int samples) {
      this.bind();
      int depthBufferID = GL32C.glGenRenderbuffers();
      GL32C.glBindRenderbuffer(36161, depthBufferID);
      GL32C.glRenderbufferStorageMultisample(36161, samples, 6402, width, height);
      GL32C.glFramebufferRenderbuffer(36160, 36096, 36161, depthBufferID);
      this.depthStencilBuffer = new RenderBuffer(depthBufferID, width, height);
      return this.depthStencilBuffer;
   }

   public DrawBuffer attachDepthStencilBuffer(Texture texture) {
      this.bind();
      GL32C.glFramebufferTexture2D(36160, 33306, 3553, texture.getID(), 0);
      this.depthStencilBuffer = new DrawBuffer(33306, texture);
      return this.depthStencilBuffer;
   }

   public void drawBuffers(int... buffers) {
      if (buffers != null) {
         int[] drawBuffers = new int[this.colorBuffers.size()];

         for (int i = 0; i < buffers.length; i++) {
            drawBuffers[i] = this.colorBuffers.get(buffers[i]).getID();
         }

         GL32C.glDrawBuffers(drawBuffers);
      } else {
         GL32C.glDrawBuffer(0);
      }
   }

   public void checkError() {
      int check = GL32C.glCheckFramebufferStatus(36160);
      String error = "Couldn't create the FBO properly: ";
      switch (check) {
         case 33305:
            throw new RuntimeException(error + "Default framebuffer bound");
         case 36053:
         default:
            return;
         case 36054:
            throw new RuntimeException(error + "Incomplete attachment");
         case 36055:
            throw new RuntimeException(error + "Missing attachment");
         case 36059:
            throw new RuntimeException(error + "Incomplete draw buffer");
         case 36060:
            throw new RuntimeException(error + "Incomplete read buffer");
         case 36061:
            throw new RuntimeException(error + "Framebuffer objects are not supported");
         case 36182:
            throw new RuntimeException(error + "Incomplete multisample");
         case 36264:
            throw new RuntimeException(error + "Incomplete layer targets");
      }
   }

   public void bind() {
      GL32C.glBindFramebuffer(36160, this.id);
   }

   public static void bind(int id) {
      GL32C.glBindFramebuffer(36160, id);
   }

   public static void unbind() {
      GL32C.glBindFramebuffer(36160, 0);
   }

   public void destroy() {
      this.destroy(true);
   }

   public void destroy(boolean deleteTextures) {
      unbind();
      GL32C.glDeleteFramebuffers(this.id);
      if (deleteTextures) {
         for (DrawBuffer buffer : this.colorBuffers) {
            buffer.destroy();
         }

         if (this.depthStencilBuffer != null) {
            this.depthStencilBuffer.destroy();
         }
      }
   }

   public int getID() {
      return this.id;
   }

   public DrawBuffer getDepthBuffer() {
      return this.depthStencilBuffer;
   }

   public List<DrawBuffer> getColorBuffers() {
      return this.colorBuffers;
   }

   private void blit(int attachment, int intoFBOid, int width, int height, int mask, int filter) {
      GL32C.glBindFramebuffer(36008, this.getID());
      GL32C.glReadBuffer(attachment);
      GL32C.glBindFramebuffer(36009, intoFBOid);
      GL32C.glBlitFramebuffer(0, 0, this.getTexture().getWidth(), this.getTexture().getHeight(), 0, 0, width, height, mask, filter);
   }

   public void blitColor(int attachment, int intoFBOid, int width, int height, int filter) {
      this.blit(36064 + attachment, intoFBOid, width, height, 16384, filter);
   }

   public void blitDepth(int intoFBOid, int width, int height, int filter) {
      this.blit(36064, intoFBOid, width, height, 256, filter);
   }

   public Texture getTexture() {
      return this.getColorBuffers().get(0).getTexture();
   }
}
