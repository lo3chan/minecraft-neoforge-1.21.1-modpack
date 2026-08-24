package net.cibernet.alchemancy.modSupport.patchouli;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

public abstract class IngredientRingComponentBase implements ICustomComponent {
   transient List<Ingredient> ingredients;
   protected transient int x;
   protected transient int y;
   protected static final int RADIUS = 24;

   public void build(int componentX, int componentY, int pageNum) {
      this.x = componentX;
      this.y = componentY;
   }

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      int i = 0;
      float totalSize = this.ingredients.size();

      for (Ingredient infusable : this.ingredients) {
         context.renderIngredient(
            graphics,
            this.x + 24 - (int)(24.0F * Mth.sin(3.1415927F + 6.2831855F * (i / totalSize))),
            this.y + 24 + (int)(24.0F * Mth.cos(3.1415927F + 6.2831855F * (i / totalSize))),
            mouseX,
            mouseY,
            infusable
         );
         i++;
      }
   }
}
