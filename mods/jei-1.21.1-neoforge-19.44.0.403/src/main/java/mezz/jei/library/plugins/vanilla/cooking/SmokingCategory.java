/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.level.block.Blocks
 */
package mezz.jei.library.plugins.vanilla.cooking;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.library.plugins.vanilla.cooking.AbstractCookingCategory;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.Blocks;

public class SmokingCategory
extends AbstractCookingCategory<SmokingRecipe> {
    public SmokingCategory(IGuiHelper guiHelper) {
        super(guiHelper, RecipeTypes.SMOKING, Blocks.SMOKER, "gui.jei.category.smoking", 100);
    }
}

