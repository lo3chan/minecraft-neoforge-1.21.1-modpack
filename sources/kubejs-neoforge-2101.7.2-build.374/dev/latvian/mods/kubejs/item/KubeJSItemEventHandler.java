package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.InventoryChangedKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Post;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSItemEventHandler {
   @SubscribeEvent
   public static void rightClick(RightClickItem event) {
      ItemStack stack = event.getItemStack();
      ResourceKey<Item> key = stack.getItem().kjs$getKey();
      if (ItemEvents.RIGHT_CLICKED.hasListeners(key) && !event.getEntity().getCooldowns().isOnCooldown(stack.getItem())) {
         ItemEvents.RIGHT_CLICKED.post(event.getEntity(), key, new ItemClickedKubeEvent(event.getEntity(), event.getHand(), stack)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void leftClickEmpty(LeftClickEmpty event) {
   }

   @SubscribeEvent
   public static void itemPickupPre(Pre event) {
      ResourceKey<Item> key = event.getItemEntity().getItem().getItem().kjs$getKey();
      if (ItemEvents.CAN_PICK_UP.hasListeners(key)) {
         ItemEvents.CAN_PICK_UP
            .post(event.getPlayer(), key, new ItemPickedUpKubeEvent(event.getPlayer(), event.getItemEntity(), event.getItemEntity().getItem()))
            .applyTristate(event::setCanPickup);
      }
   }

   @SubscribeEvent
   public static void itemPickupPost(Post event) {
      ResourceKey<Item> key = event.getCurrentStack().getItem().kjs$getKey();
      if (ItemEvents.PICKED_UP.hasListeners(key)) {
         ItemEvents.PICKED_UP.post(event.getPlayer(), key, new ItemPickedUpKubeEvent(event.getPlayer(), event.getItemEntity(), event.getCurrentStack()));
      }
   }

   @SubscribeEvent
   public static void itemDrop(ItemTossEvent event) {
      ResourceKey<Item> key = event.getEntity().getItem().getItem().kjs$getKey();
      if (ItemEvents.DROPPED.hasListeners(key)) {
         ItemEvents.DROPPED.post(event.getPlayer(), key, new ItemDroppedKubeEvent(event.getPlayer(), event.getEntity())).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void entityInteract(EntityInteract event) {
      ItemStack stack = event.getItemStack();
      ResourceKey<Item> key = stack.getItem().kjs$getKey();
      if (ItemEvents.ENTITY_INTERACTED.hasListeners(key)) {
         ItemEvents.ENTITY_INTERACTED
            .post(event.getEntity(), key, new ItemEntityInteractedKubeEvent(event.getEntity(), event.getTarget(), event.getHand(), stack))
            .applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void crafted(ItemCraftedEvent event) {
      if (!event.getCrafting().isEmpty()) {
         ResourceKey<Item> key = event.getCrafting().getItem().kjs$getKey();
         if (ItemEvents.CRAFTED.hasListeners(key)) {
            ItemEvents.CRAFTED.post(event.getEntity(), key, new ItemCraftedKubeEvent(event.getEntity(), event.getCrafting(), event.getInventory()));
         }

         if (PlayerEvents.INVENTORY_CHANGED.hasListeners(key)) {
            PlayerEvents.INVENTORY_CHANGED.post(event.getEntity(), key, new InventoryChangedKubeEvent(event.getEntity(), event.getCrafting(), -1));
         }
      }
   }

   @SubscribeEvent
   public static void smelted(ItemSmeltedEvent event) {
      if (!event.getSmelting().isEmpty()) {
         ResourceKey<Item> key = event.getSmelting().getItem().kjs$getKey();
         if (ItemEvents.SMELTED.hasListeners(key)) {
            ItemEvents.SMELTED.post(event.getEntity(), key, new ItemSmeltedKubeEvent(event.getEntity(), event.getSmelting()));
         }

         if (PlayerEvents.INVENTORY_CHANGED.hasListeners(key)) {
            PlayerEvents.INVENTORY_CHANGED.post(event.getEntity(), key, new InventoryChangedKubeEvent(event.getEntity(), event.getSmelting(), -1));
         }
      }
   }

   @SubscribeEvent
   public static void itemDestroyed(PlayerDestroyItemEvent event) {
      ResourceKey<Item> key = event.getOriginal().getItem().kjs$getKey();
      if (ItemEvents.ITEM_DESTROYED.hasListeners(key)) {
         ItemEvents.ITEM_DESTROYED.post(event.getEntity(), key, new ItemDestroyedKubeEvent(event));
      }
   }
}
