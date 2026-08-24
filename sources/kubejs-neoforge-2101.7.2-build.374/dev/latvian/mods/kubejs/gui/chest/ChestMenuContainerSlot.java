package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ChestMenuContainerSlot extends Slot {
   public final CustomChestMenu menu;
   public final int _index;

   public ChestMenuContainerSlot(CustomChestMenu menu, int index, int xPosition, int yPosition) {
      super(KubeJSGUI.EMPTY_CONTAINER, index, xPosition, yPosition);
      this.menu = menu;
      this._index = index;
   }

   public boolean mayPlace(@NotNull ItemStack stack) {
      return false;
   }

   @NotNull
   public ItemStack getItem() {
      return this.menu.data.slots[this._index].getItem();
   }

   public void set(@NotNull ItemStack stack) {
      this.menu.data.slots[this._index].setItem(stack);
   }

   public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
   }

   public int getMaxStackSize() {
      return 2147483647;
   }

   public int getMaxStackSize(@NotNull ItemStack stack) {
      return 2147483647;
   }

   public boolean mayPickup(Player playerIn) {
      return false;
   }

   @NotNull
   public ItemStack remove(int amount) {
      return ItemStack.EMPTY;
   }
}
