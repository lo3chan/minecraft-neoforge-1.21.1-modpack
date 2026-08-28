/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.opengl.GL33C
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.opengl;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import net.diebuddies.opengl.Texture;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

public class SaveTexture {
    public static void save(File file, int openglID) {
        GL32C.glBindTexture((int)3553, (int)openglID);
        int width = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4096);
        int height = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4097);
        ByteBuffer buffer = MemoryUtil.memCalloc((int)(width * height * 4));
        GL32C.glGetTexImage((int)3553, (int)0, (int)6408, (int)5121, (ByteBuffer)buffer);
        try {
            if (file.exists()) {
                file.delete();
            }
            Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath(), new FileAttribute[0]);
            STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)width, (int)height, (int)4, (ByteBuffer)buffer, (int)(width * 4));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        MemoryUtil.memFree((Buffer)buffer);
    }

    public static void saveFramebuffer(File file, int framebufferID) {
        GL32C.glReadBuffer((int)framebufferID);
        int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri((int)36009, (int)framebufferID, (int)36049);
        GL32C.glBindTexture((int)3553, (int)fboTextureID);
        int width = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4096);
        int height = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4097);
        int textureID = GL32C.glGenTextures();
        GL32C.glBindTexture((int)3553, (int)textureID);
        GL32C.glTexImage2D((int)3553, (int)0, (int)6408, (int)width, (int)height, (int)0, (int)6408, (int)5121, (ByteBuffer)null);
        GL32C.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL32C.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL32C.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL32C.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL32C.glCopyTexImage2D((int)3553, (int)0, (int)6408, (int)0, (int)0, (int)width, (int)height, (int)0);
        int format = 6408;
        System.out.println(width + ", " + height + ", " + format);
        ByteBuffer buffer = MemoryUtil.memCalloc((int)(width * height * 4));
        GL32C.glGetTexImage((int)3553, (int)0, (int)format, (int)5121, (ByteBuffer)buffer);
        try {
            if (file.exists()) {
                file.delete();
            }
            Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath(), new FileAttribute[0]);
            STBImageWrite.stbi_flip_vertically_on_write((boolean)true);
            STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)width, (int)height, (int)4, (ByteBuffer)buffer, (int)(width * 4));
            STBImageWrite.stbi_flip_vertically_on_write((boolean)false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        MemoryUtil.memFree((Buffer)buffer);
    }

    public static void saveFramebufferDepth(File file) {
        int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri((int)36009, (int)36096, (int)36049);
        GL32C.glBindTexture((int)3553, (int)fboTextureID);
        int width = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4096);
        int height = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4097);
        int internalFormat = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4099);
        int textureID = GL32C.glGenTextures();
        GL32C.glBindTexture((int)3553, (int)textureID);
        GL32C.glTexImage2D((int)3553, (int)0, (int)internalFormat, (int)width, (int)height, (int)0, (int)6402, (int)5126, (ByteBuffer)null);
        GL32C.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL32C.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL32C.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL32C.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL32C.glCopyTexImage2D((int)3553, (int)0, (int)internalFormat, (int)0, (int)0, (int)width, (int)height, (int)0);
        System.out.println(width + ", " + height + ", " + internalFormat);
        FloatBuffer depthbuffer = MemoryUtil.memCallocFloat((int)(width * height));
        GL32C.glGetTexImage((int)3553, (int)0, (int)internalFormat, (int)5126, (FloatBuffer)depthbuffer);
        ByteBuffer buffer = MemoryUtil.memCalloc((int)(width * height * 4));
        for (int i = 0; i < width * height; ++i) {
            int index = i * 4;
            float val = depthbuffer.get(i);
            buffer.put(index, (byte)(val * 255.0f));
            buffer.put(index + 1, (byte)(val * 255.0f));
            buffer.put(index + 2, (byte)(val * 255.0f));
            buffer.put(index + 3, (byte)-1);
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            Files.createDirectories(file.getAbsoluteFile().getParentFile().toPath(), new FileAttribute[0]);
            STBImageWrite.stbi_flip_vertically_on_write((boolean)true);
            STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)width, (int)height, (int)4, (ByteBuffer)buffer, (int)(width * 4));
            STBImageWrite.stbi_flip_vertically_on_write((boolean)false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        MemoryUtil.memFree((Buffer)depthbuffer);
        MemoryUtil.memFree((Buffer)buffer);
    }

    public static Texture copyFramebufferDepthTexture(Texture texture) {
        int fboTextureID = GL33C.glGetFramebufferAttachmentParameteri((int)36009, (int)36096, (int)36049);
        GL32C.glBindTexture((int)3553, (int)fboTextureID);
        int width = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4096);
        int height = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4097);
        int internalFormat = GL32C.glGetTexLevelParameteri((int)3553, (int)0, (int)4099);
        if (texture == null || texture.getWidth() != width || texture.getHeight() != height || texture.getInternalFormat() != internalFormat) {
            if (texture != null) {
                texture.destroy();
            }
            int textureID = GL32C.glGenTextures();
            if (texture == null) {
                texture = new Texture(textureID);
            }
            GL32C.glBindTexture((int)3553, (int)textureID);
            GL32C.glTexImage2D((int)3553, (int)0, (int)internalFormat, (int)width, (int)height, (int)0, (int)6402, (int)5126, (ByteBuffer)null);
            GL32C.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL32C.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL32C.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL32C.glTexParameteri((int)3553, (int)10243, (int)33071);
            texture.setId(textureID);
            texture.setWidth(width);
            texture.setHeight(height);
            texture.setInternalFormat(internalFormat);
        } else {
            GL32C.glBindTexture((int)3553, (int)texture.getID());
        }
        GL32C.glCopyTexImage2D((int)3553, (int)0, (int)internalFormat, (int)0, (int)0, (int)width, (int)height, (int)0);
        return texture;
    }
}

