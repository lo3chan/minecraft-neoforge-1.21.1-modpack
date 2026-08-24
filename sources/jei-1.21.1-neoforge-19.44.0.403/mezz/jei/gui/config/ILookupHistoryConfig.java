package mezz.jei.gui.config;

import com.mojang.serialization.Codec;
import java.util.List;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.bookmarks.IBookmark;
import net.minecraft.core.RegistryAccess;

public interface ILookupHistoryConfig {
   void save(IRecipeManager var1, IIngredientManager var2, RegistryAccess var3, ICodecHelper var4, List<IBookmark> var5, Codec<IBookmark> var6);

   List<IBookmark> load(IRecipeManager var1, IIngredientManager var2, RegistryAccess var3, ICodecHelper var4, Codec<IBookmark> var5);
}
