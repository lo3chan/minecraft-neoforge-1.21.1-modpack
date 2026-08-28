/*
 * Decompiled with CFR 0.152.
 */
package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleBufferFlagEnum {
    eNONE(PxParticleBufferFlagEnum.geteNONE()),
    eUPDATE_POSITION(PxParticleBufferFlagEnum.geteUPDATE_POSITION()),
    eUPDATE_VELOCITY(PxParticleBufferFlagEnum.geteUPDATE_VELOCITY()),
    eUPDATE_PHASE(PxParticleBufferFlagEnum.geteUPDATE_PHASE()),
    eUPDATE_RESTPOSITION(PxParticleBufferFlagEnum.geteUPDATE_RESTPOSITION()),
    eUPDATE_CLOTH(PxParticleBufferFlagEnum.geteUPDATE_CLOTH()),
    eUPDATE_RIGID(PxParticleBufferFlagEnum.geteUPDATE_RIGID()),
    eUPDATE_DIFFUSE_PARAM(PxParticleBufferFlagEnum.geteUPDATE_DIFFUSE_PARAM()),
    eUPDATE_ATTACHMENTS(PxParticleBufferFlagEnum.geteUPDATE_ATTACHMENTS()),
    eALL(PxParticleBufferFlagEnum.geteALL());

    public final int value;

    private PxParticleBufferFlagEnum(int value) {
        this.value = value;
    }

    private static native int _geteNONE();

    private static int geteNONE() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteNONE();
    }

    private static native int _geteUPDATE_POSITION();

    private static int geteUPDATE_POSITION() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_POSITION();
    }

    private static native int _geteUPDATE_VELOCITY();

    private static int geteUPDATE_VELOCITY() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_VELOCITY();
    }

    private static native int _geteUPDATE_PHASE();

    private static int geteUPDATE_PHASE() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_PHASE();
    }

    private static native int _geteUPDATE_RESTPOSITION();

    private static int geteUPDATE_RESTPOSITION() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_RESTPOSITION();
    }

    private static native int _geteUPDATE_CLOTH();

    private static int geteUPDATE_CLOTH() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_CLOTH();
    }

    private static native int _geteUPDATE_RIGID();

    private static int geteUPDATE_RIGID() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_RIGID();
    }

    private static native int _geteUPDATE_DIFFUSE_PARAM();

    private static int geteUPDATE_DIFFUSE_PARAM() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_DIFFUSE_PARAM();
    }

    private static native int _geteUPDATE_ATTACHMENTS();

    private static int geteUPDATE_ATTACHMENTS() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteUPDATE_ATTACHMENTS();
    }

    private static native int _geteALL();

    private static int geteALL() {
        Loader.load();
        PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferFlagEnum");
        return PxParticleBufferFlagEnum._geteALL();
    }

    public static PxParticleBufferFlagEnum forValue(int value) {
        for (int i = 0; i < PxParticleBufferFlagEnum.values().length; ++i) {
            if (PxParticleBufferFlagEnum.values()[i].value != value) continue;
            return PxParticleBufferFlagEnum.values()[i];
        }
        throw new IllegalArgumentException("Unknown value for enum PxParticleBufferFlagEnum: " + value);
    }
}

