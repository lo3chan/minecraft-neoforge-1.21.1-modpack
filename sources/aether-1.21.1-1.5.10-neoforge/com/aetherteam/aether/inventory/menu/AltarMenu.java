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

public class AltarMenu extends AbstractAetherFurnaceMenu {
   public AltarMenu(int containerId, Inventory playerInventory) {
      super(
         (MenuType<?>)AetherMenuTypes.ALTAR.get(),
         (RecipeType<? extends AbstractCookingRecipe>)AetherRecipeTypes.ENCHANTING.get(),
         AetherRecipeBookTypes.ALTAR,
         containerId,
         playerInventory
      );
   }

   public AltarMenu(int containerId, Inventory playerInventory, Container altarContainer, ContainerData data) {
      super(
         (MenuType<?>)AetherMenuTypes.ALTAR.get(),
         (RecipeType<? extends AbstractCookingRecipe>)AetherRecipeTypes.ENCHANTING.get(),
         AetherRecipeBookTypes.ALTAR,
         containerId,
         playerInventory,
         altarContainer,
         data
      );
   }

   @Override
   public boolean isFuel(ItemStack stack) {
      return stack.getItemHolder().getData(AetherDataMaps.ALTAR_FUEL) != null;
   }
}
