/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(value={EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
    @Inject(method={"render"}, at={@At(value="HEAD")})
    private <E extends Entity> void etf$grabContext(CallbackInfo ci, @Local(argsOnly=true) E entity) {
        ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity)entity));
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    private void etf$clearContext(CallbackInfo ci) {
        ETFRenderContext.reset();
    }
}

