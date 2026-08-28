/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.SkullModelBase
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.PlayerModelPart
 *  net.minecraft.world.item.ItemStack
 */
package traben.entity_texture_features.features.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerSkinHolder;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

public class ETFPlayerFeatureRenderer<T extends Player, M extends PlayerModel<T>>
extends RenderLayer<T, M> {
    protected static final ModelPart villagerNose = ETFPlayerFeatureRenderer.getModelData(new CubeDeformation(0.0f)).getRoot().getChild("nose").bake(64, 64);
    protected static final ModelPart textureNose = ETFPlayerFeatureRenderer.getModelData(new CubeDeformation(0.0f)).getRoot().getChild("textureNose").bake(8, 8);
    protected static final ModelPart jacket = ETFPlayerFeatureRenderer.getModelData(new CubeDeformation(0.0f)).getRoot().getChild("jacket").bake(64, 64);
    protected static final ModelPart fatJacket = ETFPlayerFeatureRenderer.getModelData(new CubeDeformation(0.0f)).getRoot().getChild("fatJacket").bake(64, 64);
    private static final ResourceLocation VILLAGER_TEXTURE = ETFUtils2.res("textures/entity/villager/villager.png");
    protected final ETFPlayerSkinHolder skinHolder;

    public ETFPlayerFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
        ETFPlayerSkinHolder holder;
        this.skinHolder = context instanceof ETFPlayerSkinHolder ? (holder = (ETFPlayerSkinHolder)context) : null;
    }

    public static MeshDefinition getModelData(CubeDeformation dilation) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0f, 12.5f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.extend(0.25f)), PartPose.ZERO);
        modelPartData.addOrReplaceChild("fatJacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0f, 12.5f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.extend(0.25f).extend(0.5f)), PartPose.ZERO);
        modelPartData.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f), PartPose.offset((float)0.0f, (float)-2.0f, (float)0.0f));
        modelPartData.addOrReplaceChild("textureNose", CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, -8.0f, -8.0f, 0.0f, 8.0f, 4.0f), PartPose.offset((float)0.0f, (float)-2.0f, (float)0.0f));
        return modelData;
    }

    public static void renderSkullFeatures(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, SkullModelBase skullModel, ETFPlayerTexture playerTexture, float yaw) {
        ETFRenderContext.preventRenderLayerTextureModify();
        ETFRenderContext.startSpecialRenderOverlayPhase();
        if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
            ETFPlayerFeatureRenderer.villagerNose.yRot = yaw * ((float)Math.PI / 180);
            ETFPlayerFeatureRenderer.villagerNose.xRot = 0.0f;
            ETFPlayerFeatureRenderer.villagerNose.y = 0.0f;
            ETFPlayerFeatureRenderer.textureNose.yRot = yaw * ((float)Math.PI / 180);
            ETFPlayerFeatureRenderer.textureNose.xRot = 0.0f;
            ETFPlayerFeatureRenderer.textureNose.y = 0.0f;
            ETFPlayerFeatureRenderer.renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
        }
        ETFPlayerFeatureRenderer.renderEnchanted(matrixStack, vertexConsumerProvider, light, playerTexture, (Model)skullModel);
        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.allowRenderLayerTextureModify();
    }

    private static void renderEnchanted(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, ETFPlayerTexture playerTexture, Model model) {
        if (playerTexture.hasEnchant && playerTexture.baseEnchantIdentifier != null && playerTexture.etfTextureOfFinalBaseSkin != null) {
            VertexConsumer enchantVert = ItemRenderer.getArmorFoilBuffer((MultiBufferSource)vertexConsumerProvider, (RenderType)RenderType.armorCutoutNoCull((ResourceLocation)(switch (playerTexture.etfTextureOfFinalBaseSkin.currentTextureState) {
                case ETFTexture.TextureReturnState.BLINK, ETFTexture.TextureReturnState.BLINK_PATCHED, ETFTexture.TextureReturnState.APPLY_BLINK -> playerTexture.baseEnchantBlinkIdentifier;
                case ETFTexture.TextureReturnState.BLINK2, ETFTexture.TextureReturnState.BLINK2_PATCHED, ETFTexture.TextureReturnState.APPLY_BLINK2 -> playerTexture.baseEnchantBlink2Identifier;
                default -> playerTexture.baseEnchantIdentifier;
            })), (boolean)true);
            model.renderToBuffer(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY);
        }
    }

    private static void renderNose(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, ETFPlayerTexture playerTexture) {
        if (playerTexture.hasVillagerNose) {
            if (playerTexture.noseType == ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED || playerTexture.noseType == ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED_REMOVE) {
                RenderType type = RenderType.entityTranslucent((ResourceLocation)playerTexture.etfTextureOfFinalBaseSkin.getTextureIdentifier(null));
                VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(type);
                villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.NO_OVERLAY);
                playerTexture.etfTextureOfFinalBaseSkin.renderEmissive(matrixStack, vertexConsumerProvider, villagerNose);
            } else {
                VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderType.entitySolid((ResourceLocation)VILLAGER_TEXTURE));
                villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.NO_OVERLAY);
            }
        } else if (playerTexture.texturedNoseIdentifier != null) {
            VertexConsumer noseVertex = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)playerTexture.texturedNoseIdentifier));
            textureNose.render(matrixStack, noseVertex, light, OverlayTexture.NO_OVERLAY);
            if (playerTexture.texturedNoseIdentifierEmissive != null) {
                VertexConsumer noseVertex_e = ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT ? vertexConsumerProvider.getBuffer(RenderType.beaconBeam((ResourceLocation)playerTexture.texturedNoseIdentifierEmissive, (boolean)true)) : vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)playerTexture.texturedNoseIdentifierEmissive));
                textureNose.render(matrixStack, noseVertex_e, 0xF000F2, OverlayTexture.NO_OVERLAY);
            }
            if (playerTexture.texturedNoseIdentifierEnchanted != null) {
                VertexConsumer noseVertex_ench = ItemRenderer.getArmorFoilBuffer((MultiBufferSource)vertexConsumerProvider, (RenderType)RenderType.armorCutoutNoCull((ResourceLocation)playerTexture.texturedNoseIdentifierEnchanted), (boolean)true);
                textureNose.render(matrixStack, noseVertex_ench, light, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    public void render(PoseStack matrices, MultiBufferSource submit, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (ETF.config().getConfig().skinFeaturesEnabled && this.skinHolder != null) {
            ETFRenderContext.preventRenderLayerTextureModify();
            ETFPlayerTexture playerTexture = this.skinHolder.etf$getETFPlayerTexture();
            if (playerTexture != null && playerTexture.hasFeatures) {
                this.renderFeatures(matrices, submit, light, (PlayerModel)this.getParentModel(), playerTexture);
            }
            ETFRenderContext.allowRenderLayerTextureModify();
        }
    }

    public void renderFeatures(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, M model, ETFPlayerTexture playerTexture) {
        if (playerTexture.canUseFeaturesForThisPlayer()) {
            ETFRenderContext.startSpecialRenderOverlayPhase();
            matrixStack.pushPose();
            if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
                matrixStack.pushPose();
                villagerNose.copyFrom(((PlayerModel)model).head);
                textureNose.copyFrom(((PlayerModel)model).head);
                ETFPlayerFeatureRenderer.renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
                matrixStack.popPose();
            }
            matrixStack.pushPose();
            this.renderCoat(matrixStack, vertexConsumerProvider, light, playerTexture, model);
            matrixStack.popPose();
            matrixStack.popPose();
            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    private void renderCoat(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, ETFPlayerTexture playerTexture, M model) {
        ItemStack armour = playerTexture.player.etf$getInventory().getArmor(1);
        if (playerTexture.coatIdentifier != null && playerTexture.player.etf$isPartVisible(PlayerModelPart.JACKET) && !armour.is(ItemTags.LEG_ARMOR)) {
            if (playerTexture.hasFatCoat) {
                fatJacket.copyFrom(((PlayerModel)model).jacket);
            } else {
                jacket.copyFrom(((PlayerModel)model).jacket);
            }
            VertexConsumer coatVert = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)playerTexture.coatIdentifier));
            matrixStack.pushPose();
            if (playerTexture.hasFatCoat) {
                fatJacket.render(matrixStack, coatVert, light, OverlayTexture.NO_OVERLAY);
            } else {
                jacket.render(matrixStack, coatVert, light, OverlayTexture.NO_OVERLAY);
            }
            if (playerTexture.coatEnchantedIdentifier != null) {
                VertexConsumer enchantVert = ItemRenderer.getArmorFoilBuffer((MultiBufferSource)vertexConsumerProvider, (RenderType)RenderType.armorCutoutNoCull((ResourceLocation)playerTexture.coatEnchantedIdentifier), (boolean)true);
                if (playerTexture.hasFatCoat) {
                    fatJacket.render(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY);
                } else {
                    jacket.render(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY);
                }
            }
            if (playerTexture.coatEmissiveIdentifier != null) {
                VertexConsumer emissiveVert = ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT ? vertexConsumerProvider.getBuffer(RenderType.beaconBeam((ResourceLocation)playerTexture.coatEmissiveIdentifier, (boolean)true)) : vertexConsumerProvider.getBuffer(RenderType.entityTranslucent((ResourceLocation)playerTexture.coatEmissiveIdentifier));
                if (playerTexture.hasFatCoat) {
                    fatJacket.render(matrixStack, emissiveVert, 0xF000F2, OverlayTexture.NO_OVERLAY);
                } else {
                    jacket.render(matrixStack, emissiveVert, 0xF000F2, OverlayTexture.NO_OVERLAY);
                }
            }
            matrixStack.popPose();
        }
    }
}

