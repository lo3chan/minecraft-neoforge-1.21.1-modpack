package net.blay09.mods.inventoryessentials.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.blay09.mods.inventoryessentials.InventoryEssentialsConfig;
import net.blay09.mods.inventoryessentials.InventoryUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public class BulkTransferAllMessage implements CustomPacketPayload {
   public static final Type<BulkTransferAllMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "bulk_transfer_all"));
   private final int slotNumber;

   public BulkTransferAllMessage(int slotNumber) {
      this.slotNumber = slotNumber;
   }

   public static BulkTransferAllMessage decode(FriendlyByteBuf buf) {
      int slotNumber = buf.readVarInt();
      return new BulkTransferAllMessage(slotNumber);
   }

   public static void encode(FriendlyByteBuf buf, BulkTransferAllMessage message) {
      buf.writeVarInt(message.slotNumber);
   }

   public static void handle(ServerPlayer player, BulkTransferAllMessage message) {
      AbstractContainerMenu menu = player.containerMenu;
      if (menu != null && menu.isValidSlotIndex(message.slotNumber)) {
         Slot clickedSlot = menu.getSlot(message.slotNumber);
         boolean isProbablyMovingToPlayerInventory = false;
         if (!(clickedSlot.container instanceof Inventory)) {
            isProbablyMovingToPlayerInventory = InventoryUtils.containerContainsPlayerInventory(menu);
         }

         boolean clickedAnArmorItem = clickedSlot.getItem().getItem() instanceof Equipable equipable && equipable.getEquipmentSlot().isArmor();
         boolean isInsideInventory = menu instanceof InventoryMenu;
         if (isProbablyMovingToPlayerInventory) {
            Deque<Slot> emptySlots = new ArrayDeque<>();
            List<Slot> nonEmptySlots = new ArrayList<>();

            for (Slot slot : menu.slots) {
               if (!InventoryUtils.isSameInventory(slot, clickedSlot) && slot.container instanceof Inventory) {
                  if (slot.hasItem()) {
                     nonEmptySlots.add(slot);
                  } else if (!Inventory.isHotbarSlot(slot.getContainerSlot())) {
                     emptySlots.add(slot);
                  }
               }
            }

            for (Slot slotx : menu.slots) {
               if (slotx.mayPickup(player) && InventoryUtils.isSameInventory(slotx, clickedSlot, true)) {
                  bulkTransferPreferInventory(player, menu, emptySlots, nonEmptySlots, slotx);
               }
            }
         } else if (clickedAnArmorItem && isInsideInventory) {
            if (!InventoryEssentialsConfig.getActive().bulkTransferArmorSets) {
               return;
            }

            if (clickedSlot.index >= 5 && clickedSlot.index < 9) {
               for (int i = 5; i < 9; i++) {
                  menu.clicked(i, 0, ClickType.QUICK_MOVE, player);
               }

               return;
            }

            Map<EquipmentSlot, Slot> armorSlots = InventoryUtils.findMatchingArmorSetSlots(menu, clickedSlot);
            List<EquipmentSlot> equipmentSlots = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

            for (int i = 5; i < 9; i++) {
               EquipmentSlot equipmentSlot = equipmentSlots.get(i - 5);
               Slot swapSlot = armorSlots.get(equipmentSlot);
               if (swapSlot != null) {
                  menu.clicked(i, 0, ClickType.PICKUP, player);
                  menu.clicked(swapSlot.index, 0, ClickType.PICKUP, player);
                  menu.clicked(i, 0, ClickType.PICKUP, player);
               }
            }
         } else {
            for (Slot slotxx : menu.slots) {
               if (slotxx.mayPickup(player) && InventoryUtils.isSameInventory(slotxx, clickedSlot, true)) {
                  menu.clicked(slotxx.index, 0, ClickType.QUICK_MOVE, player);
               }
            }
         }
      }
   }

   private static boolean bulkTransferPreferInventory(Player player, AbstractContainerMenu menu, Deque<Slot> emptySlots, List<Slot> nonEmptySlots, Slot slot) {
      ItemStack targetStack = slot.getItem().copy();
      if (targetStack.isEmpty()) {
         return false;
      } else {
         menu.clicked(slot.index, 0, ClickType.PICKUP, player);

         for (Slot nonEmptySlot : nonEmptySlots) {
            ItemStack stack = nonEmptySlot.getItem();
            if (ItemStack.isSameItemSameComponents(targetStack, stack)) {
               boolean hasSpaceLeft = stack.getCount() < Math.min(nonEmptySlot.getMaxStackSize(), nonEmptySlot.getMaxStackSize(stack));
               if (hasSpaceLeft) {
                  menu.clicked(nonEmptySlot.index, 0, ClickType.PICKUP, player);
                  ItemStack mouseItem = menu.getCarried();
                  if (mouseItem.isEmpty()) {
                     return true;
                  }
               }
            }
         }

         Iterator<Slot> iterator = emptySlots.iterator();

         while (iterator.hasNext()) {
            Slot emptySlot = iterator.next();
            menu.clicked(emptySlot.index, 0, ClickType.PICKUP, player);
            if (emptySlot.hasItem()) {
               nonEmptySlots.add(emptySlot);
               iterator.remove();
            }

            ItemStack mouseItem = menu.getCarried();
            if (mouseItem.isEmpty()) {
               return true;
            }
         }

         ItemStack mouseItem = menu.getCarried();
         if (!mouseItem.isEmpty()) {
            menu.clicked(slot.index, 0, ClickType.PICKUP, player);
         }

         return false;
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
