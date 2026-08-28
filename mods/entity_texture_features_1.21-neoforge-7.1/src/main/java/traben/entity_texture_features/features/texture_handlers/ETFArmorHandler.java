/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.armortrim.ArmorTrim
 */
package traben.entity_texture_features.features.texture_handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFArmorHandler {
    private ETFTexture trimTexture = null;
    private ETFTexture texture = null;

    public void start() {
        ETFRenderContext.preventRenderLayerTextureModify();
    }

    public void end() {
        ETFRenderContext.allowRenderLayerTextureModify();
    }

    public ResourceLocation getBaseTexture(ResourceLocation vanilla) {
        if (ETF.config().getConfig().enableArmorAndTrims) {
            this.texture = ETFManager.getInstance().getETFTextureNoVariation(vanilla);
            if (this.texture != null) {
                return this.texture.getTextureIdentifier(null);
            }
        }
        return vanilla;
    }

    public void renderBaseEmissive(PoseStack matrices, MultiBufferSource vertexConsumers, Model model, float red, float green, float blue) {
        ResourceLocation emissive;
        if (this.texture != null && ETF.config().getConfig().canDoEmissiveTextures() && (emissive = this.texture.getEmissiveIdentifierOfCurrentState()) != null) {
            VertexConsumer textureVert = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull((ResourceLocation)emissive));
            ETFRenderContext.startSpecialRenderOverlayPhase();
            if (ETF.IRIS_DETECTED) {
                matrices.scale(1.001f, 1.001f, 1.001f);
            }
            model.renderToBuffer(matrices, textureVert, 0xF000F2, OverlayTexture.NO_OVERLAY);
            ETFRenderContext.startSpecialRenderOverlayPhase();
        }
    }

    public void renderTrimEmissive(PoseStack matrices, MultiBufferSource vertexConsumers, Model model) {
        ResourceLocation emissive;
        if (this.trimTexture != null && ETF.config().getConfig().canDoEmissiveTextures() && (emissive = this.trimTexture.getEmissiveIdentifierOfCurrentState()) != null) {
            VertexConsumer textureVert = vertexConsumers.getBuffer(RenderType.armorCutoutNoCull((ResourceLocation)emissive));
            ETFRenderContext.startSpecialRenderOverlayPhase();
            if (ETF.IRIS_DETECTED) {
                matrices.scale(1.001f, 1.001f, 1.001f);
            }
            model.renderToBuffer(matrices, textureVert, 0xF000F2, OverlayTexture.NO_OVERLAY);
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    public void setTrim(Holder<ArmorMaterial> armorMaterial, ArmorTrim trim, boolean leggings) {
        if (ETF.config().getConfig().enableArmorAndTrims) {
            ResourceLocation trimBaseId = leggings ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial);
            ResourceLocation trimMaterialIdentifier = ETFUtils2.res(trimBaseId.getNamespace(), "textures/" + trimBaseId.getPath() + ".png");
            this.trimTexture = ETFManager.getInstance().getETFTextureNoVariation(trimMaterialIdentifier);
        }
    }
}

