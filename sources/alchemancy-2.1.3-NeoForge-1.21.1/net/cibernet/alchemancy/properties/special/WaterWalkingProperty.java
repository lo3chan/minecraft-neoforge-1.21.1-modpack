package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public class WaterWalkingProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if ((slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) && !user.isShiftKeyDown() && !user.isSwimming()) {
         if (user.level().getFluidState(user.getBlockPosBelowThatAffectsMyMovement()).is(Fluids.WATER)) {
            if (!user.onGround()) {
               CommonHooks.onLivingFall(user, user.fallDistance, 0.0F);
            }

            user.setOnGround(true);
            Vec3 vec = user.getDeltaMovement();
            BlockHitResult hit = user.level()
               .clip(new ClipContext(user.position(), user.getBlockPosBelowThatAffectsMyMovement().getBottomCenter(), Block.OUTLINE, Fluid.ANY, user));
            user.setDeltaMovement(new Vec3(vec.x, Math.max(hit.getLocation().y() - user.getY(), vec.y), vec.z));
            user.resetFallDistance();
            playParticles(user, user.getY(), stack, 2);
         }
      }
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (itemEntity.level().getFluidState(itemEntity.getOnPos()).is(Fluids.WATER)) {
         itemEntity.setOnGround(true);
         Vec3 vec = itemEntity.getDeltaMovement();
         BlockHitResult hit = itemEntity.level()
            .clip(
               new ClipContext(
                  itemEntity.position(), itemEntity.getBlockPosBelowThatAffectsMyMovement().getBottomCenter(), Block.OUTLINE, Fluid.ANY, itemEntity
               )
            );
         itemEntity.setDeltaMovement(new Vec3(vec.x, Math.max(hit.getLocation().y() - itemEntity.getY(), vec.y), vec.z));
         itemEntity.resetFallDistance();
         if (itemEntity.getRandom().nextFloat() < 0.2F) {
            playParticles(itemEntity, itemEntity.getY(), stack, 2);
         }
      }
   }

   public static void playParticles(Entity user, double y, ItemStack stack, int amount) {
      if (user.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            SparklingProperty.getParticles(stack).orElse(ParticleTypes.BUBBLE),
            user.getX(),
            y,
            user.getZ(),
            amount,
            user.getBbWidth() * 0.4F,
            0.0,
            user.getBbWidth() * 0.4F,
            0.0
         );
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 4705023;
   }
}
