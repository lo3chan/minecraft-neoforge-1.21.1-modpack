/*
 * Decompiled with CFR 0.152.
 */
package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.SoundPhysicsMod;

public class ReverbParams {
    public float decayTime;
    public float density;
    public float diffusion;
    public float gain;
    public float gainHF;
    public float decayHFRatio;
    public float reflectionsGain;
    public float reflectionsDelay;
    public float lateReverbGain;
    public float lateReverbDelay;
    public float airAbsorptionGainHF;
    public float roomRolloffFactor;

    public static ReverbParams getReverb0() {
        ReverbParams r = new ReverbParams();
        r.decayTime = 0.15f;
        r.density = 0.0f;
        r.diffusion = 1.0f;
        r.gain = 0.2f * ReverbParams.globalReverbMultiplier() * 0.85f;
        r.gainHF = 0.99f;
        r.decayHFRatio = 0.6f * SoundPhysicsMod.CONFIG.reverbBrightness.get().floatValue();
        r.reflectionsGain = 2.5f;
        r.reflectionsDelay = 0.001f;
        r.lateReverbGain = 1.26f;
        r.lateReverbDelay = 0.011f;
        r.airAbsorptionGainHF = 0.994f;
        r.roomRolloffFactor = 0.16f * SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
        return r;
    }

    public static ReverbParams getReverb1() {
        ReverbParams r = new ReverbParams();
        r.decayTime = 0.55f;
        r.density = 0.0f;
        r.diffusion = 1.0f;
        r.gain = 0.3f * ReverbParams.globalReverbMultiplier() * 0.85f;
        r.gainHF = 0.99f;
        r.decayHFRatio = 0.7f * SoundPhysicsMod.CONFIG.reverbBrightness.get().floatValue();
        r.reflectionsGain = 0.2f;
        r.reflectionsDelay = 0.015f;
        r.lateReverbGain = 1.26f;
        r.lateReverbDelay = 0.011f;
        r.airAbsorptionGainHF = 0.994f;
        r.roomRolloffFactor = 0.15f * SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
        return r;
    }

    public static ReverbParams getReverb2() {
        ReverbParams r = new ReverbParams();
        r.decayTime = 1.68f;
        r.density = 0.1f;
        r.diffusion = 1.0f;
        r.gain = 0.5f * ReverbParams.globalReverbMultiplier() * 0.85f;
        r.gainHF = 0.99f;
        r.decayHFRatio = 0.7f * SoundPhysicsMod.CONFIG.reverbBrightness.get().floatValue();
        r.reflectionsGain = 0.0f;
        r.reflectionsDelay = 0.021f;
        r.lateReverbGain = 1.26f;
        r.lateReverbDelay = 0.021f;
        r.airAbsorptionGainHF = 0.994f;
        r.roomRolloffFactor = 0.13f * SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
        return r;
    }

    public static ReverbParams getReverb3() {
        ReverbParams r = new ReverbParams();
        r.decayTime = 4.142f;
        r.density = 0.5f;
        r.diffusion = 1.0f;
        r.gain = 0.4f * ReverbParams.globalReverbMultiplier() * 0.85f;
        r.gainHF = 0.89f;
        r.decayHFRatio = 0.7f * SoundPhysicsMod.CONFIG.reverbBrightness.get().floatValue();
        r.reflectionsGain = 0.0f;
        r.reflectionsDelay = 0.025f;
        r.lateReverbGain = 1.26f;
        r.lateReverbDelay = 0.021f;
        r.airAbsorptionGainHF = 0.994f;
        r.roomRolloffFactor = 0.11f * SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue();
        return r;
    }

    private static float globalReverbMultiplier() {
        return 0.7f * SoundPhysicsMod.CONFIG.reverbGain.get().floatValue();
    }
}

