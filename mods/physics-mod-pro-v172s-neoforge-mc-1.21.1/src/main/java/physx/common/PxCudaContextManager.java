/*
 * Decompiled with CFR 0.152.
 */
package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.CUcontext;
import physx.common.CUdevice;
import physx.common.CUmodule;
import physx.common.PxCudaContext;

public class PxCudaContextManager
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxCudaContextManager() {
    }

    private static native int __sizeOf();

    public static PxCudaContextManager wrapPointer(long address) {
        return address != 0L ? new PxCudaContextManager(address) : null;
    }

    public static PxCudaContextManager arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxCudaContextManager.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxCudaContextManager(long address) {
        super(address);
    }

    public void acquireContext() {
        this.checkNotNull();
        PxCudaContextManager._acquireContext(this.address);
    }

    private static native void _acquireContext(long var0);

    public void releaseContext() {
        this.checkNotNull();
        PxCudaContextManager._releaseContext(this.address);
    }

    private static native void _releaseContext(long var0);

    public CUcontext getContext() {
        this.checkNotNull();
        return CUcontext.wrapPointer(PxCudaContextManager._getContext(this.address));
    }

    private static native long _getContext(long var0);

    public PxCudaContext getCudaContext() {
        this.checkNotNull();
        return PxCudaContext.wrapPointer(PxCudaContextManager._getCudaContext(this.address));
    }

    private static native long _getCudaContext(long var0);

    public boolean contextIsValid() {
        this.checkNotNull();
        return PxCudaContextManager._contextIsValid(this.address);
    }

    private static native boolean _contextIsValid(long var0);

    public boolean supportsArchSM10() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM10(this.address);
    }

    private static native boolean _supportsArchSM10(long var0);

    public boolean supportsArchSM11() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM11(this.address);
    }

    private static native boolean _supportsArchSM11(long var0);

    public boolean supportsArchSM12() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM12(this.address);
    }

    private static native boolean _supportsArchSM12(long var0);

    public boolean supportsArchSM13() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM13(this.address);
    }

    private static native boolean _supportsArchSM13(long var0);

    public boolean supportsArchSM20() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM20(this.address);
    }

    private static native boolean _supportsArchSM20(long var0);

    public boolean supportsArchSM30() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM30(this.address);
    }

    private static native boolean _supportsArchSM30(long var0);

    public boolean supportsArchSM35() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM35(this.address);
    }

    private static native boolean _supportsArchSM35(long var0);

    public boolean supportsArchSM50() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM50(this.address);
    }

    private static native boolean _supportsArchSM50(long var0);

    public boolean supportsArchSM52() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM52(this.address);
    }

    private static native boolean _supportsArchSM52(long var0);

    public boolean supportsArchSM60() {
        this.checkNotNull();
        return PxCudaContextManager._supportsArchSM60(this.address);
    }

    private static native boolean _supportsArchSM60(long var0);

    public boolean isIntegrated() {
        this.checkNotNull();
        return PxCudaContextManager._isIntegrated(this.address);
    }

    private static native boolean _isIntegrated(long var0);

    public boolean canMapHostMemory() {
        this.checkNotNull();
        return PxCudaContextManager._canMapHostMemory(this.address);
    }

    private static native boolean _canMapHostMemory(long var0);

    public int getDriverVersion() {
        this.checkNotNull();
        return PxCudaContextManager._getDriverVersion(this.address);
    }

    private static native int _getDriverVersion(long var0);

    public long getDeviceTotalMemBytes() {
        this.checkNotNull();
        return PxCudaContextManager._getDeviceTotalMemBytes(this.address);
    }

    private static native long _getDeviceTotalMemBytes(long var0);

    public int getMultiprocessorCount() {
        this.checkNotNull();
        return PxCudaContextManager._getMultiprocessorCount(this.address);
    }

    private static native int _getMultiprocessorCount(long var0);

    public int getClockRate() {
        this.checkNotNull();
        return PxCudaContextManager._getClockRate(this.address);
    }

    private static native int _getClockRate(long var0);

    public int getSharedMemPerBlock() {
        this.checkNotNull();
        return PxCudaContextManager._getSharedMemPerBlock(this.address);
    }

    private static native int _getSharedMemPerBlock(long var0);

    public int getSharedMemPerMultiprocessor() {
        this.checkNotNull();
        return PxCudaContextManager._getSharedMemPerMultiprocessor(this.address);
    }

    private static native int _getSharedMemPerMultiprocessor(long var0);

    public int getMaxThreadsPerBlock() {
        this.checkNotNull();
        return PxCudaContextManager._getMaxThreadsPerBlock(this.address);
    }

    private static native int _getMaxThreadsPerBlock(long var0);

    public String getDeviceName() {
        this.checkNotNull();
        return PxCudaContextManager._getDeviceName(this.address);
    }

    private static native String _getDeviceName(long var0);

    public CUdevice getDevice() {
        this.checkNotNull();
        return CUdevice.wrapPointer(PxCudaContextManager._getDevice(this.address));
    }

    private static native long _getDevice(long var0);

    public void setUsingConcurrentStreams(boolean flag) {
        this.checkNotNull();
        PxCudaContextManager._setUsingConcurrentStreams(this.address, flag);
    }

    private static native void _setUsingConcurrentStreams(long var0, boolean var2);

    public boolean getUsingConcurrentStreams() {
        this.checkNotNull();
        return PxCudaContextManager._getUsingConcurrentStreams(this.address);
    }

    private static native boolean _getUsingConcurrentStreams(long var0);

    public int usingDedicatedGPU() {
        this.checkNotNull();
        return PxCudaContextManager._usingDedicatedGPU(this.address);
    }

    private static native int _usingDedicatedGPU(long var0);

    public CUmodule getCuModules() {
        this.checkNotNull();
        return CUmodule.wrapPointer(PxCudaContextManager._getCuModules(this.address));
    }

    private static native long _getCuModules(long var0);

    public void release() {
        this.checkNotNull();
        PxCudaContextManager._release(this.address);
    }

    private static native void _release(long var0);

    static {
        PlatformChecks.requirePlatform(3, "physx.common.PxCudaContextManager");
        SIZEOF = PxCudaContextManager.__sizeOf();
    }
}

