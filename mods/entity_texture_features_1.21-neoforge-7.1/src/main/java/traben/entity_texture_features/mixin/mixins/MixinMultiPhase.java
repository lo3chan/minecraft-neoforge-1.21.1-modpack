/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType$CompositeRenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 */
package traben.entity_texture_features.mixin.mixins;

import java.util.Optional;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

@Pseudo
@Mixin(value={RenderType.CompositeRenderType.class})
public abstract class MixinMultiPhase
implements ETFRenderLayerWithTexture {
    @Shadow
    @Final
    public RenderType.CompositeState state;

    @Override
    public Optional<ResourceLocation> etf$getId() {
        return this.state.textureState.cutoutTexture();
    }
}

