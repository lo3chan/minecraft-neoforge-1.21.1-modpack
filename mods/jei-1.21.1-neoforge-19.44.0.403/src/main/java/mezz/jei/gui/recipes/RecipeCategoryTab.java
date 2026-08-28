/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 */
package mezz.jei.gui.recipes;

import java.util.Optional;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeCategoryIconUtil;
import mezz.jei.gui.recipes.RecipeGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class RecipeCategoryTab
extends RecipeGuiTab {
    private final IRecipeGuiLogic logic;
    private final IRecipeCategory<?> category;
    private final IRecipeManager recipeManager;
    private final IGuiHelper guiHelper;

    public RecipeCategoryTab(IRecipeGuiLogic logic, IRecipeCategory<?> category, int x, int y, IRecipeManager recipeManager, IGuiHelper guiHelper) {
        super(x, y);
        this.logic = logic;
        this.category = category;
        this.recipeManager = recipeManager;
        this.guiHelper = guiHelper;
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (!this.isMouseOver(input.getMouseX(), input.getMouseY())) {
            return Optional.empty();
        }
        if (input.is(keyBindings.getLeftClick())) {
            if (!input.isSimulate()) {
                this.logic.setRecipeCategory(this.category);
                SoundManager soundHandler = Minecraft.getInstance().getSoundManager();
                soundHandler.play((SoundInstance)SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            }
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void draw(boolean selected, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.draw(selected, guiGraphics, mouseX, mouseY);
        IDrawable icon = RecipeCategoryIconUtil.create(this.category, this.recipeManager, this.guiHelper);
        int iconX = this.area.x() + (24 - icon.getWidth()) / 2;
        int iconY = this.area.y() + (24 - icon.getHeight()) / 2;
        icon.draw(guiGraphics, iconX, iconY);
    }

    @Override
    public boolean isSelected(IRecipeCategory<?> selectedCategory) {
        return this.category.getRecipeType().equals(selectedCategory.getRecipeType());
    }

    @Override
    public JeiTooltip getTooltip() {
        JeiTooltip tooltip = new JeiTooltip();
        Component title = this.category.getTitle();
        if (title != null) {
            tooltip.add((FormattedText)title);
        }
        ResourceLocation uid = this.category.getRecipeType().getUid();
        String modId = uid.getNamespace();
        IModIdHelper modIdHelper = Internal.getJeiRuntime().getJeiHelpers().getModIdHelper();
        if (modIdHelper.isDisplayingModNameEnabled()) {
            Component modName = modIdHelper.getFormattedModNameComponentForModId(modId);
            tooltip.add((FormattedText)modName);
        }
        return tooltip;
    }
}

