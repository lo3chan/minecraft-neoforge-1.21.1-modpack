/*
 * Decompiled with CFR 0.152.
 */
package physx.support;

import physx.NativeObject;
import physx.physics.PxArticulationReducedCoordinate;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;
import physx.physics.PxShape;
import physx.support.PxArray_PxActorPtr;

public class SupportFunctions
extends NativeObject {
    public static final int SIZEOF = SupportFunctions.__sizeOf();
    public static final int ALIGNOF = 8;

    protected SupportFunctions() {
    }

    private static native int __sizeOf();

    public static SupportFunctions wrapPointer(long address) {
        return address != 0L ? new SupportFunctions(address) : null;
    }

    public static SupportFunctions arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return SupportFunctions.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected SupportFunctions(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        SupportFunctions._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public static PxShape PxActor_getShape(PxRigidActor actor, int index) {
        return PxShape.wrapPointer(SupportFunctions._PxActor_getShape(actor.getAddress(), index));
    }

    private static native long _PxActor_getShape(long var0, int var2);

    public static PxArray_PxActorPtr PxScene_getActiveActors(PxScene scene) {
        return PxArray_PxActorPtr.wrapPointer(SupportFunctions._PxScene_getActiveActors(scene.getAddress()));
    }

    private static native long _PxScene_getActiveActors(long var0);

    public static int PxArticulationReducedCoordinate_getMinSolverPositionIterations(PxArticulationReducedCoordinate articulation) {
        return SupportFunctions._PxArticulationReducedCoordinate_getMinSolverPositionIterations(articulation.getAddress());
    }

    private static native int _PxArticulationReducedCoordinate_getMinSolverPositionIterations(long var0);

    public static int PxArticulationReducedCoordinate_getMinSolverVelocityIterations(PxArticulationReducedCoordinate articulation) {
        return SupportFunctions._PxArticulationReducedCoordinate_getMinSolverVelocityIterations(articulation.getAddress());
    }

    private static native int _PxArticulationReducedCoordinate_getMinSolverVelocityIterations(long var0);
}

