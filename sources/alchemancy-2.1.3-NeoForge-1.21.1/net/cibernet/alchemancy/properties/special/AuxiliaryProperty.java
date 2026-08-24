package net.cibernet.alchemancy.properties.special;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;

@EventBusSubscriber
public class AuxiliaryProperty extends Property {
   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (user instanceof LivingEntity living) {
         triggerAuxiliaryEffects(stack, propertyHolder -> ((Property)propertyHolder.value()).onEquippedTick(living, EquipmentSlot.MAINHAND, stack));
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 13297377;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLivingDamage(Pre event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Player user) {
         triggerAuxiliaryEffects(user, (holder, stack) -> ((Property)holder.value()).modifyAttackDamage(user, stack, event));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   private static void onIncomingDamage(LivingIncomingDamageEvent event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Player user) {
         triggerAuxiliaryEffects(user, (propertyHolder, stack) -> ((Property)propertyHolder.value()).onIncomingAttack(user, stack, event.getEntity(), event));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   private static void onLivingDeath(LivingDeathEvent event) {
      if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Player user) {
         triggerAuxiliaryEffects(user, (propertyHolder, stack) -> ((Property)propertyHolder.value()).onKill(event.getEntity(), user, stack, event));
      }

      if (event.getEntity() instanceof Player user) {
         triggerAuxiliaryEffects(user, (propertyHolder, stack) -> ((Property)propertyHolder.value()).onUserDeath(user, stack, EquipmentSlot.MAINHAND, event));
      }
   }

   @SubscribeEvent
   private static void onEffectAdded(Added event) {
      if (event.getEntity() instanceof Player player) {
         triggerAuxiliaryEffects(
            player, (propertyHolder, stack) -> ((Property)propertyHolder.value()).onMobEffectAdded(stack, EquipmentSlot.MAINHAND, player, event)
         );
      }
   }

   @SubscribeEvent
   private static void onEffectApplicable(Applicable event) {
      if (event.getEntity() instanceof Player player) {
         triggerAuxiliaryEffects(
            player, (propertyHolder, stack) -> ((Property)propertyHolder.value()).isMobEffectApplicable(stack, EquipmentSlot.MAINHAND, player, event)
         );
      }
   }

   public static void triggerAuxiliaryEffects(ItemStack stack, Consumer<Holder<Property>> consumer) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY)) {
         if (PropertyModifierComponent.getOrElse(stack, AlchemancyProperties.AUXILIARY, AlchemancyProperties.Modifiers.IGNORE_INFUSED, false)) {
            InfusedPropertiesHelper.forEachInnateProperty(stack, consumer);
         } else {
            InfusedPropertiesHelper.forEachProperty(stack, consumer);
         }
      }
   }

   public static void triggerAuxiliaryEffects(Player user, BiConsumer<Holder<Property>, ItemStack> consumer) {
      Inventory inventory = user.getInventory();

      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         ItemStack stack = inventory.getItem(slot);
         triggerAuxiliaryEffects(stack, propertyHolder -> consumer.accept(propertyHolder, stack));
      }
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }
}
