/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.opengl;

import com.mojang.blaze3d.platform.TextureUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import net.diebuddies.opengl.BufferObject;
import net.diebuddies.opengl.TextureData;
import net.diebuddies.opengl.TextureFilter;
import net.diebuddies.physics.PhysicsMod;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Texture {
    public static final TextureFilter FILTER_LOAD_TEXTURE = new TextureFilter(true, true, 10241, 9987, 10240, 9729, 10242, 33071, 10243, 33071);
    public static final TextureFilter FILTER_LOAD_GUI_TEXTURE = new TextureFilter(false, false, 10241, 9729, 10240, 9729, 10242, 33071, 10243, 33071);
    public static final TextureFilter FILTER_LOAD_GUI_TEXTURE_REPEAT = new TextureFilter(false, false, 10241, 9729, 10240, 9729, 10242, 10497, 10243, 10497);
    public static final TextureFilter FILTER_LOAD_CUBEMAP = new TextureFilter(10240, 9729, 10241, 9729, 10242, 33071, 10243, 33071, 32882, 33071);
    public static final TextureFilter FILTER_LOAD_3D_TEXTURE = new TextureFilter(false, false, 10241, 9729, 10240, 9729, 10242, 10497, 10243, 10497, 32882, 10497);
    public static final TextureFilter FILTER_LOAD_3D_TEXTURE_MIPMAP = new TextureFilter(true, false, 10241, 9987, 10240, 9729, 10242, 10497, 10243, 10497, 32882, 10497);
    public static final TextureFilter FILTER_MINECRAFT_TEXTURE = new TextureFilter(true, false, 10241, 9984, 10240, 9728, 10242, 10497, 10243, 10497);
    public static final TextureFilter FILTER_LOAD_NO_MIPMAP_2D_TEXTURE = new TextureFilter(false, false, 10240, 9729, 10241, 9729, 10242, 10497, 10243, 10497);
    public static final TextureFilter FILTER_LOAD_REPEAT_TEXTURE = new TextureFilter(true, true, 10241, 9987, 10240, 9729, 10242, 10497, 10243, 10497);
    public static final TextureFilter FILTER_CREATE_TEXTURE = new TextureFilter(10241, 9728, 10240, 9728, 10242, 33071, 10243, 33071);
    public static final TextureFilter FILTER_CREATE_LINEAR_TEXTURE = new TextureFilter(10241, 9729, 10240, 9729, 10242, 33071, 10243, 33071);
    public static final TextureFilter FILTER_CREATE_3D_TEXTURE = new TextureFilter(10241, 9729, 10240, 9729, 10242, 10497, 10243, 10497, 32882, 10497);
    public static final TextureFilter FILTER_CREATE_CUBEMAP_TEXTURE = new TextureFilter(10241, 9728, 10240, 9728, 10242, 33071, 10243, 33071, 32882, 33071);
    private static Set<Texture> loadedTextures = new ObjectOpenHashSet();
    private int width;
    private int height;
    private int depth;
    private int format;
    private int internalFormat;
    private int type;
    private int id;
    private int textureType;
    private String path;

    public Texture(int id) {
        this.id = id;
        this.width = 1;
        this.height = 1;
        this.depth = 1;
    }

    public void set(Texture texture) {
        this.width = texture.width;
        this.height = texture.height;
        this.depth = texture.depth;
        this.format = texture.format;
        this.internalFormat = texture.internalFormat;
        this.type = texture.type;
        this.id = texture.id;
        this.textureType = texture.textureType;
        this.path = texture.path;
    }

    public static Texture load(String filename, TextureFilter filter) throws IOException {
        TextureData data = Texture.loadTextureData(filename);
        if (data == null) {
            return null;
        }
        Texture texture = Texture.loadTexture(data, filter);
        data.destroy();
        return texture;
    }

    public static TextureData loadTextureData(String filename) throws IOException {
        if (filename.toLowerCase().endsWith("png")) {
            int height;
            int width;
            ByteBuffer image;
            try (MemoryStack stack = MemoryStack.stackPush();){
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);
                STBImage.stbi_set_flip_vertically_on_load((boolean)true);
                image = STBImage.stbi_load((CharSequence)filename, (IntBuffer)w, (IntBuffer)h, (IntBuffer)comp, (int)4);
                STBImage.stbi_set_flip_vertically_on_load((boolean)false);
                width = w.get();
                height = h.get();
            }
            if (width <= 0 || height <= 0 || image == null) {
                STBImage.stbi_image_free((ByteBuffer)image);
                throw new IOException("Couldn't read image file (" + STBImage.stbi_failure_reason() + "): " + filename);
            }
            return new TextureData(filename, image, 6408, 6408, 5121, width, height);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static TextureData loadTextureData(InputStream stream) throws IOException {
        ByteBuffer src = null;
        try {
            int height;
            int width;
            ByteBuffer image;
            src = TextureUtil.readResource((InputStream)stream).rewind();
            try (MemoryStack stack = MemoryStack.stackPush();){
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);
                image = STBImage.stbi_load_from_memory((ByteBuffer)src, (IntBuffer)w, (IntBuffer)h, (IntBuffer)comp, (int)4);
                width = w.get();
                height = h.get();
            }
            if (width <= 0 || height <= 0 || image == null) {
                STBImage.stbi_image_free((ByteBuffer)image);
                throw new IOException("Couldn't read image file (" + STBImage.stbi_failure_reason() + "): unknown");
            }
            TextureData textureData = new TextureData("unknown", image, 6408, 6408, 5121, width, height);
            return textureData;
        }
        finally {
            MemoryUtil.memFree((Buffer)src);
        }
    }

    public static TextureData load3DTextureData(String filename) throws IOException {
        if (filename.toLowerCase().endsWith("png")) {
            ObjectArrayList image = new ObjectArrayList();
            int width = 1;
            int height = 1;
            int depth = 1;
            for (int i = 0; i < depth; ++i) {
                try (MemoryStack stack = MemoryStack.stackPush();){
                    IntBuffer w = stack.mallocInt(1);
                    IntBuffer h = stack.mallocInt(1);
                    IntBuffer comp = stack.mallocInt(1);
                    filename = Texture.replaceLast(filename, "_" + Integer.toString(i - 1), "_" + Integer.toString(i));
                    image.add(STBImage.stbi_load((CharSequence)filename, (IntBuffer)w, (IntBuffer)h, (IntBuffer)comp, (int)4));
                    width = w.get();
                    height = h.get();
                    depth = width;
                    continue;
                }
            }
            ByteBuffer finalImage = MemoryUtil.memAlloc((int)(width * height * depth * 4));
            for (ByteBuffer bb : image) {
                finalImage.put(bb);
                MemoryUtil.memFree((Buffer)bb);
            }
            finalImage.flip();
            return new TextureData(filename, finalImage, 6408, width, height);
        }
        return null;
    }

    private static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
        }
        return string;
    }

    public static Texture loadTexture(TextureData data, TextureFilter filter) {
        if (filter == null) {
            filter = FILTER_LOAD_TEXTURE;
        }
        int boundBefore = GL32C.glGetInteger((int)32873);
        int activeTexture = GL32C.glGetInteger((int)34016);
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = data.getWidth();
        texture.height = data.getHeight();
        texture.textureType = 3553;
        texture.depth = 1;
        texture.format = data.getFormat();
        texture.internalFormat = data.getInternalFormat();
        texture.type = data.getType();
        texture.path = data.getPath();
        texture.bind();
        ByteBuffer[] mipMaps = data.getBuffer();
        int ps1 = GL32C.glGetInteger((int)3312);
        int ps2 = GL32C.glGetInteger((int)3313);
        int ps3 = GL32C.glGetInteger((int)3314);
        int ps4 = GL32C.glGetInteger((int)3315);
        int ps5 = GL32C.glGetInteger((int)3316);
        int ps6 = GL32C.glGetInteger((int)3317);
        int ps7 = GL32C.glGetInteger((int)32878);
        int ps8 = GL32C.glGetInteger((int)32877);
        int pps1 = GL32C.glGetInteger((int)3328);
        int pps2 = GL32C.glGetInteger((int)3329);
        int pps3 = GL32C.glGetInteger((int)3330);
        int pps4 = GL32C.glGetInteger((int)3331);
        int pps5 = GL32C.glGetInteger((int)3332);
        int pps6 = GL32C.glGetInteger((int)3333);
        int pps7 = GL32C.glGetInteger((int)32876);
        int pps8 = GL32C.glGetInteger((int)32875);
        GL32C.glPixelStorei((int)3312, (int)0);
        GL32C.glPixelStorei((int)3313, (int)0);
        GL32C.glPixelStorei((int)3314, (int)0);
        GL32C.glPixelStorei((int)3315, (int)0);
        GL32C.glPixelStorei((int)3316, (int)0);
        GL32C.glPixelStorei((int)3317, (int)4);
        GL32C.glPixelStorei((int)32878, (int)0);
        GL32C.glPixelStorei((int)32877, (int)0);
        GL32C.glPixelStorei((int)3328, (int)0);
        GL32C.glPixelStorei((int)3329, (int)0);
        GL32C.glPixelStorei((int)3330, (int)0);
        GL32C.glPixelStorei((int)3331, (int)0);
        GL32C.glPixelStorei((int)3332, (int)0);
        GL32C.glPixelStorei((int)3333, (int)4);
        GL32C.glPixelStorei((int)32876, (int)0);
        GL32C.glPixelStorei((int)32875, (int)0);
        if (mipMaps[0] != null && data.getWidth() * data.getHeight() * 4 == mipMaps[0].capacity()) {
            for (int i = 0; i < mipMaps.length; ++i) {
                GL32C.glTexImage2D((int)texture.textureType, (int)i, (int)texture.internalFormat, (int)(data.getWidth() >> i), (int)(data.getHeight() >> i), (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)mipMaps[i]);
            }
        } else {
            System.err.println("Wrong texture data, creating default black texture!");
            GL32C.glTexImage2D((int)texture.textureType, (int)0, (int)texture.internalFormat, (int)data.getWidth(), (int)data.getHeight(), (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)null);
        }
        texture.limitMipMaps(data.getBuffer(), filter);
        texture.addFilter(filter);
        loadedTextures.add(texture);
        Texture.bind(3553, boundBefore, activeTexture - 33984);
        GL32C.glPixelStorei((int)3312, (int)ps1);
        GL32C.glPixelStorei((int)3313, (int)ps2);
        GL32C.glPixelStorei((int)3314, (int)ps3);
        GL32C.glPixelStorei((int)3315, (int)ps4);
        GL32C.glPixelStorei((int)3316, (int)ps5);
        GL32C.glPixelStorei((int)3317, (int)ps6);
        GL32C.glPixelStorei((int)32878, (int)ps7);
        GL32C.glPixelStorei((int)32877, (int)ps8);
        GL32C.glPixelStorei((int)3328, (int)pps1);
        GL32C.glPixelStorei((int)3329, (int)pps2);
        GL32C.glPixelStorei((int)3330, (int)pps3);
        GL32C.glPixelStorei((int)3331, (int)pps4);
        GL32C.glPixelStorei((int)3332, (int)pps5);
        GL32C.glPixelStorei((int)3333, (int)pps6);
        GL32C.glPixelStorei((int)32876, (int)pps7);
        GL32C.glPixelStorei((int)32875, (int)pps8);
        return texture;
    }

    public static Texture createColoredTexture(byte r, byte g, byte b, byte a, TextureFilter filter) {
        if (filter == null) {
            filter = FILTER_LOAD_TEXTURE;
        }
        int boundBefore = GL32C.glGetInteger((int)32873);
        int activeTexture = GL32C.glGetInteger((int)34016);
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = 1;
        texture.height = 1;
        texture.textureType = 3553;
        texture.depth = 1;
        texture.format = 6408;
        texture.internalFormat = 6408;
        texture.type = 5121;
        texture.bind();
        int ps1 = GL32C.glGetInteger((int)3312);
        int ps2 = GL32C.glGetInteger((int)3313);
        int ps3 = GL32C.glGetInteger((int)3314);
        int ps4 = GL32C.glGetInteger((int)3315);
        int ps5 = GL32C.glGetInteger((int)3316);
        int ps6 = GL32C.glGetInteger((int)3317);
        int ps7 = GL32C.glGetInteger((int)32878);
        int ps8 = GL32C.glGetInteger((int)32877);
        int pps1 = GL32C.glGetInteger((int)3328);
        int pps2 = GL32C.glGetInteger((int)3329);
        int pps3 = GL32C.glGetInteger((int)3330);
        int pps4 = GL32C.glGetInteger((int)3331);
        int pps5 = GL32C.glGetInteger((int)3332);
        int pps6 = GL32C.glGetInteger((int)3333);
        int pps7 = GL32C.glGetInteger((int)32876);
        int pps8 = GL32C.glGetInteger((int)32875);
        GL32C.glPixelStorei((int)3312, (int)0);
        GL32C.glPixelStorei((int)3313, (int)0);
        GL32C.glPixelStorei((int)3314, (int)0);
        GL32C.glPixelStorei((int)3315, (int)0);
        GL32C.glPixelStorei((int)3316, (int)0);
        GL32C.glPixelStorei((int)3317, (int)4);
        GL32C.glPixelStorei((int)32878, (int)0);
        GL32C.glPixelStorei((int)32877, (int)0);
        GL32C.glPixelStorei((int)3328, (int)0);
        GL32C.glPixelStorei((int)3329, (int)0);
        GL32C.glPixelStorei((int)3330, (int)0);
        GL32C.glPixelStorei((int)3331, (int)0);
        GL32C.glPixelStorei((int)3332, (int)0);
        GL32C.glPixelStorei((int)3333, (int)4);
        GL32C.glPixelStorei((int)32876, (int)0);
        GL32C.glPixelStorei((int)32875, (int)0);
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buffer = stack.malloc(4);
            buffer.put(0, new byte[]{r, g, b, a});
            GL32C.glTexImage2D((int)texture.textureType, (int)0, (int)texture.internalFormat, (int)1, (int)1, (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)buffer);
        }
        texture.addFilter(filter);
        loadedTextures.add(texture);
        Texture.bind(3553, boundBefore, activeTexture - 33984);
        GL32C.glPixelStorei((int)3312, (int)ps1);
        GL32C.glPixelStorei((int)3313, (int)ps2);
        GL32C.glPixelStorei((int)3314, (int)ps3);
        GL32C.glPixelStorei((int)3315, (int)ps4);
        GL32C.glPixelStorei((int)3316, (int)ps5);
        GL32C.glPixelStorei((int)3317, (int)ps6);
        GL32C.glPixelStorei((int)32878, (int)ps7);
        GL32C.glPixelStorei((int)32877, (int)ps8);
        GL32C.glPixelStorei((int)3328, (int)pps1);
        GL32C.glPixelStorei((int)3329, (int)pps2);
        GL32C.glPixelStorei((int)3330, (int)pps3);
        GL32C.glPixelStorei((int)3331, (int)pps4);
        GL32C.glPixelStorei((int)3332, (int)pps5);
        GL32C.glPixelStorei((int)3333, (int)pps6);
        GL32C.glPixelStorei((int)32876, (int)pps7);
        GL32C.glPixelStorei((int)32875, (int)pps8);
        return texture;
    }

    public static Texture load3DTexture(String filename, TextureFilter filter, int width, int height, int depth) {
        byte[] rawData;
        if (filter == null) {
            filter = FILTER_LOAD_TEXTURE;
        }
        try (InputStream src = PhysicsMod.class.getClassLoader().getResourceAsStream(filename);){
            rawData = src.readAllBytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            rawData = new byte[width * height * depth * 2];
        }
        int boundBefore = GL32C.glGetInteger((int)32873);
        int activeTexture = GL32C.glGetInteger((int)34016);
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.textureType = 32879;
        texture.depth = depth;
        texture.format = 33319;
        texture.internalFormat = 33323;
        texture.type = 5121;
        texture.bind();
        ByteBuffer data = MemoryUtil.memAlloc((int)rawData.length);
        data.put(0, rawData);
        int ps1 = GL32C.glGetInteger((int)3312);
        int ps2 = GL32C.glGetInteger((int)3313);
        int ps3 = GL32C.glGetInteger((int)3314);
        int ps4 = GL32C.glGetInteger((int)3315);
        int ps5 = GL32C.glGetInteger((int)3316);
        int ps6 = GL32C.glGetInteger((int)3317);
        int ps7 = GL32C.glGetInteger((int)32878);
        int ps8 = GL32C.glGetInteger((int)32877);
        int pps1 = GL32C.glGetInteger((int)3328);
        int pps2 = GL32C.glGetInteger((int)3329);
        int pps3 = GL32C.glGetInteger((int)3330);
        int pps4 = GL32C.glGetInteger((int)3331);
        int pps5 = GL32C.glGetInteger((int)3332);
        int pps6 = GL32C.glGetInteger((int)3333);
        int pps7 = GL32C.glGetInteger((int)32876);
        int pps8 = GL32C.glGetInteger((int)32875);
        GL32C.glPixelStorei((int)3312, (int)0);
        GL32C.glPixelStorei((int)3313, (int)0);
        GL32C.glPixelStorei((int)3314, (int)0);
        GL32C.glPixelStorei((int)3315, (int)0);
        GL32C.glPixelStorei((int)3316, (int)0);
        GL32C.glPixelStorei((int)3317, (int)2);
        GL32C.glPixelStorei((int)32878, (int)0);
        GL32C.glPixelStorei((int)32877, (int)0);
        GL32C.glPixelStorei((int)3328, (int)0);
        GL32C.glPixelStorei((int)3329, (int)0);
        GL32C.glPixelStorei((int)3330, (int)0);
        GL32C.glPixelStorei((int)3331, (int)0);
        GL32C.glPixelStorei((int)3332, (int)0);
        GL32C.glPixelStorei((int)3333, (int)2);
        GL32C.glPixelStorei((int)32876, (int)0);
        GL32C.glPixelStorei((int)32875, (int)0);
        GL32C.glTexImage3D((int)texture.textureType, (int)0, (int)texture.internalFormat, (int)texture.getWidth(), (int)texture.getHeight(), (int)texture.getDepth(), (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)data);
        texture.addFilter(filter);
        loadedTextures.add(texture);
        Texture.bind(3553, boundBefore, activeTexture - 33984);
        GL32C.glPixelStorei((int)3312, (int)ps1);
        GL32C.glPixelStorei((int)3313, (int)ps2);
        GL32C.glPixelStorei((int)3314, (int)ps3);
        GL32C.glPixelStorei((int)3315, (int)ps4);
        GL32C.glPixelStorei((int)3316, (int)ps5);
        GL32C.glPixelStorei((int)3317, (int)ps6);
        GL32C.glPixelStorei((int)32878, (int)ps7);
        GL32C.glPixelStorei((int)32877, (int)ps8);
        GL32C.glPixelStorei((int)3328, (int)pps1);
        GL32C.glPixelStorei((int)3329, (int)pps2);
        GL32C.glPixelStorei((int)3330, (int)pps3);
        GL32C.glPixelStorei((int)3331, (int)pps4);
        GL32C.glPixelStorei((int)3332, (int)pps5);
        GL32C.glPixelStorei((int)3333, (int)pps6);
        GL32C.glPixelStorei((int)32876, (int)pps7);
        GL32C.glPixelStorei((int)32875, (int)pps8);
        MemoryUtil.memFree((Buffer)data);
        return texture;
    }

    public static Texture createColoredTexture(byte[] data, int width, int height, TextureFilter filter) {
        if (filter == null) {
            filter = FILTER_LOAD_TEXTURE;
        }
        int boundBefore = GL32C.glGetInteger((int)32873);
        int activeTexture = GL32C.glGetInteger((int)34016);
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.textureType = 3553;
        texture.depth = 1;
        texture.format = 6403;
        texture.internalFormat = 33321;
        texture.type = 5121;
        texture.bind();
        int ps1 = GL32C.glGetInteger((int)3312);
        int ps2 = GL32C.glGetInteger((int)3313);
        int ps3 = GL32C.glGetInteger((int)3314);
        int ps4 = GL32C.glGetInteger((int)3315);
        int ps5 = GL32C.glGetInteger((int)3316);
        int ps6 = GL32C.glGetInteger((int)3317);
        int ps7 = GL32C.glGetInteger((int)32878);
        int ps8 = GL32C.glGetInteger((int)32877);
        int pps1 = GL32C.glGetInteger((int)3328);
        int pps2 = GL32C.glGetInteger((int)3329);
        int pps3 = GL32C.glGetInteger((int)3330);
        int pps4 = GL32C.glGetInteger((int)3331);
        int pps5 = GL32C.glGetInteger((int)3332);
        int pps6 = GL32C.glGetInteger((int)3333);
        int pps7 = GL32C.glGetInteger((int)32876);
        int pps8 = GL32C.glGetInteger((int)32875);
        GL32C.glPixelStorei((int)3312, (int)0);
        GL32C.glPixelStorei((int)3313, (int)0);
        GL32C.glPixelStorei((int)3314, (int)0);
        GL32C.glPixelStorei((int)3315, (int)0);
        GL32C.glPixelStorei((int)3316, (int)0);
        GL32C.glPixelStorei((int)3317, (int)1);
        GL32C.glPixelStorei((int)32878, (int)0);
        GL32C.glPixelStorei((int)32877, (int)0);
        GL32C.glPixelStorei((int)3328, (int)0);
        GL32C.glPixelStorei((int)3329, (int)0);
        GL32C.glPixelStorei((int)3330, (int)0);
        GL32C.glPixelStorei((int)3331, (int)0);
        GL32C.glPixelStorei((int)3332, (int)0);
        GL32C.glPixelStorei((int)3333, (int)1);
        GL32C.glPixelStorei((int)32876, (int)0);
        GL32C.glPixelStorei((int)32875, (int)0);
        ByteBuffer buffer = MemoryUtil.memAlloc((int)data.length);
        buffer.put(0, data);
        GL32C.glTexImage2D((int)texture.textureType, (int)0, (int)texture.internalFormat, (int)width, (int)height, (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)buffer);
        texture.addFilter(filter);
        loadedTextures.add(texture);
        MemoryUtil.memFree((Buffer)buffer);
        Texture.bind(3553, boundBefore, activeTexture - 33984);
        GL32C.glPixelStorei((int)3312, (int)ps1);
        GL32C.glPixelStorei((int)3313, (int)ps2);
        GL32C.glPixelStorei((int)3314, (int)ps3);
        GL32C.glPixelStorei((int)3315, (int)ps4);
        GL32C.glPixelStorei((int)3316, (int)ps5);
        GL32C.glPixelStorei((int)3317, (int)ps6);
        GL32C.glPixelStorei((int)32878, (int)ps7);
        GL32C.glPixelStorei((int)32877, (int)ps8);
        GL32C.glPixelStorei((int)3328, (int)pps1);
        GL32C.glPixelStorei((int)3329, (int)pps2);
        GL32C.glPixelStorei((int)3330, (int)pps3);
        GL32C.glPixelStorei((int)3331, (int)pps4);
        GL32C.glPixelStorei((int)3332, (int)pps5);
        GL32C.glPixelStorei((int)3333, (int)pps6);
        GL32C.glPixelStorei((int)32876, (int)pps7);
        GL32C.glPixelStorei((int)32875, (int)pps8);
        return texture;
    }

    public void updateTexture(byte[] data) {
        int boundBefore = GL32C.glGetInteger((int)32873);
        int activeTexture = GL32C.glGetInteger((int)34016);
        this.bind();
        int ps1 = GL32C.glGetInteger((int)3312);
        int ps2 = GL32C.glGetInteger((int)3313);
        int ps3 = GL32C.glGetInteger((int)3314);
        int ps4 = GL32C.glGetInteger((int)3315);
        int ps5 = GL32C.glGetInteger((int)3316);
        int ps6 = GL32C.glGetInteger((int)3317);
        int ps7 = GL32C.glGetInteger((int)32878);
        int ps8 = GL32C.glGetInteger((int)32877);
        int pps1 = GL32C.glGetInteger((int)3328);
        int pps2 = GL32C.glGetInteger((int)3329);
        int pps3 = GL32C.glGetInteger((int)3330);
        int pps4 = GL32C.glGetInteger((int)3331);
        int pps5 = GL32C.glGetInteger((int)3332);
        int pps6 = GL32C.glGetInteger((int)3333);
        int pps7 = GL32C.glGetInteger((int)32876);
        int pps8 = GL32C.glGetInteger((int)32875);
        GL32C.glPixelStorei((int)3312, (int)0);
        GL32C.glPixelStorei((int)3313, (int)0);
        GL32C.glPixelStorei((int)3314, (int)0);
        GL32C.glPixelStorei((int)3315, (int)0);
        GL32C.glPixelStorei((int)3316, (int)0);
        GL32C.glPixelStorei((int)3317, (int)1);
        GL32C.glPixelStorei((int)32878, (int)0);
        GL32C.glPixelStorei((int)32877, (int)0);
        GL32C.glPixelStorei((int)3328, (int)0);
        GL32C.glPixelStorei((int)3329, (int)0);
        GL32C.glPixelStorei((int)3330, (int)0);
        GL32C.glPixelStorei((int)3331, (int)0);
        GL32C.glPixelStorei((int)3332, (int)0);
        GL32C.glPixelStorei((int)3333, (int)1);
        GL32C.glPixelStorei((int)32876, (int)0);
        GL32C.glPixelStorei((int)32875, (int)0);
        ByteBuffer buffer = MemoryUtil.memAlloc((int)data.length);
        buffer.put(0, data);
        GL32C.glTexImage2D((int)this.textureType, (int)0, (int)this.internalFormat, (int)this.width, (int)this.height, (int)0, (int)this.format, (int)this.type, (ByteBuffer)buffer);
        MemoryUtil.memFree((Buffer)buffer);
        Texture.bind(3553, boundBefore, activeTexture - 33984);
        GL32C.glPixelStorei((int)3312, (int)ps1);
        GL32C.glPixelStorei((int)3313, (int)ps2);
        GL32C.glPixelStorei((int)3314, (int)ps3);
        GL32C.glPixelStorei((int)3315, (int)ps4);
        GL32C.glPixelStorei((int)3316, (int)ps5);
        GL32C.glPixelStorei((int)3317, (int)ps6);
        GL32C.glPixelStorei((int)32878, (int)ps7);
        GL32C.glPixelStorei((int)32877, (int)ps8);
        GL32C.glPixelStorei((int)3328, (int)pps1);
        GL32C.glPixelStorei((int)3329, (int)pps2);
        GL32C.glPixelStorei((int)3330, (int)pps3);
        GL32C.glPixelStorei((int)3331, (int)pps4);
        GL32C.glPixelStorei((int)3332, (int)pps5);
        GL32C.glPixelStorei((int)3333, (int)pps6);
        GL32C.glPixelStorei((int)32876, (int)pps7);
        GL32C.glPixelStorei((int)32875, (int)pps8);
    }

    public static Texture loadCubemapTexture(TextureData[] data, TextureFilter filter) {
        if (filter == null) {
            filter = FILTER_LOAD_CUBEMAP;
        }
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = data[0].getWidth();
        texture.height = data[0].getHeight();
        texture.depth = 1;
        texture.textureType = 34067;
        texture.format = data[0].getFormat();
        texture.internalFormat = data[0].getInternalFormat();
        texture.type = data[0].getType();
        texture.path = data[0].getPath();
        texture.bind();
        for (int i = 0; i < 6; ++i) {
            ByteBuffer[] mipMaps = data[i].getBuffer();
            for (int j = 0; j < mipMaps.length; ++j) {
                GL32C.glTexImage2D((int)(34069 + i), (int)j, (int)data[i].getInternalFormat(), (int)(data[i].getWidth() >> j), (int)(data[i].getHeight() >> j), (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)mipMaps[j]);
            }
        }
        texture.limitMipMaps(data[0].getBuffer(), filter);
        texture.addFilter(filter);
        return texture;
    }

    private void limitMipMaps(ByteBuffer[] data, TextureFilter filter) {
        int mipMaps;
        if (!filter.generateMipMaps && data != null && (mipMaps = data.length) > 1) {
            this.addFilter(33085, mipMaps - 1);
        }
    }

    public static Texture loadCubemapTexture(TextureData[] data) {
        return Texture.loadCubemapTexture(data, null);
    }

    public static Texture load3DTexture(TextureData data, TextureFilter filter) throws IOException {
        if (filter == null) {
            filter = FILTER_LOAD_3D_TEXTURE;
        }
        int height = data.getHeight();
        Texture texture = Texture.create3DTexture(height, height, height, data.internalFormat, data.format, data.type, data.getBuffer(), filter);
        return texture;
    }

    public static Texture load3DTexture(TextureData data) throws IOException {
        return Texture.load3DTexture(data, null);
    }

    public static Texture createTexture(int width, int height) {
        return Texture.createTexture(width, height, null);
    }

    public static Texture createTexture(int width, int height, ByteBuffer byteBuffer) {
        return Texture.createTexture(width, height, 32856, 6408, 5121, byteBuffer, null);
    }

    public static Texture createTexture(int width, int height, int internalFormat, int format, int type) {
        return Texture.createTexture(width, height, internalFormat, format, type, null);
    }

    public static Texture createTexture(int width, int height, int internalFormat, int format, int type, TextureFilter filter) {
        return Texture.createTexture(width, height, internalFormat, format, type, (ByteBuffer[])null, filter);
    }

    public static Texture createTexture(int width, int height, int internalFormat, int format, int type, ByteBuffer data, TextureFilter filter) {
        if (data == null) {
            return Texture.createTexture(width, height, internalFormat, format, type, (ByteBuffer[])null, filter);
        }
        return Texture.createTexture(width, height, internalFormat, format, type, new ByteBuffer[]{data}, filter);
    }

    public static Texture createTexture(int width, int height, int internalFormat, int format, int type, ByteBuffer[] data, TextureFilter filter) {
        if (filter == null) {
            filter = FILTER_CREATE_TEXTURE;
        }
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.depth = 1;
        texture.textureType = 3553;
        texture.internalFormat = internalFormat;
        texture.format = format;
        texture.type = type;
        texture.bind();
        if (data == null) {
            GL32C.glTexImage2D((int)texture.textureType, (int)0, (int)internalFormat, (int)width, (int)height, (int)0, (int)format, (int)type, (ByteBuffer)null);
        } else {
            int i = 0;
            for (ByteBuffer buffer : data) {
                GL32C.glTexImage2D((int)texture.textureType, (int)i, (int)internalFormat, (int)(width >> i), (int)(height >> i), (int)0, (int)format, (int)type, (ByteBuffer)buffer);
                ++i;
            }
        }
        texture.limitMipMaps(data, filter);
        texture.addFilter(filter);
        loadedTextures.add(texture);
        return texture;
    }

    public static Texture createMultisampleTexture(int width, int height, int samples) {
        return Texture.createMultisampleTexture(width, height, 6408, samples);
    }

    public static Texture createMultisampleTexture(int width, int height, int internalFormat, int samples) {
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.depth = 1;
        texture.textureType = 37120;
        texture.internalFormat = internalFormat;
        texture.format = internalFormat;
        texture.type = 5121;
        texture.bind();
        GL32C.glTexImage2DMultisample((int)texture.textureType, (int)samples, (int)internalFormat, (int)width, (int)height, (boolean)true);
        return texture;
    }

    public static Texture createCubemapTexture(int width, int height) {
        return Texture.createCubemapTexture(width, height, 32856, 6408, 5121);
    }

    public static Texture createCubemapTexture(int width, int height, int internalformat, int format, int type) {
        return Texture.createCubemapTexture(width, height, internalformat, format, type, FILTER_CREATE_CUBEMAP_TEXTURE);
    }

    public static Texture createCubemapTexture(int width, int height, int internalformat, int format, int type, TextureFilter filter) {
        return Texture.createCubemapTexture(width, height, internalformat, format, type, null, filter);
    }

    public static Texture createCubemapTexture(int width, int height, int internalFormat, int format, int type, ByteBuffer[] data, TextureFilter filter) {
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.depth = 1;
        texture.textureType = 34067;
        texture.format = format;
        texture.internalFormat = internalFormat;
        texture.type = type;
        texture.bind();
        for (int i = 0; i < 6; ++i) {
            if (data == null) {
                GL32C.glTexImage2D((int)(34069 + i), (int)0, (int)internalFormat, (int)texture.width, (int)texture.height, (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)null);
                continue;
            }
            int j = 0;
            for (ByteBuffer buffer : data) {
                GL32C.glTexImage2D((int)(34069 + i), (int)j, (int)internalFormat, (int)(texture.width >> j), (int)(texture.height >> j), (int)0, (int)texture.format, (int)texture.type, (ByteBuffer)buffer);
                ++j;
            }
        }
        texture.limitMipMaps(data, filter);
        texture.addFilter(filter);
        return texture;
    }

    private void addFilter(TextureFilter filter) {
        if (filter.generateMipMaps) {
            GL32C.glGenerateMipmap((int)this.textureType);
        }
        if (filter.anisotropic && GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            float amount = GL32C.glGetFloat((int)34047);
            this.addFilter(34046, amount);
        }
        for (int i = 0; i < filter.options.length / 2; ++i) {
            this.addFilter(filter.options[i * 2], filter.options[i * 2 + 1]);
        }
    }

    public void addFilter(int filter, int option) {
        this.bind();
        GL32C.glTexParameteri((int)this.textureType, (int)filter, (int)option);
    }

    public void addFilter(int filter, int[] option) {
        this.bind();
        GL32C.glTexParameteriv((int)this.textureType, (int)filter, (int[])option);
    }

    public void addFilter(int filter, float option) {
        this.bind();
        GL32C.glTexParameterf((int)this.textureType, (int)filter, (float)option);
    }

    public void addFilter(int filter, float[] option) {
        this.bind();
        GL32C.glTexParameterfv((int)this.textureType, (int)filter, (float[])option);
    }

    public static Texture create3DTexture(int width, int height, int depth, int internalformat, int format, int type, ByteBuffer[] data) {
        return Texture.create3DTexture(width, height, depth, internalformat, format, type, data, FILTER_CREATE_3D_TEXTURE);
    }

    public static Texture create3DTexture(int width, int height, int depth, int internalFormat, int format, int type, ByteBuffer[] data, TextureFilter filter) {
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.width = width;
        texture.height = height;
        texture.depth = depth;
        texture.textureType = 32879;
        texture.format = format;
        texture.internalFormat = internalFormat;
        texture.type = type;
        texture.bind();
        if (data == null) {
            GL32C.glTexImage3D((int)texture.textureType, (int)0, (int)internalFormat, (int)width, (int)height, (int)depth, (int)0, (int)format, (int)type, (ByteBuffer)null);
        } else {
            int i = 0;
            for (ByteBuffer buffer : data) {
                GL32C.glTexImage3D((int)texture.textureType, (int)i, (int)internalFormat, (int)(width >> i), (int)(height >> i), (int)(depth >> i), (int)0, (int)format, (int)type, (ByteBuffer)buffer);
                ++i;
            }
        }
        texture.limitMipMaps(data, filter);
        texture.addFilter(filter);
        return texture;
    }

    public static Texture createBufferTexture(BufferObject bufferObjet) {
        Texture texture = new Texture(GL32C.glGenTextures());
        texture.textureType = 35882;
        texture.internalFormat = 34836;
        texture.bind();
        GL32C.glTexBuffer((int)texture.textureType, (int)texture.internalFormat, (int)bufferObjet.id);
        return texture;
    }

    public static void unbind(int textureUnit) {
        Texture.bind(3553, 0, textureUnit);
    }

    public static void unbind() {
        Texture.bind(3553, 0, 0);
    }

    public static void unbindAll() {
        for (int i = 0; i < 16; ++i) {
            Texture.bind(3553, 0, i);
        }
    }

    public void bind() {
        Texture.bind(this.textureType, this.id, 0);
    }

    public void bind(int textureUnit) {
        Texture.bind(this.textureType, this.id, textureUnit);
    }

    private static void bind(int type, int id, int textureUnit) {
        GL32C.glActiveTexture((int)(33984 + textureUnit));
        GL32C.glBindTexture((int)type, (int)id);
    }

    public int getSizePerPixel() {
        int bytesPerValue = 0;
        switch (this.type) {
            case 5120: {
                bytesPerValue = 1;
                break;
            }
            case 5121: {
                bytesPerValue = 1;
                break;
            }
            case 5122: {
                bytesPerValue = 2;
                break;
            }
            case 5123: {
                bytesPerValue = 2;
                break;
            }
            case 5124: {
                bytesPerValue = 4;
                break;
            }
            case 5125: {
                bytesPerValue = 4;
                break;
            }
            case 5131: {
                bytesPerValue = 2;
                break;
            }
            case 5126: {
                bytesPerValue = 4;
                break;
            }
            case 5130: {
                bytesPerValue = 8;
                break;
            }
            case 34042: {
                bytesPerValue = 4;
            }
        }
        int numValues = 0;
        switch (this.format) {
            case 6401: {
                numValues = 1;
                break;
            }
            case 6402: {
                numValues = 1;
                break;
            }
            case 6403: {
                numValues = 1;
                break;
            }
            case 6404: {
                numValues = 1;
                break;
            }
            case 6405: {
                numValues = 1;
                break;
            }
            case 6406: {
                numValues = 1;
                break;
            }
            case 6407: {
                numValues = 3;
                break;
            }
            case 6408: {
                numValues = 4;
                break;
            }
            case 34041: {
                numValues = 2;
            }
        }
        return bytesPerValue * numValues;
    }

    public boolean hasAlpha() {
        return this.format == 6408 || this.format == 6406;
    }

    public int getID() {
        return this.id;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setInternalFormat(int internalFormat) {
        this.internalFormat = internalFormat;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormat() {
        return this.format;
    }

    public int getType() {
        return this.type;
    }

    public int getTextureType() {
        return this.textureType;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getInternalFormat() {
        return this.internalFormat;
    }

    public String getPath() {
        return this.path;
    }

    public void destroy() {
        GL32C.glDeleteTextures((int)this.id);
        loadedTextures.remove(this);
    }

    public static void destroyAll() {
        for (Texture texture : loadedTextures) {
            GL32C.glDeleteTextures((int)texture.id);
        }
        loadedTextures.clear();
    }
}

