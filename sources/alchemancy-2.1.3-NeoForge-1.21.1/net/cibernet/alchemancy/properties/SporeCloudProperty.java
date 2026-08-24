package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SporeCloudProperty extends Property {
   private static final float RADIUS = 4.0F;
   private static final int COOLDOWN = 100;
   private static final int DURABILITY_USED = 50;

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      this.releaseSpores(target.level(), source, target.position(), stack, null);
   }

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (user instanceof Player player) {
         ItemCooldowns cooldowns = player.getCooldowns();
         if (!cooldowns.isOnCooldown(weapon.getItem())) {
            this.releaseSpores(user.level(), user, user.position(), weapon, slot);
            cooldowns.addCooldown(weapon.getItem(), 100);
         }
      } else {
         this.releaseSpores(user.level(), user, user.position(), weapon, slot);
      }
   }

   public void releaseSpores(Level level, @Nullable Entity source, Vec3 position, ItemStack stack, @Nullable EquipmentSlot slot) {
      if (!this.preventRecursion(stack)) {
         RandomSource rand = level.getRandom();
         float radiusSqr = 16.0F;
         List<LivingEntity> entities = level.getEntitiesOfClass(
            LivingEntity.class,
            CommonUtils.boundingBoxAroundPoint(position, 4.0F),
            EntitySelector.NO_SPECTATORS.and(targetx -> targetx.distanceToSqr(position.x, position.y, position.z) <= radiusSqr)
         );
         if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 160.0F; i++) {
               serverLevel.sendParticles(
                  SparklingProperty.getParticles(stack).orElse(ParticleTypes.FALLING_SPORE_BLOSSOM),
                  position.x,
                  position.y,
                  position.z,
                  1,
                  Math.sin(rand.nextDouble() * 3.141592653589793 * 2.0) * 4.0 * 0.5,
                  Math.cos(rand.nextDouble() * 3.141592653589793 * 2.0) * 4.0 * 0.5,
                  Math.cos(rand.nextDouble() * 3.141592653589793 * 2.0) * 4.0 * 0.5,
                  0.0
               );
            }
         }

         ItemStack refStack = stack.copy().split(1);
         int durabilityTaken = PropertyModifierComponent.getOrElse(stack, this.asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 50);
         BlockPos pos = new BlockPos((int)position.x, (int)position.y, (int)position.z);

         for (LivingEntity target : entities) {
            if (target != source) {
               ItemStack s = refStack.copy();
               s.set(AlchemancyItems.Components.RECURSION_PREVENTION, Unit.INSTANCE);
               if (source == null) {
                  activateByBlock(level, pos, target, s);
               } else {
                  activateByEntity(source, target, s);
               }

               durabilityTaken += s.getDamageValue() - refStack.getDamageValue();
            }
         }

         this.damageOrConsumeItem(level, source, stack, slot, durabilityTaken);
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
      playRootedParticles(root, randomSource, ParticleTypes.FALLING_SPORE_BLOSSOM);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 8088947;
   }
}
