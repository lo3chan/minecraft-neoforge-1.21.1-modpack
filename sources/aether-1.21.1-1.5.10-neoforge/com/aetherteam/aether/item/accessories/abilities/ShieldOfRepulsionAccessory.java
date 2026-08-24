package com.aetherteam.aether.item.accessories.abilities;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.nitrogen.ConstantsUtil;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public interface ShieldOfRepulsionAccessory {
   static void deflectProjectile(ProjectileImpactEvent event, HitResult hitResult, Projectile projectile) {
      if (hitResult.getType() == Type.ENTITY
         && hitResult instanceof EntityHitResult entityHitResult
         && entityHitResult.getEntity() instanceof LivingEntity impactedLiving
         && projectile.getType().is(AetherTags.Entities.DEFLECTABLE_PROJECTILES)) {
         SlotEntryReference slotResult = EquipmentUtil.getAccessory(impactedLiving, (Item)AetherItems.SHIELD_OF_REPULSION.get());
         if (slotResult != null) {
            Vec3 motion = impactedLiving.getDeltaMovement();
            if (impactedLiving instanceof Player player) {
               AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
               if (!data.isMoving()
                  || data.isMoving() && motion.x() == 0.0 && (motion.y() == ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y || motion.y() == 0.0) && motion.z() == 0.0) {
                  if (player.level().isClientSide()) {
                     data.setProjectileImpactedMaximum(150);
                     data.setProjectileImpactedTimer(150);
                  }

                  handleDeflection(event, projectile, player, slotResult);
               }
            } else if (motion.x() == 0.0 && (motion.y() == ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y || motion.y() == 0.0) && motion.z() == 0.0) {
               handleDeflection(event, projectile, impactedLiving, slotResult);
            }
         }
      }
   }

   private static void handleDeflection(ProjectileImpactEvent event, Projectile projectile, LivingEntity impactedLiving, SlotEntryReference slotResult) {
      event.setCanceled(true);
      if (!impactedLiving.equals(projectile.getOwner())) {
         projectile.deflect(ProjectileDeflection.REVERSE, impactedLiving, projectile.getOwner(), false);
         projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.25));
         if (impactedLiving.level() instanceof ServerLevel serverLevel) {
            slotResult.stack().hurtAndBreak(1, serverLevel, impactedLiving, item -> AccessoriesAPI.breakStack(slotResult.reference()));
         }
      }
   }
}
