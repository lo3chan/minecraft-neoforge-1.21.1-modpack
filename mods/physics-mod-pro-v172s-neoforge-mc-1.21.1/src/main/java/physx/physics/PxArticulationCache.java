/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.physics.PxArticulationRootLinkData;
import physx.physics.PxSpatialForce;
import physx.physics.PxSpatialVelocity;
import physx.support.PxRealPtr;

public class PxArticulationCache
extends NativeObject {
    public static final int SIZEOF = PxArticulationCache.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationCache() {
    }

    private static native int __sizeOf();

    public static PxArticulationCache wrapPointer(long address) {
        return address != 0L ? new PxArticulationCache(address) : null;
    }

    public static PxArticulationCache arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationCache.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationCache(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationCache._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxSpatialForce getExternalForces() {
        this.checkNotNull();
        return PxSpatialForce.wrapPointer(PxArticulationCache._getExternalForces(this.address));
    }

    private static native long _getExternalForces(long var0);

    public void setExternalForces(PxSpatialForce value) {
        this.checkNotNull();
        PxArticulationCache._setExternalForces(this.address, value.getAddress());
    }

    private static native void _setExternalForces(long var0, long var2);

    public PxRealPtr getDenseJacobian() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getDenseJacobian(this.address));
    }

    private static native long _getDenseJacobian(long var0);

    public void setDenseJacobian(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setDenseJacobian(this.address, value.getAddress());
    }

    private static native void _setDenseJacobian(long var0, long var2);

    public PxRealPtr getMassMatrix() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getMassMatrix(this.address));
    }

    private static native long _getMassMatrix(long var0);

    public void setMassMatrix(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setMassMatrix(this.address, value.getAddress());
    }

    private static native void _setMassMatrix(long var0, long var2);

    public PxRealPtr getJointVelocity() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getJointVelocity(this.address));
    }

    private static native long _getJointVelocity(long var0);

    public void setJointVelocity(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setJointVelocity(this.address, value.getAddress());
    }

    private static native void _setJointVelocity(long var0, long var2);

    public PxRealPtr getJointAcceleration() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getJointAcceleration(this.address));
    }

    private static native long _getJointAcceleration(long var0);

    public void setJointAcceleration(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setJointAcceleration(this.address, value.getAddress());
    }

    private static native void _setJointAcceleration(long var0, long var2);

    public PxRealPtr getJointPosition() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getJointPosition(this.address));
    }

    private static native long _getJointPosition(long var0);

    public void setJointPosition(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setJointPosition(this.address, value.getAddress());
    }

    private static native void _setJointPosition(long var0, long var2);

    public PxRealPtr getJointForce() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getJointForce(this.address));
    }

    private static native long _getJointForce(long var0);

    public void setJointForce(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setJointForce(this.address, value.getAddress());
    }

    private static native void _setJointForce(long var0, long var2);

    public PxSpatialVelocity getLinkVelocity() {
        this.checkNotNull();
        return PxSpatialVelocity.wrapPointer(PxArticulationCache._getLinkVelocity(this.address));
    }

    private static native long _getLinkVelocity(long var0);

    public void setLinkVelocity(PxSpatialVelocity value) {
        this.checkNotNull();
        PxArticulationCache._setLinkVelocity(this.address, value.getAddress());
    }

    private static native void _setLinkVelocity(long var0, long var2);

    public PxSpatialVelocity getLinkAcceleration() {
        this.checkNotNull();
        return PxSpatialVelocity.wrapPointer(PxArticulationCache._getLinkAcceleration(this.address));
    }

    private static native long _getLinkAcceleration(long var0);

    public void setLinkAcceleration(PxSpatialVelocity value) {
        this.checkNotNull();
        PxArticulationCache._setLinkAcceleration(this.address, value.getAddress());
    }

    private static native void _setLinkAcceleration(long var0, long var2);

    public PxArticulationRootLinkData getRootLinkData() {
        this.checkNotNull();
        return PxArticulationRootLinkData.wrapPointer(PxArticulationCache._getRootLinkData(this.address));
    }

    private static native long _getRootLinkData(long var0);

    public void setRootLinkData(PxArticulationRootLinkData value) {
        this.checkNotNull();
        PxArticulationCache._setRootLinkData(this.address, value.getAddress());
    }

    private static native void _setRootLinkData(long var0, long var2);

    @Deprecated
    public PxSpatialForce getSensorForces() {
        this.checkNotNull();
        return PxSpatialForce.wrapPointer(PxArticulationCache._getSensorForces(this.address));
    }

    private static native long _getSensorForces(long var0);

    @Deprecated
    public void setSensorForces(PxSpatialForce value) {
        this.checkNotNull();
        PxArticulationCache._setSensorForces(this.address, value.getAddress());
    }

    private static native void _setSensorForces(long var0, long var2);

    public PxRealPtr getCoefficientMatrix() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getCoefficientMatrix(this.address));
    }

    private static native long _getCoefficientMatrix(long var0);

    public void setCoefficientMatrix(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setCoefficientMatrix(this.address, value.getAddress());
    }

    private static native void _setCoefficientMatrix(long var0, long var2);

    public PxRealPtr getLambda() {
        this.checkNotNull();
        return PxRealPtr.wrapPointer(PxArticulationCache._getLambda(this.address));
    }

    private static native long _getLambda(long var0);

    public void setLambda(PxRealPtr value) {
        this.checkNotNull();
        PxArticulationCache._setLambda(this.address, value.getAddress());
    }

    private static native void _setLambda(long var0, long var2);

    public NativeObject getScratchMemory() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxArticulationCache._getScratchMemory(this.address));
    }

    private static native long _getScratchMemory(long var0);

    public void setScratchMemory(NativeObject value) {
        this.checkNotNull();
        PxArticulationCache._setScratchMemory(this.address, value.getAddress());
    }

    private static native void _setScratchMemory(long var0, long var2);

    public NativeObject getScratchAllocator() {
        this.checkNotNull();
        return NativeObject.wrapPointer(PxArticulationCache._getScratchAllocator(this.address));
    }

    private static native long _getScratchAllocator(long var0);

    public void setScratchAllocator(NativeObject value) {
        this.checkNotNull();
        PxArticulationCache._setScratchAllocator(this.address, value.getAddress());
    }

    private static native void _setScratchAllocator(long var0, long var2);

    public int getVersion() {
        this.checkNotNull();
        return PxArticulationCache._getVersion(this.address);
    }

    private static native int _getVersion(long var0);

    public void setVersion(int value) {
        this.checkNotNull();
        PxArticulationCache._setVersion(this.address, value);
    }

    private static native void _setVersion(long var0, int var2);

    public void release() {
        this.checkNotNull();
        PxArticulationCache._release(this.address);
    }

    private static native void _release(long var0);
}

