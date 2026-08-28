/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 */
package mezz.jei.library.plugins.jei.tags;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.tags.TagKey;

public interface ITagInfoRecipe {
    public TagKey<?> getTag();

    public List<ITypedIngredient<?>> getTypedIngredients();
}

