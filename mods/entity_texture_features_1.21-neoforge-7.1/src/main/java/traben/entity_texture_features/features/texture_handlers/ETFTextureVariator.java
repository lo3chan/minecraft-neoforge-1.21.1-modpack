/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package traben.entity_texture_features.features.texture_handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFLruCache;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class ETFTextureVariator {
    @NotNull
    public static ETFTextureVariator of(@NotNull ResourceLocation vanillaIdentifier) {
        ETFApi.ETFVariantSuffixProvider variantProvider = ETFApi.ETFVariantSuffixProvider.getVariantProviderOrNull(ETFUtils2.replaceIdentifier(vanillaIdentifier, ".png", ".properties"), vanillaIdentifier, "skins", "textures");
        if (variantProvider != null) {
            return new ETFTextureMultiple(vanillaIdentifier, variantProvider);
        }
        return new ETFTextureSingleton(vanillaIdentifier);
    }

    public ETFTexture getVariantOf(@NotNull ETFEntityRenderState entity) {
        ETFTexture variantOfInternal = this.getVariantOfInternal(entity);
        this.logDebugMessageIfNeeded(variantOfInternal, entity);
        return variantOfInternal;
    }

    /*
     * Unable to fully structure code
     */
    private void logDebugMessageIfNeeded(ETFTexture variant, ETFEntityRenderState entity) {
        block2: {
            if (ETFManager.getInstance().ENTITY_DEBUG == null || !ETFManager.getInstance().ENTITY_DEBUG.equals(entity.uuid())) break block2;
            inChat = ETF.config().getConfig().debugLoggingMode == ETFConfig.DebugLogMode.Chat;
            v0 = ETFManager.getInstance().getGeneralPrintout();
            v1 = entity.entityType() != null ? entity.entityType().getDescriptionId() : null;
            v2 = String.valueOf(variant);
            var5_4 = this;
            if (!(var5_4 instanceof ETFTextureMultiple)) ** GOTO lbl-1000
            multi = (ETFTextureMultiple)var5_4;
            if (multi.suffixProvider.entityCanUpdate(entity.uuid())) {
                v3 = true;
            } else lbl-1000:
            // 2 sources

            {
                v3 = false;
            }
            ETFUtils2.logMessage("\n\u00a7e-----------ETF Debug Printout-------------\u00a7r\n" + v0 + "\n\u00a7eEntity:\u00a7r\n\u00a76 - type:\u00a7r " + v1 + "\n\u00a76 - texture:\u00a7r " + v2 + "\n\u00a76 - can_update_variant:\u00a7r " + v3 + "\n" + this.getVanillaVariantDetails() + "\n" + this.getPrintout() + "\n\u00a7e----------------------------------------\u00a7r", inChat);
            ETFManager.getInstance().ENTITY_DEBUG = null;
        }
    }

    private String getVanillaVariantDetails() {
        ResourceLocation vanilla = this.getVanillaIdentifier();
        if (vanilla == null) {
            return "\u00a7aProperty locations: NULL\u00a7r";
        }
        ResourceLocation property = ETFUtils2.replaceIdentifier(vanilla, ".png", ".properties");
        if (property == null) {
            return "\u00a7aProperty locations: NULL\u00a7r";
        }
        ResourceLocation optifine = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.OPTIFINE);
        ResourceLocation optifineOld = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.OLD_OPTIFINE);
        ResourceLocation etf = ETFDirectory.getIdentifierAsDirectory(property, ETFDirectory.ETF);
        return "\u00a7aProperty locations:\u00a7r\n\u00a72 - regular:\u00a7r " + String.valueOf(property) + "\n\u00a72 - etf:\u00a7r " + String.valueOf(etf) + "\n\u00a72 - optifine:\u00a7r " + String.valueOf(optifine) + "\n\u00a72 - optifine_old:\u00a7r " + String.valueOf(optifineOld);
    }

    public abstract String getPrintout();

    @NotNull
    protected abstract ETFTexture getVariantOfInternal(@NotNull ETFEntityRenderState var1);

    protected abstract ResourceLocation getVanillaIdentifier();

    private static class ETFTextureMultiple
    extends ETFTextureVariator {
        @NotNull
        public final ETFLruCache.UUIDInteger entitySuffixMap = new ETFLruCache.UUIDInteger(500);
        @NotNull
        final ETFApi.ETFVariantSuffixProvider suffixProvider;
        @NotNull
        private final Map<Integer, ETFTexture> variantMap;
        @NotNull
        private final ResourceLocation vanillaIdentifier;

        ETFTextureMultiple(@NotNull ResourceLocation vanillaIdentifier, @NotNull ETFApi.ETFVariantSuffixProvider suffixProvider) {
            this.vanillaIdentifier = vanillaIdentifier;
            this.suffixProvider = suffixProvider;
            if (suffixProvider instanceof PropertiesRandomProvider) {
                ((PropertiesRandomProvider)suffixProvider).setOnMeetsRuleHook((entity, rule) -> {
                    if (rule == null) {
                        ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.remove(entity.uuid());
                    } else {
                        ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.put(entity.uuid(), rule.ruleNumber);
                    }
                });
            }
            ResourceLocation directorized = ETF.config().getConfig().optifine_preventBaseTextureInOptifineDirectory ? null : ETFDirectory.getDirectoryVersionOf(vanillaIdentifier);
            final ETFTexture vanilla = ETFManager.getInstance().getETFTextureNoVariation(directorized == null ? vanillaIdentifier : directorized);
            this.variantMap = new HashMap<Integer, ETFTexture>(){

                @Override
                public ETFTexture get(Object key) {
                    return super.getOrDefault(key, vanilla);
                }
            };
            this.variantMap.put(1, vanilla);
            boolean logging = ETF.config().getConfig().logTextureDataInitialization;
            if (logging) {
                ETFUtils2.logMessage("Initializing texture for the first time: " + String.valueOf(vanillaIdentifier));
            }
            Set<Integer> suffixes = suffixProvider.getAllSuffixes();
            suffixes.remove(0);
            suffixes.remove(1);
            for (int suffix : suffixes) {
                ResourceLocation variant = ETFDirectory.getDirectoryVersionOf(ETFUtils2.addVariantNumberSuffix(vanillaIdentifier, suffix));
                if (logging) {
                    ETFUtils2.logMessage(" - looked for variant: " + String.valueOf(variant));
                }
                if (variant != null) {
                    this.variantMap.put(suffix, ETFManager.getInstance().getETFTextureNoVariation(variant));
                    continue;
                }
                if (logging) {
                    ETFUtils2.logMessage("   - failed to find variant: " + suffix);
                }
                this.variantMap.put(suffix, vanilla);
            }
            if (logging) {
                ETFUtils2.logMessage("Final variant map for: " + String.valueOf(vanillaIdentifier));
                this.variantMap.forEach((k, v) -> ETFUtils2.logMessage(" - " + k + " = " + String.valueOf(v)));
            }
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ETFTextureMultiple that = (ETFTextureMultiple)o;
            return this.vanillaIdentifier.equals((Object)that.vanillaIdentifier);
        }

        public int hashCode() {
            return Objects.hash(this.vanillaIdentifier);
        }

        @Override
        public String getPrintout() {
            return "\u00a7bTexture: \u00a7r\n\u00a73 - base texture:\u00a7r " + String.valueOf(this.vanillaIdentifier) + "\n\u00a73 - variates:\u00a7r yes\n\u00a73 - set by properties:\u00a7r " + (this.suffixProvider instanceof PropertiesRandomProvider) + "\n\u00a73 - variant count:\u00a7r " + this.variantMap.size() + "\n\u00a73 - all suffixes:\u00a7r " + String.valueOf(this.variantMap.keySet());
        }

        public void checkIfShouldExpireEntity(@NotNull ETFEntityRenderState entity, UUID id) {
            if (this.suffixProvider.entityCanUpdate(id)) {
                switch (ETF.config().getConfig().textureUpdateFrequency_V2) {
                    case Never: {
                        break;
                    }
                    case Instant: {
                        this.entitySuffixMap.remove(id);
                        break;
                    }
                    default: {
                        int delay = ETF.config().getConfig().textureUpdateFrequency_V2.getDelay();
                        int time = (int)(entity.world().getGameTime() % (long)delay);
                        if (time != Math.abs(id.hashCode()) % delay) break;
                        this.entitySuffixMap.remove(id);
                    }
                }
            }
        }

        @Override
        @NotNull
        protected ETFTexture getVariantOfInternal(@NotNull ETFEntityRenderState entity) {
            ETFManager.TextureSource source = this.determineTextureSource(entity);
            UUID id = entity.uuid();
            int knownSuffix = (Integer)this.entitySuffixMap.get(id);
            if (knownSuffix != -1) {
                this.checkIfShouldExpireEntity(entity, id);
                return this.variantMap.get(knownSuffix);
            }
            int newSuffix = this.determineNewSuffix(entity, source);
            this.entitySuffixMap.put(id, newSuffix);
            return this.variantMap.get(newSuffix);
        }

        private ETFManager.TextureSource determineTextureSource(@NotNull ETFEntityRenderState entity) {
            if (ETFRenderContext.isRenderingFeatures()) {
                return ETFManager.TextureSource.ENTITY_FEATURE;
            }
            if (entity.isBlockEntity()) {
                return ETFManager.TextureSource.BLOCK_ENTITY;
            }
            return ETFManager.TextureSource.ENTITY;
        }

        private int determineNewSuffix(@NotNull ETFEntityRenderState entity, ETFManager.TextureSource source) {
            if (source == ETFManager.TextureSource.ENTITY_FEATURE) {
                if (this.suffixProvider instanceof PropertiesRandomProvider) {
                    return this.suffixProvider.getSuffixForETFEntity(entity);
                }
                return this.getBaseEntitySuffixOrNew(entity);
            }
            int newSuffix = this.suffixProvider.getSuffixForETFEntity(entity);
            ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.put(entity.uuid(), newSuffix);
            return newSuffix;
        }

        private int getBaseEntitySuffixOrNew(@NotNull ETFEntityRenderState entity) {
            int baseEntitySuffix;
            int n = baseEntitySuffix = ETFRenderContext.getCurrentEntityState() == null ? -1 : (Integer)ETFManager.getInstance().LAST_SUFFIX_OF_ENTITY.get(ETFRenderContext.getCurrentEntityState().uuid());
            if (baseEntitySuffix != -1 && this.variantMap.containsKey(baseEntitySuffix)) {
                return baseEntitySuffix;
            }
            return this.suffixProvider.getSuffixForETFEntity(entity);
        }

        @Override
        @NotNull
        protected ResourceLocation getVanillaIdentifier() {
            return this.vanillaIdentifier;
        }
    }

    public static class ETFTextureSingleton
    extends ETFTextureVariator {
        private final ETFTexture self;
        private final ResourceLocation vanillaIdentifier;

        public ETFTextureSingleton(ResourceLocation singletonId) {
            this.vanillaIdentifier = singletonId;
            this.self = ETFManager.getInstance().getETFTextureNoVariation(singletonId);
            if (ETF.config().getConfig().logTextureDataInitialization) {
                ETFUtils2.logMessage("Initializing texture for the first time: " + String.valueOf(singletonId));
                ETFUtils2.logMessage(" - no variants for: " + String.valueOf(this.self));
            }
        }

        @Override
        @NotNull
        protected ETFTexture getVariantOfInternal(@NotNull ETFEntityRenderState entity) {
            return this.self;
        }

        @Override
        protected ResourceLocation getVanillaIdentifier() {
            return this.vanillaIdentifier;
        }

        @Override
        public String getPrintout() {
            return "\u00a7bTexture: \u00a7r\n\u00a73 - base texture:\u00a7r " + this.self.toString() + "\n\u00a73 - variates:\u00a7r no";
        }
    }
}

