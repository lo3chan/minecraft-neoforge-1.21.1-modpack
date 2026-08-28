/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.library.ingredients.itemStacks;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.time.Duration;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.ingredients.itemStacks.FullTypedItemStack;
import mezz.jei.library.ingredients.itemStacks.NormalizedTypedItem;
import mezz.jei.library.ingredients.itemStacks.NormalizedTypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public abstract class TypedItemStack
implements ITypedIngredient<ItemStack> {
    private static final LoadingCache<TypedItemStack, ItemStack> CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(1L)).concurrencyLevel(1).build((CacheLoader)new CacheLoader<TypedItemStack, ItemStack>(){

        public ItemStack load(TypedItemStack key) {
            return key.createItemStackUncached();
        }
    });

    public static ITypedIngredient<ItemStack> create(ItemStack ingredient) {
        if (ingredient.getCount() == 1) {
            return NormalizedTypedItemStack.create((Holder<Item>)ingredient.getItemHolder(), ingredient.getComponentsPatch());
        }
        return new FullTypedItemStack((Holder<Item>)ingredient.getItemHolder(), ingredient.getComponentsPatch(), ingredient.getCount());
    }

    public static ITypedIngredient<ItemStack> create(ItemLike itemLike) {
        Item item = itemLike.asItem();
        Holder.Reference itemHolder = item.builtInRegistryHolder();
        return new NormalizedTypedItem((Holder<Item>)itemHolder);
    }

    public static ITypedIngredient<ItemStack> normalize(ITypedIngredient<ItemStack> typedIngredient) {
        if (typedIngredient instanceof TypedItemStack) {
            TypedItemStack typedItemStack = (TypedItemStack)typedIngredient;
            return typedItemStack.getNormalized();
        }
        ItemStack itemStack = typedIngredient.getIngredient();
        return NormalizedTypedItemStack.create((Holder<Item>)itemStack.getItemHolder(), itemStack.getComponentsPatch());
    }

    @Override
    public final ItemStack getIngredient() {
        return (ItemStack)CACHE.getUnchecked((Object)this);
    }

    @Override
    public final Optional<ItemStack> getItemStack() {
        return Optional.of(this.getIngredient());
    }

    @Override
    public final <B> B getBaseIngredient(IIngredientTypeWithSubtypes<B, ItemStack> ingredientType) {
        Item item = this.getItem();
        Class<B> ingredientBaseClass = ingredientType.getIngredientBaseClass();
        return ingredientBaseClass.cast(item);
    }

    @Override
    public final IIngredientType<ItemStack> getType() {
        return VanillaTypes.ITEM_STACK;
    }

    protected abstract Item getItem();

    protected abstract TypedItemStack getNormalized();

    protected abstract ItemStack createItemStackUncached();
}

