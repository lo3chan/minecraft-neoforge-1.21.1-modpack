package net.cibernet.alchemancy.properties;

import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WildfireProperty extends OvergrowthProperty {
   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(0.5F, 5420544, 15000576, 13474560);
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      super.onEquippedTick(user, slot, stack);
      user.setRemainingFireTicks(Math.max(user.getRemainingFireTicks(), 20));
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (level.isClientSide()) {
         if (user.getRandom().nextFloat() < 0.05F) {
            user.level().addParticle(ParticleTypes.LARGE_SMOKE, user.getRandomX(1.0), user.getRandomY(), user.getRandomZ(1.0), 0.0, 0.25, 0.0);
         }
      } else {
         if (user instanceof LivingEntity living) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
               if (slot.isArmor() && InfusedPropertiesHelper.hasProperty(living.getItemBySlot(slot), AlchemancyProperties.WET)) {
                  InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
                  return;
               }
            }
         }

         if (user.isInWaterOrRain()) {
            InfusedPropertiesHelper.removeProperty(stack, this.asHolder());
         } else {
            super.onInventoryTick(user, stack, level, inventorySlot, isCurrentItem);
            if (user.getRandom().nextFloat() < 0.005F) {
               user.setRemainingFireTicks(Math.max(user.getRemainingFireTicks(), 20));
               if (canBurnUp(stack)) {
                  stack.shrink(1);
               }
            }
         }
      }
   }

   public static boolean canBurnUp(ItemStack stack) {
      return InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.BURNT_UP_BY_WILDFIRE)
         || AlchemancyProperties.getDormantProperties(stack).stream().anyMatch(p -> p.is(AlchemancyTags.Properties.BURNT_UP_BY_WILDFIRE));
   }
}
