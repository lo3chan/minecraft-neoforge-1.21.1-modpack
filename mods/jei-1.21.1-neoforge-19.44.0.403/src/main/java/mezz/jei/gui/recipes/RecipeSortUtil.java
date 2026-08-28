/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package mezz.jei.gui.recipes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RecipeSortUtil {
    private static final Comparator<IRecipeLayoutWithButtons<?>> COMPARATOR = RecipeSortUtil.createComparator();

    public static List<IRecipeCategory<?>> sortRecipeCategories(List<IRecipeCategory<?>> recipeCategories, IRecipeTransferManager recipeTransferManager) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return recipeCategories;
        }
        AbstractContainerMenu openContainer = player.containerMenu;
        if (openContainer == null) {
            return recipeCategories;
        }
        Comparator<IRecipeCategory> comparator = Comparator.comparing(r -> {
            Optional recipeTransferHandler = recipeTransferManager.getRecipeTransferHandler(openContainer, r);
            return recipeTransferHandler.isPresent();
        }).reversed();
        return recipeCategories.stream().sorted(comparator).toList();
    }

    public static Comparator<IRecipeLayoutWithButtons<?>> getComparator() {
        return COMPARATOR;
    }

    private static Comparator<IRecipeLayoutWithButtons<?>> createComparator() {
        return Comparator.comparingInt(r -> {
            IRecipeLayoutDrawable recipeLayout = r.getRecipeLayout();
            int missingCount = r.getMissingCountHint();
            if (missingCount == -1) {
                return 0;
            }
            IRecipeSlotsView recipeSlotsView = recipeLayout.getRecipeSlotsView();
            int ingredientCount = RecipeSortUtil.inputCount(recipeSlotsView);
            if (ingredientCount == 0) {
                return 0;
            }
            int matchCount = ingredientCount - missingCount;
            int matchPercent = 100 * matchCount / ingredientCount;
            return -matchPercent;
        });
    }

    private static int inputCount(IRecipeSlotsView recipeSlotsView) {
        int count = 0;
        for (IRecipeSlotView i : recipeSlotsView.getSlotViews()) {
            if (i.getRole() != RecipeIngredientRole.INPUT || i.isEmpty()) continue;
            ++count;
        }
        return count;
    }
}

