/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.plugins.vanilla.compostable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.plugins.vanilla.compostable.CompostingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CompostingRecipeMaker {
    public static List<IJeiCompostingRecipe> getRecipes(IIngredientManager ingredientManager) {
        Collection<ItemStack> allIngredients = ingredientManager.getAllItemStacks();
        IIngredientHelper ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        IPlatformIngredientHelper platformIngredientHelper = Services.PLATFORM.getIngredientHelper();
        return allIngredients.stream().mapMulti((itemStack, consumer) -> {
            float compostValue = platformIngredientHelper.getCompostValue((ItemStack)itemStack);
            if (compostValue > 0.0f) {
                ResourceLocation resourceLocation = ingredientHelper.getResourceLocation(itemStack);
                String ingredientUidPath = resourceLocation.getPath();
                ResourceLocation recipeUid = ResourceLocation.fromNamespaceAndPath((String)"jei", (String)ingredientUidPath);
                CompostingRecipe recipe = new CompostingRecipe((ItemStack)itemStack, compostValue, recipeUid);
                consumer.accept(recipe);
            }
        }).sorted(Comparator.comparingDouble(IJeiCompostingRecipe::getChance)).toList();
    }
}

