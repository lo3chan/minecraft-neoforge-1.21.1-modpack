/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.alchemy.PotionContents
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.util;

import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.library.ingredients.IngredientSet;
import mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BrewingRecipeMakerCommon {
    private static final Logger LOGGER = LogManager.getLogger();

    public static Set<IJeiBrewingRecipe> getVanillaBrewingRecipes(IVanillaRecipeFactory recipeFactory, IIngredientManager ingredientManager, PotionBrewing potionBrewing) {
        boolean foundNewPotions;
        HashSet<IJeiBrewingRecipe> recipes = new HashSet<IJeiBrewingRecipe>();
        Registry potionRegistry = RegistryUtil.getRegistry(Registries.POTION);
        IIngredientHelper<ItemStack> itemStackHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        IngredientSet<ItemStack> knownPotions = BrewingRecipeMakerCommon.getBaseKnownPotions(ingredientManager, potionRegistry, potionBrewing);
        IPlatformIngredientHelper ingredientHelper = Services.PLATFORM.getIngredientHelper();
        IngredientSet potionReagents = ingredientHelper.getPotionIngredients(potionBrewing).flatMap(i -> Arrays.stream(i.getItems())).collect(Collectors.toCollection(() -> new IngredientSet(itemStackHelper, UidContext.Ingredient)));
        do {
            List<ItemStack> newPotions;
            foundNewPotions = !(newPotions = BrewingRecipeMakerCommon.getNewPotions(potionBrewing, recipeFactory, itemStackHelper, knownPotions, potionReagents, recipes)).isEmpty();
            knownPotions.addAll(newPotions);
        } while (foundNewPotions);
        return recipes;
    }

    private static IngredientSet<ItemStack> getBaseKnownPotions(IIngredientManager ingredientManager, Registry<Potion> potionRegistry, PotionBrewing potionBrewing) {
        IPlatformIngredientHelper ingredientHelper = Services.PLATFORM.getIngredientHelper();
        IIngredientHelper itemStackHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        IngredientSet potionContainers = ingredientHelper.getPotionContainers(potionBrewing).stream().flatMap(potionItem -> Arrays.stream(potionItem.getItems())).collect(Collectors.toCollection(() -> new IngredientSet(itemStackHelper, UidContext.Ingredient)));
        IngredientSet<ItemStack> knownPotions = new IngredientSet<ItemStack>(itemStackHelper, UidContext.Ingredient);
        knownPotions.addAll(potionContainers);
        potionRegistry.holders().forEach(potion -> {
            for (ItemStack potionContainer : potionContainers) {
                ItemStack result = PotionContents.createItemStack((Item)potionContainer.getItem(), (Holder)potion);
                knownPotions.add(result);
            }
        });
        return knownPotions;
    }

    private static List<ItemStack> getNewPotions(PotionBrewing potionBrewing, IVanillaRecipeFactory recipeFactory, IIngredientHelper<ItemStack> itemStackHelper, Collection<ItemStack> knownPotions, Collection<ItemStack> potionReagents, Collection<IJeiBrewingRecipe> recipes) {
        ArrayList<ItemStack> newPotions = new ArrayList<ItemStack>();
        for (ItemStack potionInput : knownPotions) {
            Object inputId = itemStackHelper.getUid(potionInput, UidContext.Recipe);
            String inputPathId = PotionSubtypeInterpreter.INSTANCE.getStringName(potionInput);
            for (ItemStack potionReagent : potionReagents) {
                Object outputId;
                Optional potionOutputType;
                ItemStack potionInputCopy = potionInput.copy();
                ItemStack potionOutput = BrewingRecipeMakerCommon.getOutput(potionBrewing, potionInputCopy, potionReagent);
                if (potionOutput.isEmpty() || potionInput.getItem() instanceof PotionItem && potionOutput.getItem() instanceof PotionItem && (potionOutputType = ((PotionContents)potionOutput.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY)).potion()).isEmpty() || Objects.equals(inputId, outputId = itemStackHelper.getUid(potionOutput, UidContext.Recipe))) continue;
                ResourceLocation outputResourceLocation = itemStackHelper.getResourceLocation(potionOutput);
                String outputPathId = PotionSubtypeInterpreter.INSTANCE.getStringName(potionOutput);
                String outputModId = outputResourceLocation.getNamespace();
                String uidPath = ResourceLocationUtil.sanitizePath(inputPathId + ".to." + outputPathId);
                IJeiBrewingRecipe recipe = recipeFactory.createBrewingRecipe(List.of(potionReagent), potionInputCopy, potionOutput, ResourceLocation.fromNamespaceAndPath((String)outputModId, (String)uidPath));
                IJeiBrewingRecipe existingRecipe = recipes.stream().filter((Predicate<IJeiBrewingRecipe>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, equals(java.lang.Object ), (Lmezz/jei/api/recipe/vanilla/IJeiBrewingRecipe;)Z)((IJeiBrewingRecipe)recipe)).findFirst().orElse(null);
                if (existingRecipe == null) {
                    recipes.add(recipe);
                    newPotions.add(potionOutput);
                    continue;
                }
                IngredientSet<ItemStack> reagents = new IngredientSet<ItemStack>(itemStackHelper, UidContext.Recipe);
                reagents.addAll(existingRecipe.getIngredients());
                reagents.add(potionReagent);
                if (reagents.size() == existingRecipe.getIngredients().size()) continue;
                IJeiBrewingRecipe replacementRecipe = recipeFactory.createBrewingRecipe(List.copyOf(reagents), existingRecipe.getPotionInputs(), existingRecipe.getPotionOutput(), existingRecipe.getUid());
                recipes.remove(existingRecipe);
                recipes.add(replacementRecipe);
            }
        }
        return newPotions;
    }

    private static ItemStack getOutput(PotionBrewing potionBrewing, ItemStack potion, ItemStack itemStack) {
        try {
            ItemStack result = potionBrewing.mix(itemStack, potion);
            if (result != itemStack) {
                return result;
            }
        }
        catch (RuntimeException e) {
            String potionInfo = ErrorUtil.getItemStackInfo(potion);
            String itemStackInfo = ErrorUtil.getItemStackInfo(itemStack);
            LOGGER.error("A modded potion mix crashed: \nPotion: {}\nItemStack: {}", (Object)potionInfo, (Object)itemStackInfo, (Object)e);
        }
        return ItemStack.EMPTY;
    }
}

