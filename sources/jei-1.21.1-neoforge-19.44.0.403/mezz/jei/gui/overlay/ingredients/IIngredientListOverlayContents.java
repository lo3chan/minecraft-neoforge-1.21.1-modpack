package mezz.jei.gui.overlay.ingredients;

import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public interface IIngredientListOverlayContents extends IIngredientGridView, IIngredientGridPageNavigation, IRecipeFocusSource {
   boolean isEmpty();

   void drawBackground(GuiGraphics var1);

   void drawForeground(Minecraft var1, GuiGraphics var2, int var3, int var4, float var5);

   void drawTooltips(Minecraft var1, GuiGraphics var2, int var3, int var4);

   void drawOnForeground(GuiGraphics var1, int var2, int var3);

   void tick();

   IUserInputHandler createInputHandler();

   IDragHandler createDragHandler();

   <T> Stream<T> getVisibleIngredients(IIngredientType<T> var1);
}
