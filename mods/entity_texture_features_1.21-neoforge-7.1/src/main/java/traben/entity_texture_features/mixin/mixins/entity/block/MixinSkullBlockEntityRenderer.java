/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.SkullModelBase
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.SkullBlockRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.SkullBlock$Type
 *  net.minecraft.world.level.block.SkullBlock$Types
 *  net.minecraft.world.level.block.entity.SkullBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package traben.entity_texture_features.mixin.mixins.entity.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerEntity;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

@Mixin(value={SkullBlockRenderer.class})
public abstract class MixinSkullBlockEntityRenderer
implements BlockEntityRenderer<SkullBlockEntity> {
    private static final String RENDER_METHOD = "render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V";
    @Unique
    private ETFPlayerTexture entity_texture_features$thisETFPlayerTexture = null;

    @Inject(method={"render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at={@At(value="HEAD")})
    private void etf$markNotToChange(CallbackInfo ci) {
        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method={"render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at={@At(value="RETURN")})
    private void etf$markAllowedToChange(CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }

    @Inject(method={"render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;getRenderType(Lnet/minecraft/world/level/block/SkullBlock$Type;Lnet/minecraft/world/item/component/ResolvableProfile;)Lnet/minecraft/client/renderer/RenderType;")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(SkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.Type skullType, SkullModelBase skullBlockEntityModel) {
        this.entity_texture_features$thisETFPlayerTexture = null;
        if (skullType == SkullBlock.Types.PLAYER && ETF.config().getConfig().skinFeaturesEnabled && ETF.config().getConfig().enableCustomTextures && ETF.config().getConfig().enableCustomBlockEntities && skullBlockEntity.getOwnerProfile() != null) {
            ResourceLocation identifier = Minecraft.getInstance().getSkinManager().getInsecureSkin(skullBlockEntity.getOwnerProfile().gameProfile()).texture();
            this.entity_texture_features$thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture((ETFPlayerEntity)skullBlockEntity, identifier);
            if (this.entity_texture_features$thisETFPlayerTexture != null) {
                ETFRenderContext.preventRenderLayerTextureModify();
            }
        }
    }

    @Inject(method={"render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at={@At(value="TAIL")}, locals=LocalCapture.CAPTURE_FAILHARD)
    private void etf$renderFeatures(SkullBlockEntity skullBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.Type skullType, SkullModelBase skullBlockEntityModel, RenderType renderLayer) {
        if (this.entity_texture_features$thisETFPlayerTexture != null && ETF.config().getConfig().enableEmissiveBlockEntities) {
            matrixStack.pushPose();
            if (direction == null) {
                matrixStack.translate(0.5f, 0.0f, 0.5f);
            } else {
                matrixStack.translate(0.5f - (float)direction.getStepX() * 0.25f, 0.25f, 0.5f - (float)direction.getStepZ() * 0.25f);
            }
            matrixStack.scale(-1.0f, -1.0f, 1.0f);
            skullBlockEntityModel.setupAnim(g, h, 0.0f);
            ETFPlayerFeatureRenderer.renderSkullFeatures(matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, this.entity_texture_features$thisETFPlayerTexture, h);
            matrixStack.popPose();
        }
    }

    @ModifyArg(method={"render(Lnet/minecraft/world/level/block/entity/SkullBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V"), index=7)
    private RenderType etf$modifyRenderLayer(RenderType renderLayer) {
        ResourceLocation skin;
        if (this.entity_texture_features$thisETFPlayerTexture != null && (skin = this.entity_texture_features$thisETFPlayerTexture.getBaseHeadTextureIdentifierOrNullForVanilla()) != null) {
            return RenderType.entityTranslucent((ResourceLocation)skin);
        }
        return renderLayer;
    }
}

