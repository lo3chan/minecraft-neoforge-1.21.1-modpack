package com.alonie.brbe.render;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.generic.GenericGhostRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class GhostRecipeRenderer {
   public void render(
      GenericGhostRecipe ghostRecipe, GuiGraphics gui, Minecraft minecraft, int x, int y, boolean wide, float partialTick, BRBBookCategories.Category category
   ) {
      if (ghostRecipe != null) {
         ghostRecipe.render(gui, minecraft, x, y, wide, partialTick, category);
      }
   }

   public void renderTooltip(GenericGhostRecipe ghostRecipe, GuiGraphics gui, int x, int y, int mouseX, int mouseY) {
      if (ghostRecipe != null) {
         ghostRecipe.drawTooltip(gui, x, y, mouseX, mouseY);
      }
   }
}
