/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.CallbackI
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.NativeType
 *  org.lwjgl.system.libffi.FFICIF
 *  org.lwjgl.system.libffi.FFIType
 *  org.lwjgl.system.libffi.LibFFI
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.FFIType;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType(value="MSGBOXCALLBACK")
public interface MsgBoxCallbackI
extends CallbackI {
    public static final FFICIF CIF = APIUtil.apiCreateCIF((int)LibFFI.FFI_DEFAULT_ABI, (FFIType)LibFFI.ffi_type_void, (FFIType[])new FFIType[]{LibFFI.ffi_type_pointer});

    @NotNull
    default public FFICIF getCallInterface() {
        return CIF;
    }

    default public void callback(long ret, long args) {
        this.invoke(MemoryUtil.memGetAddress((long)MemoryUtil.memGetAddress((long)args)));
    }

    public void invoke(@NativeType(value="LPHELPINFO *") long var1);
}

