package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.inventory.AetherRecipeBookTypes;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class FreezerMenu extends AbstractAetherFurnaceMenu {
   public FreezerMenu(int containerId, Inventory playerInventory) {
      super(
         (MenuType<?>)AetherMenuTypes.FREEZER.get(),
         (RecipeType<? extends AbstractCookingRecipe>)AetherRecipeTypes.FREEZING.get(),
         AetherRecipeBookTypes.FREEZER,
         containerId,
         playerInventory
      );
   }

   public FreezerMenu(int containerId, Inventory playerInventory, Container freezerContainer, ContainerData data) {
      super(
         (MenuType<?>)AetherMenuTypes.FREEZER.get(),
         (RecipeType<? extends AbstractCookingRecipe>)AetherRecipeTypes.FREEZING.get(),
         AetherRecipeBookTypes.FREEZER,
         containerId,
         playerInventory,
         freezerContainer,
         data
      );
   }

   @Override
   public boolean isFuel(ItemStack stack) {
      return stack.getItemHolder().getData(AetherDataMaps.FREEZER_FUEL) != null;
   }
}
