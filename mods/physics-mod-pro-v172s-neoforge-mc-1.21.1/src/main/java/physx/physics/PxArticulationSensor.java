/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.common.PxBase;
import physx.common.PxTransform;
import physx.physics.PxArticulationLink;
import physx.physics.PxArticulationReducedCoordinate;
import physx.physics.PxArticulationSensorFlagEnum;
import physx.physics.PxArticulationSensorFlags;
import physx.physics.PxSpatialForce;

public class PxArticulationSensor
extends PxBase {
    public static final int SIZEOF = PxArticulationSensor.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationSensor() {
    }

    private static native int __sizeOf();

    public static PxArticulationSensor wrapPointer(long address) {
        return address != 0L ? new PxArticulationSensor(address) : null;
    }

    public static PxArticulationSensor arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationSensor.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationSensor(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationSensor._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public NativeObject getUserData() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxArticulationSensor._getUserData(this.address));
    }

    private static native long _getUserData(long var0);

    public void setUserData(NativeObject value) {
        this.checkNotNull();
        PxArticulationSensor._setUserData(this.address, value.getAddress());
    }

    private static native void _setUserData(long var0, long var2);

    public PxSpatialForce getForces() {
        this.checkNotNull();
        return PxSpatialForce.wrapPointer(PxArticulationSensor._getForces(this.address));
    }

    private static native long _getForces(long var0);

    public PxTransform getRelativePose() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxArticulationSensor._getRelativePose(this.address));
    }

    private static native long _getRelativePose(long var0);

    public void setRelativePose(PxTransform pose) {
        this.checkNotNull();
        PxArticulationSensor._setRelativePose(this.address, pose.getAddress());
    }

    private static native void _setRelativePose(long var0, long var2);

    public PxArticulationLink getLink() {
        this.checkNotNull();
        return PxArticulationLink.wrapPointer(PxArticulationSensor._getLink(this.address));
    }

    private static native long _getLink(long var0);

    public int getIndex() {
        this.checkNotNull();
        return PxArticulationSensor._getIndex(this.address);
    }

    private static native int _getIndex(long var0);

    public PxArticulationReducedCoordinate getArticulation() {
        this.checkNotNull();
        return PxArticulationReducedCoordinate.wrapPointer(PxArticulationSensor._getArticulation(this.address));
    }

    private static native long _getArticulation(long var0);

    public PxArticulationSensorFlags getFlags() {
        this.checkNotNull();
        return PxArticulationSensorFlags.wrapPointer(PxArticulationSensor._getFlags(this.address));
    }

    private static native long _getFlags(long var0);

    public void setFlag(PxArticulationSensorFlagEnum flag, boolean enabled) {
        this.checkNotNull();
        PxArticulationSensor._setFlag(this.address, flag.value, enabled);
    }

    private static native void _setFlag(long var0, int var2, boolean var3);
}

