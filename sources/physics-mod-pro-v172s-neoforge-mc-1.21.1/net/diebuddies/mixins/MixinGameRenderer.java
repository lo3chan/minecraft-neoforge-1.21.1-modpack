package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.BufferUploader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.minecraft.PlayerPhysicsHealth;
import net.diebuddies.opengl.VAO;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class MixinGameRenderer {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Unique
   private List<AbstractClientPlayer> physicsmod$players = new ObjectArrayList();

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void render(DeltaTracker deltaTracker, boolean render, CallbackInfo info) {
      Iterator<Entry<Level, PhysicsMod>> it = PhysicsMod.getInstances().entrySet().iterator();

      while (it.hasNext()) {
         Entry<Level, PhysicsMod> entry = it.next();
         PhysicsMod mod = entry.getValue();

         while (!mod.sodiumRemoveRagdolls.isEmpty()) {
            Ragdoll ragdoll = mod.sodiumRemoveRagdolls.poll();
            mod.physicsWorld.removeRagdoll(ragdoll);
         }
      }

      LocalPlayer clientPlayer = this.minecraft.player;
      if (clientPlayer != null && ConfigClient.areDynamicBlockPhysicsEnabled()) {
         BlockPos pos = clientPlayer.blockPosition();
         int oldChunkX = VineHelper.playerPos.getX() >> 2;
         int oldChunkY = VineHelper.playerPos.getY() >> 2;
         int oldChunkZ = VineHelper.playerPos.getZ() >> 2;
         int newChunkX = pos.getX() >> 2;
         int newChunkY = pos.getY() >> 2;
         int newChunkZ = pos.getZ() >> 2;
         if (oldChunkX != newChunkX || oldChunkY != newChunkY || oldChunkZ != newChunkZ) {
            VineHelper.playerPos = pos.immutable();
            ClientLevel client = clientPlayer.clientLevel;
            if (client != null) {
               ((DynamicLoader)client.getChunkSource()).chunkPosChanged();
            }
         }
      }

      long currentTime = Util.getNanos();
      it = PhysicsMod.getInstances().entrySet().iterator();

      while (it.hasNext()) {
         Entry<Level, PhysicsMod> entry = it.next();
         Level level = entry.getKey();
         PhysicsMod mod = entry.getValue();
         if (level instanceof ClientLevel) {
            ((DynamicLoader)((ClientLevel)level).getChunkSource()).setPhysicsMod(mod);
         }

         if ((currentTime - mod.time) / 1000000.0 > 4000.0) {
            mod.time = currentTime;
         }

         double diff = (currentTime - mod.time) / 1.0E9 * PhysicsMod.getPlaybackSpeed();
         PhysicsWorld physics = mod.getPhysicsWorld();
         if (!physics.isActive()) {
            physics.destroy();
            it.remove();
         } else {
            VAO.storePreviouslyBoundState();
            physics.update(diff);
            VAO.restorePreviouslyBoundState();
            BufferUploader.reset();
            mod.time = currentTime;
            if (level instanceof ClientLevel client) {
               this.physicsmod$players.clear();
               this.physicsmod$players.addAll(client.players());

               for (AbstractClientPlayer player : this.physicsmod$players) {
                  PlayerPhysicsHealth health = (PlayerPhysicsHealth)player;
                  if (health.getPhysicsHealth() > 0.0F && player.getHealth() <= 0.0F && ConfigMobs.getMobSetting(player).getType() != MobPhysicsType.OFF) {
                     PhysicsMod.blockifyEntity(player.getCommandSenderWorld(), player);
                  }

                  health.setPhysicsHealth(player.getHealth());
               }
            }
         }
      }
   }
}
