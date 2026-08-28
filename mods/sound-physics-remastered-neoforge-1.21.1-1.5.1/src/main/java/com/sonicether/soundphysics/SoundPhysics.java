/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.AL11
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.EXTEfx
 */
package com.sonicether.soundphysics;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.ReflectedAudio;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.ReverbParams;
import com.sonicether.soundphysics.debug.RaycastRenderer;
import com.sonicether.soundphysics.profiling.TaskProfiler;
import com.sonicether.soundphysics.utils.LevelAccessUtils;
import com.sonicether.soundphysics.utils.RaycastUtils;
import com.sonicether.soundphysics.utils.SoundRateManager;
import com.sonicether.soundphysics.world.ClientLevelProxy;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;

public class SoundPhysics {
    private static final float PHI = 1.618034f;
    private static final Pattern AMBIENT_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+:ambient\\..*$");
    private static final Pattern BLOCK_PATTERN = Pattern.compile(".*block..*");
    private static int auxFXSlot0;
    private static int auxFXSlot1;
    private static int auxFXSlot2;
    private static int auxFXSlot3;
    private static int reverb0;
    private static int reverb1;
    private static int reverb2;
    private static int reverb3;
    private static int directFilter0;
    private static int sendFilter0;
    private static int sendFilter1;
    private static int sendFilter2;
    private static int sendFilter3;
    private static Minecraft minecraft;
    private static TaskProfiler profiler;
    private static SoundSource lastSoundCategory;
    private static ResourceLocation lastSound;
    private static int maxAuxSends;

    public static void init() {
        Loggers.log("Initializing Sound Physics", new Object[0]);
        SoundPhysics.setupEFX();
        Loggers.log("EFX ready", new Object[0]);
        minecraft = Minecraft.getInstance();
        profiler = new TaskProfiler("Sound Physics");
    }

    public static void syncReverbParams() {
        if (auxFXSlot0 != 0) {
            SoundPhysics.setReverbParams(ReverbParams.getReverb0(), auxFXSlot0, reverb0);
            SoundPhysics.setReverbParams(ReverbParams.getReverb1(), auxFXSlot1, reverb1);
            SoundPhysics.setReverbParams(ReverbParams.getReverb2(), auxFXSlot2, reverb2);
            SoundPhysics.setReverbParams(ReverbParams.getReverb3(), auxFXSlot3, reverb3);
        }
    }

    static void setupEFX() {
        long currentContext = ALC10.alcGetCurrentContext();
        long currentDevice = ALC10.alcGetContextsDevice((long)currentContext);
        if (!ALC10.alcIsExtensionPresent((long)currentDevice, (CharSequence)"ALC_EXT_EFX")) {
            Loggers.error("EFX Extension not found on current device. Aborting.", new Object[0]);
            return;
        }
        Loggers.log("EFX Extension recognized", new Object[0]);
        maxAuxSends = ALC10.alcGetInteger((long)currentDevice, (int)131075);
        Loggers.log("Max auxiliary sends: {}", maxAuxSends);
        auxFXSlot0 = EXTEfx.alGenAuxiliaryEffectSlots();
        Loggers.log("Aux slot {} created", auxFXSlot0);
        EXTEfx.alAuxiliaryEffectSloti((int)auxFXSlot0, (int)3, (int)1);
        auxFXSlot1 = EXTEfx.alGenAuxiliaryEffectSlots();
        Loggers.log("Aux slot {} created", auxFXSlot1);
        EXTEfx.alAuxiliaryEffectSloti((int)auxFXSlot1, (int)3, (int)1);
        auxFXSlot2 = EXTEfx.alGenAuxiliaryEffectSlots();
        Loggers.log("Aux slot {} created", auxFXSlot2);
        EXTEfx.alAuxiliaryEffectSloti((int)auxFXSlot2, (int)3, (int)1);
        auxFXSlot3 = EXTEfx.alGenAuxiliaryEffectSlots();
        Loggers.log("Aux slot {} created", auxFXSlot3);
        EXTEfx.alAuxiliaryEffectSloti((int)auxFXSlot3, (int)3, (int)1);
        Loggers.logALError("Failed creating auxiliary effect slots");
        reverb0 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti((int)reverb0, (int)32769, (int)32768);
        Loggers.logALError("Failed creating reverb effect slot 0");
        reverb1 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti((int)reverb1, (int)32769, (int)32768);
        Loggers.logALError("Failed creating reverb effect slot 1");
        reverb2 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti((int)reverb2, (int)32769, (int)32768);
        Loggers.logALError("Failed creating reverb effect slot 2");
        reverb3 = EXTEfx.alGenEffects();
        EXTEfx.alEffecti((int)reverb3, (int)32769, (int)32768);
        Loggers.logALError("Failed creating reverb effect slot 3");
        directFilter0 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri((int)directFilter0, (int)32769, (int)1);
        Loggers.logDebug("directFilter0: {}", directFilter0);
        sendFilter0 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri((int)sendFilter0, (int)32769, (int)1);
        Loggers.logDebug("filter0: {}", sendFilter0);
        sendFilter1 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri((int)sendFilter1, (int)32769, (int)1);
        Loggers.logDebug("filter1: {}", sendFilter1);
        sendFilter2 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri((int)sendFilter2, (int)32769, (int)1);
        Loggers.logDebug("filter2: {}", sendFilter2);
        sendFilter3 = EXTEfx.alGenFilters();
        EXTEfx.alFilteri((int)sendFilter3, (int)32769, (int)1);
        Loggers.logDebug("filter3: {}", sendFilter3);
        Loggers.logALError("Error creating lowpass filters");
        SoundPhysics.syncReverbParams();
    }

