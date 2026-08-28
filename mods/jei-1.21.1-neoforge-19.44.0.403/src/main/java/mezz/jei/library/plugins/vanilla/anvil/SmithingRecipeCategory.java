/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.anvil;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.IExtendableSmithingRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class SmithingRecipeCategory
extends AbstractRecipeCategory<RecipeHolder<SmithingRecipe>>
implements IExtendableSmithingRecipeCategory {
    private final Map<Class<? extends SmithingRecipe>, ISmithingCategoryExtension<?>> extensions = new HashMap();

    public SmithingRecipeCategory(IGuiHelper guiHelper) {
        super(RecipeTypes.SMITHING, (Component)Blocks.SMITHING_TABLE.getName(), guiHelper.createDrawableItemLike((ItemLike)Blocks.SMITHING_TABLE), 108, 28);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SmithingRecipe> recipeHolder, IFocusGroup focuses) {
        SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
        ISmithingCategoryExtension<SmithingRecipe> extension = this.getExtension(recipe);
        if (extension == null) {
            return;
        }
        IRecipeSlotBuilder templateSlot = builder.addInputSlot(1, 6).setStandardSlotBackground();
        IRecipeSlotBuilder baseSlot = builder.addInputSlot(19, 6).setStandardSlotBackground();
        IRecipeSlotBuilder additionSlot = builder.addInputSlot(37, 6).setStandardSlotBackground();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(91, 6).setStandardSlotBackground();
        extension.setTemplate(recipe, templateSlot);
        extension.setBase(recipe, baseSlot);
        extension.setAddition(recipe, additionSlot);
        extension.setOutput(recipe, outputSlot);
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<SmithingRecipe> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
        ISmithingCategoryExtension<SmithingRecipe> extension = this.getExtension(recipe);
        if (extension == null) {
            return;
        }
        IRecipeSlotDrawable templateSlot = (IRecipeSlotDrawable)recipeSlots.getFirst();
        IRecipeSlotDrawable baseSlot = recipeSlots.get(1);
        IRecipeSlotDrawable additionSlot = recipeSlots.get(2);
        IRecipeSlotDrawable outputSlot = recipeSlots.get(3);
        extension.onDisplayedIngredientsUpdate(recipe, templateSlot, baseSlot, additionSlot, outputSlot, focuses);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<SmithingRecipe> recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(61, 6);
    }

    @Override
    public boolean isHandled(RecipeHolder<SmithingRecipe> recipeHolder) {
        SmithingRecipe recipe = (SmithingRecipe)recipeHolder.value();
        ISmithingCategoryExtension extension = this.getExtension(recipe);
        return extension != null;
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<SmithingRecipe> recipe) {
        return recipe.id();
    }

    @Override
    public Codec<RecipeHolder<SmithingRecipe>> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return codecHelper.getRecipeHolderCodec();
    }

    @Override
    public <R extends SmithingRecipe> void addExtension(Class<? extends R> recipeClass, ISmithingCategoryExtension<R> extension) {
        ErrorUtil.checkNotNull(recipeClass, "recipeClass");
        ErrorUtil.checkNotNull(extension, "extension");
        if (this.extensions.containsKey(recipeClass)) {
            throw new IllegalArgumentException("An extension has already been registered for: " + String.valueOf(recipeClass));
        }
        this.extensions.put(recipeClass, extension);
    }

    @Nullable
    private <R extends SmithingRecipe> ISmithingCategoryExtension<? super R> getExtension(SmithingRecipe recipe) {
        ISmithingCategoryExtension<?> extension = this.extensions.get(recipe.getClass());
        if (extension != null) {
            ISmithingCategoryExtension<?> cast = extension;
            return cast;
        }
        for (Map.Entry<Class<SmithingRecipe>, ISmithingCategoryExtension<?>> e : this.extensions.entrySet()) {
            if (!e.getKey().isInstance(recipe)) continue;
            ISmithingCategoryExtension<?> cast = e.getValue();
            return cast;
        }
        return null;
    }
}

