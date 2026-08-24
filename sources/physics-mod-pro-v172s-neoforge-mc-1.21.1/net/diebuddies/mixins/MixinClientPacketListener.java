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
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
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

@Mixin({ClientPacketListener.class})
public class MixinClientPacketListener {
   @Shadow
   @Final
   private RandomSource random;
   @Shadow
   @Final
   private ClientLevel level;

   @Inject(
      at = {@At("HEAD")},
      method = {"handleExplosion"}
   )
   public void handleExplosion(ClientboundExplodePacket explosionPacket, CallbackInfo info) {
      if (RenderSystem.isOnRenderThread()) {
         Explosion explosion = new Explosion();
         explosion.strength = explosionPacket.getPower();
         explosion.position = new Vector3d(explosionPacket.getX(), explosionPacket.getY(), explosionPacket.getZ());
         PhysicsMod mod = PhysicsMod.getInstance(this.level);
         mod.explosions.add(explosion);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleRemoveEntities"}
   )
   public void handleRemoveEntities(ClientboundRemoveEntitiesPacket packetIn, CallbackInfo info) {
      if (RenderSystem.isOnRenderThread()) {
         for (int i = 0; i < packetIn.getEntityIds().size(); i++) {
            int j = packetIn.getEntityIds().getInt(i);
            Entity entity = Minecraft.getInstance().player.clientLevel.getEntity(j);
            if (entity != null
               && (ConfigClient.pvpServerCompatibility || entity instanceof EnderDragon || entity instanceof Creeper)
               && entity.position().distanceTo(Minecraft.getInstance().player.position()) < 40.0
               && entity instanceof LivingEntity livingEntity) {
               PhysicsMod.blockifyEntity(entity.getCommandSenderWorld(), livingEntity);
            }
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleParticleEvent"},
      cancellable = true
   )
   public void handleParticleEvent(ClientboundLevelParticlesPacket clientboundLevelParticlesPacket, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(clientboundLevelParticlesPacket, (ClientPacketListener)this, Minecraft.getInstance());

      try {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         if (ConfigClient.serverBlockPhysicsParticles
            && clientboundLevelParticlesPacket.getParticle() instanceof BlockParticleOption blockParticles
            && camera.isInitialized()
            && camera.getPosition()
                  .distanceToSqr(clientboundLevelParticlesPacket.getX(), clientboundLevelParticlesPacket.getY(), clientboundLevelParticlesPacket.getZ())
               < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            BlockState state = blockParticles.getState();
            if (clientboundLevelParticlesPacket.getCount() == 0) {
               double vx = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getXDist();
               double vy = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getYDist();
               double vz = clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getZDist();
               ParticleSpawner.spawnServerBlockPhysicsParticle(
                  state,
                  this.level,
                  clientboundLevelParticlesPacket.getX() + Math.random() * 0.1F - 0.05000000074505806,
                  clientboundLevelParticlesPacket.getY() + Math.random() * 0.1F - 0.05000000074505806,
                  clientboundLevelParticlesPacket.getZ() + Math.random() * 0.1F - 0.05000000074505806,
                  vx,
                  vy,
                  vz
               );
            } else {
               for (int i = 0; i < java.lang.Math.max(clientboundLevelParticlesPacket.getCount() / 3, 1); i++) {
                  double x = this.random.nextGaussian() * clientboundLevelParticlesPacket.getXDist() + Math.random() * 0.1F - 0.05000000074505806;
                  double y = this.random.nextGaussian() * clientboundLevelParticlesPacket.getYDist() + Math.random() * 0.1F - 0.05000000074505806;
                  double z = this.random.nextGaussian() * clientboundLevelParticlesPacket.getZDist() + Math.random() * 0.1F - 0.05000000074505806;
                  double vx = this.random.nextGaussian() * clientboundLevelParticlesPacket.getMaxSpeed();
                  double vy = this.random.nextGaussian() * clientboundLevelParticlesPacket.getMaxSpeed();
                  double vz = this.random.nextGaussian() * clientboundLevelParticlesPacket.getMaxSpeed();
                  ParticleSpawner.spawnServerBlockPhysicsParticle(
                     state,
                     this.level,
                     clientboundLevelParticlesPacket.getX() + x,
                     clientboundLevelParticlesPacket.getY() + y,
                     clientboundLevelParticlesPacket.getZ() + z,
                     vx,
                     vy,
                     vz
                  );
               }
            }

            info.cancel();
         }
      } catch (Exception var19) {
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"handleBlockEntityData"}
   )
   public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)this, Minecraft.getInstance());
      BlockPos blockPos = packet.getPos();
      PhysicsMod.getInstance(this.level).blockUpdates.add(blockPos);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleLogin"}
   )
   public void handleLogin(ClientboundLoginPacket packet, CallbackInfo info) {
      boolean changed = false;

      for (ResourceKey<Level> entry : packet.levels()) {
         changed |= ConfigClient.addGravityBuoyancyEntry(entry.location());
      }

      if (changed) {
         ConfigClient.save();
      }
   }
}
