/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenPosition
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 */
package mezz.jei.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.OffsetJeiInputHandler;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class RecipeLayoutDrawableErrored<R>
implements IRecipeLayoutDrawable<R> {
    private final IRecipeCategory<R> recipeCategory;
    private final R recipe;
    private final IScrollBoxWidget scrollBoxWidget;
    private final IJeiInputHandler inputHandler;
    private final IScalableDrawable background;
    private final int borderPadding;
    private ImmutableRect2i area;

    public RecipeLayoutDrawableErrored(IRecipeCategory<R> recipeCategory, R recipe, IScalableDrawable background, int borderPadding) {
        this.recipeCategory = recipeCategory;
        this.recipe = recipe;
        this.area = new ImmutableRect2i(0, 0, Math.max(100, recipeCategory.getWidth()), recipeCategory.getHeight());
        this.background = background;
        this.borderPadding = borderPadding;
        ArrayList<FormattedText> lines = new ArrayList<FormattedText>();
        lines.add((FormattedText)Component.translatable((String)"gui.jei.category.recipe.crashed").withStyle(ChatFormatting.RED));
        ResourceLocation registryName = recipeCategory.getRegistryName(recipe);
        if (registryName != null) {
            lines.add((FormattedText)Component.literal((String)registryName.toString()).withStyle(ChatFormatting.GRAY));
        }
        lines.add((FormattedText)Component.empty());
        lines.add((FormattedText)Component.literal((String)recipeCategory.getRecipeType().getUid().toString()).withStyle(ChatFormatting.GRAY));
        IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
        IJeiHelpers jeiHelpers = jeiRuntime.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        this.scrollBoxWidget = guiHelper.createScrollBoxWidget(this.area.width(), this.area.getHeight(), 0, 0).setContents(lines);
        this.inputHandler = new OffsetJeiInputHandler(this.scrollBoxWidget, this::getScreenPosition);
    }

    private ScreenPosition getScreenPosition() {
        return this.area.getScreenPosition();
    }

    @Override
    public void setPosition(int posX, int posY) {
        this.area = this.area.setPosition(posX, posY);
    }

    @Override
    public void drawRecipe(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.background.draw(guiGraphics, this.getRectWithBorder());
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float)this.area.x(), (float)this.area.y(), 0.0f);
        int recipeMouseX = mouseX - this.area.x();
        int recipeMouseY = mouseY - this.area.y();
        ScreenPosition position = this.scrollBoxWidget.getPosition();
        poseStack.pushPose();
        poseStack.translate((float)position.x(), (float)position.y(), 0.0f);
        this.scrollBoxWidget.drawWidget(guiGraphics, recipeMouseX - position.x(), recipeMouseY - position.y());
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public void drawOverlays(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.area.contains(mouseX, mouseY);
    }

    @Override
    public <T> Optional<T> getIngredientUnderMouse(int mouseX, int mouseY, IIngredientType<T> ingredientType) {
        return Optional.empty();
    }

    @Override
    public Optional<IRecipeSlotDrawable> getRecipeSlotUnderMouse(double mouseX, double mouseY) {
        return Optional.empty();
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        return Optional.empty();
    }

    @Override
    public Rect2i getRect() {
        return this.area.toMutable();
    }

    @Override
    public Rect2i getRectWithBorder() {
        return this.area.expandBy(this.borderPadding).toMutable();
    }

    @Override
    public Rect2i getSideButtonArea(int buttonIndex) {
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public IRecipeSlotsView getRecipeSlotsView() {
        return List::of;
    }

    @Override
    public IRecipeCategory<R> getRecipeCategory() {
        return this.recipeCategory;
    }

    @Override
    public R getRecipe() {
        return this.recipe;
    }

    @Override
    public IJeiInputHandler getInputHandler() {
        return this.inputHandler;
    }

    @Override
    public void tick() {
    }
}

