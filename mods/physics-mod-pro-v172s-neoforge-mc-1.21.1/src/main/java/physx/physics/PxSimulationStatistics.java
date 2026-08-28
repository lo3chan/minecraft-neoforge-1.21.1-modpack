/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.NativeObject;

public class PxSimulationStatistics
extends NativeObject {
    public static final int SIZEOF = PxSimulationStatistics.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxSimulationStatistics() {
    }

    private static native int __sizeOf();

    public static PxSimulationStatistics wrapPointer(long address) {
        return address != 0L ? new PxSimulationStatistics(address) : null;
    }

    public static PxSimulationStatistics arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxSimulationStatistics.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxSimulationStatistics(long address) {
        super(address);
    }

    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxSimulationStatistics._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public int getNbActiveConstraints() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbActiveConstraints(this.address);
    }

    private static native int _getNbActiveConstraints(long var0);

    public void setNbActiveConstraints(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbActiveConstraints(this.address, value);
    }

    private static native void _setNbActiveConstraints(long var0, int var2);

    public int getNbActiveDynamicBodies() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbActiveDynamicBodies(this.address);
    }

    private static native int _getNbActiveDynamicBodies(long var0);

    public void setNbActiveDynamicBodies(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbActiveDynamicBodies(this.address, value);
    }

    private static native void _setNbActiveDynamicBodies(long var0, int var2);

    public int getNbActiveKinematicBodies() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbActiveKinematicBodies(this.address);
    }

    private static native int _getNbActiveKinematicBodies(long var0);

    public void setNbActiveKinematicBodies(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbActiveKinematicBodies(this.address, value);
    }

    private static native void _setNbActiveKinematicBodies(long var0, int var2);

    public int getNbStaticBodies() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbStaticBodies(this.address);
    }

    private static native int _getNbStaticBodies(long var0);

    public void setNbStaticBodies(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbStaticBodies(this.address, value);
    }

    private static native void _setNbStaticBodies(long var0, int var2);

    public int getNbDynamicBodies() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbDynamicBodies(this.address);
    }

    private static native int _getNbDynamicBodies(long var0);

    public void setNbDynamicBodies(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbDynamicBodies(this.address, value);
    }

    private static native void _setNbDynamicBodies(long var0, int var2);

    public int getNbKinematicBodies() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbKinematicBodies(this.address);
    }

    private static native int _getNbKinematicBodies(long var0);

    public void setNbKinematicBodies(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbKinematicBodies(this.address, value);
    }

    private static native void _setNbKinematicBodies(long var0, int var2);

    public int getNbShapes(int index) {
        this.checkNotNull();
        return PxSimulationStatistics._getNbShapes(this.address, index);
    }

    private static native int _getNbShapes(long var0, int var2);

    public void setNbShapes(int index, int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbShapes(this.address, index, value);
    }

    private static native void _setNbShapes(long var0, int var2, int var3);

    public int getNbAggregates() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbAggregates(this.address);
    }

    private static native int _getNbAggregates(long var0);

    public void setNbAggregates(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbAggregates(this.address, value);
    }

    private static native void _setNbAggregates(long var0, int var2);

    public int getNbArticulations() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbArticulations(this.address);
    }

    private static native int _getNbArticulations(long var0);

    public void setNbArticulations(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbArticulations(this.address, value);
    }

    private static native void _setNbArticulations(long var0, int var2);

    public int getNbAxisSolverConstraints() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbAxisSolverConstraints(this.address);
    }

    private static native int _getNbAxisSolverConstraints(long var0);

    public void setNbAxisSolverConstraints(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbAxisSolverConstraints(this.address, value);
    }

    private static native void _setNbAxisSolverConstraints(long var0, int var2);

    public int getCompressedContactSize() {
        this.checkNotNull();
        return PxSimulationStatistics._getCompressedContactSize(this.address);
    }

    private static native int _getCompressedContactSize(long var0);

    public void setCompressedContactSize(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setCompressedContactSize(this.address, value);
    }

    private static native void _setCompressedContactSize(long var0, int var2);

    public int getRequiredContactConstraintMemory() {
        this.checkNotNull();
        return PxSimulationStatistics._getRequiredContactConstraintMemory(this.address);
    }

    private static native int _getRequiredContactConstraintMemory(long var0);

    public void setRequiredContactConstraintMemory(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setRequiredContactConstraintMemory(this.address, value);
    }

    private static native void _setRequiredContactConstraintMemory(long var0, int var2);

    public int getPeakConstraintMemory() {
        this.checkNotNull();
        return PxSimulationStatistics._getPeakConstraintMemory(this.address);
    }

    private static native int _getPeakConstraintMemory(long var0);

    public void setPeakConstraintMemory(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setPeakConstraintMemory(this.address, value);
    }

    private static native void _setPeakConstraintMemory(long var0, int var2);

    public int getNbDiscreteContactPairsTotal() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbDiscreteContactPairsTotal(this.address);
    }

    private static native int _getNbDiscreteContactPairsTotal(long var0);

    public void setNbDiscreteContactPairsTotal(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbDiscreteContactPairsTotal(this.address, value);
    }

    private static native void _setNbDiscreteContactPairsTotal(long var0, int var2);

    public int getNbDiscreteContactPairsWithCacheHits() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbDiscreteContactPairsWithCacheHits(this.address);
    }

    private static native int _getNbDiscreteContactPairsWithCacheHits(long var0);

    public void setNbDiscreteContactPairsWithCacheHits(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbDiscreteContactPairsWithCacheHits(this.address, value);
    }

    private static native void _setNbDiscreteContactPairsWithCacheHits(long var0, int var2);

    public int getNbDiscreteContactPairsWithContacts() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbDiscreteContactPairsWithContacts(this.address);
    }

    private static native int _getNbDiscreteContactPairsWithContacts(long var0);

    public void setNbDiscreteContactPairsWithContacts(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbDiscreteContactPairsWithContacts(this.address, value);
    }

    private static native void _setNbDiscreteContactPairsWithContacts(long var0, int var2);

    public int getNbNewPairs() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbNewPairs(this.address);
    }

    private static native int _getNbNewPairs(long var0);

    public void setNbNewPairs(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbNewPairs(this.address, value);
    }

    private static native void _setNbNewPairs(long var0, int var2);

    public int getNbLostPairs() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbLostPairs(this.address);
    }

    private static native int _getNbLostPairs(long var0);

    public void setNbLostPairs(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbLostPairs(this.address, value);
    }

    private static native void _setNbLostPairs(long var0, int var2);

    public int getNbNewTouches() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbNewTouches(this.address);
    }

    private static native int _getNbNewTouches(long var0);

    public void setNbNewTouches(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbNewTouches(this.address, value);
    }

    private static native void _setNbNewTouches(long var0, int var2);

    public int getNbLostTouches() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbLostTouches(this.address);
    }

    private static native int _getNbLostTouches(long var0);

    public void setNbLostTouches(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbLostTouches(this.address, value);
    }

    private static native void _setNbLostTouches(long var0, int var2);

    public int getNbPartitions() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbPartitions(this.address);
    }

    private static native int _getNbPartitions(long var0);

    public void setNbPartitions(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbPartitions(this.address, value);
    }

    private static native void _setNbPartitions(long var0, int var2);

    public int getNbBroadPhaseAdds() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbBroadPhaseAdds(this.address);
    }

    private static native int _getNbBroadPhaseAdds(long var0);

    public void setNbBroadPhaseAdds(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbBroadPhaseAdds(this.address, value);
    }

    private static native void _setNbBroadPhaseAdds(long var0, int var2);

    public int getNbBroadPhaseRemoves() {
        this.checkNotNull();
        return PxSimulationStatistics._getNbBroadPhaseRemoves(this.address);
    }

    private static native int _getNbBroadPhaseRemoves(long var0);

    public void setNbBroadPhaseRemoves(int value) {
        this.checkNotNull();
        PxSimulationStatistics._setNbBroadPhaseRemoves(this.address, value);
    }

    private static native void _setNbBroadPhaseRemoves(long var0, int var2);
}

