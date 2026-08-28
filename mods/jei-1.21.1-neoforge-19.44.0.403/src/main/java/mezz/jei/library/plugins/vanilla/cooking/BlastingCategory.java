/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.BlastingRecipe
 *  net.minecraft.world.level.block.Blocks
 */
package mezz.jei.library.plugins.vanilla.cooking;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.library.plugins.vanilla.cooking.AbstractCookingCategory;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.level.block.Blocks;

public class BlastingCategory
extends AbstractCookingCategory<BlastingRecipe> {
    public BlastingCategory(IGuiHelper guiHelper) {
        super(guiHelper, RecipeTypes.BLASTING, Blocks.BLAST_FURNACE, "gui.jei.category.blasting", 100);
    }
}

