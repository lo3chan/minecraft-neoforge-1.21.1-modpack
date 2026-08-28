/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.snow;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.math.Ray;
import net.diebuddies.physics.snow.math.RayHit;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public class MixinCamera {
    @Shadow
    private BlockGetter level;
    @Shadow
    private Vec3 position = Vec3.ZERO;
    @Shadow
    @Final
    private Vector3f forwards;
    @Unique
    private Vector3d rayStartPos = new Vector3d();
    @Unique
    private Vector3d rayDirection = new Vector3d();

    @Inject(at={@At(value="RETURN")}, method={"getMaxZoom"}, cancellable=true)
    private void getMaxZoom(float maxZoom, CallbackInfoReturnable<Float> info) {
        BlockGetter blockGetter = this.level;
        if (blockGetter instanceof ClientLevel) {
            ClientLevel clientLevel = (ClientLevel)blockGetter;
            if (ConfigClient.areSnowPhysicsEnabled()) {
                try {
                    SnowWorld snowWorld = PhysicsMod.getInstance((Level)clientLevel).getPhysicsWorld().getSnowWorld();
                    double offset = -0.5;
                    this.rayStartPos.set(this.position.x * (double)IChunk.CHUNK_MULTIPLE + offset, this.position.y * (double)IChunk.CHUNK_MULTIPLE + offset, this.position.z * (double)IChunk.CHUNK_MULTIPLE + offset);
                    this.rayDirection.set((double)(-this.forwards.x()), (double)(-this.forwards.y()), (double)(-this.forwards.z()));
                    Ray ray = new Ray(this.rayStartPos, this.rayDirection);
                    RayHit rayHit = snowWorld.contouring.castFastLevelRay(ray, 6.0 * (double)IChunk.CHUNK_MULTIPLE, 0.25 * (double)IChunk.CHUNK_MULTIPLE, 0.01 * (double)IChunk.CHUNK_MULTIPLE, 24 * IChunk.CHUNK_MULTIPLE);
                    if (rayHit != null) {
                        double distanceToHit = rayHit.point.distance((Vector3dc)this.rayStartPos) / (double)IChunk.CHUNK_MULTIPLE;
                        double zoomAdjustment = (double)ConfigClient.snowThickness * 0.5;
                        if (ConfigClient.snowQuality == 0) {
                            zoomAdjustment = (double)ConfigClient.snowThickness * 0.75;
                        }
                        if (ConfigClient.snowType == 1 || !ConfigClient.snowSmoothShading) {
                            zoomAdjustment = 0.0;
                        }
                        info.setReturnValue((Object)Float.valueOf((float)Math.min((double)maxZoom, distanceToHit * (0.9 - zoomAdjustment))));
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }
}

