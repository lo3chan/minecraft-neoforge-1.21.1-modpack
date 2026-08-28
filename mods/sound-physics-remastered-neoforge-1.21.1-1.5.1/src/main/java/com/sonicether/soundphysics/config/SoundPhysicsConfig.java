/*
 * Decompiled with CFR 0.152.
 */
package com.sonicether.soundphysics.config;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysics;
import de.maxhenkel.sound_physics_remastered.configbuilder.ConfigBuilder;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.ConfigEntry;

public class SoundPhysicsConfig {
    public final ConfigEntry<Boolean> enabled;
    public final ConfigEntry<Float> attenuationFactor;
    public final ConfigEntry<Float> reverbAttenuationDistance;
    public final ConfigEntry<Float> reverbGain;
    public final ConfigEntry<Float> reverbBrightness;
    public final ConfigEntry<Float> reverbDistance;
    public final ConfigEntry<Float> blockAbsorption;
    public final ConfigEntry<Float> occlusionVariation;
    public final ConfigEntry<Float> defaultBlockReflectivity;
    public final ConfigEntry<Float> defaultBlockOcclusionFactor;
    public final ConfigEntry<Float> soundDistanceAllowance;
    public final ConfigEntry<Float> airAbsorption;
    public final ConfigEntry<Float> underwaterFilter;
    public final ConfigEntry<Boolean> evaluateAmbientSounds;
    public final ConfigEntry<Integer> environmentEvaluationRayCount;
    public final ConfigEntry<Integer> environmentEvaluationRayBounces;
    public final ConfigEntry<Float> nonFullBlockOcclusionFactor;
    public final ConfigEntry<Integer> maxOcclusionRays;
    public final ConfigEntry<Float> maxOcclusion;
    public final ConfigEntry<Boolean> strictOcclusion;
    public final ConfigEntry<Boolean> soundDirectionEvaluation;
    public final ConfigEntry<Boolean> redirectNonOccludedSounds;
    public final ConfigEntry<Boolean> updateMovingSounds;
    public final ConfigEntry<Integer> soundUpdateInterval;
    public final ConfigEntry<Double> maxSoundProcessingDistance;
    public final ConfigEntry<Boolean> unsafeLevelAccess;
    public final ConfigEntry<Integer> levelCloneRange;
    public final ConfigEntry<Integer> levelCloneMaxRetainTicks;
    public final ConfigEntry<Integer> levelCloneMaxRetainBlockDistance;
    public final ConfigEntry<Boolean> debugLogging;
    public final ConfigEntry<Boolean> occlusionLogging;
    public final ConfigEntry<Boolean> environmentLogging;
    public final ConfigEntry<Boolean> performanceLogging;
    public final ConfigEntry<Boolean> renderSoundBounces;
    public final ConfigEntry<Boolean> renderOcclusion;
    public final ConfigEntry<Boolean> simpleVoiceChatIntegration;
    public final ConfigEntry<Boolean> hearSelf;

