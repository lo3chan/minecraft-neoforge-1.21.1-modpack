/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Coerce
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.snow;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.SnowSearcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"net.optifine.util.BlockUtils"}, remap=false)
public class MixinBlockUtils {
    @Inject(at={@At(value="RETURN")}, method={"shouldSideBeRendered"}, cancellable=true)
    private static void shouldRenderFace(BlockState blockStateIn, BlockGetter blockReaderIn, BlockPos blockPosIn, Direction facingIn, @Coerce Object renderEnv, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValueZ() && ConfigClient.areSnowPhysicsEnabled() && SnowSearcher.getSnowProperty(blockReaderIn.getBlockState(blockPosIn.relative(facingIn))) != null) {
            info.setReturnValue((Object)true);
        }
    }
}

