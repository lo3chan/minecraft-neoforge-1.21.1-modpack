package jeresources.jei.villager;

import jeresources.entry.AbstractVillagerEntry;
import jeresources.reference.Resources;
import jeresources.util.Font;
import jeresources.util.RenderHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VillagerWrapper implements IRecipeCategoryExtension<AbstractVillagerEntry> {
   private IFocus<ItemStack> focus;

   public void setFocus(IFocus<ItemStack> focus) {
      this.focus = focus;
   }

   public void drawInfo(AbstractVillagerEntry recipe, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      AbstractVillager villager = recipe.getVillagerEntity();
      RenderHelper.renderEntity(guiGraphics, 37, 118, 36.0, 38.0 - mouseX, 80.0 - mouseY, villager);
      int y = 22 * (6 - recipe.getPossibleLevels(this.focus).size()) / 2;

      for (int i = 0; i < recipe.getPossibleLevels(this.focus).size(); i++) {
         RenderHelper.drawTexture(guiGraphics, 130, y + i * 22, 0, 120, 20, 20, Resources.Gui.Jei.VILLAGER.getResource());
         RenderHelper.drawTexture(guiGraphics, 95, y + i * 22, 22, 120, 18, 18, Resources.Gui.Jei.VILLAGER.getResource());
         RenderHelper.drawTexture(guiGraphics, 113, y + i * 22, 22, 120, 18, 18, Resources.Gui.Jei.VILLAGER.getResource());
         RenderHelper.drawTexture(guiGraphics, 150, y + i * 22, 22, 120, 18, 18, Resources.Gui.Jei.VILLAGER.getResource());
      }

      if (recipe.hasLevels()) {
         int var15 = 0;

         for (Object oLevel : recipe.getPossibleLevels(this.focus)) {
            Integer level = (Integer)oLevel;
            Font.normal.print(guiGraphics, "lv. " + (level + 1), 72, y + var15++ * 22 + 6);
         }
      }

      Font.normal.print(guiGraphics, TranslationHelper.translateAndFormat(recipe.getDisplayName()), 5, 5);
      if (recipe.hasPois()) {
         Font.normal.splitPrint(guiGraphics, TranslationHelper.translateAndFormat("jer.villager.poi"), 5, 18, 45);
         RenderHelper.drawTexture(guiGraphics, 49, 18, 22, 120, 18, 18, Resources.Gui.Jei.VILLAGER.getResource());
      }
   }
}
