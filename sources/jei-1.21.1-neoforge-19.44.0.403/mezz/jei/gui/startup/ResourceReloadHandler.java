package mezz.jei.gui.startup;

import mezz.jei.common.util.LoggedTimer;
import mezz.jei.gui.ingredients.IngredientFilter;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadHandler implements ResourceManagerReloadListener {
   private final IngredientListOverlay ingredientListOverlay;
   private final IngredientFilter ingredientFilter;

   public ResourceReloadHandler(IngredientListOverlay ingredientListOverlay, IngredientFilter ingredientFilter) {
      this.ingredientListOverlay = ingredientListOverlay;
      this.ingredientFilter = ingredientFilter;
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      LoggedTimer timer = new LoggedTimer();
      timer.start("Rebuilding ingredient filter");
      this.ingredientFilter.rebuildItemFilter();
      timer.stop();
      Minecraft minecraft = Minecraft.getInstance();
      this.ingredientListOverlay.getScreenPropertiesUpdater().updateScreen(minecraft.screen).forceUpdate();
   }
}
