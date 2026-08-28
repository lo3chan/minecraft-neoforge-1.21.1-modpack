/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.Util
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.BufferUploader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GameRenderer.class})
public class MixinGameRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private List<AbstractClientPlayer> physicsmod$players = new ObjectArrayList();

    @Inject(at={@At(value="HEAD")}, method={"render"})
    public void render(DeltaTracker deltaTracker, boolean render, CallbackInfo info) {
        for (Map.Entry entry : PhysicsMod.getInstances().entrySet()) {
            PhysicsMod mod = (PhysicsMod)entry.getValue();
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
        ObjectIterator it = PhysicsMod.getInstances().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Level level = (Level)entry.getKey();
            PhysicsMod mod = (PhysicsMod)entry.getValue();
            if (level instanceof ClientLevel) {
                ((DynamicLoader)((ClientLevel)level).getChunkSource()).setPhysicsMod(mod);
            }
            if ((double)(currentTime - mod.time) / 1000000.0 > 4000.0) {
                mod.time = currentTime;
            }
            double diff = (double)(currentTime - mod.time) / 1.0E9 * PhysicsMod.getPlaybackSpeed();
            PhysicsWorld physics = mod.getPhysicsWorld();
            if (!physics.isActive()) {
                physics.destroy();
                it.remove();
                continue;
            }
            VAO.storePreviouslyBoundState();
            physics.update(diff);
            VAO.restorePreviouslyBoundState();
            BufferUploader.reset();
            mod.time = currentTime;
            if (!(level instanceof ClientLevel)) continue;
            ClientLevel client = (ClientLevel)level;
            this.physicsmod$players.clear();
            this.physicsmod$players.addAll(client.players());
            for (AbstractClientPlayer player : this.physicsmod$players) {
                PlayerPhysicsHealth health = (PlayerPhysicsHealth)player;
                if (health.getPhysicsHealth() > 0.0f && player.getHealth() <= 0.0f && ConfigMobs.getMobSetting((Entity)player).getType() != MobPhysicsType.OFF) {
                    PhysicsMod.blockifyEntity(player.getCommandSenderWorld(), (LivingEntity)player);
                }
                health.setPhysicsHealth(player.getHealth());
            }
        }
    }
}

