/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockBehaviour.BlockStateBase.class}, priority=990)
public abstract class MixinBlockStateBehavior {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Inject(method={"getShadeBrightness"}, at={@At(value="RETURN")}, cancellable=true)
    public void getShadeBrightness(BlockGetter pBlockBehaviour$BlockStateBase0, BlockPos pBlockPos1, CallbackInfoReturnable<Float> cir) {
        float originalValue = ((Float)cir.getReturnValue()).floatValue();
        float aoLightValue = WorldRenderingSettings.INSTANCE.getAmbientOcclusionLevel();
        cir.setReturnValue((Object)Float.valueOf(1.0f - aoLightValue * (1.0f - originalValue)));
    }
}

