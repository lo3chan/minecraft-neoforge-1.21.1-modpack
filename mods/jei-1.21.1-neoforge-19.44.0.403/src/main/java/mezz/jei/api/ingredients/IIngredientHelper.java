/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.ingredients;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.constants.Tags;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IIngredientHelper<V> {
    public IIngredientType<V> getIngredientType();

    public String getDisplayName(V var1);

    @Deprecated(since="19.9.0", forRemoval=true)
    public String getUniqueId(V var1, UidContext var2);

    default public Object getUid(V ingredient, UidContext context) {
        return this.getUniqueId(ingredient, context);
    }

    default public Object getUid(ITypedIngredient<V> typedIngredient, UidContext context) {
        return this.getUid(typedIngredient.getIngredient(), context);
    }

    default public Object getGroupingUid(V ingredient) {
        return this.getWildcardId(ingredient);
    }

    default public Object getGroupingUid(ITypedIngredient<V> typedIngredient) {
        return this.getGroupingUid(typedIngredient.getIngredient());
    }

    default public boolean hasSubtypes(V ingredient) {
        return this.getIngredientType() instanceof IIngredientTypeWithSubtypes;
    }

    @Deprecated(since="19.13.0", forRemoval=true)
    default public String getWildcardId(V ingredient) {
        return this.getUniqueId(ingredient, UidContext.Ingredient);
    }

    default public String getDisplayModId(V ingredient) {
        return this.getResourceLocation(ingredient).getNamespace();
    }

    default public long getAmount(V ingredient) {
        return -1L;
    }

    default public V copyWithAmount(V ingredient, long amount) {
        return this.copyIngredient(ingredient);
    }

    default public Iterable<Integer> getColors(V ingredient) {
        return Collections.emptyList();
    }

    public ResourceLocation getResourceLocation(V var1);

    default public ItemStack getCheatItemStack(V ingredient) {
        return ItemStack.EMPTY;
    }

    public V copyIngredient(V var1);

    default public V normalizeIngredient(V ingredient) {
        return ingredient;
    }

    default public boolean isValidIngredient(V ingredient) {
        return true;
    }

    default public boolean isIngredientOnServer(V ingredient) {
        return true;
    }

    default public Stream<ResourceLocation> getTagStream(V ingredient) {
        return Stream.empty();
    }

    default public boolean isHiddenFromRecipeViewersByTags(V ingredient) {
        return this.getTagStream(ingredient).anyMatch(arg_0 -> ((ResourceLocation)Tags.HIDDEN_FROM_RECIPE_VIEWERS).equals(arg_0));
    }

    default public boolean isHiddenFromRecipeViewersByTags(ITypedIngredient<V> ingredient) {
        return this.isHiddenFromRecipeViewersByTags(ingredient.getIngredient());
    }

    public String getErrorInfo(@Nullable V var1);

    default public Optional<TagKey<?>> getTagKeyEquivalent(Collection<V> ingredients) {
        return Optional.empty();
    }

    @Deprecated(since="19.5.5", forRemoval=true)
    default public Optional<ResourceLocation> getTagEquivalent(Collection<V> ingredients) {
        return this.getTagKeyEquivalent(ingredients).map(TagKey::location);
    }
}

