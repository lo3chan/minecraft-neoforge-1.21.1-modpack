package fuzs.visualworkbench.integration.jei;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

@JeiPlugin
public class VisualWorkbenchJEIPlugin implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return VisualWorkbench.id("crafting");
   }

   public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
      registration.addRecipeTransferHandler(VisualCraftingMenu.class, (MenuType)ModRegistry.CRAFTING_MENU_TYPE.value(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
   }
}
