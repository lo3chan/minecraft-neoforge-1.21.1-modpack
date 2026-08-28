/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EndCrystalRenderer
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;

@Mixin(value={EndCrystalRenderer.class})
public abstract class MixinEndCrystalRenderer {
    @Shadow
    @Final
    private static ResourceLocation END_CRYSTAL_LOCATION;

    @ModifyArg(method={"render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private RenderType etf$modifyTexture(RenderType renderType) {
        if (ETF.config().getConfig().canDoCustomTextures()) {
            return RenderType.entityCutoutNoCull((ResourceLocation)END_CRYSTAL_LOCATION);
        }
        return renderType;
    }
}