    public SoundPhysicsConfig(ConfigBuilder builder) {
        this.enabled = builder.booleanEntry("enabled", true, new String[0]).comment(new String[]{"Enables/Disables all sound effects"});
        this.attenuationFactor = builder.floatEntry("attenuation_factor", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(1.0f), new String[0]).comment(new String[]{"Affects how quiet a sound gets based on distance", "Lower values mean distant sounds are louder", "This setting requires you to be in singleplayer or having the mod installed on the server", "1.0 is the physically correct value"});
        this.reverbAttenuationDistance = builder.floatEntry("reverb_attenuation_distance", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(512.0f), new String[0]).comment(new String[]{"The ray distance at which reverb starts", "0.0 disables reverb attenuation"});
        this.reverbGain = builder.floatEntry("reverb_gain", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), new String[0]).comment(new String[]{"The volume of simulated reverberations"});
        this.reverbBrightness = builder.floatEntry("reverb_brightness", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), new String[0]).comment(new String[]{"The brightness of reverberation", "Higher values result in more high frequencies in reverberation", "Lower values give a more muffled sound to the reverb"});
        this.reverbDistance = builder.floatEntry("reverb_distance", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(16.0f), new String[0]).comment(new String[]{"The distance of reverb relative to the sound distance"});
        this.blockAbsorption = builder.floatEntry("block_absorption", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(4.0f), new String[0]).comment(new String[]{"The amount of sound that will be absorbed when traveling through blocks"});
        this.occlusionVariation = builder.floatEntry("occlusion_variation", Float.valueOf(0.35f), Float.valueOf(0.0f), Float.valueOf(16.0f), new String[0]).comment(new String[]{"Higher values mean smaller objects won't be considered as occluding"});
        this.defaultBlockReflectivity = builder.floatEntry("default_block_reflectivity", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(4.0f), new String[0]).comment(new String[]{"The default amount of sound reflectance energy for all blocks", "Lower values result in more conservative reverb simulation with shorter reverb tails", "Higher values result in more generous reverb simulation with higher reverb tails"});
        this.defaultBlockOcclusionFactor = builder.floatEntry("default_block_occlusion_factor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), new String[0]).comment(new String[]{"The default amount of occlusion for all blocks", "Lower values will result in sounds being less muffled through walls", "Higher values mean sounds will be not audible though thicker walls"});
        this.soundDistanceAllowance = builder.floatEntry("sound_distance_allowance", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), new String[0]).comment(new String[]{"Minecraft won't allow sounds to play past a certain distance", "This parameter is a multiplier for how far away a sound source is allowed to be in order for it to actually play", "This setting only takes affect in singleplayer worlds and when installed on the server"});
        this.airAbsorption = builder.floatEntry("air_absorption", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), new String[0]).comment(new String[]{"A value controlling the amount that air absorbs high frequencies with distance", "A value of 1.0 is physically correct for air with normal humidity and temperature", "Higher values mean air will absorb more high frequencies with distance", "0 disables this effect"});
        this.underwaterFilter = builder.floatEntry("underwater_filter", Float.valueOf(0.9f), Float.valueOf(0.0f), Float.valueOf(1.0f), new String[0]).comment(new String[]{"How much sound is filtered when the player is underwater", "0.0 means no filter", "1.0 means fully filtered"});
        this.evaluateAmbientSounds = builder.booleanEntry("evaluate_ambient_sounds", false, new String[0]).comment(new String[]{"Whether sounds like cave, nether or underwater ambient sounds should have sound physics"});
        this.environmentEvaluationRayCount = builder.integerEntry("environment_evaluation_ray_count", (Integer)32, 8, 64, new String[0]).comment(new String[]{"The number of rays to trace to determine reverberation for each sound source", "More rays provides more consistent tracing results but takes more time to calculate", "Decrease this value if you experience lag spikes when sounds play"});
        this.environmentEvaluationRayBounces = builder.integerEntry("environment_evaluation_ray_bounces", (Integer)4, 2, 64, new String[0]).comment(new String[]{"The number of rays bounces to trace to determine reverberation for each sound source", "More bounces provides more echo and sound ducting but takes more time to calculate", "Decrease this value if you experience lag spikes when sounds play"});
        this.nonFullBlockOcclusionFactor = builder.floatEntry("non_full_block_occlusion_factor", Float.valueOf(0.25f), Float.valueOf(0.0f), Float.valueOf(1.0f), new String[0]).comment(new String[]{"If sound hits a non-full-square side, block occlusion is multiplied by this"});
        this.maxOcclusionRays = builder.integerEntry("max_occlusion_rays", (Integer)16, 1, 128, new String[0]).comment(new String[]{"The maximum amount of rays to determine occlusion", "Directly correlates to the amount of blocks between walls that are considered"});
        this.maxOcclusion = builder.floatEntry("max_occlusion", Float.valueOf(64.0f), Float.valueOf(0.0f), Float.valueOf(1024.0f), new String[0]).comment(new String[]{"The amount at which occlusion is capped"});
        this.strictOcclusion = builder.booleanEntry("strict_occlusion", false, new String[0]).comment(new String[]{"If enabled, the occlusion calculation only uses one path between the sound source and the listener instead of 9"});
        this.soundDirectionEvaluation = builder.booleanEntry("sound_direction_evaluation", true, new String[0]).comment(new String[]{"Whether to try calculating where the sound should come from based on reflections"});
        this.redirectNonOccludedSounds = builder.booleanEntry("redirect_non_occluded_sounds", true, new String[0]).comment(new String[]{"Skip redirecting non-occluded sounds (the ones you can see directly)"});
        this.updateMovingSounds = builder.booleanEntry("update_moving_sounds", false, new String[0]).comment(new String[]{"If music discs or other longer sounds should be frequently reevaluated"});
        this.soundUpdateInterval = builder.integerEntry("sound_update_interval", (Integer)5, 1, Integer.MAX_VALUE, new String[0]).comment(new String[]{"The interval in ticks that moving sounds are reevaluated", "Lower values mean more frequent reevaluation but also more lag", "This option only takes effect if update_moving_sounds is enabled"});
        this.maxSoundProcessingDistance = builder.doubleEntry("max_sound_processing_distance", (Double)512.0, 0.0, (Double)Double.MAX_VALUE, new String[0]).comment(new String[]{"The maximum distance a sound can be processed"});
        this.unsafeLevelAccess = builder.booleanEntry("unsafe_level_access", false, new String[0]).comment(new String[]{"Disable level clone and cache. This will fall back to original main thread access.", "WARNING! Enabling this will cause instability and issues with other mods."});
        this.levelCloneRange = builder.integerEntry("level_clone_range", (Integer)4, 2, 16, new String[0]).comment(new String[]{"The radius of chunks to clone for level access"});
        this.levelCloneMaxRetainTicks = builder.integerEntry("level_clone_max_retain_ticks", (Integer)20, 1, Integer.MAX_VALUE, "The maximum number of ticks to retain the cloned level in the cache");
        this.levelCloneMaxRetainBlockDistance = builder.integerEntry("level_clone_max_retain_block_distance", (Integer)16, 1, Integer.MAX_VALUE, "The maximum distance a player can move from the cloned origin before invalidation");
        this.debugLogging = builder.booleanEntry("debug_logging", false, new String[0]).comment(new String[]{"Enables debug logging"});
        this.occlusionLogging = builder.booleanEntry("occlusion_logging", false, new String[0]).comment(new String[]{"Provides more information about occlusion in the logs"});
        this.environmentLogging = builder.booleanEntry("environment_logging", false, new String[0]).comment(new String[]{"Provides more information about the environment calculation in the logs"});
        this.performanceLogging = builder.booleanEntry("performance_logging", false, new String[0]).comment(new String[]{"Provides more information about how long computations take"});
        this.renderSoundBounces = builder.booleanEntry("render_sound_bounces", false, new String[0]).comment(new String[]{"If enabled, the path of the sound will be rendered in game"});
        this.renderOcclusion = builder.booleanEntry("render_occlusion", false, new String[0]).comment(new String[]{"If enabled, occlusion will be visualized in game"});
        this.simpleVoiceChatIntegration = builder.booleanEntry("simple_voice_chat_integration", true, new String[0]).comment(new String[]{"Enables/Disables sound effects for Simple Voice Chat audio"});
        this.hearSelf = builder.booleanEntry("simple_voice_chat_hear_self", false, new String[0]).comment(new String[]{"Enables/Disables hearing your own echo with Simple Voice Chat"});
    }

    public void reloadClient() {
        Loggers.log("Reloading reverb parameters", new Object[0]);
        SoundPhysics.syncReverbParams();
    }
}

