package fuzs.puzzleslib.api.container.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.NonInteractiveResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ContainerMenuHelper {
   private ContainerMenuHelper() {
   }

   public static <T> void openMenu(Player player, MenuProvider menuProvider, T data) {
      Objects.requireNonNull(player, "player is null");
      Objects.requireNonNull(menuProvider, "menu provider is null");
      Objects.requireNonNull(data, "data is null");
      ProxyImpl.get().openMenu(player, menuProvider, data);
   }

   public static void setSelectedSlotLocked(AbstractContainerMenu containerMenu) {
      for (int i = 0; i < containerMenu.slots.size(); i++) {
         Slot slot = (Slot)containerMenu.slots.get(i);
         if (slot.container instanceof Inventory inventory && inventory.selected == slot.getContainerSlot()) {
            NonInteractiveResultSlot newSlot = new NonInteractiveResultSlot(slot.container, slot.getContainerSlot(), slot.x, slot.y) {
               public boolean isFake() {
                  return false;
               }
            };
            newSlot.index = slot.index;
            containerMenu.slots.set(i, newSlot);
            break;
         }
      }
   }

   @Deprecated
   public static void addInventorySlots(AbstractContainerMenu abstractContainerMenu, Inventory inventory, int offsetY) {
      addInventorySlots(abstractContainerMenu, inventory, 8, offsetY);
   }

   @Deprecated
   public static void addInventorySlots(AbstractContainerMenu abstractContainerMenu, Inventory inventory, int offsetX, int offsetY) {
      addStandardInventorySlots(abstractContainerMenu, inventory, offsetX, offsetY);
   }

   public static void addInventoryHotbarSlots(AbstractContainerMenu abstractContainerMenu, Container container, int offsetX, int offsetY) {
      for (int i = 0; i < 9; i++) {
         abstractContainerMenu.addSlot(new Slot(container, i, offsetX + i * 18, offsetY));
      }
   }

   public static void addInventoryExtendedSlots(AbstractContainerMenu abstractContainerMenu, Container container, int offsetX, int offsetY) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            abstractContainerMenu.addSlot(new Slot(container, j + (i + 1) * 9, offsetX + j * 18, offsetY + i * 18));
         }
      }
   }

   public static void addStandardInventorySlots(AbstractContainerMenu abstractContainerMenu, Container container, int offsetX, int offsetY) {
      addInventoryExtendedSlots(abstractContainerMenu, container, offsetX, offsetY);
      addInventoryHotbarSlots(abstractContainerMenu, container, offsetX, offsetY + 58);
   }

   public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable Container listener) {
      return createListBackedContainer(items, listener != null ? $ -> listener.setChanged() : null);
   }

   public static SimpleContainer createListBackedContainer(NonNullList<ItemStack> items, @Nullable ContainerListener listener) {
      SimpleContainer simpleContainer = new SimpleContainer(new ItemStack[0]);
      simpleContainer.size = items.size();
      simpleContainer.items = items;
      if (listener != null) {
         simpleContainer.addListener(listener);
      }

      return simpleContainer;
   }

   public static void copyItemsIntoContainer(NonNullList<ItemStack> from, Container to) {
      for (int i = 0; i < from.size(); i++) {
         if (i < to.getContainerSize()) {
            to.setItem(i, (ItemStack)from.get(i));
         }
      }
   }

   public static void copyItemsIntoList(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
      for (int i = 0; i < from.size(); i++) {
         if (i < to.size()) {
            to.set(i, (ItemStack)from.get(i));
         }
      }
   }
}
