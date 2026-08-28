/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.apache.commons.lang3.StringUtils
 */
package mezz.jei.library.plugins.jei.tags;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.plugins.jei.tags.ITagInfoRecipe;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.StringUtils;

public class TagInfoRecipeCategory<R extends ITagInfoRecipe, T extends RecipeType<R>>
extends AbstractRecipeCategory<R> {
    private static final int WIDTH = 142;
    private static final int HEIGHT = 110;

    public TagInfoRecipeCategory(IGuiHelper guiHelper, T recipeType, ResourceLocation registryLocation) {
        super(recipeType, TagInfoRecipeCategory.createTitle(registryLocation), guiHelper.createDrawableItemLike((ItemLike)Items.NAME_TAG), 142, 110);
    }

    private static Component createTitle(ResourceLocation registryLocation) {
        String registryName = ResourceLocationUtil.sanitizePath(registryLocation.getPath());
        String registryNameTranslationKey = "gui.jei.category.tagInformation." + registryName;
        Language language = Language.getInstance();
        if (language.has(registryNameTranslationKey)) {
            return Component.translatable((String)registryNameTranslationKey);
        }
        return Component.translatable((String)"gui.jei.category.tagInformation", (Object[])new Object[]{StringUtils.capitalize((String)registryLocation.getPath())});
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, R recipe, IFocusGroup focuses) {
        ((IRecipeSlotBuilder)builder.addInputSlot().addTypedIngredients((List)recipe.getTypedIngredients())).setStandardSlotBackground();
        for (ITypedIngredient<?> stack : recipe.getTypedIngredients()) {
            builder.addOutputSlot().addTypedIngredient(stack);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, R recipe, IFocusGroup focuses) {
        TagKey<?> tag = recipe.getTag();
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        Component tagName = renderHelper.getName(tag);
        List<MutableComponent> text = List.of(tagName, Component.literal((String)tag.location().toString()).withStyle(ChatFormatting.GRAY));
        ((ITextWidget)builder.addText(text, this.getWidth() - 22, 20).setPosition(22, 0)).setColor(-11513776).setLineSpacing(0).setTextAlignment(VerticalAlignment.CENTER).setTextAlignment(HorizontalAlignment.CENTER);
        IRecipeSlotDrawablesView recipeSlots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> outputSlots = recipeSlots.getSlots(RecipeIngredientRole.OUTPUT);
        IScrollGridWidget scrollGridWidget = builder.addScrollGridWidget(outputSlots, 7, 5);
        scrollGridWidget.setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        IRecipeSlotDrawable inputSlot = (IRecipeSlotDrawable)recipeSlots.getSlots(RecipeIngredientRole.INPUT).getFirst();
        inputSlot.setPosition(scrollGridWidget.getScreenRectangle().position().x() + 1, 1);
    }

    @Override
    public ResourceLocation getRegistryName(R recipe) {
        return recipe.getTag().location();
    }
}

