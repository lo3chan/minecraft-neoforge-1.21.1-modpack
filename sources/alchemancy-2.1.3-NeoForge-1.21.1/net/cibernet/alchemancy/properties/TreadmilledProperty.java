package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TreadmilledProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (slot == EquipmentSlot.BODY || slot == EquipmentSlot.FEET) {
         float xRot = user.getXRot();
         float yRot = user.getYRot();
         Vec3 currentVel = user.getDeltaMovement();
         if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ANTIGRAV)) {
            float xVel = -Mth.sin(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
            float yVel = -Mth.sin(xRot * 0.017453292F);
            float zVel = Mth.cos(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
            user.setDeltaMovement(new Vec3(xVel, yVel, zVel).normalize().scale(Math.max(this.getSpeed(stack), currentVel.length())));
         } else if (user.onGround()) {
            float xVel = -Mth.sin(yRot * 0.017453292F);
            float zVel = Mth.cos(yRot * 0.017453292F);
            user.setDeltaMovement(
               new Vec3(xVel, 0.0, zVel).normalize().scale(Math.max(this.getSpeed(stack), currentVel.length())).add(0.0, currentVel.y(), 0.0)
            );
         }
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      boolean agrav = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ANTIGRAV);
      boolean goesUpWalls = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SPIKING);
      if (itemEntity.getDeltaMovement().multiply(1.0, agrav ? 1.0 : 0.0, 1.0).lengthSqr() > 0.005) {
         this.updateRotation(itemEntity);
      }

      float xRot = itemEntity.getXRot();
      float yRot = itemEntity.getYRot();
      Vec3 currentVel = itemEntity.getDeltaMovement();
      Vec3 newVel = null;
      double speed = this.getSpeed(stack);
      if (agrav) {
         float xVel = Mth.sin(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
         float yVel = Mth.sin(xRot * 0.017453292F);
         float zVel = Mth.cos(yRot * 0.017453292F) * Mth.cos(xRot * 0.017453292F);
         newVel = new Vec3(xVel, yVel, zVel).normalize().scale(Math.max(speed, currentVel.length()));
      } else if (itemEntity.onGround() || goesUpWalls) {
         float xVel = Mth.sin(yRot * 0.017453292F);
         float zVel = Mth.cos(yRot * 0.017453292F);
         newVel = new Vec3(xVel, 0.0, zVel).normalize().scale(Math.max(speed, currentVel.length())).add(0.0, currentVel.y(), 0.0);
      }

      if (newVel != null) {
         BlockHitResult clip = itemEntity.level()
            .clip(
               new ClipContext(
                  itemEntity.position(), itemEntity.position().add(newVel.multiply(1.0, agrav ? 1.0 : 0.0, 1.0)), Block.COLLIDER, Fluid.NONE, itemEntity
               )
            );
         if (clip.getType() != Type.MISS) {
            if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BOUNCY)) {
               newVel = newVel.multiply(switch (clip.getDirection().getAxis()) {
                  case X -> new Vec3(-0.5, 1.0, 1.0);
                  case Y -> new Vec3(1.0, -0.5, 1.0);
                  case Z -> new Vec3(1.0, 1.0, -0.5);
                  default -> throw new MatchException(null, null);
               });
            } else if (goesUpWalls) {
               newVel = newVel.multiply(1.0, 0.0, 1.0).add(0.0, speed, 0.0);
            } else if (clip.getDirection().equals(Direction.fromYRot(yRot))) {
               return;
            }
         } else if (goesUpWalls && !itemEntity.onGround()) {
            return;
         }

         itemEntity.setDeltaMovement(newVel);
      }
   }

   public double getSpeed(ItemStack stack) {
      double result = 0.25;
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SWIFT)) {
         result *= 2.0;
      }

      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SLUGGISH)) {
         result *= 0.5;
      }

      return result;
   }

   protected void updateRotation(ItemEntity entity) {
      Vec3 vec3 = entity.getDeltaMovement();
      double d0 = vec3.horizontalDistance();
      entity.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0 / 3.1415927410125732));
      entity.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0 / 3.1415927410125732));
   }

   @Override
   public int getColor(ItemStack stack) {
      return 14832198;
   }
}
