/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.BlockMarker
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.fantastic;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BlockMarker;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockMarker.class})
public class MixinStationaryItemParticle {
    @Unique
    private boolean isOpaque;

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void iris$resolveTranslucency(ClientLevel clientLevel, double d, double e, double f, BlockState blockState, CallbackInfo ci) {
        RenderType type = ItemBlockRenderTypes.getChunkRenderType((BlockState)blockState);
        if (type == RenderType.solid() || type == RenderType.cutout() || type == RenderType.cutoutMipped()) {
            this.isOpaque = true;
        }
    }

    @Inject(method={"getRenderType"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$overrideParticleRenderType(CallbackInfoReturnable<ParticleRenderType> cir) {
        if (this.isOpaque) {
            cir.setReturnValue((Object)ParticleRenderType.TERRAIN_SHEET);
        }
    }
}

