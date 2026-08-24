package net.blay09.mods.inventoryessentials.network;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record DumpToContainerMessage(boolean fillEmptySlots) implements CustomPacketPayload {
   public static final Type<DumpToContainerMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "dump_to_container"));
   public static final StreamCodec<RegistryFriendlyByteBuf, DumpToContainerMessage> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, DumpToContainerMessage::fillEmptySlots, DumpToContainerMessage::new
   );

   public static void handle(ServerPlayer player, DumpToContainerMessage message) {
      AbstractContainerMenu menu = player.containerMenu;
      if (menu.getCarried().isEmpty() && !(menu instanceof InventoryMenu)) {
         ArrayList<Slot> sourceSlots = new ArrayList<>();
         ArrayList<Slot> nonEmptyTargetSlots = new ArrayList<>();
         ArrayList<Slot> emptyTargetSlots = new ArrayList<>();

         for (Slot slot : menu.slots) {
            if (slot.container instanceof Inventory) {
               int containerSlot = slot.getContainerSlot();
               if (containerSlot >= 9 && containerSlot < 36 && slot.mayPickup(player) && slot.hasItem()) {
                  sourceSlots.add(slot);
               }
            } else if (slot.hasItem()) {
               nonEmptyTargetSlots.add(slot);
            } else {
               emptyTargetSlots.add(slot);
            }
         }

         if (!sourceSlots.isEmpty() && !nonEmptyTargetSlots.isEmpty()) {
            for (Slot sourceSlot : sourceSlots) {
               if (sourceSlot.hasItem()) {
                  menu.clicked(sourceSlot.index, 0, ClickType.PICKUP, player);
                  ItemStack carried = menu.getCarried();
                  if (!carried.isEmpty()) {
                     ItemStack sourceStack = carried.copy();
                     boolean hasMatchingItemInContainer = false;

                     for (Slot targetSlot : nonEmptyTargetSlots) {
                        ItemStack targetStack = targetSlot.getItem();
                        if (!targetStack.isEmpty() && ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
                           hasMatchingItemInContainer = true;
                           int targetLimit = Math.min(targetSlot.getMaxStackSize(), targetSlot.getMaxStackSize(targetStack));
                           if (targetStack.getCount() < targetLimit) {
                              menu.clicked(targetSlot.index, 0, ClickType.PICKUP, player);
                              carried = menu.getCarried();
                              if (carried.isEmpty()) {
                                 break;
                              }
                           }
                        }
                     }

                     if (message.fillEmptySlots && !carried.isEmpty() && hasMatchingItemInContainer) {
                        Iterator<Slot> iterator = emptyTargetSlots.iterator();

                        while (iterator.hasNext()) {
                           Slot emptyTargetSlot = iterator.next();
                           if (emptyTargetSlot.hasItem()) {
                              nonEmptyTargetSlots.add(emptyTargetSlot);
                              iterator.remove();
                           } else if (emptyTargetSlot.mayPlace(sourceStack)) {
                              menu.clicked(emptyTargetSlot.index, 0, ClickType.PICKUP, player);
                              carried = menu.getCarried();
                              if (emptyTargetSlot.hasItem()) {
                                 nonEmptyTargetSlots.add(emptyTargetSlot);
                                 iterator.remove();
                              }

                              if (carried.isEmpty()) {
                                 break;
                              }
                           }
                        }
                     }

                     if (!menu.getCarried().isEmpty()) {
                        menu.clicked(sourceSlot.index, 0, ClickType.PICKUP, player);
                     }
                  }
               }
            }
         }
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
