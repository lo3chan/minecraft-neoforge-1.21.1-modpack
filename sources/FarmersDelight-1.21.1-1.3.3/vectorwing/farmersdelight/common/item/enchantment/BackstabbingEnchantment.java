package vectorwing.farmersdelight.common.item.enchantment;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.lang3.mutable.MutableFloat;
import vectorwing.farmersdelight.common.registry.ModDataComponents;

public class BackstabbingEnchantment {
   public static boolean isLookingBehindTarget(LivingEntity target, Vec3 attackerLocation) {
      if (attackerLocation != null) {
         Vec3 lookingVector = target.getViewVector(1.0F);
         Vec3 attackAngleVector = attackerLocation.subtract(target.position()).normalize();
         attackAngleVector = new Vec3(attackAngleVector.x, 0.0, attackAngleVector.z);
         return attackAngleVector.dot(lookingVector) < -0.5;
      } else {
         return false;
      }
   }

   public static float getBackstabbingDamagePerLevel(float amount, int level) {
      float multiplier = level * 0.2F + 1.2F;
      return amount * multiplier;
   }

   @EventBusSubscriber(
      modid = "farmersdelight"
   )
   public static class BackstabbingEvent {
      @SubscribeEvent
      public static void onKnifeBackstab(LivingIncomingDamageEvent event) {
         Entity attacker = event.getSource().getEntity();
         if (attacker instanceof LivingEntity living
            && BackstabbingEnchantment.isLookingBehindTarget(event.getEntity(), event.getSource().getSourcePosition())
            && attacker.level() instanceof ServerLevel serverLevel) {
            ItemStack weapon = living.getWeaponItem();
            float preModifiedDamage = event.getAmount();
            MutableFloat dmg = new MutableFloat(event.getAmount());
            EnchantmentHelper.runIterationOnItem(
               weapon,
               (enchantment, powerLevel) -> ((Enchantment)enchantment.value())
                  .modifyDamageFilteredValue(
                     (DataComponentType)ModDataComponents.BACKSTABBING.get(), serverLevel, powerLevel, weapon, attacker, event.getSource(), dmg
                  )
            );
            if (preModifiedDamage != dmg.getValue()) {
               event.setAmount(dmg.getValue());
               serverLevel.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
         }
      }
   }
}
