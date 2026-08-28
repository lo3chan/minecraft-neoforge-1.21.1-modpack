/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.FunctionProvider
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api;

import net.caffeinemc.mods.sodium.client.platform.windows.api.msgbox.MsgBoxParamSw;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.SharedLibrary;

public class User32 {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"user32");
    private static final long PFN_MessageBoxIndirectW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"MessageBoxIndirectW");

    public static void callMessageBoxIndirectW(MsgBoxParamSw params) {
        JNI.callPI((long)params.address(), (long)PFN_MessageBoxIndirectW);
    }
}

