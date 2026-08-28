/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 */
package com.sonicether.soundphysics;

import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.integration.voicechat.AudioChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ReflectedAudio {
    private final List<Map.Entry<Vec3, Double>> airspaceDirections;
    @Nullable
    private Map.Entry<Vec3, Double> directDirection;
    private final double occlusion;
    private final ResourceLocation sound;
    private int sharedAirspaces;

    public ReflectedAudio(double occlusion, ResourceLocation sound) {
        this.occlusion = occlusion;
        this.sound = sound;
        this.airspaceDirections = new LinkedList<Map.Entry<Vec3, Double>>();
    }

    public boolean shouldEvaluateDirection() {
        return SoundPhysicsMod.CONFIG.soundDirectionEvaluation.get() != false && (this.occlusion > 0.0 || SoundPhysicsMod.CONFIG.redirectNonOccludedSounds.get() == false) && !AudioChannel.isVoicechatSound(this.sound);
    }

    public int getSharedAirspaces() {
        return this.sharedAirspaces;
    }

    public void addDirectAirspace(Vec3 sharedAirspaceVector) {
        this.directDirection = Map.entry(sharedAirspaceVector, sharedAirspaceVector.length());
    }

    public void addSharedAirspace(Vec3 sharedAirspaceVector, double totalRayDistance) {
        double length = totalRayDistance + sharedAirspaceVector.length();
        if (length <= 0.0 || length > SoundPhysicsMod.CONFIG.maxSoundProcessingDistance.get() / (double)SoundPhysicsMod.CONFIG.attenuationFactor.get().floatValue()) {
            return;
        }
        ++this.sharedAirspaces;
        if (!this.shouldEvaluateDirection()) {
            return;
        }
        this.airspaceDirections.add(Map.entry(sharedAirspaceVector, length));
    }

    @Nullable
    public Vec3 evaluateSoundPosition(Vec3 soundPos, Vec3 listenerPos) {
        if (!this.shouldEvaluateDirection()) {
            return null;
        }
        if (this.airspaceDirections.isEmpty()) {
            return null;
        }
        Vec3 sum = this.directDirection != null ? this.directDirection.getKey().normalize() : new Vec3(0.0, 0.0, 0.0);
        for (Map.Entry<Vec3, Double> direction : this.airspaceDirections) {
            double val = direction.getValue();
            if (val <= 0.0) {
                return null;
            }
            double w = 1.0 / (val * val);
            sum = sum.add(direction.getKey().normalize().scale(w));
        }
        Vec3 normalized = sum.normalize();
        if (normalized.length() < 0.5) {
            return null;
        }
        return normalized.scale(soundPos.distanceTo(listenerPos)).add(listenerPos);
    }
}

