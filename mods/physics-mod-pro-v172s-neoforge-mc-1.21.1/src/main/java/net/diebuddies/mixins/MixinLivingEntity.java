/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public class MixinLivingEntity {
    @Shadow
    @Final
    protected boolean dead;

    @Inject(at={@At(value="HEAD")}, method={"die"})
    public void onDeath(DamageSource source, CallbackInfo info) {
        LivingEntity entity = (LivingEntity)this;
        if (RenderSystem.isOnRenderThread() && ConfigMobs.getMobSetting((Entity)entity).getType() != MobPhysicsType.OFF) {
            PhysicsMod.blockifyEntity(entity.getCommandSenderWorld(), entity);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"spawnItemParticles"}, cancellable=true)
    private void spawnItemParticles(ItemStack itemStack, int amount, CallbackInfo info) {
        Camera camera;
        LivingEntity ths = (LivingEntity)this;
        if (ConfigClient.eatingPhysicsParticles && ths.level() instanceof ClientLevel && (camera = Minecraft.getInstance().gameRenderer.getMainCamera()).isInitialized() && camera.getPosition().distanceToSqr(ths.getX(), ths.getEyeY(), ths.getZ()) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            for (int j = 0; j < amount * 2; ++j) {
                Vec3 vel = new Vec3(((double)Math.random() - 0.5) * 0.1, (double)Math.random() * 0.1 + 0.1, 0.0);
                vel = vel.xRot(-ths.getXRot() * ((float)java.lang.Math.PI / 180));
                vel = vel.yRot(-ths.getYRot() * ((float)java.lang.Math.PI / 180));
                double d = (double)(-Math.random()) * 0.6 - 0.3;
                Vec3 pos = new Vec3(((double)Math.random() - 0.5) * 0.3, d, 0.6);
                pos = pos.xRot(-ths.getXRot() * ((float)java.lang.Math.PI / 180));
                pos = pos.yRot(-ths.getYRot() * ((float)java.lang.Math.PI / 180));
                pos = pos.add(ths.getX(), ths.getEyeY(), ths.getZ());
                ParticleSpawner.spawnEatingPhysicsParticle(itemStack, ths.level(), pos.x, pos.y, pos.z, vel.x * 15.0, vel.y * 15.0, vel.z * 15.0);
            }
            info.cancel();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"handleEntityEvent"}, cancellable=true)
    private void handleEntityEvent(byte event, CallbackInfo info) {
        ItemStack stack;
        LivingEntity ths = (LivingEntity)this;
        if (ths != Minecraft.getInstance().player) {
            return;
        }
        if (event == 47) {
            ItemStack stack2 = ths.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!stack2.isEmpty()) {
                this.spawnItemBreakPhysics(stack2, true);
            }
        } else if (event == 48 && !(stack = ths.getItemBySlot(EquipmentSlot.OFFHAND)).isEmpty()) {
            this.spawnItemBreakPhysics(stack, false);
        }
    }

    @Unique
    private void spawnItemBreakPhysics(ItemStack stack, boolean mainHand) {
        if (ConfigClient.itemBreakPhysics && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            LivingEntity ths = (LivingEntity)this;
            PhysicsMod.blockifyItemStack(ths.level(), stack, mainHand);
        }
    }
}

