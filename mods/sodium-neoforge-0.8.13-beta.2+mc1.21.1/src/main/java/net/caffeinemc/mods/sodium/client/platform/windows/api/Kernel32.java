/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.CustomBuffer
 *  org.lwjgl.system.FunctionProvider
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

public class Kernel32 {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"kernel32");
    private static final int MAX_PATH = Short.MAX_VALUE;
    private static final int GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT = 1;
    private static final int GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS = 4;
    private static final long PFN_GetCommandLineW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetCommandLineW");
    private static final long PFN_GetCommandLineA = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetCommandLineA");
    private static final long PFN_SetEnvironmentVariableW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"SetEnvironmentVariableW");
    private static final long PFN_GetModuleHandleExW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetModuleHandleExW");
    private static final long PFN_GetLastError = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetLastError");
    private static final long PFN_GetModuleFileNameW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetModuleFileNameW");

    public static void setEnvironmentVariable(String name, @Nullable String value) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer lpNameBuf = stack.malloc(16, MemoryUtil.memLengthUTF16((CharSequence)name, (boolean)true));
            MemoryUtil.memUTF16((CharSequence)name, (boolean)true, (ByteBuffer)lpNameBuf);
            ByteBuffer lpValueBuf = null;
            if (value != null) {
                lpValueBuf = stack.malloc(16, MemoryUtil.memLengthUTF16((CharSequence)value, (boolean)true));
                MemoryUtil.memUTF16((CharSequence)value, (boolean)true, (ByteBuffer)lpValueBuf);
            }
            JNI.callPPI((long)MemoryUtil.memAddress0((Buffer)lpNameBuf), (long)MemoryUtil.memAddressSafe(lpValueBuf), (long)PFN_SetEnvironmentVariableW);
        }
    }

    public static long getCommandLine() {
        return JNI.callP((long)PFN_GetCommandLineW);
    }

    public static long getCommandLineA() {
        return JNI.callP((long)PFN_GetCommandLineA);
    }

    public static long getModuleHandleByNames(String[] names) {
        for (String name : names) {
            long handle = Kernel32.getModuleHandleByName(name);
            if (handle == 0L) continue;
            return handle;
        }
        throw new RuntimeException("Could not obtain handle of module");
    }

    public static long getModuleHandleByName(String name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer lpFunctionNameBuf = stack.malloc(16, MemoryUtil.memLengthUTF16((CharSequence)name, (boolean)true));
            MemoryUtil.memUTF16((CharSequence)name, (boolean)true, (ByteBuffer)lpFunctionNameBuf);
            PointerBuffer phModule = stack.callocPointer(1);
            int result = JNI.callPPI((int)1, (long)MemoryUtil.memAddress((ByteBuffer)lpFunctionNameBuf), (long)MemoryUtil.memAddress((CustomBuffer)phModule), (long)PFN_GetModuleHandleExW);
            if (result == 0) {
                int error = Kernel32.getLastError();
                switch (error) {
                    case 126: {
                        long l = 0L;
                        return l;
                    }
                }
                throw new RuntimeException("GetModuleHandleEx failed, error=" + error);
            }
            long l = phModule.get(0);
            return l;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getModuleFileName(long phModule) {
        ByteBuffer lpFileName = MemoryUtil.memAlignedAlloc((int)16, (int)65534);
        try {
            int length = JNI.callPPI((long)phModule, (long)MemoryUtil.memAddress((ByteBuffer)lpFileName), (int)(lpFileName.capacity() / 2), (long)PFN_GetModuleFileNameW);
            if (length == 0) {
                throw new RuntimeException("GetModuleFileNameW failed, error=" + Kernel32.getLastError());
            }
            String string = MemoryUtil.memUTF16((ByteBuffer)lpFileName, (int)length);
            return string;
        }
        finally {
            MemoryUtil.memAlignedFree((ByteBuffer)lpFileName);
        }
    }

    public static int getLastError() {
        return JNI.callI((long)PFN_GetLastError);
    }
}

