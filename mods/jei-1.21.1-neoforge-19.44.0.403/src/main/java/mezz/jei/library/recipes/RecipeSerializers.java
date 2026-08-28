/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package mezz.jei.library.recipes;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSerializers {
    @Nullable
    private static RecipeSerializers INSTANCE;
    private final Supplier<RecipeSerializer<?>> jeiShapedRecipeSerializer;

    public static void register(Supplier<RecipeSerializer<?>> jeiShapedRecipeSerializer) {
        INSTANCE = new RecipeSerializers(jeiShapedRecipeSerializer);
    }

    private RecipeSerializers(Supplier<RecipeSerializer<?>> jeiShapedRecipeSerializer) {
        this.jeiShapedRecipeSerializer = jeiShapedRecipeSerializer;
    }

    public static RecipeSerializer<?> getJeiShapedRecipeSerializer() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Recipe serializer not yet initialized");
        }
        return RecipeSerializers.INSTANCE.jeiShapedRecipeSerializer.get();
    }
}

