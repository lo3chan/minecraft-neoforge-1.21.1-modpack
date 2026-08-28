/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.joml.Math
 *  org.joml.Vector2d
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 */
package net.diebuddies.minecraft.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.wind.WeatherDomain;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WindSoundInstance
extends AbstractTickableSoundInstance {
    private float volumeTarget;
    private float pitchTarget;
    private float tmpVolume;

    public WindSoundInstance(SoundEvent soundEvent, SoundSource soundSource) {
        super(soundEvent, soundSource, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.relative = true;
        this.volume = 0.0f;
        this.volumeTarget = 0.0f;
        this.pitch = 1.0f;
        this.pitchTarget = 1.0f;
    }

    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        BlockPos camPos = camera.getBlockPosition();
        int camX = Mth.floor((double)camera.getPosition().x);
        int camY = Mth.floor((double)camera.getPosition().y);
        int camZ = Mth.floor((double)camera.getPosition().z);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            this.stopWind();
            return;
        }
        double distance = level.getHeight(Heightmap.Types.MOTION_BLOCKING, camPos.getX(), camPos.getZ()) - camPos.getY();
        if (distance > 0.0) {
            distance *= distance;
            int checkRange = 6;
            for (int zo = camZ - checkRange; zo <= camZ + checkRange; ++zo) {
                for (int xo = camX - checkRange; xo <= camX + checkRange; ++xo) {
                    int diffX = xo - camX;
                    int diffZ = zo - camZ;
                    if (diffX * diffX + diffZ * diffZ > 36) continue;
                    int rainToThisHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, xo, zo);
                    double dsquared = 0.0;
                    dsquared = rainToThisHeight <= camPos.getY() ? Vector2d.distanceSquared((double)camPos.getX(), (double)camPos.getZ(), (double)xo, (double)zo) : Vector3d.distanceSquared((double)camPos.getX(), (double)camPos.getY(), (double)camPos.getZ(), (double)xo, (double)rainToThisHeight, (double)zo);
                    if (!(dsquared < distance)) continue;
                    distance = dsquared;
                }
            }
            distance = java.lang.Math.sqrt(distance);
        } else {
            distance = 0.0;
        }
        WeatherDomain weatherDomain = PhysicsMod.getInstance((Level)level).getPhysicsWorld().getWeatherDomain();
        Vector3f windDirection = weatherDomain.getWindDirection(camX, camY, camZ);
        float distanceBasedVolume = 1.0f - Math.clamp((float)distance / 6.0f, 0.0f, 1.0f);
        float strengthBasedVolume = Math.clamp(weatherDomain.getWindStrengthFast() / 0.9f, 0.0f, 1.0f);
        this.pitchTarget = strengthBasedVolume > 0.3f ? 1.0f + java.lang.Math.max(0.0f, windDirection.length() - 0.4f) : 1.0f;
        if (strengthBasedVolume < 0.12f) {
            strengthBasedVolume = Math.remapClamp(java.lang.Math.max(0.0f, windDirection.length() - 0.3f), 0.0f, 0.3f, 0.0f, 0.08f);
        }
        this.volumeTarget = distanceBasedVolume * strengthBasedVolume;
        this.tmpVolume = org.joml.Math.lerp((float)this.tmpVolume, (float)this.volumeTarget, (float)0.05f);
        this.volume = this.tmpVolume * ConfigClient.windVolume;
        this.pitch = org.joml.Math.lerp((float)this.pitch, (float)this.pitchTarget, (float)0.075f);
    }

    public boolean canStartSilent() {
        return true;
    }

    public void stopWind() {
        this.stop();
    }
}

