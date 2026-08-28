/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.ocean;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.diebuddies.config.ConfigClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DefaultFluidRenderer.class})
public class MixinFluidRenderer {
    @Unique
    private final BlockPos.MutableBlockPos tmpPos = new BlockPos.MutableBlockPos();

    @Inject(at={@At(value="HEAD")}, method={"isSideExposed"}, remap=false, cancellable=true)
    public void isSideExposed(BlockAndTintGetter world, int x, int y, int z, Direction dir, float height, CallbackInfoReturnable<Boolean> info) {
        BlockState state;
        FluidState fluidState;
        if (ConfigClient.areOceanPhysicsEnabled() && dir == Direction.UP && (fluidState = (state = world.getBlockState((BlockPos)this.tmpPos.set(x, y, z))).getFluidState()).is(FluidTags.WATER) && !state.blocksMotion()) {
            Vec3 flow = state.getFluidState().getFlow((BlockGetter)world, (BlockPos)this.tmpPos);
            if (flow.x == 0.0 && flow.z == 0.0) {
                info.setReturnValue((Object)false);
            }
        }
    }
}

