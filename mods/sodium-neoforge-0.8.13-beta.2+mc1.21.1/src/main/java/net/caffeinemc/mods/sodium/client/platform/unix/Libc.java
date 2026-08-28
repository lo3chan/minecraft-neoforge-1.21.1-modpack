/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.FunctionProvider
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.unix;

import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

public class Libc {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"libc.so.6");
    private static final long PFN_setenv = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"setenv");
    private static final long PFN_unsetenv = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"unsetenv");

    public static void setEnvironmentVariable(String name, @Nullable String value) {
        if (value != null) {
            Libc.setenv(name, value);
        } else {
            Libc.unsetenv(name);
        }
    }

    private static void setenv(String name, @NotNull String value) {
        int result;
        try (MemoryStack stack = MemoryStack.stackPush();){
            result = JNI.callPPI((long)MemoryUtil.memAddress((ByteBuffer)stack.UTF8((CharSequence)name)), (long)MemoryUtil.memAddress((ByteBuffer)stack.UTF8((CharSequence)value)), (int)1, (long)PFN_setenv);
        }
        if (result != 0) {
            throw new RuntimeException("setenv() failed: %d".formatted(result));
        }
    }

    private static void unsetenv(@NotNull String name) {
        int result;
        try (MemoryStack stack = MemoryStack.stackPush();){
            result = JNI.callPI((long)MemoryUtil.memAddress((ByteBuffer)stack.UTF8((CharSequence)name)), (long)PFN_unsetenv);
        }
        if (result != 0) {
            throw new RuntimeException("unsetenv() failed: %d".formatted(result));
        }
    }
}

