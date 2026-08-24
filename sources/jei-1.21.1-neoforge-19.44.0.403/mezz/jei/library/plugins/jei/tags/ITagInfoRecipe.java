package mezz.jei.library.plugins.jei.tags;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.tags.TagKey;

public interface ITagInfoRecipe {
   TagKey<?> getTag();

   List<ITypedIngredient<?>> getTypedIngredients();
}
