/*
 * Decompiled with CFR 0.152.
 */
package physx.physics;

import physx.common.PxVec3;
import physx.physics.PxArticulationAttachment;
import physx.physics.PxArticulationLink;
import physx.physics.PxArticulationTendon;

public class PxArticulationSpatialTendon
extends PxArticulationTendon {
    public static final int SIZEOF = PxArticulationSpatialTendon.__sizeOf();
    public static final int ALIGNOF = 8;

    protected PxArticulationSpatialTendon() {
    }

    private static native int __sizeOf();

    public static PxArticulationSpatialTendon wrapPointer(long address) {
        return address != 0L ? new PxArticulationSpatialTendon(address) : null;
    }

    public static PxArticulationSpatialTendon arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxArticulationSpatialTendon.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxArticulationSpatialTendon(long address) {
        super(address);
    }

    @Override
    public void destroy() {
        if (this.address == 0L) {
            throw new IllegalStateException(this + " is already deleted");
        }
        if (this.isExternallyAllocated) {
            throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
        }
        PxArticulationSpatialTendon._delete_native_instance(this.address);
        this.address = 0L;
    }

    private static native long _delete_native_instance(long var0);

    public PxArticulationAttachment createAttachment(PxArticulationAttachment parent, float coefficient, PxVec3 relativeOffset, PxArticulationLink link) {
        this.checkNotNull();
        return PxArticulationAttachment.wrapPointer(PxArticulationSpatialTendon._createAttachment(this.address, parent.getAddress(), coefficient, relativeOffset.getAddress(), link.getAddress()));
    }

    private static native long _createAttachment(long var0, long var2, float var4, long var5, long var7);

    public int getNbAttachments() {
        this.checkNotNull();
        return PxArticulationSpatialTendon._getNbAttachments(this.address);
    }

    private static native int _getNbAttachments(long var0);
}

