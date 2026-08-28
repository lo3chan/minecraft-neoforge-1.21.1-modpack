/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketUtils
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundExplodePacket
 *  net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
 *  net.minecraft.network.protocol.game.ClientboundLoginPacket
 *  net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.monster.Creeper
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public class MixinClientPacketListener {
    @Shadow
    @Final
    private RandomSource random;
    @Shadow
    @Final
    private ClientLevel level;

    @Inject(at={@At(value="HEAD")}, method={"handleExplosion"})
    public void handleExplosion(ClientboundExplodePacket explosionPacket, CallbackInfo info) {
        if (RenderSystem.isOnRenderThread()) {
            Explosion explosion = new Explosion();
            explosion.strength = explosionPacket.getPower();
            explosion.position = new Vector3d(explosionPacket.getX(), explosionPacket.getY(), explosionPacket.getZ());
            PhysicsMod mod = PhysicsMod.getInstance((Level)this.level);
            mod.explosions.add(explosion);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"handleRemoveEntities"})
    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket packetIn, CallbackInfo info) {
        if (RenderSystem.isOnRenderThread()) {
            for (int i = 0; i < packetIn.getEntityIds().size(); ++i) {
                int j = packetIn.getEntityIds().getInt(i);
                Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(j);
                if (entity == null || !ConfigClient.pvpServerCompatibility && !(entity instanceof EnderDragon) && !(entity instanceof Creeper) || !(entity.position().distanceTo(Minecraft.getInstance().player.position()) < 40.0) || !(entity instanceof LivingEntity)) continue;
                LivingEntity livingEntity = (LivingEntity)entity;
                PhysicsMod.blockifyEntity(entity.getCommandSenderWorld(), livingEntity);
            }
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"handleParticleEvent"}, cancellable=true)
    public void handleParticleEvent(ClientboundLevelParticlesPacket clientboundLevelParticlesPacket, CallbackInfo info) {
        PacketUtils.ensureRunningOnSameThread((Packet)clientboundLevelParticlesPacket, (PacketListener)((ClientPacketListener)this), (BlockableEventLoop)Minecraft.getInstance());
        try {
            ParticleOptions particleOptions;
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            if (ConfigClient.serverBlockPhysicsParticles && (particleOptions = clientboundLevelParticlesPacket.getParticle()) instanceof BlockParticleOption) {
                BlockParticleOption blockParticles = (BlockParticleOption)particleOptions;
                if (camera.isInitialized() && camera.getPosition().distanceToSqr(clientboundLevelParticlesPacket.getX(), clientboundLevelParticlesPacket.getY(), clientboundLevelParticlesPacket.getZ()) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
                    BlockState state = blockParticles.getState();
                    if (clientboundLevelParticlesPacket.getCount() == 0) {
                        double vx = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getXDist();
                        double vy = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getYDist();
                        double vz = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getZDist();
                        ParticleSpawner.spawnServerBlockPhysicsParticle(state, (Level)this.level, clientboundLevelParticlesPacket.getX() + (double)(Math.random() * 0.1f) - (double)0.05f, clientboundLevelParticlesPacket.getY() + (double)(Math.random() * 0.1f) - (double)0.05f, clientboundLevelParticlesPacket.getZ() + (double)(Math.random() * 0.1f) - (double)0.05f, vx, vy, vz);
                    } else {
                        for (int i = 0; i < java.lang.Math.max(clientboundLevelParticlesPacket.getCount() / 3, 1); ++i) {
                            double x = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getXDist() + (double)(Math.random() * 0.1f) - (double)0.05f;
                            double y = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getYDist() + (double)(Math.random() * 0.1f) - (double)0.05f;
                            double z = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getZDist() + (double)(Math.random() * 0.1f) - (double)0.05f;
                            double vx = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                            double vy = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                            double vz = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
                            ParticleSpawner.spawnServerBlockPhysicsParticle(state, (Level)this.level, clientboundLevelParticlesPacket.getX() + x, clientboundLevelParticlesPacket.getY() + y, clientboundLevelParticlesPacket.getZ() + z, vx, vy, vz);
                        }
                    }
                    info.cancel();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"handleBlockEntityData"})
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo info) {
        PacketUtils.ensureRunningOnSameThread((Packet)packet, (PacketListener)((ClientPacketListener)this), (BlockableEventLoop)Minecraft.getInstance());
        BlockPos blockPos = packet.getPos();
        PhysicsMod.getInstance((Level)this.level).blockUpdates.add(blockPos);
    }

    @Inject(at={@At(value="HEAD")}, method={"handleLogin"})
    public void handleLogin(ClientboundLoginPacket packet, CallbackInfo info) {
        boolean changed = false;
        for (ResourceKey entry : packet.levels()) {
            changed |= ConfigClient.addGravityBuoyancyEntry(entry.location());
        }
        if (changed) {
            ConfigClient.save();
        }
    }
}

