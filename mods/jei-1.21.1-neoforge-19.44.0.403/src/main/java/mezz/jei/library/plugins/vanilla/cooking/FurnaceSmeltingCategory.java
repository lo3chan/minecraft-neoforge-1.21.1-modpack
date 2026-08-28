/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.SmeltingRecipe
 *  net.minecraft.world.level.block.Blocks
 */
package mezz.jei.library.plugins.vanilla.cooking;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.library.plugins.vanilla.cooking.AbstractCookingCategory;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Blocks;

public class FurnaceSmeltingCategory
extends AbstractCookingCategory<SmeltingRecipe> {
    public FurnaceSmeltingCategory(IGuiHelper guiHelper) {
        super(guiHelper, RecipeTypes.SMELTING, Blocks.FURNACE, "gui.jei.category.smelting", 200);
    }
}

