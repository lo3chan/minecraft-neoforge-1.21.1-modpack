package mezz.jei.gui.recipes;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IBookmark;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class RecipeBookmarkButtonController implements IIconButtonController {
   private final BookmarkList bookmarks;
   @Nullable
   private final IBookmark recipeBookmark;
   private boolean bookmarked;

   public RecipeBookmarkButtonController(BookmarkList bookmarks, @Nullable IBookmark recipeBookmark) {
      this.bookmarks = bookmarks;
      this.recipeBookmark = recipeBookmark;
   }

   @Override
   public void getTooltips(ITooltipBuilder tooltip) {
      if (this.recipeBookmark != null) {
         if (this.bookmarks.contains(this.recipeBookmark)) {
            tooltip.add(Component.translatable("jei.tooltip.bookmarks.recipe.remove"));
         } else {
            tooltip.add(Component.translatable("jei.tooltip.bookmarks.recipe.add"));
         }
      }
   }

   @Override
   public void initState(IButtonState state) {
      Textures textures = Internal.getTextures();
      state.setIcon(textures.getRecipeBookmark());
      if (this.recipeBookmark == null) {
         state.setActive(false);
         state.setVisible(false);
      }

      this.updateState(state);
   }

   @Override
   public void updateState(IButtonState state) {
      this.bookmarked = this.recipeBookmark != null && this.bookmarks.contains(this.recipeBookmark);
      state.setForcePressed(this.bookmarked);
   }

   @Override
   public boolean onPress(IJeiUserInput input) {
      if (this.recipeBookmark != null) {
         if (!input.isSimulate()) {
            this.bookmarks.toggleBookmark(this.recipeBookmark);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void drawExtras(GuiGraphics guiGraphics, Rect2i buttonArea, int mouseX, int mouseY, float partialTicks) {
      if (this.bookmarked) {
         guiGraphics.fill(
            RenderType.gui(),
            buttonArea.getX(),
            buttonArea.getY(),
            buttonArea.getX() + buttonArea.getWidth(),
            buttonArea.getY() + buttonArea.getHeight(),
            285277952
         );
      }
   }

   public boolean isBookmarked() {
      return this.bookmarked;
   }
}
