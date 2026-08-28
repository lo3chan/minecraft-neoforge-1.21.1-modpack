/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.renderer.entity.IllusionerRenderer
 *  net.minecraft.world.entity.monster.Illusioner
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.minecraft.world.entity.monster.Illusioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value={IllusionerRenderer.class})
public abstract class MixinIllusionerRenderer {
    @Unique
    private ETFEntityRenderState etf$heldEntity = null;
    private static final String RENDER = "render(Lnet/minecraft/world/entity/monster/Illusioner;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

    @Inject(method={"render(Lnet/minecraft/world/entity/monster/Illusioner;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")})
    private void etf$start(CallbackInfo ci, @Local(argsOnly=true) Illusioner entity) {
        ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)entity));
    }

    @Inject(method={"render(Lnet/minecraft/world/entity/monster/Illusioner;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/IllagerRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")})
    private void etf$loop(CallbackInfo ci) {
        ETFRenderContext.setCurrentEntity(this.etf$heldEntity);
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }
}

