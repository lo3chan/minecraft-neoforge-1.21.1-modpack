package net.cibernet.alchemancy.properties.special;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class InfusionCodexProperty extends Property {
   public static Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> inspectItem(Player user, ItemStack stack) {
      Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> result = new HashMap<>();
      InfusedPropertiesHelper.getInnateProperties(stack).forEach(propertyHolder -> addEntry(result, (Holder<Property>)propertyHolder));
      InfusedPropertiesHelper.getInfusedProperties(stack).forEach(propertyHolder -> addEntry(result, (Holder<Property>)propertyHolder));
      InfusedPropertiesHelper.getStoredProperties(stack).forEach(propertyHolder -> addEntry(result, (Holder<Property>)propertyHolder));
      if (InfusedPropertiesHelper.hasProperty(user.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING)
         || InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.REVEALED)
         || InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AWAKENED)) {
         AlchemancyProperties.getDormantProperties(stack).forEach(propertyHolder -> addEntry(result, (Holder<Property>)propertyHolder));
      }

      return result;
   }

   public static boolean canInspect(Player user, ItemStack stack) {
      if (user.level().isClientSide()) {
         return !inspectItem(user, stack).isEmpty();
      } else {
         return InfusedPropertiesHelper.hasProperty(user.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING)
               && !AlchemancyProperties.getDormantProperties(stack).isEmpty()
            ? true
            : !InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty()
               || !InfusedPropertiesHelper.getInnateProperties(stack).isEmpty()
               || !InfusedPropertiesHelper.getStoredProperties(stack).isEmpty();
      }
   }

   private static void addEntry(Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> map, Holder<Property> propertyHolder) {
      if (!map.containsKey(propertyHolder) && CodexEntryReloadListenener.getEntries().containsKey(propertyHolder)) {
         map.put(propertyHolder, CodexEntryReloadListenener.getEntries().get(propertyHolder));
      }
   }

   @Override
   public void onStackedOverItem(
      ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY && !stackedOnItem.isEmpty() && !ItemStack.isSameItemSameComponents(stackedOnItem, stack)) {
         if (player.level().isClientSide() && canInspect(player, stackedOnItem)) {
            ClientUtil.openCodexScreen(stackedOnItem);
         }

         isCancelled.set(true);
      }
   }

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY && !carriedItem.isEmpty() && !ItemStack.isSameItemSameComponents(stackedOnItem, carriedItem)) {
         if (player.level().isClientSide() && canInspect(player, carriedItem)) {
            ClientUtil.openCodexScreen(carriedItem);
         }

         isCancelled.set(true);
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         if (event.getLevel().isClientSide()) {
            ClientUtil.openCodexScreen(event.getItemStack().getDisplayName());
         }

         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 11341852;
   }
}
