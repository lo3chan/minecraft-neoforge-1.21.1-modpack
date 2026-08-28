/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.systems.RenderSystem
 *  java.lang.MatchException
 *  me.flashyreese.mods.greenlight.feature.ClientFeature
 *  me.flashyreese.mods.greenlight.feature.Greenlight
 *  net.caffeinemc.mods.sodium.api.config.ConfigState
 *  net.caffeinemc.mods.sodium.api.config.option.Range
 *  net.caffeinemc.mods.sodium.api.config.option.SteppedValidator
 *  net.caffeinemc.mods.sodium.client.config.ConfigManager
 *  net.caffeinemc.mods.sodium.client.config.structure.Config
 *  net.caffeinemc.mods.sodium.client.config.structure.IntegerOption
 *  net.caffeinemc.mods.sodium.client.config.structure.Option
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.OptionInstance$ClampingLazyMaxIntRange
 *  net.minecraft.client.OptionInstance$IntRange
 *  net.minecraft.client.OptionInstance$ValueSet
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.players.PlayerList
 *  net.minecraft.util.GsonHelper
 */
package me.flashyreese.mods.sodiumextra.client.fog;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;
import me.flashyreese.mods.greenlight.feature.ClientFeature;
import me.flashyreese.mods.greenlight.feature.Greenlight;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.client.fog.FogShaderTransformer;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.Config;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.GsonHelper;

public final class FogDistanceHelper {
    public static final ResourceLocation SODIUM_RENDER_DISTANCE_OPTION_ID = ResourceLocation.parse((String)"sodium:general.render_distance");
    public static final int FOG_DISTANCE_OFF = -1;
    public static final int FOG_DISTANCE_VANILLA = 0;
    private static final int LEGACY_FOG_DISTANCE_OFF = 33;
    public static final int VANILLA_CLOUD_FOG_PERCENT = 100;
    private static final int VANILLA_MAX_FOG_DISTANCE = 32;
    private static final int VANILLA_MAX_CLOUD_RENDER_DISTANCE = 128;
    private static final int PROTECTED_FOG_DISTANCE_MAX_BLOCKS = 256;
    private static final float PLANAR_RENDER_DISTANCE_OFFSET = 2097152.0f;
    private static final float CYLINDRICAL_RENDER_DISTANCE_OFFSET = 3145728.0f;
    private static final float CYLINDRICAL_CULL_DISTANCE_MARKER = 0.75f;
    private static final float CHUNK_SIZE = 16.0f;
    public static final float CYLINDRICAL_VERTICAL_SCALE = 16.0f;
    private static final ClientFeature<ProtectedGameplayFogPolicy> PROTECTED_GAMEPLAY_FOG = Greenlight.feature((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"sodium-extra", (String)"protected_gameplay_fog")).decoder(1, ProtectedGameplayFogPolicy::fromJson).register();
    private static volatile ExpandedCylindricalCull activeExpandedCylindricalCull;

    private FogDistanceHelper() {
    }

    public static SodiumExtraGameOptions.AtmosphericFogSettings getAtmosphericSettings(ClientLevel level) {
        SodiumExtraGameOptions.FogSettings fogSettings = FogDistanceHelper.getFogSettings();
        ResourceLocation dimensionEffectsId = level.dimensionType().effectsLocation();
        return fogSettings.getAtmospheric(dimensionEffectsId);
    }

    public static int normalizeFogDistance(int fogDistance) {
        return fogDistance == 33 ? -1 : fogDistance;
    }

    public static Range getFogDistanceRange(ConfigState state) {
        return new Range(-1, FogDistanceHelper.getMaxFogDistance(state), 1);
    }

    public static Range getProtectedGameplayFogDistanceRange() {
        return new Range(-1, 256, 1);
    }

