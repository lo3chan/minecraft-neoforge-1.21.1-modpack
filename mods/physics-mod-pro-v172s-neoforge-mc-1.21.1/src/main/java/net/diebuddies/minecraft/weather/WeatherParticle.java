/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 */
package net.diebuddies.minecraft.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.FastTextureSheetParticle;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public abstract class WeatherParticle
extends FastTextureSheetParticle {
    protected Quaternionf tmpRotation = new Quaternionf();
    protected BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
    protected BlockPos.MutableBlockPos lastPos = new BlockPos.MutableBlockPos(0, 1, 0);
    protected int cachedBrightness;
    protected double dampingX = 0.98;
    protected double dampingY = 0.98;
    protected double dampingZ = 0.98;
    protected int r;
    protected int g;
    protected int b;
    protected int a;
    protected int argb;
    protected AABB3D aabb;

    public WeatherParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz) {
        super(clientLevel, x, y, z);
        this.gravity = 0.06f;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.quadSize = 0.14f;
        this.lifetime = Math.randomInt(200) + 200;
        this.aabb = new AABB3D(x - 0.01, y - 0.01, z - 0.01, x + 0.01, y + 0.01, z + 0.01);
        this.move();
        this.calculateLight();
    }

    private void move() {
        Vector3d start = this.aabb.start;
        Vector3d end = this.aabb.end;
        start.x += this.xd;
        start.y += this.yd;
        start.z += this.zd;
        end.x += this.xd;
        end.y += this.yd;
        end.z += this.zd;
        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;
    }

    public void setColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.argb = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float renderPercent) {
        return this.cachedBrightness;
    }

    private void calculateLight() {
        this.blockPos.set(this.xo, this.yo, this.zo);
        if (WeatherEffects.invalidateLight || (this.blockPos.getX() != this.lastPos.getX() || this.blockPos.getY() != this.lastPos.getY() || this.blockPos.getZ() != this.lastPos.getZ()) && this.level.hasChunkAt((BlockPos)this.blockPos)) {
            this.lastPos.set((Vec3i)this.blockPos);
            this.cachedBrightness = LevelRenderer.getLightColor((BlockAndTintGetter)this.level, (BlockPos)this.blockPos);
        }
    }

    public void tick() {
        ++WeatherEffects.aliveParticles;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        if (this.removed) {
            return;
        }
        this.calculateLight();
        this.yd -= (double)this.gravity;
        this.move();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        float currentX = (float)(this.x - cameraPos.x());
        float currentY = (float)(this.y - cameraPos.y());
        float currentZ = (float)(this.z - cameraPos.z());
        if ((double)(currentX * currentX + currentY * currentY + currentZ * currentZ) > 196.0) {
            this.remove();
            return;
        }
        this.blockPos.set(this.x, this.y, this.z);
        BlockState state = this.level.getBlockState((BlockPos)this.blockPos);
        VoxelShape voxelShape = state.getCollisionShape((BlockGetter)this.level, (BlockPos)this.blockPos);
        if (!voxelShape.isEmpty() && VineHelper.getSetting(state) == null) {
            for (AABB aabb : voxelShape.toAabbs()) {
                if (!this.aabb.intersect(aabb.minX + (double)this.blockPos.getX(), aabb.minY + (double)this.blockPos.getY(), aabb.minZ + (double)this.blockPos.getZ(), aabb.maxX + (double)this.blockPos.getX(), aabb.maxY + (double)this.blockPos.getY(), aabb.maxZ + (double)this.blockPos.getZ())) continue;
                this.remove();
                return;
            }
        }
        this.xd *= this.dampingX;
        this.yd *= this.dampingY;
        this.zd *= this.dampingZ;
        FluidState fluidState = this.level.getFluidState((BlockPos)this.blockPos);
        if (fluidState.getType() == Fluids.WATER && this.y < (double)((float)this.blockPos.getY() + fluidState.getHeight((BlockGetter)this.level, (BlockPos)this.blockPos))) {
            if (ConfigClient.areOceanPhysicsEnabled() && Math.random() < ConfigClient.oceanRainPuddleAmount && RenderSystem.isOnRenderThread()) {
                PhysicsMod.getInstance((Level)this.level).getPhysicsWorld().getOceanWorld().spawnRainRipple(20, 0.65f + Math.random() * 0.5f, this.x, this.y, this.z);
            }
            this.remove();
        }
    }
}

