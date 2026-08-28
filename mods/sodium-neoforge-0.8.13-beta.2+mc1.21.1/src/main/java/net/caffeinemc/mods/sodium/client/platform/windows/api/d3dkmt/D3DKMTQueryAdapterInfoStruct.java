/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Pointer
 *  org.lwjgl.system.Struct
 *  org.lwjgl.system.Struct$Layout
 *  org.lwjgl.system.Struct$Member
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt;

import java.nio.ByteBuffer;
import net.caffeinemc.mods.sodium.client.platform.windows.api.d3dkmt.D3DKMTAdapterInfoStruct;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.Struct;

class D3DKMTQueryAdapterInfoStruct
extends Struct<D3DKMTAdapterInfoStruct> {
    private static final int SIZEOF;
    private static final int ALIGNOF;
    private static final int OFFSET_ADAPTER_HANDLE;
    private static final int OFFSET_TYPE;
    private static final int OFFSET_DATA_PTR;
    private static final int OFFSET_DATA_SIZE;

    private D3DKMTQueryAdapterInfoStruct(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @NotNull
    protected D3DKMTAdapterInfoStruct create(long address, ByteBuffer container) {
        return new D3DKMTAdapterInfoStruct(address, container);
    }

    public static D3DKMTQueryAdapterInfoStruct malloc(MemoryStack stack) {
        return new D3DKMTQueryAdapterInfoStruct(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public int sizeof() {
        return SIZEOF;
    }

    public void setAdapterHandle(int adapter) {
        MemoryUtil.memPutInt((long)(this.address + (long)OFFSET_ADAPTER_HANDLE), (int)adapter);
    }

    public void setType(int type) {
        MemoryUtil.memPutInt((long)(this.address + (long)OFFSET_TYPE), (int)type);
    }

    public void setDataPointer(long address) {
        MemoryUtil.memPutAddress((long)(this.address + (long)OFFSET_DATA_PTR), (long)address);
    }

    public void setDataLength(int length) {
        MemoryUtil.memPutInt((long)(this.address + (long)OFFSET_DATA_SIZE), (int)length);
    }

    static {
        Struct.Layout layout = D3DKMTQueryAdapterInfoStruct.__struct((Struct.Member[])new Struct.Member[]{D3DKMTQueryAdapterInfoStruct.__member((int)4), D3DKMTQueryAdapterInfoStruct.__member((int)4), D3DKMTQueryAdapterInfoStruct.__member((int)Pointer.POINTER_SIZE), D3DKMTQueryAdapterInfoStruct.__member((int)4)});
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OFFSET_ADAPTER_HANDLE = layout.offsetof(0);
        OFFSET_TYPE = layout.offsetof(1);
        OFFSET_DATA_PTR = layout.offsetof(2);
        OFFSET_DATA_SIZE = layout.offsetof(3);
    }
}

