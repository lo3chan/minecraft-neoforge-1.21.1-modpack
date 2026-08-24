package com.aetherteam.aether.integration.jei.categories.fuel;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.FuelRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public final class AetherFuelRecipeMaker {
   private AetherFuelRecipeMaker() {
   }

   public static List<FuelRecipe> getFuelRecipes() {
      List<FuelRecipe> fuelRecipes = new ArrayList<>();
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.ALTAR_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.ALTAR.get())
            )
         );
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.FREEZER_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.FREEZER.get())
            )
         );
      BuiltInRegistries.ITEM
         .getDataMap(AetherDataMaps.INCUBATOR_FUEL)
         .forEach(
            (item, fuel) -> fuelRecipes.add(
               new FuelRecipe(List.of(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(item))), fuel.burnTime(), (Block)AetherBlocks.INCUBATOR.get())
            )
         );
      return fuelRecipes;
   }
}
