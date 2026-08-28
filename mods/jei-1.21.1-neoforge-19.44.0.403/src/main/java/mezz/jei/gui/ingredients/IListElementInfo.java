/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.ingredients;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.gui.ingredients.IListElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Unmodifiable;

public interface IListElementInfo<V> {
    public List<String> getNames();

    public String getModNameForSorting();

    public Collection<String> getModNames(IIngredientFilterConfig var1);

    public @Unmodifiable Set<String> getTooltipStrings(IIngredientFilterConfig var1, IIngredientManager var2);

    public Collection<String> getTagStrings(IIngredientManager var1);

    public Stream<ResourceLocation> getTagIds(IIngredientManager var1);

    public Iterable<Integer> getColors(IIngredientManager var1);

    public @Unmodifiable Collection<String> getColorNames(IIngredientManager var1, IColorHelper var2);

    public @Unmodifiable Collection<String> getCreativeTabsStrings(IIngredientManager var1);

    public ResourceLocation getResourceLocation();

    public IListElement<V> getElement();

    public ITypedIngredient<V> getTypedIngredient();

    public int getCreatedIndex();
}

