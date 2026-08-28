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
package net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox;

import java.nio.ByteBuffer;
import net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox.MsgBoxCallbackI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.Struct;

public class MsgBoxParamSw
extends Struct<MsgBoxParamSw> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int OFFSET_CB_SIZE;
    public static final int OFFSET_HWND_OWNER;
    public static final int OFFSET_HINSTANCE;
    public static final int OFFSET_LPSZ_TEXT;
    public static final int OFFSET_LPSZ_CAPTION;
    public static final int OFFSET_DW_STYLE;
    public static final int OFFSET_LPSZ_ICON;
    public static final int OFFSET_DW_CONTEXT_HELP_ID;
    public static final int OFFSET_LPFN_MSG_BOX_CALLBACK;
    public static final int OFFSET_DW_LANGUAGE_ID;

    public static MsgBoxParamSw allocate(MemoryStack stack) {
        return new MsgBoxParamSw(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    private MsgBoxParamSw(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @NotNull
    protected MsgBoxParamSw create(long address, ByteBuffer container) {
        return new MsgBoxParamSw(address, container);
    }

    public int sizeof() {
        return SIZEOF;
    }

    public void setCbSize(int size) {
        MemoryUtil.memPutInt((long)(this.address + (long)OFFSET_CB_SIZE), (int)size);
    }

    public void setHWndOwner(long hWnd) {
        MemoryUtil.memPutAddress((long)(this.address + (long)OFFSET_HWND_OWNER), (long)hWnd);
    }

    public void setText(ByteBuffer buffer) {
        MemoryUtil.memPutAddress((long)(this.address + (long)OFFSET_LPSZ_TEXT), (long)MemoryUtil.memAddress((ByteBuffer)buffer));
    }

    public void setCaption(ByteBuffer buffer) {
        MemoryUtil.memPutAddress((long)(this.address + (long)OFFSET_LPSZ_CAPTION), (long)MemoryUtil.memAddress((ByteBuffer)buffer));
    }

    public void setStyle(int style) {
        MemoryUtil.memPutInt((long)(this.address + (long)OFFSET_DW_STYLE), (int)style);
    }

    public void setCallback(@Nullable MsgBoxCallbackI callback) {
        MemoryUtil.memPutAddress((long)(this.address + (long)OFFSET_LPFN_MSG_BOX_CALLBACK), (long)(callback == null ? 0L : callback.address()));
    }

    static {
        Struct.Layout layout = MsgBoxParamSw.__struct((Struct.Member[])new Struct.Member[]{MsgBoxParamSw.__member((int)4), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)4), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)Pointer.POINTER_SIZE), MsgBoxParamSw.__member((int)4)});
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OFFSET_CB_SIZE = layout.offsetof(0);
        OFFSET_HWND_OWNER = layout.offsetof(1);
        OFFSET_HINSTANCE = layout.offsetof(2);
        OFFSET_LPSZ_TEXT = layout.offsetof(3);
        OFFSET_LPSZ_CAPTION = layout.offsetof(4);
        OFFSET_DW_STYLE = layout.offsetof(5);
        OFFSET_LPSZ_ICON = layout.offsetof(6);
        OFFSET_DW_CONTEXT_HELP_ID = layout.offsetof(7);
        OFFSET_LPFN_MSG_BOX_CALLBACK = layout.offsetof(8);
        OFFSET_DW_LANGUAGE_ID = layout.offsetof(9);
    }
}

