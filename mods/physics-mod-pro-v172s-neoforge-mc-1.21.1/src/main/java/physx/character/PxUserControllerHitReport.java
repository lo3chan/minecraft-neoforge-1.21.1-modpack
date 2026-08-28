/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxControllerObstacleHit;
import physx.character.PxControllerShapeHit;
import physx.character.PxControllersHit;

public class PxUserControllerHitReport
extends NativeObject {
    public static final int SIZEOF = PxUserControllerHitReport.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxUserControllerHitReport() {
    }

    private static native int __sizeOf();

    public static PxUserControllerHitReport wrapPointer(long address) {
        return address != 0L ? new PxUserControllerHitReport(address) : null;
    }

    public static PxUserControllerHitReport arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxUserControllerHitReport.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxUserControllerHitReport(long address) {
        super(address);
    }

    public void onShapeHit(PxControllerShapeHit hit) {
        this.checkNotNull();
        PxUserControllerHitReport._onShapeHit(this.address, hit.getAddress());
    }

    private static native void _onShapeHit(long var0, long var2);

    public void onControllerHit(PxControllersHit hit) {
        this.checkNotNull();
        PxUserControllerHitReport._onControllerHit(this.address, hit.getAddress());
    }

    private static native void _onControllerHit(long var0, long var2);

    public void onObstacleHit(PxControllerObstacleHit hit) {
        this.checkNotNull();
        PxUserControllerHitReport._onObstacleHit(this.address, hit.getAddress());
    }

    private static native void _onObstacleHit(long var0, long var2);
}

