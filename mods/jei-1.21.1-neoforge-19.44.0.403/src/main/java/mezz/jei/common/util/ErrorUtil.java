/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.CrashReport
 *  net.minecraft.CrashReportCategory
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.RegistryOps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.block.Block
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformModHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ErrorUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private ErrorUtil() {
    }

    public static <T> String getIngredientInfo(T ingredient, IIngredientType<T> ingredientType, IIngredientManager ingredientManager) {
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        return ingredientHelper.getErrorInfo(ingredient);
    }

    public static String getItemStackInfo(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return "null";
        }
        Item item = itemStack.getItem();
        String itemName = ErrorUtil.getItemName(item);
        String components = itemStack.getComponentsPatch().toString();
        return String.valueOf(itemStack) + " " + itemName + " components:" + components;
    }

    private static String getItemName(Item item) {
        Registry itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
        ResourceLocation key = itemRegistry.getKey((Object)item);
        if (key != null) {
            return key.toString();
        }
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            String blockName = ErrorUtil.getBlockName(blockItem);
            return "BlockItem(" + blockName + ")";
        }
        return item.getClass().getName();
    }

    private static String getBlockName(BlockItem blockItem) {
        Block block = blockItem.getBlock();
        if (block == null) {
            return "null";
        }
        Registry blockRegistry = RegistryUtil.getRegistry(Registries.BLOCK);
        ResourceLocation key = blockRegistry.getKey((Object)block);
        if (key != null) {
            return key.toString();
        }
        return block.getClass().getName();
    }

    public static void checkNotEmpty(ItemStack itemStack, String name) {
        if (itemStack == null) {
            throw new NullPointerException(name + " must not be null.");
        }
        if (itemStack.isEmpty()) {
            String info = ErrorUtil.getItemStackInfo(itemStack);
            throw new IllegalArgumentException("ItemStack " + name + " must not be empty. " + info);
        }
    }

    public static <T> void checkNotEmpty(T[] values, String name) {
        if (values == null) {
            throw new NullPointerException(name + " must not be null.");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException(name + " must not be empty.");
        }
        for (T value : values) {
            if (value != null) continue;
            throw new NullPointerException(name + " must not contain null values.");
        }
    }

    public static void checkNotEmpty(Collection<?> values, String name) {
        if (values == null) {
            throw new NullPointerException(name + " must not be null.");
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty.");
        }
        if (!(values instanceof NonNullList)) {
            for (Object value : values) {
                if (value != null) continue;
                throw new NullPointerException(name + " must not contain null values.");
            }
        }
    }

    public static <T> void checkNotNull(@Nullable T object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " must not be null.");
        }
    }

    public static void checkNotNull(Collection<?> values, String name) {
        if (values == null) {
            throw new NullPointerException(name + " must not be null.");
        }
        if (!(values instanceof NonNullList)) {
            for (Object value : values) {
                if (value != null) continue;
                throw new NullPointerException(name + " must not contain null values.");
            }
        }
    }

    public static void assertMainThread() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && !minecraft.isSameThread()) {
            Thread currentThread = Thread.currentThread();
            throw new IllegalStateException("A JEI API method is being called by another mod from the wrong thread:\n" + String.valueOf(currentThread) + "\nIt must be called on the main thread by using Minecraft.addScheduledTask.");
        }
    }

    public static <T> void validateRecipes(RecipeType<T> recipeType, Iterable<? extends T> recipes) {
        Class<T> recipeClass = recipeType.getRecipeClass();
        for (T recipe : recipes) {
            if (recipeClass.isInstance(recipe)) continue;
            throw new IllegalArgumentException(String.valueOf(recipeType) + " recipes must be an instance of " + String.valueOf(recipeClass) + ". Instead got: " + String.valueOf(recipe.getClass()));
        }
    }

    public static <T> CrashReport createIngredientCrashReport(Throwable throwable, String title, IIngredientManager ingredientManager, ITypedIngredient<T> typedIngredient) {
        return ErrorUtil.createIngredientCrashReport(throwable, title, ingredientManager, typedIngredient.getType(), typedIngredient.getIngredient());
    }

    public static <T> CrashReport createIngredientCrashReport(Throwable throwable, String title, IIngredientManager ingredientManager, IIngredientType<T> ingredientType, T ingredient) {
        CrashReport crashReport = CrashReport.forThrowable((Throwable)throwable, (String)title);
        CrashReportCategory category = crashReport.addCategory("Ingredient");
        ErrorUtil.setIngredientCategoryDetails(category, ingredientType, ingredient, ingredientManager);
        return crashReport;
    }

    public static <T> void logIngredientCrash(Throwable throwable, String title, IIngredientManager ingredientManager, IIngredientType<T> ingredientType, T ingredient) {
        CrashReportCategory category = new CrashReportCategory("Ingredient");
        ErrorUtil.setIngredientCategoryDetails(category, ingredientType, ingredient, ingredientManager);
        LOGGER.error(ErrorUtil.crashReportToString(throwable, title, category));
    }

    private static <T> void setIngredientCategoryDetails(CrashReportCategory category, IIngredientType<T> ingredientType, T ingredient, IIngredientManager ingredientManager) {
        IIngredientHelper ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        Codec ingredientCodec = ingredientManager.getIngredientCodec(ingredientType);
        IPlatformModHelper modHelper = Services.PLATFORM.getModHelper();
        category.setDetail("Name", () -> ingredientHelper.getDisplayName(ingredient));
        category.setDetail("Mod's Name", () -> {
            String modId = ingredientHelper.getDisplayModId(ingredient);
            return modHelper.getModNameForModId(modId);
        });
        category.setDetail("Registry Name", () -> ingredientHelper.getResourceLocation(ingredient).toString());
        category.setDetail("Class Name", () -> ingredient.getClass().toString());
        category.setDetail("toString Name", ingredient::toString);
        category.setDetail("JSON", () -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            assert (level != null);
            RegistryAccess registryAccess = level.registryAccess();
            RegistryOps registryOps = registryAccess.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
            return (String)ingredientCodec.encodeStart((DynamicOps)registryOps, ingredient).mapOrElse(JsonElement::toString, DataResult.Error::message);
        });
        category.setDetail("Ingredient Type for JEI", () -> ingredientType.getIngredientClass().toString());
        category.setDetail("Error Info gathered from JEI", () -> ingredientHelper.getErrorInfo(ingredient));
    }

    private static String crashReportToString(Throwable t, String title, CrashReportCategory ... categories) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(":\n\n");
        for (CrashReportCategory category : categories) {
            category.getDetails(sb);
            sb.append("\n\n");
        }
        sb.append("-- Stack Trace --\n\n");
        sb.append(ExceptionUtils.getStackTrace((Throwable)t));
        return sb.toString();
    }

    public static <T> String getRecipeInfo(IRecipeLayoutDrawable<T> recipeLayoutDrawable) {
        IRecipeCategory<T> recipeCategory = recipeLayoutDrawable.getRecipeCategory();
        T recipe = recipeLayoutDrawable.getRecipe();
        return ErrorUtil.getRecipeInfo(recipeCategory, recipe);
    }

    public static <T> String getRecipeInfo(IRecipeCategory<T> recipeCategory, T recipe) {
        String recipeClass;
        ResourceLocation recipeType = recipeCategory.getRecipeType().getUid();
        ResourceLocation registryName = recipeCategory.getRegistryName(recipe);
        if (recipe instanceof RecipeHolder) {
            RecipeHolder recipeHolder = (RecipeHolder)recipe;
            recipeClass = "RecipeHolder(%s)".formatted(recipeHolder.value().getClass());
        } else {
            recipeClass = recipe.getClass().toString();
        }
        String modName = "<unknown>";
        if (registryName != null) {
            String modId = registryName.getNamespace();
            IPlatformModHelper modHelper = Services.PLATFORM.getModHelper();
            modName = "%s (%s)".formatted(modHelper.getModNameForModId(modId), modId);
        }
        return "Recipe is from Mod: " + modName + "\nRecipe Name: " + String.valueOf(registryName) + "\nRecipe Class: " + recipeClass + "\nRecipe Type: " + String.valueOf(recipeType);
    }
}

