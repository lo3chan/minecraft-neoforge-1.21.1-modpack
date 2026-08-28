/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.opengl.DrawBuffer;
import net.diebuddies.opengl.RenderBuffer;
import net.diebuddies.opengl.Texture;
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
        FBO.unbind();
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
        DrawBuffer drawBuffer;
        this.bind();
        int id = 36064 + count;
        GL32C.glFramebufferTexture2D((int)36160, (int)id, (int)textureType, (int)texture.getID(), (int)mipLevel);
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
            GL32C.glFramebufferTexture((int)36160, (int)36096, (int)texture.getID(), (int)0);
        } else {
            GL32C.glFramebufferTexture2D((int)36160, (int)36096, (int)textureType, (int)texture.getID(), (int)0);
        }
        this.depthStencilBuffer = new DrawBuffer(36096, texture);
        return this.depthStencilBuffer;
    }

    public DrawBuffer attachMultisampleDepthBuffer(int width, int height, int samples) {
        this.bind();
        int depthBufferID = GL32C.glGenRenderbuffers();
        GL32C.glBindRenderbuffer((int)36161, (int)depthBufferID);
        GL32C.glRenderbufferStorageMultisample((int)36161, (int)samples, (int)6402, (int)width, (int)height);
        GL32C.glFramebufferRenderbuffer((int)36160, (int)36096, (int)36161, (int)depthBufferID);
        this.depthStencilBuffer = new RenderBuffer(depthBufferID, width, height);
        return this.depthStencilBuffer;
    }

    public DrawBuffer attachDepthStencilBuffer(Texture texture) {
        this.bind();
        GL32C.glFramebufferTexture2D((int)36160, (int)33306, (int)3553, (int)texture.getID(), (int)0);
        this.depthStencilBuffer = new DrawBuffer(33306, texture);
        return this.depthStencilBuffer;
    }

    public void drawBuffers(int ... buffers) {
        if (buffers != null) {
            int[] drawBuffers = new int[this.colorBuffers.size()];
            for (int i = 0; i < buffers.length; ++i) {
                drawBuffers[i] = this.colorBuffers.get(buffers[i]).getID();
            }
            GL32C.glDrawBuffers((int[])drawBuffers);
        } else {
            GL32C.glDrawBuffer((int)0);
        }
    }

    public void checkError() {
        int check = GL32C.glCheckFramebufferStatus((int)36160);
        String error = "Couldn't create the FBO properly: ";
        switch (check) {
            case 36053: {
                break;
            }
            case 33305: {
                throw new RuntimeException(error + "Default framebuffer bound");
            }
            case 36054: {
                throw new RuntimeException(error + "Incomplete attachment");
            }
            case 36055: {
                throw new RuntimeException(error + "Missing attachment");
            }
            case 36059: {
                throw new RuntimeException(error + "Incomplete draw buffer");
            }
            case 36060: {
                throw new RuntimeException(error + "Incomplete read buffer");
            }
            case 36182: {
                throw new RuntimeException(error + "Incomplete multisample");
            }
            case 36264: {
                throw new RuntimeException(error + "Incomplete layer targets");
            }
            case 36061: {
                throw new RuntimeException(error + "Framebuffer objects are not supported");
            }
        }
    }

    public void bind() {
        GL32C.glBindFramebuffer((int)36160, (int)this.id);
    }

    public static void bind(int id) {
        GL32C.glBindFramebuffer((int)36160, (int)id);
    }

    public static void unbind() {
        GL32C.glBindFramebuffer((int)36160, (int)0);
    }

    public void destroy() {
        this.destroy(true);
    }

    public void destroy(boolean deleteTextures) {
        FBO.unbind();
        GL32C.glDeleteFramebuffers((int)this.id);
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
        GL32C.glBindFramebuffer((int)36008, (int)this.getID());
        GL32C.glReadBuffer((int)attachment);
        GL32C.glBindFramebuffer((int)36009, (int)intoFBOid);
        GL32C.glBlitFramebuffer((int)0, (int)0, (int)this.getTexture().getWidth(), (int)this.getTexture().getHeight(), (int)0, (int)0, (int)width, (int)height, (int)mask, (int)filter);
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

