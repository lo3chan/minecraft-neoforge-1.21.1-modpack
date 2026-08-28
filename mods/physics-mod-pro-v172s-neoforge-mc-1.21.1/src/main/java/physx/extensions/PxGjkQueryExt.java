/*
 * Decompiled with CFR 0.152.
 */
package physx.extensions;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.extensions.Support;
import physx.geometry.PxContactBuffer;

public class PxGjkQueryExt
extends NativeObject {
    public static final int SIZEOF = PxGjkQueryExt.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxGjkQueryExt() {
    }

    private static native int __sizeOf();

    public static PxGjkQueryExt wrapPointer(long address) {
        return address != 0L ? new PxGjkQueryExt(address) : null;
    }

    public static PxGjkQueryExt arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxGjkQueryExt.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxGjkQueryExt(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxGjkQueryExt._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public static boolean generateContacts(Support a, Support b, PxTransform poseA, PxTransform poseB, float contactDistance, float toleranceLength, PxContactBuffer contactBuffer) {
        return PxGjkQueryExt._generateContacts(a.getAddress(), b.getAddress(), poseA.getAddress(), poseB.getAddress(), contactDistance, toleranceLength, contactBuffer.getAddress());
    }

    private static native boolean _generateContacts(long var0, long var2, long var4, long var6, float var8, float var9, long var10);
}

