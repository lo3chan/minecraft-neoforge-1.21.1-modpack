package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.inventory.menu.FreezerMenu;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;

public class FreezerBlockEntity extends AbstractAetherFurnaceBlockEntity {
   public FreezerBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType<?>)AetherBlockEntityTypes.FREEZER.get(), pos, state, (RecipeType<? extends AbstractCookingRecipe>)AetherRecipeTypes.FREEZING.get());
   }

   protected Component getDefaultName() {
      return Component.translatable("menu.aether.freezer");
   }

   protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
      return new FreezerMenu(id, playerInventory, this, this.dataAccess);
   }

   protected int getBurnDuration(ItemStack fuelStack) {
      if (!fuelStack.isEmpty()) {
         FurnaceFuel datamap = (FurnaceFuel)fuelStack.getItemHolder().getData(AetherDataMaps.FREEZER_FUEL);
         if (datamap != null) {
            return datamap.burnTime();
         }
      }

      return 0;
   }
}
