package jeresources.jei.dungeon;

import jeresources.entry.DungeonEntry;
import jeresources.registry.DungeonRegistry;
import jeresources.util.Font;
import jeresources.util.RenderHelper;
import jeresources.util.TranslationHelper;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class DungeonWrapper implements IRecipeCategoryExtension<DungeonEntry> {
   private boolean done;
   private int lidStart;

   public void drawInfo(DungeonEntry entry, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      RenderHelper.renderChest(guiGraphics, 15.0F, 20.0F, -40.0F, 20.0F, this.getLidAngle());
      Font.normal.print(guiGraphics, TranslationHelper.translateAndFormat(entry.getName()), 60, 7);
      Font.small.print(guiGraphics, DungeonRegistry.getInstance().getNumStacks(entry), 60, 20);
   }

   private float getLidAngle() {
      float angle = ((int)System.currentTimeMillis() / 100 - this.lidStart) % 80;
      if (angle > 50.0F || this.done) {
         this.done = true;
         angle = 50.0F;
      }

      return angle;
   }

   public void resetLid() {
      this.lidStart = (int)System.currentTimeMillis() / 100;
   }
}
