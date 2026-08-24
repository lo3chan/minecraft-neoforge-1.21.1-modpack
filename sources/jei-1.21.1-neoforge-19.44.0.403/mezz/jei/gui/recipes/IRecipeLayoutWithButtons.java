package mezz.jei.gui.recipes;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.input.IUserInputHandler;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public interface IRecipeLayoutWithButtons<R> {
   void draw(GuiGraphics var1, int var2, int var3, float var4);

   void updateBounds(int var1, int var2);

   int totalWidth();

   IUserInputHandler createUserInputHandler();

   void tick();

   IRecipeLayoutDrawable<R> getRecipeLayout();

   @Nullable
   RecipeBookmark<?, ?> getRecipeBookmark();

   void drawTooltips(GuiGraphics var1, int var2, int var3);

   int getMissingCountHint();
}