    public static void setLastSoundCategoryAndName(SoundSource sc, ResourceLocation id) {
        lastSoundCategory = sc;
        lastSound = id;
    }

    public static void onPlaySound(double posX, double posY, double posZ, int sourceID) {
        SoundPhysics.processSound(sourceID, posX, posY, posZ, lastSoundCategory, lastSound, false);
    }

    public static void onPlayReverb(double posX, double posY, double posZ, int sourceID) {
        SoundPhysics.processSound(sourceID, posX, posY, posZ, lastSoundCategory, lastSound, true);
    }

    public static Vec3 processSound(int source, double posX, double posY, double posZ, SoundSource category, ResourceLocation sound) {
        return SoundPhysics.processSound(source, posX, posY, posZ, category, sound, false);
    }

    @Nullable
    public static Vec3 processSound(int source, double posX, double posY, double posZ, SoundSource category, ResourceLocation sound, boolean auxOnly) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return null;
        }
        Loggers.logDebug("Playing sound with source id '{}', position x:{}, y:{}, z:{}, \tcategory: '{}' \tname: '{}'", source, posX, posY, posZ, category.toString(), sound);
        TaskProfiler.TaskProfilerHandle profile = profiler.profile();
        Vec3 newPos = SoundPhysics.evaluateEnvironment(source, posX, posY, posZ, category, sound, auxOnly);
        profile.finish();
        Loggers.logProfiling("Evaluated environment for sound {} in {} ms", sound, profile.getDuration());
        profiler.onTally(() -> profiler.logResults());
        return newPos;
    }

    @Nullable
    private static Vec3 evaluateEnvironment(int sourceID, double posX, double posY, double posZ, SoundSource category, ResourceLocation sound, boolean auxOnly) {
        int i;
        LocalPlayer player = SoundPhysics.minecraft.player;
        ClientLevel level = SoundPhysics.minecraft.level;
        if (player == null || level == null || posX == 0.0 && posY == 0.0 && posZ == 0.0) {
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        Vec3 soundPos = new Vec3(posX, posY, posZ);
        double distance = player.position().distanceTo(soundPos);
        if (distance > SoundPhysicsMod.CONFIG.maxSoundProcessingDistance.get()) {
            Loggers.logDebug("Sound {} is too far away from player ({} blocks)", sound, distance);
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        if (!SoundPhysicsMod.CONFIG.updateMovingSounds.get().booleanValue() && category == SoundSource.RECORDS) {
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        if (!SoundRateManager.isWorldInitialized()) {
            Loggers.logDebug("Sound {} skipped because the world is not initialized yet", sound);
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        if (SoundRateManager.incrementAndCheckLimit(sound)) {
            Loggers.logDebug("Sound {} skipped due to sound rate limit", sound);
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        if (!SoundPhysicsMod.CONFIG.evaluateAmbientSounds.get().booleanValue() && SoundPhysics.isAmbientSound(sound)) {
            Loggers.logDebug("Sound {} skipped due to ambient sound evaluation option", sound);
            SoundPhysics.setDefaultEnvironment(sourceID, auxOnly);
            return null;
        }
        float absorptionCoeff = (float)((double)SoundPhysicsMod.CONFIG.blockAbsorption.get().floatValue() * 3.0);
        Vec3 playerPos = SoundPhysics.minecraft.gameRenderer.getMainCamera().getPosition();
        Vec3 normalToPlayer = playerPos.subtract(soundPos).normalize();
        BlockPos soundBlockPos = BlockPos.containing((Position)soundPos);
        FluidState soundFluidState = SoundPhysics.getLevelProxy().getFluidState(soundBlockPos);
        boolean sourceIsUnderwater = soundFluidState.is(FluidTags.WATER);
        Loggers.logDebug("Player pos: {}, {}, {} \tSound Pos: {}, {}, {} \tTo player vector: {}, {}, {}", playerPos.x, playerPos.y, playerPos.z, soundPos.x, soundPos.y, soundPos.z, normalToPlayer.x, normalToPlayer.y, normalToPlayer.z);
        double occlusionAccumulation = SoundPhysics.calculateOcclusion(soundPos, playerPos, category, sound);
        float directCutoff = (float)Math.exp(-occlusionAccumulation * (double)absorptionCoeff);
        float directGain = auxOnly ? 0.0f : (float)Math.pow(directCutoff, 0.1);
        Loggers.logOcclusion("Direct cutoff: {}, direct gain: {}", Float.valueOf(directCutoff), Float.valueOf(directGain));
        float sendGain0 = 0.0f;
        float sendGain1 = 0.0f;
        float sendGain2 = 0.0f;
        float sendGain3 = 0.0f;
        float sendCutoff0 = 1.0f;
        float sendCutoff1 = 1.0f;
        float sendCutoff2 = 1.0f;
        float sendCutoff3 = 1.0f;
        if (SoundPhysics.minecraft.player.isUnderWater() || sourceIsUnderwater) {
            directCutoff *= 1.0f - SoundPhysicsMod.CONFIG.underwaterFilter.get().floatValue();
        }
        float maxDistance = 256.0f;
        int numRays = SoundPhysicsMod.CONFIG.environmentEvaluationRayCount.get();
        int rayBounces = SoundPhysicsMod.CONFIG.environmentEvaluationRayBounces.get();
        ReflectedAudio audioDirection = new ReflectedAudio(occlusionAccumulation, sound);
        float[] bounceReflectivityRatio = new float[rayBounces];
        float rcpTotalRays = 1.0f / (float)(numRays * rayBounces);
        float gAngle = 10.166408f;
        Vec3 directSharedAirspaceVector = SoundPhysics.getSharedAirspace(soundPos, playerPos);
        if (directSharedAirspaceVector != null) {
            audioDirection.addDirectAirspace(directSharedAirspaceVector);
        }
        block0: for (i = 0; i < numRays; ++i) {
            float fiN = (float)i / (float)numRays;
            float longitude = gAngle * (float)i * 1.0f;
            float latitude = (float)Math.asin(fiN * 2.0f - 1.0f);
            Vec3 rayDir = new Vec3(Math.cos(latitude) * Math.cos(longitude), Math.cos(latitude) * Math.sin(longitude), Math.sin(latitude));
            Vec3 rayEnd = new Vec3(soundPos.x + rayDir.x * (double)maxDistance, soundPos.y + rayDir.y * (double)maxDistance, soundPos.z + rayDir.z * (double)maxDistance);
            BlockHitResult rayHit = RaycastUtils.rayCast(SoundPhysics.getLevelProxy(), soundPos, rayEnd, soundBlockPos);
            if (rayHit.getType() != HitResult.Type.BLOCK) continue;
            double rayLength = soundPos.distanceTo(rayHit.getLocation());
            BlockPos lastHitBlock = rayHit.getBlockPos();
            Vec3 lastHitPos = rayHit.getLocation();
            Vec3 lastHitNormal = new Vec3(rayHit.getDirection().step());
            Vec3 lastRayDir = rayDir;
            float totalRayDistance = (float)rayLength;
            RaycastRenderer.addSoundBounceRay(soundPos, rayHit.getLocation(), ChatFormatting.GREEN.getColor());
            Vec3 firstSharedAirspaceVector = SoundPhysics.getSharedAirspace(rayHit, playerPos);
            if (firstSharedAirspaceVector != null) {
                audioDirection.addSharedAirspace(firstSharedAirspaceVector, totalRayDistance);
            }
            for (int j = 0; j < rayBounces; ++j) {
                Vec3 newRayDir = SoundPhysics.reflect(lastRayDir, lastHitNormal);
                Vec3 newRayStart = lastHitPos;
                Vec3 newRayEnd = new Vec3(newRayStart.x + newRayDir.x * (double)maxDistance, newRayStart.y + newRayDir.y * (double)maxDistance, newRayStart.z + newRayDir.z * (double)maxDistance);
                BlockHitResult newRayHit = RaycastUtils.rayCast(SoundPhysics.getLevelProxy(), newRayStart, newRayEnd, lastHitBlock);
                float blockReflectivity = SoundPhysics.getBlockReflectivity(lastHitBlock);
                float energyTowardsPlayer = 0.25f * (blockReflectivity * 0.75f + 0.25f);
                if (newRayHit.getType() == HitResult.Type.MISS) {
                    totalRayDistance = (float)((double)totalRayDistance + lastHitPos.distanceTo(playerPos));
                    RaycastRenderer.addSoundBounceRay(newRayStart, newRayEnd, ChatFormatting.RED.getColor());
                } else {
                    Vec3 newRayHitPos = newRayHit.getLocation();
                    RaycastRenderer.addSoundBounceRay(newRayStart, newRayHitPos, ChatFormatting.BLUE.getColor());
                    double newRayLength = lastHitPos.distanceTo(newRayHitPos);
                    int n = j;
                    bounceReflectivityRatio[n] = bounceReflectivityRatio[n] + blockReflectivity;
                    totalRayDistance = (float)((double)totalRayDistance + newRayLength);
                    lastHitPos = newRayHitPos;
                    lastHitNormal = new Vec3(newRayHit.getDirection().step());
                    lastRayDir = newRayDir;
                    lastHitBlock = newRayHit.getBlockPos();
                    Vec3 sharedAirspaceVector = SoundPhysics.getSharedAirspace(newRayHit, playerPos);
                    if (sharedAirspaceVector != null) {
                        audioDirection.addSharedAirspace(sharedAirspaceVector, totalRayDistance);
                    }
                }
                if (totalRayDistance < SoundPhysicsMod.CONFIG.reverbAttenuationDistance.get().floatValue()) continue;
                float reflectionDelay = (float)Math.max((double)totalRayDistance, 0.0) * 0.12f * blockReflectivity;
                float cross0 = 1.0f - Mth.clamp((float)Math.abs(reflectionDelay - 0.0f), (float)0.0f, (float)1.0f);
                float cross1 = 1.0f - Mth.clamp((float)Math.abs(reflectionDelay - 1.0f), (float)0.0f, (float)1.0f);
                float cross2 = 1.0f - Mth.clamp((float)Math.abs(reflectionDelay - 2.0f), (float)0.0f, (float)1.0f);
                float cross3 = Mth.clamp((float)(reflectionDelay - 2.0f), (float)0.0f, (float)1.0f);
                sendGain0 += cross0 * energyTowardsPlayer * 6.4f * rcpTotalRays;
                sendGain1 += cross1 * energyTowardsPlayer * 12.8f * rcpTotalRays;
                sendGain2 += cross2 * energyTowardsPlayer * 12.8f * rcpTotalRays;
                sendGain3 += cross3 * energyTowardsPlayer * 12.8f * rcpTotalRays;
                if (newRayHit.getType() == HitResult.Type.MISS) continue block0;
            }
        }
        for (i = 0; i < bounceReflectivityRatio.length; ++i) {
            bounceReflectivityRatio[i] = bounceReflectivityRatio[i] / (float)numRays;
            Loggers.logEnvironment("Bounce reflectivity {}: {}", i, Float.valueOf(bounceReflectivityRatio[i]));
        }
        Vec3 newSoundPos = audioDirection.evaluateSoundPosition(soundPos, playerPos);
        if (newSoundPos != null) {
            SoundPhysics.setSoundPos(sourceID, newSoundPos);
            soundPos = newSoundPos;
        }
        float sharedAirspace = (float)audioDirection.getSharedAirspaces() * 64.0f * rcpTotalRays;
        Loggers.logEnvironment("Shared airspace: {} ({})", Float.valueOf(sharedAirspace), audioDirection.getSharedAirspaces());
        float sharedAirspaceWeight0 = Mth.clamp((float)(sharedAirspace / 20.0f), (float)0.0f, (float)1.0f);
        float sharedAirspaceWeight1 = Mth.clamp((float)(sharedAirspace / 15.0f), (float)0.0f, (float)1.0f);
        float sharedAirspaceWeight2 = Mth.clamp((float)(sharedAirspace / 10.0f), (float)0.0f, (float)1.0f);
        float sharedAirspaceWeight3 = Mth.clamp((float)(sharedAirspace / 10.0f), (float)0.0f, (float)1.0f);
        sendCutoff0 = (float)Math.exp(-occlusionAccumulation * (double)absorptionCoeff * 1.0) * (1.0f - sharedAirspaceWeight0) + sharedAirspaceWeight0;
        sendCutoff1 = (float)Math.exp(-occlusionAccumulation * (double)absorptionCoeff * 1.0) * (1.0f - sharedAirspaceWeight1) + sharedAirspaceWeight1;
        sendCutoff2 = (float)Math.exp(-occlusionAccumulation * (double)absorptionCoeff * 1.0) * (1.0f - sharedAirspaceWeight2) + sharedAirspaceWeight2;
        sendCutoff3 = (float)Math.exp(-occlusionAccumulation * (double)absorptionCoeff * 1.0) * (1.0f - sharedAirspaceWeight3) + sharedAirspaceWeight3;
        float averageSharedAirspace = (sharedAirspaceWeight0 + sharedAirspaceWeight1 + sharedAirspaceWeight2 + sharedAirspaceWeight3) * 0.25f;
        directCutoff = Math.max((float)Math.pow(averageSharedAirspace, 0.5) * 0.2f, directCutoff);
        directGain = auxOnly ? 0.0f : (float)Math.pow(directCutoff, 0.1);
        sendGain1 *= bounceReflectivityRatio[1];
        if (bounceReflectivityRatio.length > 2) {
            sendGain2 *= (float)Math.pow(bounceReflectivityRatio[2], 3.0);
        }
        if (bounceReflectivityRatio.length > 3) {
            sendGain3 *= (float)Math.pow(bounceReflectivityRatio[3], 4.0);
        }
        sendGain0 = Mth.clamp((float)sendGain0, (float)0.0f, (float)1.0f);
        sendGain1 = Mth.clamp((float)sendGain1, (float)0.0f, (float)1.0f);
        sendGain2 = Mth.clamp((float)(sendGain2 * 1.05f - 0.05f), (float)0.0f, (float)1.0f);
        sendGain3 = Mth.clamp((float)(sendGain3 * 1.05f - 0.05f), (float)0.0f, (float)1.0f);
        sendGain0 *= (float)Math.pow(sendCutoff0, 0.1);
        sendGain1 *= (float)Math.pow(sendCutoff1, 0.1);
        sendGain2 *= (float)Math.pow(sendCutoff2, 0.1);
        sendGain3 *= (float)Math.pow(sendCutoff3, 0.1);
        float soundDistance = (float)playerPos.distanceTo(soundPos);
        float maxSoundDistance = AL10.alGetSourcef((int)sourceID, (int)4131);
        float sendGainMultiplier = 1.0f - Math.min(soundDistance / (maxSoundDistance * SoundPhysicsMod.CONFIG.reverbDistance.get().floatValue()), 1.0f);
        sendGain0 = sendGainMultiplier * sendGain0;
        sendGain1 = sendGainMultiplier * sendGain1;
        sendGain2 = sendGainMultiplier * sendGain2;
        sendGain3 = sendGainMultiplier * sendGain3;
        Loggers.logEnvironment("Final environment settings: {}, {}, {}, {}", Float.valueOf(sendGain0), Float.valueOf(sendGain1), Float.valueOf(sendGain2), Float.valueOf(sendGain3));
        assert (SoundPhysics.minecraft.player != null);
        if (SoundPhysics.minecraft.player.isUnderWater() || sourceIsUnderwater) {
            sendCutoff0 *= 0.4f;
            sendCutoff1 *= 0.4f;
            sendCutoff2 *= 0.4f;
            sendCutoff3 *= 0.4f;
        }
        SoundPhysics.setEnvironment(sourceID, sendGain0, sendGain1, sendGain2, sendGain3, sendCutoff0, sendCutoff1, sendCutoff2, sendCutoff3, directCutoff, directGain);
        return newSoundPos;
    }

    public static boolean isAmbientSound(ResourceLocation sound) {
        return AMBIENT_PATTERN.matcher(sound.toString()).matches();
    }

    private static float getBlockReflectivity(BlockPos blockPos) {
        ClientLevelProxy levelProxy = SoundPhysics.getLevelProxy();
        if (levelProxy == null) {
            return SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get().floatValue();
        }
        BlockState blockState = levelProxy.getBlockState(blockPos);
        return SoundPhysicsMod.REFLECTIVITY_CONFIG.getBlockDefinitionValue(blockState);
    }

    private static Vec3 reflect(Vec3 dir, Vec3 normal) {
        double dot = dir.dot(normal) * 2.0;
        double x = dir.x - dot * normal.x;
        double y = dir.y - dot * normal.y;
        double z = dir.z - dot * normal.z;
        return new Vec3(x, y, z);
    }

    private static double calculateOcclusion(Vec3 soundPos, Vec3 playerPos, SoundSource category, ResourceLocation sound) {
        if (SoundPhysicsMod.CONFIG.strictOcclusion.get().booleanValue()) {
            return Math.min(SoundPhysics.runOcclusion(soundPos, playerPos), (double)SoundPhysicsMod.CONFIG.maxOcclusion.get().floatValue());
        }
        boolean isBlock = category == SoundSource.BLOCKS || BLOCK_PATTERN.matcher(sound.toString()).matches();
        double variationFactor = SoundPhysicsMod.CONFIG.occlusionVariation.get().floatValue();
        if (isBlock) {
            variationFactor = Math.max(variationFactor, 0.49);
        }
        double occlusionAccMin = Double.MAX_VALUE;
        occlusionAccMin = Math.min(occlusionAccMin, SoundPhysics.runOcclusion(soundPos, playerPos));
        if (variationFactor > 0.0) {
            for (int x = -1; x <= 1; x += 2) {
                for (int y = -1; y <= 1; y += 2) {
                    for (int z = -1; z <= 1; z += 2) {
                        Vec3 offset = new Vec3((double)x, (double)y, (double)z).scale(variationFactor);
                        occlusionAccMin = Math.min(occlusionAccMin, SoundPhysics.runOcclusion(soundPos.add(offset), playerPos.add(offset)));
                    }
                }
            }
        }
        return Math.min(occlusionAccMin, (double)SoundPhysicsMod.CONFIG.maxOcclusion.get().floatValue());
    }

    private static double runOcclusion(Vec3 soundPos, Vec3 playerPos) {
        ClientLevelProxy levelProxy = SoundPhysics.getLevelProxy();
        if (levelProxy == null) {
            return 0.0;
        }
        double occlusionAccumulation = 0.0;
        Vec3 rayOrigin = soundPos;
        BlockPos lastBlockPos = BlockPos.containing((Position)soundPos);
        for (int i = 0; i < SoundPhysicsMod.CONFIG.maxOcclusionRays.get(); ++i) {
            BlockHitResult rayHit = RaycastUtils.rayCast(SoundPhysics.getLevelProxy(), rayOrigin, playerPos, lastBlockPos);
            lastBlockPos = rayHit.getBlockPos();
            if (rayHit.getType() == HitResult.Type.MISS) {
                RaycastRenderer.addOcclusionRay(rayOrigin, playerPos.add(0.0, -0.1, 0.0), Mth.hsvToRgb((float)(0.33333334f * (1.0f - Math.min(1.0f, (float)occlusionAccumulation / 12.0f))), (float)1.0f, (float)1.0f));
                break;
            }
            RaycastRenderer.addOcclusionRay(rayOrigin, rayHit.getLocation(), Mth.hsvToRgb((float)(0.33333334f * (1.0f - Math.min(1.0f, (float)occlusionAccumulation / 12.0f))), (float)1.0f, (float)1.0f));
            BlockPos blockHitPos = rayHit.getBlockPos();
            rayOrigin = rayHit.getLocation();
            BlockState blockHit = levelProxy.getBlockState(blockHitPos);
            float blockOcclusion = SoundPhysicsMod.OCCLUSION_CONFIG.getBlockDefinitionValue(blockHit);
            Vec3 dirVec = rayOrigin.subtract((double)blockHitPos.getX() + 0.5, (double)blockHitPos.getY() + 0.5, (double)blockHitPos.getZ() + 0.5);
            Direction sideHit = Direction.getNearest((double)dirVec.x, (double)dirVec.y, (double)dirVec.z);
            if (!blockHit.isFaceSturdy((BlockGetter)levelProxy, rayHit.getBlockPos(), sideHit)) {
                blockOcclusion *= SoundPhysicsMod.CONFIG.nonFullBlockOcclusionFactor.get().floatValue();
            }
            Loggers.logOcclusion("{} \t{},{},{}", blockHit.getBlock().getDescriptionId(), rayOrigin.x, rayOrigin.y, rayOrigin.z);
            occlusionAccumulation += (double)blockOcclusion;
            if (!(occlusionAccumulation > (double)SoundPhysicsMod.CONFIG.maxOcclusion.get().floatValue())) continue;
            Loggers.logOcclusion("Max occlusion reached after {} steps", i + 1);
            break;
        }
        return occlusionAccumulation;
    }

    private static ClientLevelProxy getLevelProxy() {
        return LevelAccessUtils.getClientLevelProxy(minecraft);
    }

    @Nullable
    private static Vec3 getSharedAirspace(BlockHitResult hit, Vec3 listenerPosition) {
        Vector3f hitNormal = hit.getDirection().step();
        Vec3 rayStart = new Vec3(hit.getLocation().x + (double)hitNormal.x() * 0.001, hit.getLocation().y + (double)hitNormal.y() * 0.001, hit.getLocation().z + (double)hitNormal.z() * 0.001);
        return SoundPhysics.getSharedAirspace(rayStart, listenerPosition);
    }

    @Nullable
    private static Vec3 getSharedAirspace(Vec3 soundPosition, Vec3 listenerPosition) {
        BlockHitResult finalRayHit = RaycastUtils.rayCast(SoundPhysics.getLevelProxy(), soundPosition, listenerPosition, null);
        if (finalRayHit.getType() == HitResult.Type.MISS) {
            RaycastRenderer.addSoundBounceRay(soundPosition, listenerPosition.add(0.0, -0.1, 0.0), ChatFormatting.WHITE.getColor());
            return soundPosition.subtract(listenerPosition);
        }
        return null;
    }

    public static void setDefaultEnvironment(int sourceID) {
        SoundPhysics.setDefaultEnvironment(sourceID, false);
    }

    public static void setDefaultEnvironment(int sourceID, boolean auxOnly) {
        SoundPhysics.setEnvironment(sourceID, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, auxOnly ? 0.0f : 1.0f);
    }

    public static void setEnvironment(int sourceID, float sendGain0, float sendGain1, float sendGain2, float sendGain3, float sendCutoff0, float sendCutoff1, float sendCutoff2, float sendCutoff3, float directCutoff, float directGain) {
        if (!SoundPhysicsMod.CONFIG.enabled.get().booleanValue()) {
            return;
        }
        if (maxAuxSends >= 4) {
            EXTEfx.alFilterf((int)sendFilter0, (int)1, (float)sendGain0);
            EXTEfx.alFilterf((int)sendFilter0, (int)2, (float)sendCutoff0);
            AL11.alSource3i((int)sourceID, (int)131078, (int)auxFXSlot0, (int)3, (int)sendFilter0);
            Loggers.logALError("Set environment filter0:");
        }
        if (maxAuxSends >= 3) {
            EXTEfx.alFilterf((int)sendFilter1, (int)1, (float)sendGain1);
            EXTEfx.alFilterf((int)sendFilter1, (int)2, (float)sendCutoff1);
            AL11.alSource3i((int)sourceID, (int)131078, (int)auxFXSlot1, (int)2, (int)sendFilter1);
            Loggers.logALError("Set environment filter1:");
        }
        if (maxAuxSends >= 2) {
            EXTEfx.alFilterf((int)sendFilter2, (int)1, (float)sendGain2);
            EXTEfx.alFilterf((int)sendFilter2, (int)2, (float)sendCutoff2);
            AL11.alSource3i((int)sourceID, (int)131078, (int)auxFXSlot2, (int)1, (int)sendFilter2);
            Loggers.logALError("Set environment filter2:");
        }
        if (maxAuxSends >= 1) {
            EXTEfx.alFilterf((int)sendFilter3, (int)1, (float)sendGain3);
            EXTEfx.alFilterf((int)sendFilter3, (int)2, (float)sendCutoff3);
            AL11.alSource3i((int)sourceID, (int)131078, (int)auxFXSlot3, (int)0, (int)sendFilter3);
            Loggers.logALError("Set environment filter3:");
        }
        EXTEfx.alFilterf((int)directFilter0, (int)1, (float)directGain);
        EXTEfx.alFilterf((int)directFilter0, (int)2, (float)directCutoff);
        AL11.alSourcei((int)sourceID, (int)131077, (int)directFilter0);
        Loggers.logALError("Set environment directFilter0:");
        AL11.alSourcef((int)sourceID, (int)131079, (float)SoundPhysicsMod.CONFIG.airAbsorption.get().floatValue());
        Loggers.logALError("Set environment airAbsorption:");
    }

    private static void setSoundPos(int sourceID, Vec3 pos) {
        AL11.alSource3f((int)sourceID, (int)4100, (float)((float)pos.x), (float)((float)pos.y), (float)((float)pos.z));
    }

    protected static void setReverbParams(ReverbParams r, int auxFXSlot, int reverbSlot) {
        EXTEfx.alEffectf((int)reverbSlot, (int)1, (float)r.density);
        Loggers.logALError("Error while assigning reverb density: " + r.density);
        EXTEfx.alEffectf((int)reverbSlot, (int)2, (float)r.diffusion);
        Loggers.logALError("Error while assigning reverb diffusion: " + r.diffusion);
        EXTEfx.alEffectf((int)reverbSlot, (int)3, (float)r.gain);
        Loggers.logALError("Error while assigning reverb gain: " + r.gain);
        EXTEfx.alEffectf((int)reverbSlot, (int)4, (float)r.gainHF);
        Loggers.logALError("Error while assigning reverb gainHF: " + r.gainHF);
        EXTEfx.alEffectf((int)reverbSlot, (int)6, (float)r.decayTime);
        Loggers.logALError("Error while assigning reverb decayTime: " + r.decayTime);
        EXTEfx.alEffectf((int)reverbSlot, (int)7, (float)r.decayHFRatio);
        Loggers.logALError("Error while assigning reverb decayHFRatio: " + r.decayHFRatio);
        EXTEfx.alEffectf((int)reverbSlot, (int)9, (float)r.reflectionsGain);
        Loggers.logALError("Error while assigning reverb reflectionsGain: " + r.reflectionsGain);
        EXTEfx.alEffectf((int)reverbSlot, (int)12, (float)r.lateReverbGain);
        Loggers.logALError("Error while assigning reverb lateReverbGain: " + r.lateReverbGain);
        EXTEfx.alEffectf((int)reverbSlot, (int)13, (float)r.lateReverbDelay);
        Loggers.logALError("Error while assigning reverb lateReverbDelay: " + r.lateReverbDelay);
        EXTEfx.alEffectf((int)reverbSlot, (int)19, (float)r.airAbsorptionGainHF);
        Loggers.logALError("Error while assigning reverb airAbsorptionGainHF: " + r.airAbsorptionGainHF);
        EXTEfx.alEffectf((int)reverbSlot, (int)22, (float)r.roomRolloffFactor);
        Loggers.logALError("Error while assigning reverb roomRolloffFactor: " + r.roomRolloffFactor);
        EXTEfx.alAuxiliaryEffectSloti((int)auxFXSlot, (int)1, (int)reverbSlot);
    }
}

