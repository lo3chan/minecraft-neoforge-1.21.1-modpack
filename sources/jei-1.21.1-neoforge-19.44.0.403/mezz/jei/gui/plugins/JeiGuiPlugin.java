package mezz.jei.gui.plugins;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiFeatures;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JeiGuiPlugin implements IModPlugin {
   @Nullable
   private IJeiFeatures jeiFeatures;

   @Override
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("jei", "gui");
   }

   @Override
   public void configureJei(IJeiFeatures jeiFeatures) {
      this.jeiFeatures = jeiFeatures;
   }

   @Override
   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      if (this.isJeiGuiEnabled()) {
         IIngredientManager ingredientManager = registration.getJeiHelpers().getIngredientManager();
         registration.addGuiScreenHandler(AbstractContainerScreen.class, GuiProperties::create);
         registration.addGuiScreenHandler(ChatScreen.class, new ChatScreenHandler(ingredientManager));
         registration.addGuiScreenHandler(RecipesGui.class, RecipesGui::getProperties);
      }
   }

   private boolean isJeiGuiEnabled() {
      IJeiFeatures jeiFeatures = this.jeiFeatures;
      return jeiFeatures == null || jeiFeatures.isJeiGuiEnabled();
   }
}
