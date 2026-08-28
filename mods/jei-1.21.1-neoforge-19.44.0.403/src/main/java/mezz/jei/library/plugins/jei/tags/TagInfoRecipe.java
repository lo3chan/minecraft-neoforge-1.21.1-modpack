/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 */
package mezz.jei.library.plugins.jei.tags;

import java.util.Collections;
import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.plugins.jei.tags.ITagInfoRecipe;
import net.minecraft.tags.TagKey;

public class TagInfoRecipe<B, I>
implements ITagInfoRecipe {
    private final TagKey<B> tag;
    private final List<ITypedIngredient<I>> ingredients;

    public TagInfoRecipe(TagKey<B> tag, List<ITypedIngredient<I>> ingredients) {
        this.tag = tag;
        this.ingredients = ingredients;
    }

    public TagKey<B> getTag() {
        return this.tag;
    }

    @Override
    public List<ITypedIngredient<?>> getTypedIngredients() {
        return Collections.unmodifiableList(this.ingredients);
    }
}

