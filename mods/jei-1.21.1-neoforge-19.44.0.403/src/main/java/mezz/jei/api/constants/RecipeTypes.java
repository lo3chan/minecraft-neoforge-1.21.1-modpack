/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.BlastingRecipe
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SmeltingRecipe
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 */
package mezz.jei.api.constants;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import mezz.jei.api.recipe.vanilla.IJeiFuelingRecipe;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import mezz.jei.api.recipe.vanilla.IJeiIngredientInfoRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public final class RecipeTypes {
    public static final RecipeType<RecipeHolder<CraftingRecipe>> CRAFTING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
    public static final RecipeType<RecipeHolder<StonecutterRecipe>> STONECUTTING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.STONECUTTING);
    public static final RecipeType<RecipeHolder<SmeltingRecipe>> SMELTING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.SMELTING);
    public static final RecipeType<RecipeHolder<SmokingRecipe>> SMOKING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.SMOKING);
    public static final RecipeType<RecipeHolder<BlastingRecipe>> BLASTING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.BLASTING);
    public static final RecipeType<RecipeHolder<CampfireCookingRecipe>> CAMPFIRE_COOKING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING);
    public static final RecipeType<IJeiFuelingRecipe> FUELING = RecipeType.create("minecraft", "fuel", IJeiFuelingRecipe.class);
    public static final RecipeType<IJeiBrewingRecipe> BREWING = RecipeType.create("minecraft", "brewing", IJeiBrewingRecipe.class);
    public static final RecipeType<IJeiAnvilRecipe> ANVIL = RecipeType.create("minecraft", "anvil", IJeiAnvilRecipe.class);
    public static final RecipeType<IJeiGrindstoneRecipe> GRINDSTONE = RecipeType.create("minecraft", "grindstone", IJeiGrindstoneRecipe.class);
    public static final RecipeType<RecipeHolder<SmithingRecipe>> SMITHING = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.SMITHING);
    public static final RecipeType<IJeiCompostingRecipe> COMPOSTING = RecipeType.create("minecraft", "compostable", IJeiCompostingRecipe.class);
    public static final RecipeType<IJeiIngredientInfoRecipe> INFORMATION = RecipeType.create("jei", "information", IJeiIngredientInfoRecipe.class);

    private RecipeTypes() {
    }
}

