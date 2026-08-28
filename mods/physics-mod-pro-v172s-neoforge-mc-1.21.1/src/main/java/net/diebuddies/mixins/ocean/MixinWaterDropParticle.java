/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.WaterDropParticle
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.ocean;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.mixins.ocean.MixinParticle;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WaterDropParticle.class})
public class MixinWaterDropParticle
extends MixinParticle {
    @Unique
    private double physicsOffset;
    @Unique
    private BlockPos.MutableBlockPos mutable;

    @Inject(at={@At(value="TAIL")}, method={"<init>"})
    protected void constructor(ClientLevel clientLevel, double x, double y, double z, CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled() && this.level != null) {
            OceanWorld oceanWorld = PhysicsMod.getInstance((Level)this.level).getPhysicsWorld().getOceanWorld();
            this.physicsOffset = oceanWorld.calculateYOffset(x, y, z);
            this.yo = y + this.physicsOffset;
            Particle particle = (Particle)this;
            particle.setPos(x, y + this.physicsOffset, z);
        }
    }

    @Override
    protected void getLightColor(float renderPercent, CallbackInfoReturnable<Integer> info) {
        if (ConfigClient.areOceanPhysicsEnabled() && this.level != null) {
            if (this.mutable == null) {
                this.mutable = new BlockPos.MutableBlockPos();
            }
            this.mutable.set(this.x, this.y - this.physicsOffset, this.z);
            if (this.level.hasChunkAt((BlockPos)this.mutable)) {
                info.setReturnValue((Object)LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)this.mutable));
            } else {
                info.setReturnValue((Object)0);
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"tick"}, cancellable=true)
    public void tick(CallbackInfo info) {
        if (ConfigClient.areOceanPhysicsEnabled()) {
            double d;
            Particle particle = (Particle)this;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (this.lifetime-- <= 0) {
                particle.remove();
                return;
            }
            this.yd -= (double)this.gravity;
            particle.move(this.xd, this.yd, this.zd);
            this.xd *= (double)0.98f;
            this.yd *= (double)0.98f;
            this.zd *= (double)0.98f;
            if (this.onGround) {
                if (Math.random() < 0.5) {
                    particle.remove();
                }
                this.xd *= (double)0.7f;
                this.zd *= (double)0.7f;
            }
            BlockPos blockPos = BlockPos.containing((double)this.x, (double)(this.y - this.physicsOffset), (double)this.z);
            double d2 = Math.max(this.level.getBlockState(blockPos).getCollisionShape((BlockGetter)this.level, blockPos).max(Direction.Axis.Y, this.x - (double)blockPos.getX(), this.z - (double)blockPos.getZ()), (double)this.level.getFluidState(blockPos).getHeight((BlockGetter)this.level, blockPos));
            if (d > 0.0 && this.y < (double)blockPos.getY() + d2) {
                particle.remove();
            }
            info.cancel();
        }
    }
}

