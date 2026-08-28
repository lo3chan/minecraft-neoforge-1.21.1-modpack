/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.PlatformChecks;
import physx.support.OmniPvdWriteStream;

public class OmniPvdFileWriteStream
extends OmniPvdWriteStream {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected OmniPvdFileWriteStream() {
    }

    private static native int __sizeOf();

    public static OmniPvdFileWriteStream wrapPointer(long address) {
        return address != 0L ? new OmniPvdFileWriteStream(address) : null;
    }

    public static OmniPvdFileWriteStream arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return OmniPvdFileWriteStream.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected OmniPvdFileWriteStream(long address) {
        super(address);
    }

    public void setFileName(String fileName) {
        this.checkNotNull();
        OmniPvdFileWriteStream._setFileName(this.address, fileName);
    }

    private static native void _setFileName(long var0, String var2);

    static {
        PlatformChecks.requirePlatform(7, "physx.support.OmniPvdFileWriteStream");
        SIZEOF = OmniPvdFileWriteStream.__sizeOf();
    }
}

