/*
 * Decompiled with CFR 0.152.
 */
package physx.vehicle2;

import physx.NativeObject;
import physx.vehicle2.BaseVehicleParams;
import physx.vehicle2.BaseVehicleState;
import physx.vehicle2.PxVehicleComponentSequence;
import physx.vehicle2.PxVehicleSimulationContext;

public class BaseVehicle
extends NativeObject {
    public static final int SIZEOF = BaseVehicle.__sizeOf();
    public static final int ALIGNOF = 8;

    protected BaseVehicle() {
    }

    private static native int __sizeOf();

    public static BaseVehicle wrapPointer(long address) {
        return address != 0L ? new BaseVehicle(address) : null;
    }

    public static BaseVehicle arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return BaseVehicle.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected BaseVehicle(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        BaseVehicle._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public BaseVehicleParams getBaseParams() {
        this.checkNotNull();
        return BaseVehicleParams.wrapPointer(BaseVehicle._getBaseParams(this.address));
    }

    private static native long _getBaseParams(long var0);

    public void setBaseParams(BaseVehicleParams value) {
        this.checkNotNull();
        BaseVehicle._setBaseParams(this.address, value.getAddress());
    }

    private static native void _setBaseParams(long var0, long var2);

    public BaseVehicleState getBaseState() {
        this.checkNotNull();
        return BaseVehicleState.wrapPointer(BaseVehicle._getBaseState(this.address));
    }

    private static native long _getBaseState(long var0);

    public void setBaseState(BaseVehicleState value) {
        this.checkNotNull();
        BaseVehicle._setBaseState(this.address, value.getAddress());
    }

    private static native void _setBaseState(long var0, long var2);

    public PxVehicleComponentSequence getComponentSequence() {
        this.checkNotNull();
        return PxVehicleComponentSequence.wrapPointer(BaseVehicle._getComponentSequence(this.address));
    }

    private static native long _getComponentSequence(long var0);

    public void setComponentSequence(PxVehicleComponentSequence value) {
        this.checkNotNull();
        BaseVehicle._setComponentSequence(this.address, value.getAddress());
    }

    private static native void _setComponentSequence(long var0, long var2);

    public byte getComponentSequenceSubstepGroupHandle() {
        this.checkNotNull();
        return BaseVehicle._getComponentSequenceSubstepGroupHandle(this.address);
    }

    private static native byte _getComponentSequenceSubstepGroupHandle(long var0);

    public void setComponentSequenceSubstepGroupHandle(byte value) {
        this.checkNotNull();
        BaseVehicle._setComponentSequenceSubstepGroupHandle(this.address, value);
    }

    private static native void _setComponentSequenceSubstepGroupHandle(long var0, byte var2);

    public boolean initialize() {
        this.checkNotNull();
        return BaseVehicle._initialize(this.address);
    }

    private static native boolean _initialize(long var0);

    public void destroyState() {
        this.checkNotNull();
        BaseVehicle._destroyState(this.address);
    }

    private static native void _destroyState(long var0);

    public void initComponentSequence(boolean addPhysXBeginEndComponents) {
        this.checkNotNull();
        BaseVehicle._initComponentSequence(this.address, addPhysXBeginEndComponents);
    }

    private static native void _initComponentSequence(long var0, boolean var2);

    public void step(float dt, PxVehicleSimulationContext context) {
        this.checkNotNull();
        BaseVehicle._step(this.address, dt, context.getAddress());
    }

    private static native void _step(long var0, float var2, long var3);
}

