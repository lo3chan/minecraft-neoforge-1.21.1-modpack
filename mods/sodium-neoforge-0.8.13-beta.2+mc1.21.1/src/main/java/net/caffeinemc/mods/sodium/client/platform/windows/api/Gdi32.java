/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.SharedLibrary;

public class Gdi32 {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"gdi32");
    public static final long PFN_D3DKMTQueryAdapterInfo = APIUtil.apiGetFunctionAddressOptional((SharedLibrary)LIBRARY, (String)"D3DKMTQueryAdapterInfo");
    public static final long PFN_D3DKMTCloseAdapter = APIUtil.apiGetFunctionAddressOptional((SharedLibrary)LIBRARY, (String)"D3DKMTCloseAdapter");
    public static final long PFN_D3DKMTEnumAdapters = APIUtil.apiGetFunctionAddressOptional((SharedLibrary)LIBRARY, (String)"D3DKMTEnumAdapters");

    public static boolean isD3DKMTSupported() {
        return PFN_D3DKMTQueryAdapterInfo != 0L && PFN_D3DKMTCloseAdapter != 0L && PFN_D3DKMTEnumAdapters != 0L;
    }

    public static int nd3dKmtQueryAdapterInfo(long ptr) {
        return JNI.callPI((long)ptr, (long)Gdi32.checkPfn(PFN_D3DKMTQueryAdapterInfo));
    }

    public static int nD3DKMTEnumAdapters(long ptr) {
        return JNI.callPI((long)ptr, (long)Gdi32.checkPfn(PFN_D3DKMTEnumAdapters));
    }

    public static int nD3DKMTCloseAdapter(long ptr) {
        return JNI.callPI((long)ptr, (long)Gdi32.checkPfn(PFN_D3DKMTCloseAdapter));
    }

    private static long checkPfn(long pfn) {
        if (pfn == 0L) {
            throw new NullPointerException("Function pointer not available");
        }
        return pfn;
    }
}

