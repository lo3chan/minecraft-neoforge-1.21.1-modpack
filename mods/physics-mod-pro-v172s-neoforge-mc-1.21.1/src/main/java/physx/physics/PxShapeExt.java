/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;
import physx.common.PxBounds3;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxGeometry;
import physx.physics.PxHitFlags;
import physx.physics.PxRaycastHit;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.PxSweepHit;

public class PxShapeExt
extends NativeObject {
    public static final int SIZEOF = PxShapeExt.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxShapeExt() {
    }

    private static native int __sizeOf();

    public static PxShapeExt wrapPointer(long address) {
        return address != 0L ? new PxShapeExt(address) : null;
    }

    public static PxShapeExt arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxShapeExt.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxShapeExt(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxShapeExt._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public static PxTransform getGlobalPose(PxShape shape, PxRigidActor actor) {
        return PxTransform.wrapPointer(PxShapeExt._getGlobalPose(shape.getAddress(), actor.getAddress()));
    }

    private static native long _getGlobalPose(long var0, long var2);

    public static int raycast(PxShape shape, PxRigidActor actor, PxVec3 rayOrigin, PxVec3 rayDir, float maxDist, PxHitFlags hitFlags, int maxHits, PxRaycastHit rayHits) {
        return PxShapeExt._raycast(shape.getAddress(), actor.getAddress(), rayOrigin.getAddress(), rayDir.getAddress(), maxDist, hitFlags.getAddress(), maxHits, rayHits.getAddress());
    }

    private static native int _raycast(long var0, long var2, long var4, long var6, float var8, long var9, int var11, long var12);

    public static boolean overlap(PxShape shape, PxRigidActor actor, PxGeometry otherGeom, PxTransform otherGeomPose) {
        return PxShapeExt._overlap(shape.getAddress(), actor.getAddress(), otherGeom.getAddress(), otherGeomPose.getAddress());
    }

    private static native boolean _overlap(long var0, long var2, long var4, long var6);

    public static boolean sweep(PxShape shape, PxRigidActor actor, PxVec3 unitDir, float distance, PxGeometry otherGeom, PxTransform otherGeomPose, PxSweepHit sweepHit, PxHitFlags hitFlags) {
        return PxShapeExt._sweep(shape.getAddress(), actor.getAddress(), unitDir.getAddress(), distance, otherGeom.getAddress(), otherGeomPose.getAddress(), sweepHit.getAddress(), hitFlags.getAddress());
    }

    private static native boolean _sweep(long var0, long var2, long var4, float var6, long var7, long var9, long var11, long var13);

    public static PxBounds3 getWorldBounds(PxShape shape, PxRigidActor actor) {
        return PxBounds3.wrapPointer(PxShapeExt._getWorldBounds(shape.getAddress(), actor.getAddress()));
    }

    private static native long _getWorldBounds(long var0, long var2);

    public static PxBounds3 getWorldBounds(PxShape shape, PxRigidActor actor, float inflation) {
        return PxBounds3.wrapPointer(PxShapeExt._getWorldBounds(shape.getAddress(), actor.getAddress(), inflation));
    }

    private static native long _getWorldBounds(long var0, long var2, float var4);
}

