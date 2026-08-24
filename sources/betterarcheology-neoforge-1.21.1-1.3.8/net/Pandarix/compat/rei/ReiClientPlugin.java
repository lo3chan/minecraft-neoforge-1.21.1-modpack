package net.Pandarix.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.Pandarix.block.ModBlocks;
import net.Pandarix.recipe.IdentifyingRecipe;
import net.Pandarix.screen.IdentifyingScreen;
import net.minecraft.world.level.block.Block;

public class ReiClientPlugin implements REIClientPlugin {
   public String getPluginProviderName() {
      return "Better ArcheologyClient";
   }

   public void registerCategories(CategoryRegistry registry) {
      registry.add(new REIIdentifyingCategory());
      registry.addWorkstations(IdentifyingDisplay.CATEGORY, new EntryStack[]{EntryStacks.of(((Block)ModBlocks.ARCHEOLOGY_TABLE.get()).asItem())});
   }

   public void registerDisplays(DisplayRegistry registry) {
      registry.registerRecipeFiller(IdentifyingRecipe.class, IdentifyingRecipe.Type.INSTANCE, IdentifyingDisplay::new);
   }

   public void registerScreens(ScreenRegistry registry) {
      registry.registerContainerClickArea(new Rectangle(51, 48, 74, 24), IdentifyingScreen.class, new CategoryIdentifier[]{IdentifyingDisplay.CATEGORY});
   }
}
