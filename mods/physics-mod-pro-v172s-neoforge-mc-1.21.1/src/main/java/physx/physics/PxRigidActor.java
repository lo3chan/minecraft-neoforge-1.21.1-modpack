/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxTransform;
import physx.physics.PxActor;
import physx.physics.PxShape;

public class PxRigidActor
extends PxActor {
    public static final int SIZEOF = PxRigidActor.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxRigidActor() {
    }

    private static native int __sizeOf();

    public static PxRigidActor wrapPointer(long address) {
        return address != 0L ? new PxRigidActor(address) : null;
    }

    public static PxRigidActor arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxRigidActor.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxRigidActor(long address) {
        super(address);
    }

    public PxTransform getGlobalPose() {
        this.checkNotNull();
        return PxTransform.wrapPointer(PxRigidActor._getGlobalPose(this.address));
    }

    private static native long _getGlobalPose(long var0);

    public void setGlobalPose(PxTransform pose) {
        this.checkNotNull();
        PxRigidActor._setGlobalPose(this.address, pose.getAddress());
    }

    private static native void _setGlobalPose(long var0, long var2);

    public void setGlobalPose(PxTransform pose, boolean autowake) {
        this.checkNotNull();
        PxRigidActor._setGlobalPose(this.address, pose.getAddress(), autowake);
    }

    private static native void _setGlobalPose(long var0, long var2, boolean var4);

    public boolean attachShape(PxShape shape) {
        this.checkNotNull();
        return PxRigidActor._attachShape(this.address, shape.getAddress());
    }

    private static native boolean _attachShape(long var0, long var2);

    public void detachShape(PxShape shape) {
        this.checkNotNull();
        PxRigidActor._detachShape(this.address, shape.getAddress());
    }

    private static native void _detachShape(long var0, long var2);

    public void detachShape(PxShape shape, boolean wakeOnLostTouch) {
        this.checkNotNull();
        PxRigidActor._detachShape(this.address, shape.getAddress(), wakeOnLostTouch);
    }

    private static native void _detachShape(long var0, long var2, boolean var4);

    public int getNbShapes() {
        this.checkNotNull();
        return PxRigidActor._getNbShapes(this.address);
    }

    private static native int _getNbShapes(long var0);

    public int getNbConstraints() {
        this.checkNotNull();
        return PxRigidActor._getNbConstraints(this.address);
    }

    private static native int _getNbConstraints(long var0);
}

