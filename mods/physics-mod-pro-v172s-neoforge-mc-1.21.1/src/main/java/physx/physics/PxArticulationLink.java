/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.physics.PxArticulationJointReducedCoordinate;
import physx.physics.PxArticulationReducedCoordinate;
import physx.physics.PxRigidBody;

public class PxArticulationLink
extends PxRigidBody {
    public static final int SIZEOF = PxArticulationLink.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationLink() {
    }

    private static native int __sizeOf();

    public static PxArticulationLink wrapPointer(long address) {
        return address != 0L ? new PxArticulationLink(address) : null;
    }

    public static PxArticulationLink arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationLink.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationLink(long address) {
        super(address);
    }

    public PxArticulationReducedCoordinate getArticulation() {
        this.checkNotNull();
        return PxArticulationReducedCoordinate.wrapPointer(PxArticulationLink._getArticulation(this.address));
    }

    private static native long _getArticulation(long var0);

    public PxArticulationJointReducedCoordinate getInboundJoint() {
        this.checkNotNull();
        return PxArticulationJointReducedCoordinate.wrapPointer(PxArticulationLink._getInboundJoint(this.address));
    }

    private static native long _getInboundJoint(long var0);

    public int getInboundJointDof() {
        this.checkNotNull();
        return PxArticulationLink._getInboundJointDof(this.address);
    }

    private static native int _getInboundJointDof(long var0);

    public int getNbChildren() {
        this.checkNotNull();
        return PxArticulationLink._getNbChildren(this.address);
    }

    private static native int _getNbChildren(long var0);

    public int getLinkIndex() {
        this.checkNotNull();
        return PxArticulationLink._getLinkIndex(this.address);
    }

    private static native int _getLinkIndex(long var0);

    public void setCfmScale(float cfm) {
        this.checkNotNull();
        PxArticulationLink._setCfmScale(this.address, cfm);
    }

    private static native void _setCfmScale(long var0, float var2);

    public float getCfmScale() {
        this.checkNotNull();
        return PxArticulationLink._getCfmScale(this.address);
    }

    private static native float _getCfmScale(long var0);
}

