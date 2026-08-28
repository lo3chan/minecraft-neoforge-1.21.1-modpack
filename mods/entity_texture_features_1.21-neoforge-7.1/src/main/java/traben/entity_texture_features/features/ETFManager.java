/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.features.texture_handlers.ETFTextureVariator;
import traben.entity_texture_features.utils.ETFLruCache;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFManager {
    private static final ETFTexture ETF_ERROR_TEXTURE = ETFManager.getErrorETFTexture();
    private static ETFManager instance;
    public final Set<String> EMISSIVE_SUFFIX_LIST = new HashSet<String>();
    public final ETFLruCache<UUID, ETFPlayerTexture> PLAYER_TEXTURE_MAP = new ETFLruCache();
    public final ArrayList<String> KNOWN_RESOURCEPACK_ORDER = new ArrayList();
    public final Set<EntityType<?>> ENTITY_TYPE_IGNORE_PARTICLES = new HashSet();
    public final HashMap<@NotNull ResourceLocation, @Nullable ETFTexture> ETF_TEXTURE_CACHE = new HashMap();
    public final ETFLruCache.UUIDInteger LAST_SUFFIX_OF_ENTITY = new ETFLruCache.UUIDInteger();
    public final ETFLruCache.UUIDInteger LAST_RULE_INDEX_OF_ENTITY = new ETFLruCache.UUIDInteger();
    public final HashMap<@NotNull ResourceLocation, @NotNull ETFDirectory> ETF_DIRECTORY_CACHE = new HashMap();
    private final HashMap<ResourceLocation, ETFTextureVariator> VARIATOR_MAP = new HashMap();
    public UUID ENTITY_DEBUG = null;
    public Boolean mooshroomBrownCustomShroomExists = null;
    public Boolean mooshroomRedCustomShroomExists = null;
    public ETFTexture redMooshroomAlt = null;
    public ETFTexture brownMooshroomAlt = null;

    private ETFManager() {
        for (PackResources pack : Minecraft.getInstance().getResourceManager().listPacks().toList()) {
            this.KNOWN_RESOURCEPACK_ORDER.add(pack.packId());
        }
        try {
            ArrayList<Properties> props = new ArrayList<Properties>();
            String[] paths = new String[]{"optifine/emissive.properties", "textures/emissive.properties", "etf/emissive.properties"};
            for (String path : paths) {
                List<Properties> prop = ETFUtils2.readAndReturnAllLayeredPropertiesElseNull(ETFUtils2.res(path));
                if (prop == null) continue;
                props.addAll(prop);
            }
            for (Properties prop : props) {
                String[] keys;
                for (String key : keys = new String[]{"entities.suffix.emissive", "suffix.emissive"}) {
                    String value = prop.getProperty(key);
                    if (value == null) continue;
                    this.EMISSIVE_SUFFIX_LIST.add(value);
                }
            }
            if (ETF.config().getConfig().alwaysCheckVanillaEmissiveSuffix) {
                this.EMISSIVE_SUFFIX_LIST.add("_e");
            }
            if (this.EMISSIVE_SUFFIX_LIST.isEmpty()) {
                ETFUtils2.logMessage("no emissive suffixes found: default emissive suffix '_e' used");
                this.EMISSIVE_SUFFIX_LIST.add("_e");
            } else {
                ETFUtils2.logMessage("emissive suffixes loaded: " + String.valueOf(this.EMISSIVE_SUFFIX_LIST));
            }
        }
        catch (Exception e) {
            ETFUtils2.logError("emissive suffixes could not be read: default emissive suffix '_e' used");
            this.EMISSIVE_SUFFIX_LIST.add("_e");
        }
    }

    public static ETFManager getInstance() {
        if (instance == null) {
            instance = new ETFManager();
        }
        return instance;
    }

    public static void resetInstance() {
        ETF.config().loadFromFile();
        instance = new ETFManager();
    }

    @NotNull
    public static ETFTexture getErrorETFTexture() {
        ETFUtils2.registerNativeImageToIdentifier(ETFUtils2.emptyNativeImage(), ETFUtils2.res("entity_texture_features", "error.png"));
        return new ETFTexture(ETFUtils2.res("entity_texture_features", "error.png"));
    }

    public static ETFConfig.EmissiveRenderModes getEmissiveMode() {
        ETFConfig.EmissiveRenderModes mode = ETF.config().getConfig().getEmissiveRenderMode();
        if (mode == ETFConfig.EmissiveRenderModes.BRIGHT && ETFRenderContext.getCurrentEntityState() != null && !ETFRenderContext.getCurrentEntityState().canRenderBright()) {
            return ETFConfig.EmissiveRenderModes.DULL;
        }
        return mode;
    }

    public String getGeneralPrintout() {
        return "\u00a7aGeneral: \u00a7r\n\u00a72 - amount of 'base' textures: \u00a7r" + this.VARIATOR_MAP.size() + "\n\u00a72 - total textures including variants: \u00a7r" + this.ETF_TEXTURE_CACHE.size();
    }

    public void doTheBigBoyPrintoutKronk() {
        StringBuilder out = new StringBuilder();
        out.append("\n||||||||||||||-ETF EVERYTHING LOG START-|||||||||||||||").append("\n----------------------------------------").append("\n-----------General stats-------------").append("\n----------------------------------------").append("\n known emissive suffixes: \n - ").append(this.EMISSIVE_SUFFIX_LIST).append("\n player textures: \n - ").append(this.PLAYER_TEXTURE_MAP.size()).append("\n----------------------------------------").append("\n----------Texture totals----------------").append("\n----------------------------------------").append("\n amount of textures that have or can variate: \n - ").append(this.VARIATOR_MAP.size()).append("\n amount of textures seen by ETF total (not including emissives): \n - ").append(this.ETF_TEXTURE_CACHE.size());
        StringBuilder textureLoopVariates = new StringBuilder();
        StringBuilder textureLoopNormal = new StringBuilder();
        int totalEmissive = 0;
        int totalEnchant = 0;
        for (ETFTexture texture : this.ETF_TEXTURE_CACHE.values()) {
            if (texture == null) continue;
            if (texture.isEmissive()) {
                ++totalEmissive;
            }
            if (texture.isEnchanted()) {
                ++totalEnchant;
            }
            if (this.VARIATOR_MAP.containsKey(texture.thisIdentifier)) {
                textureLoopVariates.append("\n - ").append(this.VARIATOR_MAP.get(texture.thisIdentifier).getPrintout().replaceAll("\n", "\n      "));
            }
            textureLoopNormal.append("\n - ").append(texture);
        }
        out.append("\n total emissives: \n - ").append(totalEmissive).append("\n total enchanted: \n - ").append(totalEnchant).append("\n----------------------------------------").append("\n----------ALL texture groups-------------").append("\n----------------------------------------").append("\n (Note: all of these can be varied via random entity rules)").append(textureLoopVariates.toString().replaceAll("\u00a7.", "")).append("\n----------------------------------------").append("\n----------ALL Textures Seen-------------").append("\n----------------------------------------").append("\n (Note: these are not all variable by random entity rules, but can usually be emissive)").append((CharSequence)textureLoopNormal).append("\n----------------------------------------");
        out.append("\n----------------------------------------").append("\n||||||||||||||-ETF EVERYTHING LOG END-|||||||||||||||");
        ETFUtils2.logMessage(out.toString());
    }

    public void grabSpecialProperties(Properties props, ETFEntityRenderState entity) {
        if (entity == null) {
            return;
        }
        if (props.containsKey("vanillaBrightnessOverride")) {
            String value = props.getProperty("vanillaBrightnessOverride").trim();
            try {
                int tryNumber = Integer.parseInt(value.replaceAll("\\D", ""));
                if (tryNumber >= 16) {
                    tryNumber = 15;
                }
                if (tryNumber < 0) {
                    tryNumber = 0;
                }
                ETF.config().getConfig().entityLightOverrides.put(entity.entityKey(), tryNumber);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (props.containsKey("suppressParticles") && "true".equals(props.getProperty("suppressParticles"))) {
            this.ENTITY_TYPE_IGNORE_PARTICLES.add(entity.entityType());
        }
        if (props.containsKey("entityRenderLayerOverride")) {
            String layer;
            switch (layer = props.getProperty("entityRenderLayerOverride")) {
                case "translucent": {
                    ETF.config().getConfig().entityRenderLayerOverrides.put(entity.entityKey(), ETFConfig.RenderLayerOverride.TRANSLUCENT);
                    break;
                }
                case "translucent_cull": {
                    ETF.config().getConfig().entityRenderLayerOverrides.put(entity.entityKey(), ETFConfig.RenderLayerOverride.TRANSLUCENT_CULL);
                    break;
                }
                case "end_portal": {
                    ETF.config().getConfig().entityRenderLayerOverrides.put(entity.entityKey(), ETFConfig.RenderLayerOverride.END);
                    break;
                }
                case "outline": {
                    ETF.config().getConfig().entityRenderLayerOverrides.put(entity.entityKey(), ETFConfig.RenderLayerOverride.OUTLINE);
                }
            }
        }
    }

    public void markEntityForDebugPrint(UUID uuid) {
        if (ETF.config().getConfig().debugLoggingMode != ETFConfig.DebugLogMode.None) {
            this.ENTITY_DEBUG = uuid;
        }
    }

    @NotNull
    public ETFTexture getETFTextureNoVariation(ResourceLocation vanillaIdentifier) {
        return this.getOrCreateETFTexture(vanillaIdentifier);
    }

    @NotNull
    public ETFTexture getETFTextureVariant(@NotNull ResourceLocation vanillaIdentifier, @Nullable ETFEntityRenderState entity) {
        if (entity == null || entity.uuid() == ETFApi.ETF_GENERIC_UUID || entity.blockPos().equals((Object)Vec3i.ZERO) && entity.uuid().getLeastSignificantBits() != 0x303900003039L) {
            return this.getETFTextureNoVariation(vanillaIdentifier);
        }
        if (!this.VARIATOR_MAP.containsKey(vanillaIdentifier)) {
            if ("etf_skin".equals(vanillaIdentifier.getNamespace())) {
                return this.getETFTextureNoVariation(vanillaIdentifier);
            }
            this.VARIATOR_MAP.put(vanillaIdentifier, ETFTextureVariator.of(vanillaIdentifier));
            if (ETF.config().getConfig().logTextureDataInitialization) {
                ETFUtils2.logMessage("Amount of 'base' textures: " + this.VARIATOR_MAP.size());
                ETFUtils2.logMessage("Total textures including variants: " + this.ETF_TEXTURE_CACHE.size());
            }
        }
        return this.VARIATOR_MAP.get(vanillaIdentifier).getVariantOf(entity);
    }

    @NotNull
    private ETFTexture getOrCreateETFTexture(ResourceLocation ofIdentifier) {
        try {
            ETFTexture texture = this.ETF_TEXTURE_CACHE.get(ofIdentifier);
            if (texture != null) {
                return texture;
            }
            ETFTexture texture2 = new ETFTexture(ofIdentifier);
            this.ETF_TEXTURE_CACHE.put(ofIdentifier, texture2);
            return texture2;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return new ETFTexture(ofIdentifier);
        }
    }

    @Nullable
    public ETFPlayerTexture getPlayerTexture(Player player, ResourceLocation rendererGivenSkin) {
        return this.getPlayerTexture((ETFPlayerEntity)player, rendererGivenSkin);
    }

    @Nullable
    public ETFPlayerTexture getPlayerTexture(ETFPlayerEntity player, ResourceLocation rendererGivenSkin) {
        try {
            UUID id = player.etf$getUuid();
            if (this.PLAYER_TEXTURE_MAP.containsKey(id)) {
                ETFPlayerTexture possibleSkin = this.PLAYER_TEXTURE_MAP.get(id);
                if (possibleSkin == null || possibleSkin.player == null && possibleSkin.isCorrectObjectForThisSkin(rendererGivenSkin)) {
                    return null;
                }
                if (possibleSkin.isCorrectObjectForThisSkin(rendererGivenSkin) || Minecraft.getInstance().screen instanceof ETFConfigScreenSkinTool) {
                    return possibleSkin;
                }
            }
            this.PLAYER_TEXTURE_MAP.put(id, null);
            ETFPlayerTexture etfPlayerTexture = new ETFPlayerTexture(player, rendererGivenSkin);
            ETFPlayerTexture set = this.PLAYER_TEXTURE_MAP.get(id);
            if (set != null) {
                if (set.shouldRetryOnFail) {
                    this.PLAYER_TEXTURE_MAP.remove(id);
                    return null;
                }
                return set;
            }
            this.PLAYER_TEXTURE_MAP.put(id, etfPlayerTexture);
            return etfPlayerTexture;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static enum TextureSource {
        ENTITY,
        BLOCK_ENTITY,
        ENTITY_FEATURE;

    }
}

