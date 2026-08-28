/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.EnchantmentTags
 *  net.minecraft.world.inventory.GrindstoneMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.grindstone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.library.plugins.vanilla.grindstone.GrindstoneHelper;
import mezz.jei.library.plugins.vanilla.grindstone.GrindstoneRecipe;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public final class GrindstoneRecipeMaker {
    public static List<IJeiGrindstoneRecipe> getGrindstoneRecipes(IIngredientManager ingredientManager, IPlatformRecipeHelper platformHelper) {
        GrindstoneMenu grindstoneMenu = GrindstoneHelper.getFakeGrindstoneMenu();
        if (grindstoneMenu == null) {
            return List.of();
        }
        return GrindstoneRecipeMaker.getGrindstoneRecipes(ingredientManager, platformHelper, grindstoneMenu);
    }

    public static List<IJeiGrindstoneRecipe> getGrindstoneRecipes(IIngredientManager ingredientManager, IPlatformRecipeHelper platformHelper, GrindstoneMenu grindstoneMenu) {
        return GrindstoneRecipeMaker.getGrindstoneRecipes(ingredientManager, platformHelper, Services.PLATFORM.getIngredientHelper(), grindstoneMenu);
    }

    public static List<IJeiGrindstoneRecipe> getGrindstoneRecipes(IIngredientManager ingredientManager, IPlatformRecipeHelper platformHelper, IPlatformIngredientHelper ingredientHelper, GrindstoneMenu grindstoneMenu) {
        return Stream.concat(GrindstoneRecipeMaker.getRepairRecipes(platformHelper, ingredientManager, grindstoneMenu), GrindstoneRecipeMaker.getDisenchantRecipes(platformHelper, ingredientHelper, grindstoneMenu)).toList();
    }

    private static Stream<IJeiGrindstoneRecipe> getDisenchantRecipes(IPlatformRecipeHelper platformHelper, IPlatformIngredientHelper ingredientHelper, GrindstoneMenu grindstoneMenu) {
        Registry registry = RegistryUtil.getRegistry(Registries.ENCHANTMENT);
        List enchantments = registry.holders().toList();
        ArrayList<IJeiGrindstoneRecipe> grindstoneRecipes = new ArrayList<IJeiGrindstoneRecipe>();
        for (Holder.Reference enchantmentHolder : enchantments) {
            if (enchantmentHolder.is(EnchantmentTags.CURSE)) continue;
            Enchantment enchantment = (Enchantment)enchantmentHolder.value();
            Optional enchantmentResourceLocation = registry.getResourceKey((Object)enchantment);
            String enchantmentPath = enchantmentResourceLocation.map(enchantmentResourceKey -> enchantmentResourceKey.location().getPath()).orElse(null);
            for (Holder itemHolder : ingredientHelper.getSupportedItems((Holder<Enchantment>)enchantmentHolder)) {
                ItemStack stack = new ItemStack(itemHolder);
                if (!stack.isEnchantable() || !platformHelper.isItemEnchantable(stack, (Holder<Enchantment>)enchantmentHolder)) continue;
                for (int level = 1; level <= Math.min(enchantment.getMaxLevel(), 10); ++level) {
                    ItemStack enchantedStack = stack.copy();
                    enchantedStack.enchant((Holder)enchantmentHolder, level);
                    String itemId = stack.getItem().getDescriptionId();
                    String asciiLevel = Integer.toString(level);
                    String rawPath = "grindstone.disenchantment.%s.%s.%s".formatted(itemId, enchantmentPath, asciiLevel);
                    String uidPath = ResourceLocationUtil.sanitizePath(rawPath);
                    ResourceLocation uid = ResourceLocation.withDefaultNamespace((String)uidPath);
                    IJeiGrindstoneRecipe grindstoneRecipe = GrindstoneRecipeMaker.getGrindstoneRecipe(platformHelper, grindstoneMenu, enchantedStack, ItemStack.EMPTY, uid);
                    if (grindstoneRecipe == null) continue;
                    grindstoneRecipes.add(grindstoneRecipe);
                }
            }
        }
        return grindstoneRecipes.stream();
    }

    private static Stream<IJeiGrindstoneRecipe> getRepairRecipes(IPlatformRecipeHelper platformHelper, IIngredientManager ingredientManager, GrindstoneMenu grindstoneMenu) {
        return ingredientManager.getAllItemStacks().stream().filter(ItemStack::isDamageableItem).map(stack -> {
            stack.setDamageValue(stack.getMaxDamage() * 3 / 4);
            ItemStack topInput = stack.copy();
            ItemStack bottomInput = stack.copy();
            String itemId = stack.getItem().getDescriptionId();
            String rawPath = "grindstone.self_repair." + itemId;
            String uidPath = ResourceLocationUtil.sanitizePath(rawPath);
            return GrindstoneRecipeMaker.getGrindstoneRecipe(platformHelper, grindstoneMenu, topInput, bottomInput, ResourceLocation.withDefaultNamespace((String)uidPath));
        }).filter(Objects::nonNull);
    }

    @Nullable
    private static IJeiGrindstoneRecipe getGrindstoneRecipe(IPlatformRecipeHelper platformHelper, GrindstoneMenu grindstoneMenu, ItemStack topInput, ItemStack bottomInput, @Nullable ResourceLocation uid) {
        ItemStack output = platformHelper.getGrindstoneResult(grindstoneMenu, topInput, bottomInput);
        if (output.isEmpty()) {
            return null;
        }
        return new GrindstoneRecipe(List.of(topInput), List.of(bottomInput), List.of(output), -1, -1, uid);
    }
}

