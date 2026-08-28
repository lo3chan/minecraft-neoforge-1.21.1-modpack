/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.DynamicsWorld;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanLayer;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.diebuddies.physics.smoke.SmokeShadowTransformer;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ConfigClient {
    public static final int PLAYER_SIMULATION_QUALITY = 90;
    public static final int CLOTH_SIMULATION_QUALITY = 45;
    public static final int FISHING_ROD_SIMULATION_QUALITY = 20;
    public static final int LEASH_SIMULATION_QUALITY = 20;
    public static final int BANNER_SIMULATION_QUALITY = 25;
    public static final int SNOWBALL_VOXEL = 0;
    public static final int SNOWBALL_ROUND = 1;
    public static final int SNOWBALL_CLASSIC = 2;
    public static final int ENDERPEARL_VOXEL = 0;
    public static final int ENDERPEARL_ROUND = 1;
    public static final int ENDERPEARL_CLASSIC = 2;
    public static final int EGG_VOXEL = 0;
    public static final int EGG_ROUND = 1;
    public static final int EGG_CLASSIC = 2;
    public static final int IMPACT_SHATTER = 0;
    public static final int IMPACT_BOUNCE = 1;
    public static final int IMPACT_DISAPPEAR = 2;
    public static final int SNOW_ROUND = 0;
    public static final int SNOW_CUBE = 1;
    public static final int SNOW_CHUNK_SIZE_MEDIUM = 0;
    public static final int SNOW_CHUNK_SIZE_HIGH = 1;
    private static final String DIR = "config/physicsmod";
    private static final String CONFIG = "physics_client_config.json";
    public static Map<String, Vector3f> customizedGravities = new Object2ObjectOpenHashMap();
    public static Map<String, Vector3f> customizedBuoyancies = new Object2ObjectOpenHashMap();
    public static boolean blockPhysics = true;
    public static boolean mobPhysics = true;
    public static boolean vinePhysics = true;
    public static boolean capePhysics = true;
    public static boolean itemPhysics = true;
    public static boolean fishingRodPhysics = true;
    public static boolean leashPhysics = true;
    public static boolean bannerPhysics = true;
    public static boolean liquidPhysics = true;
    public static boolean snowPhysics = true;
    public static boolean smokePhysics = true;
    public static boolean guiPhysics = false;
    public static boolean windPhysics = true;
    public static boolean weatherParticles = true;
    public static boolean oceanPhysics = true;
    public static boolean crackPhysicsParticles = true;
    public static boolean sprintingPhysicsParticles = true;
    public static boolean eatingPhysicsParticles = true;
    public static boolean serverBlockPhysicsParticles = true;
    public static boolean minecraftBlockBreakParticles = false;
    public static boolean itemBreakPhysics = true;
    public static int snowballModel = 0;
    public static int enderpearlModel = 0;
    public static int eggModel = 0;
    public static int maxPhysicsObjects = 10000;
    public static int cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
    public static BlockSetting blockSetting = new BlockSetting(BlockPhysicsType.FRACTURED, 4.0, 3.0, 1.0, ConfigAnimations.DEFAULT_ANIMATION);
    public static MobSetting mobSetting = new MobSetting(MobPhysicsType.RAGDOLL, 4.0, 3.0, ConfigAnimations.DEFAULT_ANIMATION);
    public static double particleLifetimeVines = 4.0;
    public static double particleLifetimeVarianceVines = 3.0;
    public static double particleLifetimeItems = 3.0;
    public static double particleLifetimeVarianceItems = 3.0;
    public static double particleLifetimeParticles = 0.1;
    public static double particleLifetimeVarianceParticles = 3.0;
    public static double particleLifetimeLiquids = 6.0;
    public static double particleLifetimeVarianceLiquids = 3.0;
    public static boolean gravityChanged = false;
    public static double bannerPhysicsRange = 48.0;
    public static volatile boolean clothSmoothShading = false;
    public static double leashLength = 3.0;
    public static double fishingLineLength = 12.0;
    public static boolean showUpdateNotifications = true;
    public static boolean pvpServerCompatibility = false;
    public static boolean snowballShade = false;
    public static boolean enderpearlShade = false;
    public static boolean eggShade = false;
    public static int clothThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 8);
    public static float clothEntityRange = 48.0f;
    public static int liquidThreads = 2;
    public static volatile double vineRange = 32.0;
    public static volatile double liquidSourceDistance = 6.0;
    public static float impactVolume = 1.0f;
    public static float windVolume = 1.0f;
    public static double blockPhysicsRange = 96.0;
    public static int waterDensity = 3;
    public static boolean snowTracks = true;
    public static boolean grassSnowy = true;
    public static volatile float snowLOD = 1.0f;
    public static double snowTrackDistance = 48.0;
    public static volatile int snowTrackEntities = 6;
    public static volatile int snowType = 0;
    public static volatile boolean snowSmoothShading = true;
    public static volatile int snowQuality = 0;
    public static volatile float snowThickness = 0.0f;
    public static int snowballImpact = 1;
    public static int enderpearlImpact = 1;
    public static int eggImpact = 1;
    public static double smokePhysicsRange = 100.0;
    public static double particleLifetimeSmoke = 60.0;
    public static double particleLifetimeVarianceSmoke = 15.0;
    public static double particleDespawnTimeSmoke = 3.0;
    public static double particleDespawnTimeVarianceSmoke = 12.0;
    public static float smokeColorRed = 0.56f;
    public static float smokeColorGreen = 0.56f;
    public static float smokeColorBlue = 0.56f;
    public static float smokeDenseColorRed = 0.364f;
    public static float smokeDenseColorGreen = 0.364f;
    public static float smokeDenseColorBlue = 0.364f;
    public static float smokeDensity = 0.9f;
    public static float smokeBlaze = 0.05f;
    public static float smokeCampfire = 0.3f;
    public static float smokeFire = 0.3f;
    public static float smokeOther = 1.0f;
    public static int smokeParticleLimit = 6000;
    public static int maxLoadedDynamicBlocks = 20;
    public static float weatherClearStrength = 0.1f;
    public static float weatherRainStrength = 1.0f;
    public static float weatherThunderStrength = 1.4f;
    public static int weatherRainParticleAmount = 5;
    public static int weatherThunderParticleAmount = 2;
    public static float particleRainOpacity = 1.0f;
    public static float particleSnowOpacity = 1.0f;
    public static float particleDustOpacity = 1.0f;
    public static SmokeShadowTransformer smokeShadowTransformer = SmokeShadowTransformer.DISABLED;
    public static String verificationCode = "";
    public static boolean firstStartup;
    public static float oceanDetail;
    public static boolean oceanAdjustHitbox;
    public static float oceanWaveHeightMultiplier;
    public static byte oceanBlockRange;
    public static float oceanWeatherClear;
    public static float oceanWeatherRain;
    public static float oceanWeatherThunder;
    public static float oceanBaseSpeed;
    public static float oceanHorizontalWaveScale;
    public static boolean oceanParticles;
    public static float oceanParticleAlpha;
    public static float oceanFoamAmount;
    public static float oceanFoamOpacity;
    public static boolean oceanStickyEntities;
    public static float oceanSplashVolume;
    public static float itemRotationSpeed;
    public static float jointBreakForce;
    public static float jointBlood;
    public static int mobRagdollLimit;
    public static boolean clothForceArmor;
    public static boolean renderPhysicsDebugOverlay;
    public static boolean oceanRipples;
    public static boolean cudaLiquids;
    public static float cudaLiquidsParticleSize;
    public static int cudaLiquidsBlurPasses;
    public static float playbackSpeed;
    public static float oceanRainPuddleAmount;
    public static int oceanPuddleResolutionQuality;
    private static boolean stored_vinePhysics;
    private static boolean stored_capePhysics;
    private static boolean stored_itemPhysics;
    private static boolean stored_fishingRodPhysics;
    private static boolean stored_leashPhysics;
    private static boolean stored_bannerPhysics;
    private static boolean stored_liquidPhysics;
    private static boolean stored_snowPhysics;
    private static boolean stored_smokePhysics;
    private static boolean stored_guiPhysics;
    private static boolean stored_windPhysics;
    private static boolean stored_weatherParticles;
    private static boolean stored_oceanPhysics;
    private static boolean stored_crackPhysicsParticles;
    private static boolean stored_sprintingPhysicsParticles;
    private static boolean stored_eatingPhysicsParticles;
    private static boolean stored_serverBlockPhysicsParticles;
    private static boolean stored_minecraftBlockBreakParticles;
    private static boolean stored_itemBreakPhysics;
    private static int stored_snowballModel;
    private static int stored_enderpearlModel;
    private static int stored_eggModel;
    private static boolean stored;

    public static void reload() {
        File configFile;
        JsonObject config = ConfigClient.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_client_config.json")).exists()) {
            firstStartup = true;
            try {
                configFile.createNewFile();
                try (FileWriter writer = new FileWriter(configFile);){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    gson.toJson((JsonElement)config, (Appendable)writer);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gson gson = new Gson();
            try {
                config = (JsonObject)gson.fromJson((Reader)new FileReader(configFile), JsonObject.class);
            }
            catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
            firstStartup = false;
        }
        try {
            maxPhysicsObjects = config.get("maxPhysicsObjects").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVines = config.get("particleLifetimeVines").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeItems = config.get("particleLifetimeItems").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeParticles = config.get("particleLifetimeParticles").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeLiquids = config.get("particleLifetimeLiquids").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeSmoke = config.get("particleLifetimeSmoke").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleDespawnTimeSmoke = config.get("particleDespawnTimeSmoke").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVarianceVines = config.get("particleLifetimeVarianceVines").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVarianceItems = config.get("particleLifetimeVarianceItems").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVarianceParticles = config.get("particleLifetimeVarianceParticles").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVarianceLiquids = config.get("particleLifetimeVarianceLiquids").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleLifetimeVarianceSmoke = config.get("particleLifetimeVarianceSmoke").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleDespawnTimeVarianceSmoke = config.get("particleDespawnTimeVarianceSmoke").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            cpuThreads = config.get("cpuThreads").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemPhysics = config.get("itemPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            minecraftBlockBreakParticles = config.get("minecraftBlockBreakParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            vinePhysics = config.get("vinePhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            capePhysics = config.get("capePhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            fishingRodPhysics = config.get("fishingRodPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            leashPhysics = config.get("leashPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            bannerPhysics = config.get("bannerPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            clothSmoothShading = config.get("clothSmoothShading").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            showUpdateNotifications = config.get("showUpdateNotifications").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            clothThreads = config.get("clothThreads").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            leashLength = config.get("leashLength").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            fishingLineLength = config.get("fishingLineLength").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            pvpServerCompatibility = config.get("pvpServerCompatibility").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowballModel = config.get("snowballModel").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowballImpact = config.get("snowballImpact").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowballShade = config.get("snowballShade").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            enderpearlModel = config.get("enderpearlModel").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            enderpearlImpact = config.get("enderpearlImpact").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            enderpearlShade = config.get("enderpearlShade").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            eggModel = config.get("eggModel").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            eggImpact = config.get("eggImpact").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            eggShade = config.get("eggShade").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            crackPhysicsParticles = config.get("crackPhysicsParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            liquidPhysics = config.get("liquidPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            liquidSourceDistance = config.get("liquidSourceDistance").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            liquidThreads = config.get("liquidThreads").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            bannerPhysicsRange = config.get("bannerPhysicsRange").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            impactVolume = config.get("soundVolume").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            blockPhysicsRange = config.get("blockPhysicsRange").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            vineRange = config.get("vineRange").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            waterDensity = config.get("waterDensity").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowPhysics = config.get("snowPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowTracks = config.get("snowTracks").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowTrackEntities = config.get("snowTrackEntities").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowTrackDistance = config.get("snowTrackDistance").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowThickness = config.get("snowThickness").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            grassSnowy = config.get("grassSnowy").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowType = config.get("snowType").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowSmoothShading = config.get("snowSmoothShading").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowQuality = config.get("snowChunkSize").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            blockSetting = (BlockSetting)AdjustableUtil.readObject(BlockSetting.class, config.get("blockSettings").getAsJsonObject());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            mobSetting = (MobSetting)AdjustableUtil.readObject(MobSetting.class, config.get("mobSettings").getAsJsonObject());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            sprintingPhysicsParticles = config.get("sprintingPhysicsParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            eatingPhysicsParticles = config.get("eatingPhysicsParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            serverBlockPhysicsParticles = config.get("serverBlockPhysicsParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokePhysics = config.get("smokePhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeParticleLimit = config.get("smokeParticleLimit").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            verificationCode = config.get("verificationCode").getAsString();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeColorRed = config.get("smokeColorRed").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeColorGreen = config.get("smokeColorGreen").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeColorBlue = config.get("smokeColorBlue").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeDenseColorRed = config.get("smokeDenseColorRed").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeDenseColorGreen = config.get("smokeDenseColorGreen").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeDenseColorBlue = config.get("smokeDenseColorBlue").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeBlaze = config.get("smokeBlaze").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeFire = config.get("smokeFire").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeCampfire = config.get("smokeCampfire").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeOther = config.get("smokeOther").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeDensity = config.get("smokeDensity").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokePhysicsRange = config.get("smokePhysicsRange").getAsDouble();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            smokeShadowTransformer = SmokeShadowTransformer.values()[config.get("smokeShadowTransformer").getAsInt()];
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            maxLoadedDynamicBlocks = config.get("maxLoadedDynamicBlocks").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            windPhysics = config.get("windPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherParticles = config.get("weatherParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherClearStrength = config.get("weatherClearStrength").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherRainStrength = config.get("weatherRainStrength").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherThunderStrength = config.get("weatherThunderStrength").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            windVolume = config.get("windVolume").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherRainParticleAmount = config.get("weatherRainParticleAmount").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            weatherThunderParticleAmount = config.get("weatherThunderParticleAmount").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanPhysics = config.get("oceanPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanAdjustHitbox = config.get("oceanAdjustHitbox").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanDetail = config.get("oceanDetail").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanWaveHeightMultiplier = config.get("oceanWaveHeightMultiplier").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanBlockRange = config.get("oceanBlockRange").getAsByte();
            OceanLayer.updateRange(oceanBlockRange);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanWeatherClear = config.get("oceanWeatherClear").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanWeatherRain = config.get("oceanWeatherRain").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanWeatherThunder = config.get("oceanWeatherThunder").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanBaseSpeed = config.get("oceanBaseSpeed").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanHorizontalWaveScale = config.get("oceanHorizontalWaveScale").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanParticles = config.get("oceanParticles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanParticleAlpha = config.get("oceanParticleAlpha").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanStickyEntities = config.get("oceanStickyEntities").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanSplashVolume = config.get("oceanSplashVolumeNew").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            snowLOD = config.get("snowLOD").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleRainOpacity = config.get("particleRainOpacity").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleSnowOpacity = config.get("particleSnowOpacity").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            particleDustOpacity = config.get("particleDustOpacity").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemRotationSpeed = config.get("itemRotationSpeed").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            jointBreakForce = config.get("jointBreakForce").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            jointBlood = config.get("jointBlood").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            mobRagdollLimit = config.get("mobRagdollLimit").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            clothEntityRange = config.get("clothEntityRange").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            clothForceArmor = config.get("clothForceArmor").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanRipples = config.get("oceanPuddles").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanFoamAmount = config.get("oceanFoamAmount").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanFoamOpacity = config.get("oceanFoamOpacity").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            cudaLiquids = config.get("cudaLiquids").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            cudaLiquidsParticleSize = config.get("cudaLiquidsParticleSize").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            cudaLiquidsBlurPasses = config.get("cudaLiquidsBlurPasses").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            playbackSpeed = config.get("playbackSpeed").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanRainPuddleAmount = config.get("oceanRainPuddleAmount").getAsFloat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            oceanPuddleResolutionQuality = config.get("puddleResolutionQuality").getAsInt();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemBreakPhysics = config.get("itemBreakPhysics").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            ConfigClient.get(config, customizedGravities, "customizedGravities");
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            ConfigClient.get(config, customizedBuoyancies, "customizedBuoyancies");
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (ConfigClient.blockSetting.animation == null) {
            ConfigClient.blockSetting.animation = ConfigAnimations.DEFAULT_ANIMATION;
        }
        if (ConfigClient.mobSetting.animation == null) {
            ConfigClient.mobSetting.animation = ConfigAnimations.DEFAULT_ANIMATION;
        }
    }

    public static boolean areSnowPhysicsEnabled() {
        return snowPhysics && !StarterClient.immersivePortals;
    }

    public static boolean areOceanPhysicsEnabled() {
        return oceanPhysics && !StarterClient.immersivePortals;
    }

    public static boolean areDynamicBlockPhysicsEnabled() {
        return vinePhysics && !StarterClient.immersivePortals;
    }

    public static void init() {
    }

    private static void get(JsonObject config, Map<String, Vector3f> map, String name) {
        JsonArray array = config.get(name).getAsJsonArray();
        for (int i = 0; i < array.size() / 4; ++i) {
            String id = array.get(i * 4).getAsString();
            Float x = Float.valueOf(array.get(i * 4 + 1).getAsFloat());
            Float y = Float.valueOf(array.get(i * 4 + 2).getAsFloat());
            Float z = Float.valueOf(array.get(i * 4 + 3).getAsFloat());
            map.put(id, new Vector3f(x.floatValue(), y.floatValue(), z.floatValue()));
        }
    }

    public static Vector3f getGravity(String id) {
        Vector3f val = customizedGravities.get(id);
        if (val == null) {
            val = new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_GRAVITY);
        }
        return val;
    }

    public static Vector3f getGravity(ResourceLocation id) {
        return ConfigClient.getGravity(id.toString());
    }

    public static Vector3f getBuoyancy(String id) {
        Vector3f val = customizedBuoyancies.get(id);
        if (val == null) {
            val = new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_BUOYANCY);
        }
        return val;
    }

    public static Vector3f getBuoyancy(ResourceLocation id) {
        return ConfigClient.getBuoyancy(id.toString());
    }

    public static void setGravity(String id, Vector3f gravity) {
        customizedGravities.put(id, gravity);
        gravityChanged = true;
    }

    public static void setBuoyancy(String id, Vector3f buoyancy) {
        customizedBuoyancies.put(id, buoyancy);
        gravityChanged = true;
    }

    public static boolean addGravityBuoyancyEntry(ResourceLocation id) {
        if (!customizedGravities.containsKey(id.toString())) {
            customizedGravities.put(id.toString(), new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_GRAVITY));
            customizedBuoyancies.put(id.toString(), new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_BUOYANCY));
            return true;
        }
        return false;
    }

    public static boolean hasItemPhysics() {
        return itemPhysics;
    }

    public static boolean cudaLiquids() {
        return cudaLiquids && StarterClient.cudaAvailable;
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        config.add("maxPhysicsObjects", (JsonElement)new JsonPrimitive((Number)maxPhysicsObjects));
        config.add("particleLifetimeVines", (JsonElement)new JsonPrimitive((Number)particleLifetimeVines));
        config.add("particleLifetimeItems", (JsonElement)new JsonPrimitive((Number)particleLifetimeItems));
        config.add("particleLifetimeParticles", (JsonElement)new JsonPrimitive((Number)particleLifetimeParticles));
        config.add("particleLifetimeLiquids", (JsonElement)new JsonPrimitive((Number)particleLifetimeLiquids));
        config.add("particleLifetimeSmoke", (JsonElement)new JsonPrimitive((Number)particleLifetimeSmoke));
        config.add("particleDespawnTimeSmoke", (JsonElement)new JsonPrimitive((Number)particleDespawnTimeSmoke));
        config.add("particleLifetimeVarianceVines", (JsonElement)new JsonPrimitive((Number)particleLifetimeVarianceVines));
        config.add("particleLifetimeVarianceItems", (JsonElement)new JsonPrimitive((Number)particleLifetimeVarianceItems));
        config.add("particleLifetimeVarianceParticles", (JsonElement)new JsonPrimitive((Number)particleLifetimeVarianceParticles));
        config.add("particleLifetimeVarianceLiquids", (JsonElement)new JsonPrimitive((Number)particleLifetimeVarianceLiquids));
        config.add("particleLifetimeVarianceSmoke", (JsonElement)new JsonPrimitive((Number)particleLifetimeVarianceSmoke));
        config.add("particleDespawnTimeVarianceSmoke", (JsonElement)new JsonPrimitive((Number)particleDespawnTimeVarianceSmoke));
        config.add("cpuThreads", (JsonElement)new JsonPrimitive((Number)cpuThreads));
        config.add("itemPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(itemPhysics)));
        config.add("minecraftBlockBreakParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(minecraftBlockBreakParticles)));
        config.add("vinePhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(vinePhysics)));
        config.add("capePhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(capePhysics)));
        config.add("fishingRodPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(fishingRodPhysics)));
        config.add("leashPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(leashPhysics)));
        config.add("bannerPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(bannerPhysics)));
        config.add("clothSmoothShading", (JsonElement)new JsonPrimitive(Boolean.valueOf(clothSmoothShading)));
        config.add("showUpdateNotifications", (JsonElement)new JsonPrimitive(Boolean.valueOf(showUpdateNotifications)));
        config.add("clothThreads", (JsonElement)new JsonPrimitive((Number)clothThreads));
        config.add("leashLength", (JsonElement)new JsonPrimitive((Number)leashLength));
        config.add("fishingLineLength", (JsonElement)new JsonPrimitive((Number)fishingLineLength));
        config.add("pvpServerCompatibility", (JsonElement)new JsonPrimitive(Boolean.valueOf(pvpServerCompatibility)));
        config.add("snowballModel", (JsonElement)new JsonPrimitive((Number)snowballModel));
        config.add("snowballImpact", (JsonElement)new JsonPrimitive((Number)snowballImpact));
        config.add("snowballShade", (JsonElement)new JsonPrimitive(Boolean.valueOf(snowballShade)));
        config.add("enderpearlModel", (JsonElement)new JsonPrimitive((Number)enderpearlModel));
        config.add("enderpearlImpact", (JsonElement)new JsonPrimitive((Number)enderpearlImpact));
        config.add("enderpearlShade", (JsonElement)new JsonPrimitive(Boolean.valueOf(enderpearlShade)));
        config.add("eggModel", (JsonElement)new JsonPrimitive((Number)eggModel));
        config.add("eggImpact", (JsonElement)new JsonPrimitive((Number)eggImpact));
        config.add("eggShade", (JsonElement)new JsonPrimitive(Boolean.valueOf(eggShade)));
        config.add("crackPhysicsParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(crackPhysicsParticles)));
        config.add("liquidPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(liquidPhysics)));
        config.add("liquidSourceDistance", (JsonElement)new JsonPrimitive((Number)liquidSourceDistance));
        config.add("liquidThreads", (JsonElement)new JsonPrimitive((Number)liquidThreads));
        config.add("bannerPhysicsRange", (JsonElement)new JsonPrimitive((Number)bannerPhysicsRange));
        config.add("soundVolume", (JsonElement)new JsonPrimitive((Number)Float.valueOf(impactVolume)));
        config.add("blockPhysicsRange", (JsonElement)new JsonPrimitive((Number)blockPhysicsRange));
        config.add("vineRange", (JsonElement)new JsonPrimitive((Number)vineRange));
        config.add("waterDensity", (JsonElement)new JsonPrimitive((Number)waterDensity));
        config.add("snowPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(snowPhysics)));
        config.add("snowTracks", (JsonElement)new JsonPrimitive(Boolean.valueOf(snowTracks)));
        config.add("snowTrackEntities", (JsonElement)new JsonPrimitive((Number)snowTrackEntities));
        config.add("snowTrackDistance", (JsonElement)new JsonPrimitive((Number)snowTrackDistance));
        config.add("snowThickness", (JsonElement)new JsonPrimitive((Number)Float.valueOf(snowThickness)));
        config.add("grassSnowy", (JsonElement)new JsonPrimitive(Boolean.valueOf(grassSnowy)));
        config.add("snowType", (JsonElement)new JsonPrimitive((Number)snowType));
        config.add("snowSmoothShading", (JsonElement)new JsonPrimitive(Boolean.valueOf(snowSmoothShading)));
        config.add("snowChunkSize", (JsonElement)new JsonPrimitive((Number)snowQuality));
        config.add("blockSettings", (JsonElement)AdjustableUtil.writeObject(new JsonObject(), blockSetting));
        config.add("mobSettings", (JsonElement)AdjustableUtil.writeObject(new JsonObject(), mobSetting));
        config.add("sprintingPhysicsParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(sprintingPhysicsParticles)));
        config.add("eatingPhysicsParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(eatingPhysicsParticles)));
        config.add("serverBlockPhysicsParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(serverBlockPhysicsParticles)));
        config.add("smokePhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(smokePhysics)));
        config.add("smokeParticleLimit", (JsonElement)new JsonPrimitive((Number)smokeParticleLimit));
        config.add("verificationCode", (JsonElement)new JsonPrimitive(verificationCode));
        config.add("smokeColorRed", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeColorRed)));
        config.add("smokeColorGreen", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeColorGreen)));
        config.add("smokeColorBlue", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeColorBlue)));
        config.add("smokeDenseColorRed", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeDenseColorRed)));
        config.add("smokeDenseColorGreen", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeDenseColorGreen)));
        config.add("smokeDenseColorBlue", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeDenseColorBlue)));
        config.add("smokeDensity", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeDensity)));
        config.add("smokePhysicsRange", (JsonElement)new JsonPrimitive((Number)smokePhysicsRange));
        config.add("smokeBlaze", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeBlaze)));
        config.add("smokeCampfire", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeCampfire)));
        config.add("smokeFire", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeFire)));
        config.add("smokeOther", (JsonElement)new JsonPrimitive((Number)Float.valueOf(smokeOther)));
        config.add("smokeShadowTransformer", (JsonElement)new JsonPrimitive((Number)smokeShadowTransformer.ordinal()));
        config.add("maxLoadedDynamicBlocks", (JsonElement)new JsonPrimitive((Number)maxLoadedDynamicBlocks));
        config.add("windPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(windPhysics)));
        config.add("weatherParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(weatherParticles)));
        config.add("weatherClearStrength", (JsonElement)new JsonPrimitive((Number)Float.valueOf(weatherClearStrength)));
        config.add("weatherRainStrength", (JsonElement)new JsonPrimitive((Number)Float.valueOf(weatherRainStrength)));
        config.add("weatherThunderStrength", (JsonElement)new JsonPrimitive((Number)Float.valueOf(weatherThunderStrength)));
        config.add("windVolume", (JsonElement)new JsonPrimitive((Number)Float.valueOf(windVolume)));
        config.add("weatherRainParticleAmount", (JsonElement)new JsonPrimitive((Number)weatherRainParticleAmount));
        config.add("weatherThunderParticleAmount", (JsonElement)new JsonPrimitive((Number)weatherThunderParticleAmount));
        config.add("oceanPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(oceanPhysics)));
        config.add("oceanAdjustHitbox", (JsonElement)new JsonPrimitive(Boolean.valueOf(oceanAdjustHitbox)));
        config.add("oceanDetail", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanDetail)));
        config.add("oceanWaveHeightMultiplier", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanWaveHeightMultiplier)));
        config.add("oceanBlockRange", (JsonElement)new JsonPrimitive((Number)oceanBlockRange));
        config.add("oceanWeatherClear", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanWeatherClear)));
        config.add("oceanWeatherRain", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanWeatherRain)));
        config.add("oceanWeatherThunder", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanWeatherThunder)));
        config.add("oceanBaseSpeed", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanBaseSpeed)));
        config.add("oceanHorizontalWaveScale", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanHorizontalWaveScale)));
        config.add("oceanParticles", (JsonElement)new JsonPrimitive(Boolean.valueOf(oceanParticles)));
        config.add("oceanParticleAlpha", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanParticleAlpha)));
        config.add("oceanStickyEntities", (JsonElement)new JsonPrimitive(Boolean.valueOf(oceanStickyEntities)));
        config.add("oceanSplashVolumeNew", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanSplashVolume)));
        config.add("snowLOD", (JsonElement)new JsonPrimitive((Number)Float.valueOf(snowLOD)));
        config.add("particleRainOpacity", (JsonElement)new JsonPrimitive((Number)Float.valueOf(particleRainOpacity)));
        config.add("particleSnowOpacity", (JsonElement)new JsonPrimitive((Number)Float.valueOf(particleSnowOpacity)));
        config.add("particleDustOpacity", (JsonElement)new JsonPrimitive((Number)Float.valueOf(particleDustOpacity)));
        config.add("itemRotationSpeed", (JsonElement)new JsonPrimitive((Number)Float.valueOf(itemRotationSpeed)));
        config.add("jointBreakForce", (JsonElement)new JsonPrimitive((Number)Float.valueOf(jointBreakForce)));
        config.add("jointBlood", (JsonElement)new JsonPrimitive((Number)Float.valueOf(jointBlood)));
        config.add("mobRagdollLimit", (JsonElement)new JsonPrimitive((Number)mobRagdollLimit));
        config.add("clothEntityRange", (JsonElement)new JsonPrimitive((Number)Float.valueOf(clothEntityRange)));
        config.add("clothForceArmor", (JsonElement)new JsonPrimitive(Boolean.valueOf(clothForceArmor)));
        config.add("oceanPuddles", (JsonElement)new JsonPrimitive(Boolean.valueOf(oceanRipples)));
        config.add("oceanFoamAmount", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanFoamAmount)));
        config.add("oceanFoamOpacity", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanFoamOpacity)));
        config.add("cudaLiquids", (JsonElement)new JsonPrimitive(Boolean.valueOf(cudaLiquids)));
        config.add("cudaLiquidsParticleSize", (JsonElement)new JsonPrimitive((Number)Float.valueOf(cudaLiquidsParticleSize)));
        config.add("cudaLiquidsBlurPasses", (JsonElement)new JsonPrimitive((Number)cudaLiquidsBlurPasses));
        config.add("playbackSpeed", (JsonElement)new JsonPrimitive((Number)Float.valueOf(playbackSpeed)));
        config.add("oceanRainPuddleAmount", (JsonElement)new JsonPrimitive((Number)Float.valueOf(oceanRainPuddleAmount)));
        config.add("puddleResolutionQuality", (JsonElement)new JsonPrimitive((Number)oceanPuddleResolutionQuality));
        config.add("itemBreakPhysics", (JsonElement)new JsonPrimitive(Boolean.valueOf(itemBreakPhysics)));
        ConfigClient.add(config, customizedGravities, "customizedGravities");
        ConfigClient.add(config, customizedBuoyancies, "customizedBuoyancies");
        return config;
    }

    private static void add(JsonObject config, Map<String, Vector3f> map, String name) {
        JsonArray array = new JsonArray();
        for (Map.Entry<String, Vector3f> entry : map.entrySet()) {
            array.add(entry.getKey());
            Vector3f val = entry.getValue();
            array.add((Number)Float.valueOf(val.x));
            array.add((Number)Float.valueOf(val.y));
            array.add((Number)Float.valueOf(val.z));
        }
        config.add(name, (JsonElement)array);
    }

    public static void resetOptions() {
        itemPhysics = true;
        maxPhysicsObjects = 10000;
        cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
        minecraftBlockBreakParticles = false;
        vinePhysics = true;
        ConfigClient.save();
    }

    public static void resetGravities() {
        for (Map.Entry<String, Vector3f> entry : customizedGravities.entrySet()) {
            customizedGravities.put(entry.getKey(), new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_GRAVITY));
        }
        for (Map.Entry<String, Vector3f> entry : customizedBuoyancies.entrySet()) {
            customizedBuoyancies.put(entry.getKey(), new Vector3f((Vector3fc)DynamicsWorld.DEFAULT_BUOYANCY));
        }
        gravityChanged = true;
        ConfigClient.save();
    }

    public static void resetWeatherSettings() {
        windPhysics = true;
        weatherParticles = true;
        weatherClearStrength = 0.1f;
        weatherRainStrength = 1.0f;
        weatherThunderStrength = 1.4f;
        weatherRainParticleAmount = 5;
        weatherThunderParticleAmount = 2;
        particleRainOpacity = 1.0f;
        particleSnowOpacity = 1.0f;
        particleDustOpacity = 1.0f;
        ConfigClient.save();
    }

    public static void resetSmokeSettings() {
        smokePhysics = true;
        smokePhysicsRange = 100.0;
        particleLifetimeSmoke = 60.0;
        particleLifetimeVarianceSmoke = 15.0;
        particleDespawnTimeSmoke = 3.0;
        particleDespawnTimeVarianceSmoke = 12.0;
        smokeColorRed = 0.56f;
        smokeColorGreen = 0.56f;
        smokeColorBlue = 0.56f;
        smokeDenseColorRed = 0.364f;
        smokeDenseColorGreen = 0.364f;
        smokeDenseColorBlue = 0.364f;
        smokeDensity = 0.9f;
        smokeParticleLimit = 6000;
        smokeShadowTransformer = SmokeShadowTransformer.DISABLED;
        smokeBlaze = 0.05f;
        smokeCampfire = 0.3f;
        smokeFire = 0.3f;
        smokeOther = 1.0f;
        ConfigClient.save();
    }

    public static void resetOceanSettings() {
        oceanPhysics = true;
        oceanDetail = 1.0f;
        oceanAdjustHitbox = false;
        oceanWaveHeightMultiplier = 1.0f;
        oceanBlockRange = (byte)32;
        oceanWeatherClear = 0.0f;
        oceanWeatherRain = 0.75f;
        oceanWeatherThunder = 0.25f;
        oceanBaseSpeed = 1.0f;
        oceanHorizontalWaveScale = 1.0f;
        oceanParticles = true;
        oceanParticleAlpha = 0.5f;
        oceanStickyEntities = false;
        oceanRipples = true;
        oceanFoamAmount = 0.8f;
        oceanFoamOpacity = 0.5f;
        oceanPuddleResolutionQuality = 2048;
        oceanRainPuddleAmount = 0.5f;
        ConfigClient.save();
    }

    public static void resetSnowSettings() {
        snowPhysics = true;
        snowTracks = true;
        grassSnowy = true;
        snowLOD = 1.0f;
        snowTrackDistance = 48.0;
        snowTrackEntities = 6;
        snowType = 0;
        snowSmoothShading = true;
        snowQuality = 0;
        snowThickness = 0.0f;
        ConfigClient.save();
    }

    public static void toggleSettings() {
        if (!stored) {
            stored_vinePhysics = vinePhysics;
            stored_capePhysics = capePhysics;
            stored_itemPhysics = itemPhysics;
            stored_fishingRodPhysics = fishingRodPhysics;
            stored_leashPhysics = leashPhysics;
            stored_bannerPhysics = bannerPhysics;
            stored_liquidPhysics = liquidPhysics;
            stored_snowPhysics = snowPhysics;
            stored_smokePhysics = smokePhysics;
            stored_guiPhysics = guiPhysics;
            stored_windPhysics = windPhysics;
            stored_weatherParticles = weatherParticles;
            stored_oceanPhysics = oceanPhysics;
            stored_crackPhysicsParticles = crackPhysicsParticles;
            stored_sprintingPhysicsParticles = sprintingPhysicsParticles;
            stored_eatingPhysicsParticles = eatingPhysicsParticles;
            stored_serverBlockPhysicsParticles = serverBlockPhysicsParticles;
            stored_minecraftBlockBreakParticles = minecraftBlockBreakParticles;
            stored_snowballModel = snowballModel;
            stored_enderpearlModel = enderpearlModel;
            stored_eggModel = eggModel;
            stored_itemBreakPhysics = itemBreakPhysics;
            vinePhysics = false;
            capePhysics = false;
            itemPhysics = false;
            fishingRodPhysics = false;
            leashPhysics = false;
            bannerPhysics = false;
            liquidPhysics = false;
            snowPhysics = false;
            smokePhysics = false;
            windPhysics = false;
            weatherParticles = false;
            oceanPhysics = false;
            crackPhysicsParticles = false;
            sprintingPhysicsParticles = false;
            eatingPhysicsParticles = false;
            serverBlockPhysicsParticles = false;
            minecraftBlockBreakParticles = false;
            snowballModel = 2;
            enderpearlModel = 2;
            eggModel = 2;
            blockPhysics = false;
            mobPhysics = false;
            itemBreakPhysics = false;
        } else {
            vinePhysics = stored_vinePhysics;
            capePhysics = stored_capePhysics;
            itemPhysics = stored_itemPhysics;
            fishingRodPhysics = stored_fishingRodPhysics;
            leashPhysics = stored_leashPhysics;
            bannerPhysics = stored_bannerPhysics;
            liquidPhysics = stored_liquidPhysics;
            snowPhysics = stored_snowPhysics;
            smokePhysics = stored_smokePhysics;
            guiPhysics = stored_guiPhysics;
            windPhysics = stored_windPhysics;
            weatherParticles = stored_weatherParticles;
            oceanPhysics = stored_oceanPhysics;
            crackPhysicsParticles = stored_crackPhysicsParticles;
            sprintingPhysicsParticles = stored_sprintingPhysicsParticles;
            eatingPhysicsParticles = stored_eatingPhysicsParticles;
            serverBlockPhysicsParticles = stored_serverBlockPhysicsParticles;
            minecraftBlockBreakParticles = stored_minecraftBlockBreakParticles;
            snowballModel = stored_snowballModel;
            enderpearlModel = stored_enderpearlModel;
            eggModel = stored_eggModel;
            itemBreakPhysics = stored_itemBreakPhysics;
            blockPhysics = true;
            mobPhysics = true;
        }
        stored = !stored;
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            mod.getPhysicsWorld().destroy();
        }
        PhysicsMod.getInstances().clear();
        Minecraft.getInstance().levelRenderer.allChanged();
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_client_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigClient.createConfig();
        try {
            configFile.createNewFile();
            try (FileWriter writer = new FileWriter(configFile);){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson((JsonElement)config, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        oceanDetail = 1.0f;
        oceanAdjustHitbox = false;
        oceanWaveHeightMultiplier = 1.0f;
        oceanBlockRange = (byte)32;
        oceanWeatherClear = 0.0f;
        oceanWeatherRain = 0.75f;
        oceanWeatherThunder = 0.25f;
        oceanBaseSpeed = 1.0f;
        oceanHorizontalWaveScale = 1.0f;
        oceanParticles = true;
        oceanParticleAlpha = 0.5f;
        oceanFoamAmount = 0.8f;
        oceanFoamOpacity = 0.5f;
        oceanStickyEntities = false;
        oceanSplashVolume = 1.0f;
        itemRotationSpeed = 1.0f;
        jointBreakForce = 1.0f;
        jointBlood = 1.0f;
        mobRagdollLimit = 16;
        clothForceArmor = false;
        renderPhysicsDebugOverlay = false;
        oceanRipples = true;
        cudaLiquids = false;
        cudaLiquidsParticleSize = 0.1f;
        cudaLiquidsBlurPasses = 3;
        playbackSpeed = 1.0f;
        oceanRainPuddleAmount = 0.5f;
        oceanPuddleResolutionQuality = 2048;
        ConfigClient.reload();
        stored_vinePhysics = true;
        stored_capePhysics = true;
        stored_itemPhysics = true;
        stored_fishingRodPhysics = true;
        stored_leashPhysics = true;
        stored_bannerPhysics = true;
        stored_liquidPhysics = true;
        stored_snowPhysics = true;
        stored_smokePhysics = true;
        stored_guiPhysics = false;
        stored_windPhysics = true;
        stored_weatherParticles = true;
        stored_oceanPhysics = true;
        stored_crackPhysicsParticles = true;
        stored_sprintingPhysicsParticles = true;
        stored_eatingPhysicsParticles = true;
        stored_serverBlockPhysicsParticles = true;
        stored_minecraftBlockBreakParticles = false;
        stored_itemBreakPhysics = false;
        stored_snowballModel = 0;
        stored_enderpearlModel = 0;
        stored_eggModel = 0;
        stored = false;
    }
}

