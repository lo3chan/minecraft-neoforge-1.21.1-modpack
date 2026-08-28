/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  java.lang.MatchException
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Deprecated
public class ETFRenderContext {
    public static boolean renderingFeatures = false;
    private static boolean allowRenderLayerTextureModify = true;
    private static boolean limitModifyToProperties = false;
    private static ETFEntityRenderState currentEntity = null;
    private static int currentModelPartDepth = 0;
    private static boolean isInSpecialRenderOverlayPhase = false;
    private static boolean allowedToPatch = false;
    private static CompoundTag currentEntityNBT = null;
    private static UUID entityNBT_UUID = null;

    public static CompoundTag cacheEntityNBTForFrame(UUID entityUUID, Supplier<CompoundTag> computeNBT) {
        if (currentEntityNBT == null || !entityUUID.equals(entityNBT_UUID)) {
            currentEntityNBT = computeNBT.get();
            entityNBT_UUID = entityUUID;
        }
        return currentEntityNBT;
    }

    public static boolean isRenderingFeatures() {
        return renderingFeatures;
    }

    public static void setRenderingFeatures(boolean renderingFeatures) {
        ETFRenderContext.renderingFeatures = renderingFeatures;
    }

    public static boolean isAllowedToRenderLayerTextureModify() {
        return allowRenderLayerTextureModify && ETF.config().getConfig().canDoCustomTextures();
    }

    public static void preventRenderLayerTextureModify() {
        allowRenderLayerTextureModify = false;
    }

    public static void allowRenderLayerTextureModify() {
        allowRenderLayerTextureModify = true;
    }

    @Deprecated
    @Nullable
    public static ETFEntityRenderState getCurrentEntityState() {
        return currentEntity;
    }

    public static void setCurrentEntity(ETFEntityRenderState currentEntity) {
        allowRenderLayerTextureModify = true;
        currentEntityNBT = null;
        entityNBT_UUID = null;
        ETFRenderContext.currentEntity = currentEntity;
    }

    public static boolean canRenderInBrightMode() {
        boolean setForBrightMode;
        boolean bl = setForBrightMode = ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT;
        if (setForBrightMode) {
            if (currentEntity != null) {
                return currentEntity.canRenderBright();
            }
            return true;
        }
        return false;
    }

    public static boolean shouldEmissiveUseCullingLayer() {
        if (currentEntity != null) {
            return currentEntity.isBlockEntity();
        }
        return true;
    }

    public static int getCurrentModelPartDepth() {
        return currentModelPartDepth;
    }

    public static void incrementCurrentModelPartDepth() {
        ++currentModelPartDepth;
    }

    public static void decrementCurrentModelPartDepth() {
        --currentModelPartDepth;
    }

    public static void resetCurrentModelPartDepth() {
        currentModelPartDepth = 0;
    }

    public static void reset() {
        currentModelPartDepth = 0;
        currentEntity = null;
        allowedToPatch = false;
        allowRenderLayerTextureModify = true;
        limitModifyToProperties = false;
        currentEntityNBT = null;
        entityNBT_UUID = null;
    }

    public static boolean isIsInSpecialRenderOverlayPhase() {
        return isInSpecialRenderOverlayPhase;
    }

    public static void startSpecialRenderOverlayPhase() {
        isInSpecialRenderOverlayPhase = true;
    }

    public static void endSpecialRenderOverlayPhase() {
        isInSpecialRenderOverlayPhase = false;
    }

    public static boolean isAllowedToPatch() {
        return allowedToPatch;
    }

    public static void allowTexturePatching() {
        allowedToPatch = true;
    }

    public static void allowOnlyPropertiesRandom() {
        limitModifyToProperties = true;
    }

    public static void allowAllRandom() {
        limitModifyToProperties = false;
    }

    public static boolean isRandomLimitedToProperties() {
        return limitModifyToProperties;
    }

    public static void preventTexturePatching() {
        allowedToPatch = false;
    }

    public static RenderType modifyRenderLayerIfRequired(RenderType value) {
        ETFRenderLayerWithTexture multiphase;
        Optional<ResourceLocation> texture;
        ETFConfig.RenderLayerOverride layer;
        if (ETFRenderContext.isCurrentlyRenderingEntity() && ETFRenderContext.isAllowedToRenderLayerTextureModify() && (layer = ETF.config().getConfig().getRenderLayerOverride()) != null && !value.isOutline() && value instanceof ETFRenderLayerWithTexture && (texture = (multiphase = (ETFRenderLayerWithTexture)value).etf$getId()).isPresent()) {
            ETFRenderContext.preventRenderLayerTextureModify();
            RenderType forReturn = switch (layer) {
                default -> throw new MatchException(null, null);
                case ETFConfig.RenderLayerOverride.TRANSLUCENT -> RenderType.entityTranslucent((ResourceLocation)texture.get());
                case ETFConfig.RenderLayerOverride.TRANSLUCENT_CULL -> RenderType.entityTranslucentCull((ResourceLocation)texture.get());
                case ETFConfig.RenderLayerOverride.END -> RenderType.endGateway();
                case ETFConfig.RenderLayerOverride.OUTLINE -> RenderType.outline((ResourceLocation)texture.get());
            };
            ETFRenderContext.allowRenderLayerTextureModify();
            return forReturn;
        }
        return value;
    }

    public static void insertETFDataIntoVertexConsumer(MultiBufferSource provider, RenderType renderLayer, VertexConsumer vertexConsumer) {
        if (ETFRenderContext.isCurrentlyRenderingEntity() && vertexConsumer instanceof ETFVertexConsumer) {
            ETFVertexConsumer etfVertexConsumer = (ETFVertexConsumer)vertexConsumer;
            etfVertexConsumer.etf$initETFVertexConsumer(provider, renderLayer);
        }
    }

    public static boolean isCurrentlyRenderingEntity() {
        return currentEntity != null;
    }
}

