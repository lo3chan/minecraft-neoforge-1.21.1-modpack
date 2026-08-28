/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.serialization.Codec
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.recipe.category;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public interface IRecipeCategory<T> {
    public RecipeType<T> getRecipeType();

    public Component getTitle();

    @Deprecated(since="19.19.0", forRemoval=true)
    @Nullable
    default public IDrawable getBackground() {
        return null;
    }

    default public int getWidth() {
        IDrawable background = this.getBackground();
        if (background == null) {
            throw new IllegalStateException("getWidth() and getHeight() must be overridden if background is null");
        }
        return background.getWidth();
    }

    default public int getHeight() {
        IDrawable background = this.getBackground();
        if (background == null) {
            throw new IllegalStateException("getWidth() and getHeight() must be overridden if background is null");
        }
        return background.getHeight();
    }

    @Nullable
    public IDrawable getIcon();

    public void setRecipe(IRecipeLayoutBuilder var1, T var2, IFocusGroup var3);

    @Deprecated(since="19.19.3", forRemoval=true)
    default public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
    }

    default public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        this.createRecipeExtras(builder, recipe, () -> Collections.unmodifiableList(builder.getRecipeSlots().getSlots()), focuses);
    }

    default public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    default public void onDisplayedIngredientsUpdate(T recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    }

    @Deprecated(since="19.5.4", forRemoval=true)
    default public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return List.of();
    }

    default public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltipStrings = this.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
        tooltip.addAll(tooltipStrings);
    }

    @Deprecated(since="19.6.0", forRemoval=true)
    default public boolean handleInput(T recipe, double mouseX, double mouseY, InputConstants.Key input) {
        return false;
    }

    default public boolean isHandled(T recipe) {
        return true;
    }

    @Nullable
    default public ResourceLocation getRegistryName(T recipe) {
        if (recipe instanceof RecipeHolder) {
            RecipeHolder recipeHolder = (RecipeHolder)recipe;
            return recipeHolder.id();
        }
        return null;
    }

    default public Codec<T> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        RecipeType<T> recipeType = this.getRecipeType();
        if (RecipeHolder.class.isAssignableFrom(recipeType.getRecipeClass())) {
            Codec recipeHolderCodec = codecHelper.getRecipeHolderCodec();
            return recipeHolderCodec;
        }
        return codecHelper.getSlowRecipeCategoryCodec(this, recipeManager);
    }

    default public boolean needsRecipeBorder() {
        return true;
    }
}

