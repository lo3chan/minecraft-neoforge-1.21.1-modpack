/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.model.PlayerModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.player.PlayerRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerSkinHolder;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

@Mixin(value={PlayerRenderer.class})
public abstract class MixinPlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
implements ETFPlayerSkinHolder {
    @Unique
    private ETFPlayerTexture etf$ETFPlayerTexture = null;

    public MixinPlayerEntityRenderer(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void etf$addFeatures(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        this.addLayer(new ETFPlayerFeatureRenderer(this));
    }

    @Inject(method={"renderHand"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", shift=At.Shift.AFTER)}, cancellable=true)
    private void etf$redirectNicely(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        ResourceLocation etfTexture;
        ETFPlayerTexture thisETFPlayerTexture;
        if (ETF.config().getConfig().skinFeaturesEnabled && (thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture((Player)player, player.getSkin().texture())) != null && thisETFPlayerTexture.hasFeatures && (etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla((Player)player)) != null) {
            ETFRenderContext.preventRenderLayerTextureModify();
            arm.xRot = 0.0f;
            sleeve.xRot = 0.0f;
            VertexConsumer vc1 = vertexConsumers.getBuffer(RenderType.entityTranslucent((ResourceLocation)etfTexture));
            this.etf$renderOnce(matrices, vc1, light, player, arm, sleeve);
            ETFRenderContext.startSpecialRenderOverlayPhase();
            ResourceLocation emissive = thisETFPlayerTexture.getBaseTextureEmissiveIdentifierOrNullForNone();
            if (emissive != null) {
                VertexConsumer vc2 = vertexConsumers.getBuffer(RenderType.entityTranslucent((ResourceLocation)emissive));
                this.etf$renderOnce(matrices, vc2, 0xF000F2, player, arm, sleeve);
            }
            if (thisETFPlayerTexture.baseEnchantIdentifier != null) {
                VertexConsumer vc3 = ItemRenderer.getArmorFoilBuffer((MultiBufferSource)vertexConsumers, (RenderType)RenderType.armorCutoutNoCull((ResourceLocation)thisETFPlayerTexture.baseEnchantIdentifier), (boolean)true);
                this.etf$renderOnce(matrices, vc3, light, player, arm, sleeve);
            }
            ETFRenderContext.endSpecialRenderOverlayPhase();
            ETFRenderContext.allowRenderLayerTextureModify();
            ci.cancel();
        }
    }

    @Unique
    private void etf$renderOnce(PoseStack matrixStack, VertexConsumer consumer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
        arm.render(matrixStack, consumer, light, OverlayTexture.NO_OVERLAY);
        sleeve.render(matrixStack, consumer, light, OverlayTexture.NO_OVERLAY);
    }

    @Inject(method={"getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;"}, at={@At(value="RETURN")}, cancellable=true)
    private void etf$getTexture(AbstractClientPlayer abstractClientPlayerEntity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (ETF.config().getConfig().skinFeaturesEnabled) {
            ResourceLocation texture;
            this.etf$ETFPlayerTexture = ETFManager.getInstance().getPlayerTexture((Player)abstractClientPlayerEntity, (ResourceLocation)cir.getReturnValue());
            if (this.etf$ETFPlayerTexture != null && this.etf$ETFPlayerTexture.hasFeatures && (texture = this.etf$ETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla((Player)abstractClientPlayerEntity)) != null) {
                cir.setReturnValue((Object)texture);
            }
        } else {
            this.etf$ETFPlayerTexture = null;
        }
    }

    @Override
    @Nullable
    public ETFPlayerTexture etf$getETFPlayerTexture() {
        return this.etf$ETFPlayerTexture;
    }
}

