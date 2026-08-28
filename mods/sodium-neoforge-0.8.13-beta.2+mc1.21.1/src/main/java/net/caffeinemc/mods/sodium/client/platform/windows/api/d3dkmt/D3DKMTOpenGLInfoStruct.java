/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Struct
 *  org.lwjgl.system.Struct$Layout
 *  org.lwjgl.system.Struct$Member
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;

public class D3DKMTOpenGLInfoStruct
extends Struct<D3DKMTOpenGLInfoStruct> {
    private static final int MAX_PATH = 260;
    private static final int SIZEOF;
    private static final int ALIGNOF;
    private static final int OFFSET_UMD_OPENGL_ICD_FILE_NAME;
    private static final int OFFSET_VERSION;
    private static final int OFFSET_FLAGS;

    private D3DKMTOpenGLInfoStruct(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @NotNull
    protected D3DKMTOpenGLInfoStruct create(long address, ByteBuffer container) {
        return new D3DKMTOpenGLInfoStruct(address, container);
    }

    public static D3DKMTOpenGLInfoStruct calloc() {
        return new D3DKMTOpenGLInfoStruct(MemoryUtil.nmemCalloc((long)1L, (long)SIZEOF), null);
    }

    public static D3DKMTOpenGLInfoStruct calloc(MemoryStack stack) {
        return new D3DKMTOpenGLInfoStruct(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public ByteBuffer getUserModeDriverFileNameBuffer() {
        return MemoryUtil.memByteBuffer((long)(this.address + (long)OFFSET_UMD_OPENGL_ICD_FILE_NAME), (int)520);
    }

    @Nullable
    public String getUserModeDriverFileName() {
        ByteBuffer name = this.getUserModeDriverFileNameBuffer();
        int length = MemoryUtil.memLengthNT2((ByteBuffer)name);
        if (length == 0) {
            return null;
        }
        return MemoryUtil.memUTF16((long)MemoryUtil.memAddress((ByteBuffer)name), (int)(length >> 1));
    }

    public int getVersion() {
        return MemoryUtil.memGetInt((long)(this.address + (long)OFFSET_VERSION));
    }

    public int getFlags() {
        return MemoryUtil.memGetInt((long)(this.address + (long)OFFSET_FLAGS));
    }

    public int sizeof() {
        return SIZEOF;
    }

    static {
        Struct.Layout layout = D3DKMTOpenGLInfoStruct.__struct((Struct.Member[])new Struct.Member[]{D3DKMTOpenGLInfoStruct.__member((int)520, (int)2), D3DKMTOpenGLInfoStruct.__member((int)4), D3DKMTOpenGLInfoStruct.__member((int)4)});
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OFFSET_UMD_OPENGL_ICD_FILE_NAME = layout.offsetof(0);
        OFFSET_VERSION = layout.offsetof(1);
        OFFSET_FLAGS = layout.offsetof(2);
    }
}

