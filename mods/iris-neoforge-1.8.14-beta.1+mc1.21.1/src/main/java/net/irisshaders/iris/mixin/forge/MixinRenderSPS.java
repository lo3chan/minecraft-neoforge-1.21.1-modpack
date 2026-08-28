/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 */
package net.irisshaders.iris.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Function;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pathways.LightningHandler;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets={"mekanism.client.render.lib.effect.BillboardingEffectRenderer"}, remap=false)
public class MixinRenderSPS {
    @WrapOperation(method={"render(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;Ljava/util/function/Supplier;)V"}, at={@At(value="FIELD", target="Lmekanism/client/render/MekanismRenderType;SPS:Ljava/util/function/Function;")})
    private static Function<ResourceLocation, RenderType> doNotSwitchShaders(Operation<Function<ResourceLocation, RenderType>> original) {
        if (Iris.isPackInUseQuick() && ImmediateState.isRenderingLevel) {
            return LightningHandler.SPS;
        }
        return (Function)original.call(new Object[0]);
    }
}

