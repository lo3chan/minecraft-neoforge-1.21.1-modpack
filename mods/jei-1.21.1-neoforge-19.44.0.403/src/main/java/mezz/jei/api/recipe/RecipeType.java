/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 */
package mezz.jei.api.recipe;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeType<T> {
    private final ResourceLocation uid;
    private final Class<? extends T> recipeClass;

    public static <T> RecipeType<T> create(String nameSpace, String path, Class<? extends T> recipeClass) {
        ResourceLocation uid = ResourceLocation.fromNamespaceAndPath((String)nameSpace, (String)path);
        return new RecipeType<T>(uid, recipeClass);
    }

    public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createFromVanilla(net.minecraft.world.item.crafting.RecipeType<R> vanillaRecipeType) {
        ResourceLocation uid = BuiltInRegistries.RECIPE_TYPE.getKey(vanillaRecipeType);
        if (uid == null) {
            throw new IllegalArgumentException("Vanilla Recipe Type must be registered before using it here. %s".formatted(vanillaRecipeType));
        }
        return RecipeType.createRecipeHolderType(uid);
    }

    public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createRecipeHolderType(ResourceLocation uid) {
        Class<RecipeHolder> holderClass = RecipeHolder.class;
        return new RecipeType<RecipeHolder<R>>(uid, holderClass);
    }

    public static <R extends Recipe<?>> Supplier<RecipeType<RecipeHolder<R>>> createFromDeferredVanilla(Supplier<net.minecraft.world.item.crafting.RecipeType<R>> deferredVanillaRecipeType) {
        return Suppliers.memoize(() -> RecipeType.createFromVanilla((net.minecraft.world.item.crafting.RecipeType)deferredVanillaRecipeType.get()));
    }

    public RecipeType(ResourceLocation uid, Class<? extends T> recipeClass) {
        if (uid == null) {
            throw new NullPointerException("uid must not be null.");
        }
        if (recipeClass == null) {
            throw new NullPointerException("recipeClass must not be null.");
        }
        this.uid = uid;
        this.recipeClass = recipeClass;
    }

    public ResourceLocation getUid() {
        return this.uid;
    }

    public Class<? extends T> getRecipeClass() {
        return this.recipeClass;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RecipeType)) {
            return false;
        }
        RecipeType other = (RecipeType)obj;
        return this.recipeClass == other.recipeClass && this.uid.equals((Object)other.uid);
    }

    public int hashCode() {
        return 31 * this.uid.hashCode() + this.recipeClass.hashCode();
    }

    public String toString() {
        return "RecipeType[uid=" + String.valueOf(this.uid) + ", recipeClass=" + String.valueOf(this.recipeClass) + "]";
    }
}

