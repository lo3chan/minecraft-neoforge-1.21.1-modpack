/*
 * Decompiled with CFR 0.152.
 */
package physx.character;

import physx.NativeObject;
import physx.character.PxController;
import physx.character.PxControllerDesc;
import physx.character.PxObstacleContext;
import physx.common.PxVec3;
import physx.physics.PxScene;

public class PxControllerManager
extends NativeObject {
    public static final int SIZEOF = PxControllerManager.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxControllerManager() {
    }

    private static native int __sizeOf();

    public static PxControllerManager wrapPointer(long address) {
        return address != 0L ? new PxControllerManager(address) : null;
    }

    public static PxControllerManager arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxControllerManager.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxControllerManager(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxControllerManager._release(this.address);
    }

    private static native void _release(long var0);

    public PxScene getScene() {
        this.checkNotNull();
        return PxScene.wrapPointer(PxControllerManager._getScene(this.address));
    }

    private static native long _getScene(long var0);

    public int getNbControllers() {
        this.checkNotNull();
        return PxControllerManager._getNbControllers(this.address);
    }

    private static native int _getNbControllers(long var0);

    public PxController getController(int index) {
        this.checkNotNull();
        return PxController.wrapPointer(PxControllerManager._getController(this.address, index));
    }

    private static native long _getController(long var0, int var2);

    public PxController createController(PxControllerDesc desc) {
        this.checkNotNull();
        return PxController.wrapPointer(PxControllerManager._createController(this.address, desc.getAddress()));
    }

    private static native long _createController(long var0, long var2);

    public void purgeControllers() {
        this.checkNotNull();
        PxControllerManager._purgeControllers(this.address);
    }

    private static native void _purgeControllers(long var0);

    public int getNbObstacleContexts() {
        this.checkNotNull();
        return PxControllerManager._getNbObstacleContexts(this.address);
    }

    private static native int _getNbObstacleContexts(long var0);

    public PxObstacleContext getObstacleContext(int index) {
        this.checkNotNull();
        return PxObstacleContext.wrapPointer(PxControllerManager._getObstacleContext(this.address, index));
    }

    private static native long _getObstacleContext(long var0, int var2);

    public PxObstacleContext createObstacleContext() {
        this.checkNotNull();
        return PxObstacleContext.wrapPointer(PxControllerManager._createObstacleContext(this.address));
    }

    private static native long _createObstacleContext(long var0);

    public void computeInteractions(float elapsedTime) {
        this.checkNotNull();
        PxControllerManager._computeInteractions(this.address, elapsedTime);
    }

    private static native void _computeInteractions(long var0, float var2);

    public void setTessellation(boolean flag, float maxEdgeLength) {
        this.checkNotNull();
        PxControllerManager._setTessellation(this.address, flag, maxEdgeLength);
    }

    private static native void _setTessellation(long var0, boolean var2, float var3);

    public void setOverlapRecoveryModule(boolean flag) {
        this.checkNotNull();
        PxControllerManager._setOverlapRecoveryModule(this.address, flag);
    }

    private static native void _setOverlapRecoveryModule(long var0, boolean var2);

    public void setPreciseSweeps(boolean flags) {
        this.checkNotNull();
        PxControllerManager._setPreciseSweeps(this.address, flags);
    }

    private static native void _setPreciseSweeps(long var0, boolean var2);

    public void setPreventVerticalSlidingAgainstCeiling(boolean flag) {
        this.checkNotNull();
        PxControllerManager._setPreventVerticalSlidingAgainstCeiling(this.address, flag);
    }

    private static native void _setPreventVerticalSlidingAgainstCeiling(long var0, boolean var2);

    public void shiftOrigin(PxVec3 shift) {
        this.checkNotNull();
        PxControllerManager._shiftOrigin(this.address, shift.getAddress());
    }

    private static native void _shiftOrigin(long var0, long var2);
}

