package net.diebuddies.mixins;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public class MixinClientLevel {
   @Inject(
      at = {@At("HEAD")},
      method = {"addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"},
      cancellable = true
   )
   public void addParticle(ParticleOptions particleOptions, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
      Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (ConfigClient.serverBlockPhysicsParticles
         && particleOptions instanceof BlockParticleOption blockParticle
         && camera.isInitialized()
         && camera.getPosition().distanceToSqr(x, y, z) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
         BlockState block = blockParticle.getState();
         boolean isBarrier = block.getBlock() == Blocks.BARRIER;
         boolean isLeaf = block.getBlock() instanceof LeavesBlock || block.is(BlockTags.LEAVES);
         if (isBarrier || isLeaf) {
            return;
         }

         ParticleSpawner.spawnServerBlockPhysicsParticle(
            blockParticle.getState(),
            (ClientLevel)this,
            x + Math.random() * 0.1F - 0.05000000074505806,
            y + Math.random() * 0.1F - 0.05000000074505806,
            z + Math.random() * 0.1F - 0.05000000074505806,
            vx,
            vy,
            vz
         );
         info.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"addEntity"}
   )
   public void addEntity(Entity entity, CallbackInfo info) {
      if (entity instanceof FallingBlockEntity fallingBlock) {
         PhysicsMod mod = PhysicsMod.getInstance((ClientLevel)this);
         mod.fallingBlocks.add(fallingBlock.getStartPos());
      }
   }
}
