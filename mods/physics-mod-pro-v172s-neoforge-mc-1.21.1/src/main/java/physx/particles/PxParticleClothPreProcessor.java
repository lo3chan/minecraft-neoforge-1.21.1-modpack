/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleClothDesc;
import physx.particles.PxPartitionedParticleCloth;

public class PxParticleClothPreProcessor
extends NativeObject {
    public static final int SIZEOF;
    public static final int ALIGNOF = 8;

    protected PxParticleClothPreProcessor() {
    }

    private static native int __sizeOf();

    public static PxParticleClothPreProcessor wrapPointer(long address) {
        return address != 0L ? new PxParticleClothPreProcessor(address) : null;
    }

    public static PxParticleClothPreProcessor arrayGet(long baseAddress, int index) {
        if (baseAddress == 0L) {
            throw new NullPointerException("baseAddress is 0");
        }
        return PxParticleClothPreProcessor.wrapPointer(baseAddress + (long)SIZEOF * (long)index);
    }

    protected PxParticleClothPreProcessor(long address) {
        super(address);
    }

    public void release() {
        this.checkNotNull();
        PxParticleClothPreProcessor._release(this.address);
    }

    private static native void _release(long var0);

    public void partitionSprings(PxParticleClothDesc clothDesc, PxPartitionedParticleCloth output) {
        this.checkNotNull();
        PxParticleClothPreProcessor._partitionSprings(this.address, clothDesc.getAddress(), output.getAddress());
    }

    private static native void _partitionSprings(long var0, long var2, long var4);

    static {
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothPreProcessor");
        SIZEOF = PxParticleClothPreProcessor.__sizeOf();
    }
}

