/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api;

import java.util.Objects;
import net.caffeinemc.mods.sodium.client.platform.NativeWindowHandle;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;

public class Shell32 {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"shell32");
    private static final long PFN_ShellExecuteW = APIUtil.apiGetFunctionAddressOptional((SharedLibrary)LIBRARY, (String)"ShellExecuteW");

    public static void browseUrl(@Nullable NativeWindowHandle window, String url) {
        Objects.requireNonNull(url, "URL parameter must be non-null");
        try (MemoryStack stack = MemoryStack.stackPush();){
            stack.nUTF16((CharSequence)"open", true);
            long lpOperation = stack.getPointerAddress();
            stack.nUTF16((CharSequence)url, true);
            long lpFile = stack.getPointerAddress();
            Shell32.nShellExecuteW(window != null ? window.getWin32Handle() : 0L, lpOperation, lpFile, 0L, 0L, 1);
        }
    }

    public static long nShellExecuteW(long hwnd, long lpOperation, long lpFile, long lpParameters, long lpDirectory, int nShowCmd) {
        return JNI.invokePPPPPP((long)hwnd, (long)lpOperation, (long)lpFile, (long)lpParameters, (long)lpDirectory, (int)nShowCmd, (long)Shell32.checkPfn(PFN_ShellExecuteW));
    }

    private static long checkPfn(long pfn) {
        if (pfn == 0L) {
            throw new NullPointerException("Function pointer not available");
        }
        return pfn;
    }
}

