/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxCUresult;

public class PxCudaContext
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxCudaContext() {
    }

    private static native int __sizeOf();

    public static PxCudaContext wrapPointer(long address) {
        return address != 0L ? new PxCudaContext(address) : null;
    }

    public static PxCudaContext arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxCudaContext.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxCudaContext(long address) {
        super(address);
    }

    public PxCUresult memcpyDtoH(NativeObject dstHost, long srcDevice, int byteCount) {
        this.checkNotNull();
        return PxCUresult.wrapPointer(PxCudaContext._memcpyDtoH(this.address, dstHost.getAddress(), srcDevice, byteCount));
    }

    private static native long _memcpyDtoH(long var0, long var2, long var4, int var6);

    public PxCUresult memcpyHtoD(long dstDevice, NativeObject srcHost, int byteCount) {
        this.checkNotNull();
        return PxCUresult.wrapPointer(PxCudaContext._memcpyHtoD(this.address, dstDevice, srcHost.getAddress(), byteCount));
    }

    private static native long _memcpyHtoD(long var0, long var2, long var4, int var6);

    static {
        PlatformChecks.requirePlatform(3, "physx.common.PxCudaContext");
        SIZEOF = PxCudaContext.__sizeOf();
    }
}

