package fuzs.visualworkbench.world.inventory;

import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class VisualCraftingMenu extends CraftingMenu implements ContainerListener {
   private final Container blockEntity;

   public VisualCraftingMenu(int id, Inventory inventory) {
      super(id, inventory);
      this.blockEntity = new SimpleContainer(new ItemStack[0]);
   }

   public VisualCraftingMenu(int id, Inventory inventory, CraftingTableBlockEntity blockEntity, ContainerLevelAccess access) {
      super(id, inventory, access);
      ((TransientCraftingContainer)this.craftSlots).items = blockEntity.getItems();
      this.resultSlots.itemStacks = blockEntity.getResultItems();
      this.blockEntity = blockEntity;
      this.addSlotListener(this);
      this.slotsChanged(this.craftSlots);
   }

   public MenuType<?> getType() {
      return (MenuType<?>)ModRegistry.CRAFTING_MENU_TYPE.value();
   }

   public void slotsChanged(Container container) {
      super.slotsChanged(container);
      if (container == this.craftSlots || container == this.resultSlots) {
         this.access.execute((level, blockPos) -> this.blockEntity.setChanged());
      }
   }

   public void removed(Player player) {
      ContainerLevelAccess containerLevelAccess = this.access;
      this.access = ContainerLevelAccess.NULL;
      super.removed(player);
      this.access = containerLevelAccess;
   }

   public boolean stillValid(Player player) {
      return (Boolean)this.access.evaluate((level, blockPos) -> this.blockEntity.stillValid(player), true);
   }

   public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack itemStack) {
      this.access.execute((level, blockPos) -> {
         if (dataSlotIndex >= 0 && dataSlotIndex < this.getSize()) {
            this.blockEntity.setChanged();
         }
      });
   }

   public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
   }
}
