package mezz.jei.gui.config;

import com.mojang.serialization.Codec;
import java.util.List;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.bookmarks.BookmarkFactory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.IBookmark;
import net.minecraft.core.RegistryAccess;

public interface IBookmarkConfig {
   boolean saveBookmarks(
      IRecipeManager var1,
      IFocusFactory var2,
      IGuiHelper var3,
      IIngredientManager var4,
      RegistryAccess var5,
      ICodecHelper var6,
      List<IBookmark> var7,
      Codec<IBookmark> var8
   );

   void loadBookmarks(
      IRecipeManager var1,
      IFocusFactory var2,
      IGuiHelper var3,
      IIngredientManager var4,
      RegistryAccess var5,
      BookmarkList var6,
      ICodecHelper var7,
      Codec<IBookmark> var8,
      BookmarkFactory var9
   );
}
