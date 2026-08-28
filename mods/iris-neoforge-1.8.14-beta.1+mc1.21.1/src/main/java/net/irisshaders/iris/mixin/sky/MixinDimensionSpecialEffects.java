/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.world.level.material.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.sky;

import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DimensionSpecialEffects.class})
public class MixinDimensionSpecialEffects {
    @Inject(method={"getSunriseColor"}, at={@At(value="HEAD")}, cancellable=true)
    private void iris$getSunriseColor(float timeOfDay, float partialTicks, CallbackInfoReturnable<float[]> cir) {
        FogType fogType;
        boolean blockSky = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).invokeDoesMobEffectBlockSky(Minecraft.getInstance().gameRenderer.getMainCamera());
        if (blockSky) {
            cir.setReturnValue(null);
        }
        if ((fogType = Minecraft.getInstance().gameRenderer.getMainCamera().getFluidInCamera()) != FogType.NONE) {
            cir.setReturnValue(null);
        }
    }
}

