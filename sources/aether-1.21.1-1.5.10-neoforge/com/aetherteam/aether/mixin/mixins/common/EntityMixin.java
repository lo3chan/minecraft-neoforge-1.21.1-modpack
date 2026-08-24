package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.DroppedItemAttachment;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.aetherteam.aether.world.LevelUtil;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public class EntityMixin {
   @Inject(
      at = {@At("TAIL")},
      method = {"tick()V"}
   )
   private void travel(CallbackInfo ci) {
      Entity entity = (Entity)this;
      Level level = entity.level();
      if (level instanceof ServerLevel serverLevel
         && !(Boolean)AetherConfig.SERVER.disable_falling_to_overworld.get()
         && serverLevel.dimension() == LevelUtil.destinationDimension()
         && entity.getY() <= serverLevel.getMinBuildHeight()
         && !entity.isPassenger()) {
         if (!(entity instanceof Player) && !entity.isVehicle() && (!(entity instanceof Saddleable) || !((Saddleable)entity).isSaddled())) {
            if (entity instanceof Projectile projectile && projectile.getOwner() instanceof Player) {
               entityFell(projectile);
            } else if (entity instanceof ItemEntity itemEntity
               && itemEntity.hasData(AetherDataAttachments.DROPPED_ITEM)
               && (
                  itemEntity.getOwner() instanceof Player
                     || ((DroppedItemAttachment)itemEntity.getData(AetherDataAttachments.DROPPED_ITEM)).getOwner(level) instanceof Player
               )) {
               entityFell(entity);
            }
         } else {
            entityFell(entity);
         }
      }
   }

   @Unique
   @Nullable
   private static Entity entityFell(Entity entity) {
      Level serverLevel = entity.level();
      MinecraftServer minecraftserver = serverLevel.getServer();
      if (minecraftserver != null) {
         ServerLevel destination = minecraftserver.getLevel(LevelUtil.returnDimension());
         if (destination != null && LevelUtil.returnDimension() != LevelUtil.destinationDimension()) {
            serverLevel.getProfiler().push("aether_fall");
            entity.setPortalCooldown();
            double vehicleOffset = 0.0;
            if (entity.getVehicle() != null) {
               vehicleOffset = entity.getVehicle().getBbHeight();
            }

            DimensionTransition transition = new DimensionTransition(
               destination,
               new Vec3(entity.getX(), destination.getMaxBuildHeight() - entity.getBbHeight() - vehicleOffset, entity.getZ()),
               entity.getDeltaMovement(),
               entity.getYRot(),
               entity.getXRot(),
               false,
               DimensionTransition.DO_NOTHING
            );
            Entity target = entity.changeDimension(transition);
            serverLevel.getProfiler().pop();
            if (target != null && target instanceof ServerPlayer) {
               DimensionHooks.teleportationTimer = 500;
            }

            return target;
         }
      }

      return null;
   }
}
