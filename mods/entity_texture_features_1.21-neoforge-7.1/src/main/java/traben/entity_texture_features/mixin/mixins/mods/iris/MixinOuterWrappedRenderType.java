/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.layer.OuterWrappedRenderType
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 */
package traben.entity_texture_features.mixin.mixins.mods.iris;

import java.util.Optional;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

@Pseudo
@Mixin(value={OuterWrappedRenderType.class})
public abstract class MixinOuterWrappedRenderType
implements ETFRenderLayerWithTexture {
    @Shadow
    public abstract RenderType unwrap();

    @Override
    public Optional<ResourceLocation> etf$getId() {
        RenderType renderType = this.unwrap();
        if (renderType instanceof ETFRenderLayerWithTexture) {
            ETFRenderLayerWithTexture etf = (ETFRenderLayerWithTexture)renderType;
            return etf.etf$getId();
        }
        return Optional.empty();
    }
}