    public static int getMaxFogDistance(ConfigState state) {
        int maxFogDistance = 32;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.options != null) {
            maxFogDistance = Math.max(maxFogDistance, (Integer)minecraft.options.renderDistance().get());
            OptionInstance.ValueSet valueSet = minecraft.options.renderDistance().values();
            if (valueSet instanceof OptionInstance.IntRange) {
                OptionInstance.IntRange range = (OptionInstance.IntRange)valueSet;
                maxFogDistance = Math.max(maxFogDistance, range.maxInclusive());
            } else if (valueSet instanceof OptionInstance.ClampingLazyMaxIntRange) {
                OptionInstance.ClampingLazyMaxIntRange range = (OptionInstance.ClampingLazyMaxIntRange)valueSet;
                maxFogDistance = Math.max(maxFogDistance, range.maxInclusive());
            } else {
                maxFogDistance = Math.max(maxFogDistance, FogDistanceHelper.getIntAccessor(valueSet, "maxInclusive", maxFogDistance));
            }
        }
        maxFogDistance = Math.max(maxFogDistance, FogDistanceHelper.getSodiumRenderDistanceMax(state, maxFogDistance));
        SodiumExtraGameOptions.FogSettings fogSettings = FogDistanceHelper.getFogSettings();
        maxFogDistance = Math.max(maxFogDistance, FogDistanceHelper.normalizeFogDistance(fogSettings.atmospheric.distanceChunks));
        for (SodiumExtraGameOptions.AtmosphericFogSettings settings : fogSettings.dimensionOverrides.values()) {
            maxFogDistance = Math.max(maxFogDistance, FogDistanceHelper.normalizeFogDistance(settings.distanceChunks));
        }
        return maxFogDistance;
    }

    private static int getSodiumRenderDistanceMax(ConfigState state, int fallback) {
        Config config = FogDistanceHelper.getSodiumConfig(state);
        if (config == null) {
            return fallback;
        }
        try {
            Option option = config.getOption(SODIUM_RENDER_DISTANCE_OPTION_ID);
            if (option instanceof IntegerOption) {
                IntegerOption integerOption = (IntegerOption)option;
                SteppedValidator validator = integerOption.getSteppedValidator();
                return validator.max();
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return fallback;
    }

    private static Config getSodiumConfig(ConfigState state) {
        if (state instanceof Config) {
            Config config = (Config)state;
            return config;
        }
        Config reflectedConfig = FogDistanceHelper.getConfigFromState(state);
        return reflectedConfig != null ? reflectedConfig : ConfigManager.CONFIG;
    }

    private static Config getConfigFromState(ConfigState state) {
        if (state == null) {
            return null;
        }
        for (Class type = state.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Config config;
                Field field = type.getDeclaredField("state");
                field.setAccessible(true);
                Object value = field.get(state);
                return value instanceof Config ? (config = (Config)value) : null;
            }
            catch (NoSuchFieldException ignored) {
                continue;
            }
            catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }
        return null;
    }

    public static float getStart(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
        return (float)settings.distanceChunks * 16.0f * ((float)settings.startPercent / 100.0f);
    }

    public static float applyStartMultiplier(float start, SodiumExtraGameOptions.AtmosphericFogSettings settings) {
        return start * ((float)settings.startPercent / 100.0f);
    }

    public static float getEnd(int fogDistance) {
        return (float)(fogDistance + 1) * 16.0f;
    }

    public static float getCloudEnd(int cloudFogPercent) {
        return (float)FogDistanceHelper.getCloudRenderDistance() * 16.0f * ((float)Math.clamp((long)cloudFogPercent, (int)0, (int)100) / 100.0f);
    }

    private static int getCloudRenderDistance() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return 128;
        }
        return Math.max(1, (Integer)minecraft.options.renderDistance().get());
    }

    public static boolean disablesFog(int fogDistance) {
        return fogDistance == -1;
    }

    public static void applyRenderDistanceShape(float fogStart, float fogEnd, SodiumExtraGameOptions.AtmosphericFogSettings settings) {
        if (fogEnd == Float.MAX_VALUE) {
            return;
        }
        switch (settings.shapeMode) {
            case CYLINDRICAL: {
                if (FogShaderTransformer.isShapeSupported()) {
                    RenderSystem.setShaderFogStart((float)(fogStart + 3145728.0f));
                    RenderSystem.setShaderFogEnd((float)(fogEnd + 3145728.0f));
                    break;
                }
                RenderSystem.setShaderFogShape((FogShape)FogShape.CYLINDER);
                break;
            }
            case RADIAL: {
                RenderSystem.setShaderFogShape((FogShape)FogShape.SPHERE);
                break;
            }
            case PLANAR: {
                if (!FogShaderTransformer.isShapeSupported()) break;
                RenderSystem.setShaderFogStart((float)(fogStart + 2097152.0f));
                RenderSystem.setShaderFogEnd((float)(fogEnd + 2097152.0f));
                break;
            }
        }
    }

    public static float expandCylindricalCullDistance(float currentDistance, float renderDistanceStart, float renderDistanceEnd, float renderDistance) {
        if (!FogDistanceHelper.isCylindricalRenderDistanceEncoded(renderDistanceStart, renderDistanceEnd)) {
            return currentDistance;
        }
        float decodedRenderDistanceEnd = renderDistanceEnd - 3145728.0f;
        if (!Float.isFinite(decodedRenderDistanceEnd) || decodedRenderDistanceEnd <= 0.0f || !Float.isFinite(renderDistance) || renderDistance <= 0.0f) {
            return currentDistance;
        }
        float horizontalLimit = renderDistance;
        float verticalLimit = renderDistance * 16.0f;
        float expandedDistance = (float)Math.ceil(Math.max(horizontalLimit, verticalLimit)) + 0.75f;
        activeExpandedCylindricalCull = new ExpandedCylindricalCull(expandedDistance, horizontalLimit, verticalLimit);
        return expandedDistance;
    }

    public static boolean isExpandedCylindricalCullDistance(float distanceLimit) {
        ExpandedCylindricalCull active = activeExpandedCylindricalCull;
        return active != null && active.matches(distanceLimit);
    }

    public static boolean testExpandedCylindricalCullDistance(float horizontalDistanceSquared, float verticalDistance, float distanceLimit) {
        ExpandedCylindricalCull active = activeExpandedCylindricalCull;
        if (active == null || !active.matches(distanceLimit)) {
            return horizontalDistanceSquared < distanceLimit * distanceLimit && Math.abs(verticalDistance) < distanceLimit;
        }
        return horizontalDistanceSquared < active.horizontalLimit() * active.horizontalLimit() && Math.abs(verticalDistance) < active.verticalLimit();
    }

    private static boolean isCylindricalRenderDistanceEncoded(float renderDistanceStart, float renderDistanceEnd) {
        return FogShaderTransformer.isShapeSupported() && Float.isFinite(renderDistanceStart) && Float.isFinite(renderDistanceEnd) && renderDistanceStart >= 3145728.0f && renderDistanceEnd >= 3145728.0f;
    }

    public static boolean isBossFogActive() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft != null && minecraft.gui != null && minecraft.gui.getBossOverlay().shouldCreateWorldFog();
    }

    public static boolean shouldModifyProtectedGameplayFog() {
        SodiumExtraGameOptions.FogSettings fogSettings = FogDistanceHelper.getFogSettings();
        return fogSettings.advanced && fogSettings.protectedGameplay.enabledWhenAllowed && (FogDistanceHelper.isLocalWorldAllowedForProtectedGameplayFog() || PROTECTED_GAMEPLAY_FOG.policy().isPresent());
    }

    public static int getProtectedGameplayFogDistance(ProtectedFogType type) {
        SodiumExtraGameOptions.FogSettings fogSettings = FogDistanceHelper.getFogSettings();
        if (!fogSettings.advanced || !fogSettings.protectedGameplay.enabledWhenAllowed) {
            return 0;
        }
        int distanceBlocks = FogDistanceHelper.getConfiguredProtectedGameplayFogDistance(fogSettings.protectedGameplay, type);
        if (FogDistanceHelper.isLocalWorldAllowedForProtectedGameplayFog()) {
            return distanceBlocks;
        }
        return PROTECTED_GAMEPLAY_FOG.policy().map(policy -> policy.clamp(type, distanceBlocks)).orElse(0);
    }

    private static int getConfiguredProtectedGameplayFogDistance(SodiumExtraGameOptions.ProtectedFogSettings settings, ProtectedFogType type) {
        return switch (type.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> settings.blindnessDistanceBlocks;
            case 1 -> settings.darknessDistanceBlocks;
            case 2 -> settings.lavaDistanceBlocks;
            case 3 -> settings.powderSnowDistanceBlocks;
            case 4 -> settings.waterDistanceBlocks;
        };
    }

    public static void applyProtectedGameplayFog(int distanceBlocks, float startMultiplier, float endMultiplier) {
        if ((distanceBlocks = FogDistanceHelper.normalizeFogDistance(distanceBlocks)) == 0) {
            return;
        }
        if (FogDistanceHelper.disablesFog(distanceBlocks)) {
            RenderSystem.setShaderFogStart((float)Float.MAX_VALUE);
            RenderSystem.setShaderFogEnd((float)Float.MAX_VALUE);
            return;
        }
        float end = (float)distanceBlocks * endMultiplier;
        RenderSystem.setShaderFogStart((float)((float)distanceBlocks * startMultiplier));
        RenderSystem.setShaderFogEnd((float)end);
    }

    public static SodiumExtraGameOptions.FogSettings getFogSettings() {
        SodiumExtraGameOptions.RenderSettings renderSettings = SodiumExtraClientMod.options().renderSettings;
        renderSettings.sanitize();
        return renderSettings.fogSettings;
    }

    private static boolean isLocalWorldAllowedForProtectedGameplayFog() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || !minecraft.hasSingleplayerServer()) {
            return false;
        }
        if (minecraft.isSingleplayer()) {
            return true;
        }
        PlayerList playerList = minecraft.getSingleplayerServer().getPlayerList();
        return playerList != null && playerList.isAllowCommandsForAllPlayers();
    }

    private static int getIntAccessor(Object object, String methodName, int fallback) {
        if (object == null) {
            return fallback;
        }
        try {
            int n;
            Method method = object.getClass().getMethod(methodName, new Class[0]);
            Object result = method.invoke(object, new Object[0]);
            if (result instanceof Integer) {
                Integer value = (Integer)result;
                n = value;
            } else {
                n = fallback;
            }
            return n;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) {
            return fallback;
        }
    }

    private record ExpandedCylindricalCull(float distanceLimit, float horizontalLimit, float verticalLimit) {
        private boolean matches(float candidate) {
            return Float.floatToRawIntBits(candidate) == Float.floatToRawIntBits(this.distanceLimit);
        }
    }

    public static enum ProtectedFogType {
        BLINDNESS("blindness"),
        DARKNESS("darkness"),
        LAVA("lava"),
        POWDER_SNOW("powder_snow"),
        WATER("water");

        private final String policyKey;

        private ProtectedFogType(String policyKey) {
            this.policyKey = policyKey;
        }
    }

    private record ProtectedGameplayFogPolicy(Map<ProtectedFogType, ProtectedFogRule> rules) {
        private static ProtectedGameplayFogPolicy fromJson(JsonObject settings) {
            EnumMap<ProtectedFogType, ProtectedFogRule> rules = new EnumMap<ProtectedFogType, ProtectedFogRule>(ProtectedFogType.class);
            for (ProtectedFogType type : ProtectedFogType.values()) {
                JsonObject rule = GsonHelper.getAsJsonObject((JsonObject)settings, (String)type.policyKey, (JsonObject)new JsonObject());
                boolean enabled = GsonHelper.getAsBoolean((JsonObject)rule, (String)"enabled", (boolean)false);
                int maxDistanceBlocks = Math.max(0, Math.min(GsonHelper.getAsInt((JsonObject)rule, (String)"max_distance_blocks", (int)0), 256));
                boolean allowOff = GsonHelper.getAsBoolean((JsonObject)rule, (String)"allow_off", (boolean)false);
                rules.put(type, new ProtectedFogRule(enabled, maxDistanceBlocks, allowOff));
            }
            return new ProtectedGameplayFogPolicy(Map.copyOf(rules));
        }

        private int clamp(ProtectedFogType type, int distanceBlocks) {
            ProtectedFogRule rule = this.rules.get((Object)type);
            return rule != null ? rule.clamp(distanceBlocks) : 0;
        }
    }

    private record ProtectedFogRule(boolean enabled, int maxDistanceBlocks, boolean allowOff) {
        private int clamp(int distanceBlocks) {
            if (!this.enabled) {
                return 0;
            }
            if (distanceBlocks == 0) {
                return 0;
            }
            if (FogDistanceHelper.disablesFog(distanceBlocks)) {
                return this.allowOff ? -1 : this.maxDistanceBlocks;
            }
            return Math.min(distanceBlocks, this.maxDistanceBlocks);
        }
    }
}

