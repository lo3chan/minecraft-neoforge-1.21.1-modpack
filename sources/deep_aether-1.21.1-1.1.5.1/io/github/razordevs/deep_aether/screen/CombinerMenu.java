package io.github.razordevs.deep_aether.screen;

import io.github.razordevs.deep_aether.init.DAMenuTypes;
import io.github.razordevs.deep_aether.init.DARecipeBookTypes;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipe;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerRecipeInput;
import io.github.razordevs.deep_aether.recipe.combiner.CombinerServerPlaceRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class CombinerMenu extends RecipeBookMenu<CombinerRecipeInput, CombinerRecipe> {
   private final Level level;
   private final ContainerData data;
   private final Container container;

   public CombinerMenu(int containerId, Inventory playerInventory) {
      this(containerId, playerInventory, new SimpleContainer(4), new SimpleContainerData(2));
   }

   public void handlePlacement(boolean flag, RecipeHolder<?> holder, ServerPlayer serverPlayer) {
      this.beginPlacingRecipe();

      try {
         new CombinerServerPlaceRecipe(this).recipeClicked(serverPlayer, holder, flag);
      } finally {
         this.finishPlacingRecipe(holder);
      }
   }

   public CombinerMenu(int pContainerId, Inventory inv, Container container, ContainerData data) {
      super((MenuType)DAMenuTypes.COMBINER_MENU.get(), pContainerId);
      checkContainerSize(container, 4);
      checkContainerDataCount(data, 2);
      this.level = inv.player.level();
      this.data = data;
      this.container = container;
      this.addSlot(new Slot(container, 0, 57, 17));
      this.addSlot(new Slot(container, 1, 80, 17));
      this.addSlot(new Slot(container, 2, 103, 17));
      this.addSlot(new FurnaceResultSlot(inv.player, container, 3, 80, 53));
      this.addPlayerInventory(inv);
      this.addPlayerHotbar(inv);
      this.addDataSlots(data);
   }

   public boolean isCrafting() {
      return this.data.get(0) > 0;
   }

   public int getScaledProgress() {
      int progress = this.data.get(0);
      int maxProgress = this.data.get(1);
      int progressArrowSize = 14;
      return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
   }

   public boolean stillValid(Player pPlayer) {
      return this.container.stillValid(pPlayer);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      if (this.container instanceof StackedContentsCompatible stackedContentsCompatible) {
         stackedContentsCompatible.fillStackedContents(stackedContents);
      }
   }

   public void clearCraftingContent() {
      this.getSlot(0).set(ItemStack.EMPTY);
      this.getSlot(1).set(ItemStack.EMPTY);
      this.getSlot(2).set(ItemStack.EMPTY);
   }

   public boolean recipeMatches(RecipeHolder<CombinerRecipe> recipeHolder) {
      return ((CombinerRecipe)recipeHolder.value()).matches(new CombinerRecipeInput(this.getIngredients()), this.level);
   }

   private List<ItemStack> getIngredients() {
      List<ItemStack> stacks = new ArrayList<>();
      stacks.add(this.container.getItem(0));
      stacks.add(this.container.getItem(1));
      stacks.add(this.container.getItem(2));
      return stacks;
   }

   public int getResultSlotIndex() {
      return 3;
   }

   public int getGridWidth() {
      return 1;
   }

   public int getGridHeight() {
      return 1;
   }

   public int getSize() {
      return 4;
   }

   public RecipeBookType getRecipeBookType() {
      return DARecipeBookTypes.COMBINER;
   }

   public boolean shouldMoveToInventory(int slotIndex) {
      return slotIndex != 3;
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemStack1 = slot.getItem();
         itemStack = itemStack1.copy();
         if (index == 3) {
            if (!this.moveItemStackTo(itemStack1, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack1, itemStack);
         } else if (index > 3) {
            if (!this.moveItemStackTo(itemStack1, 0, 3, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemStack1, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemStack1.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemStack1);
      }

      return itemStack;
   }
}
