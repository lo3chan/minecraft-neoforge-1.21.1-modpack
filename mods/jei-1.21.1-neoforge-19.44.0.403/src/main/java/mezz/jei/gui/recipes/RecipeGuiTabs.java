/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 */
package mezz.jei.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.PageNavigation;
import mezz.jei.gui.input.IPaged;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.input.handlers.ProxyInputHandler;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeCategoryTab;
import mezz.jei.gui.recipes.RecipeGuiTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class RecipeGuiTabs
implements IPaged {
    private static final int TAB_GUI_OVERLAP = 3;
    private static final int TAB_HORIZONTAL_INSET = 2;
    private static final int NAVIGATION_HEIGHT = 20;
    private final IRecipeGuiLogic recipeGuiLogic;
    private final List<RecipeGuiTab> tabs = new ArrayList<RecipeGuiTab>();
    private final PageNavigation pageNavigation;
    private final IRecipeManager recipeManager;
    private final IGuiHelper guiHelper;
    private IUserInputHandler inputHandler;
    private ImmutableRect2i area = ImmutableRect2i.EMPTY;
    private int pageCount = 1;
    private int pageNumber = 0;
    private int categoriesPerPage = 1;

    public RecipeGuiTabs(IRecipeGuiLogic recipeGuiLogic, IRecipeManager recipeManager, IGuiHelper guiHelper) {
        this.recipeGuiLogic = recipeGuiLogic;
        this.pageNavigation = new PageNavigation(this, true);
        this.recipeManager = recipeManager;
        this.guiHelper = guiHelper;
        this.inputHandler = this.pageNavigation.createInputHandler();
    }

    public void initLayout(ImmutableRect2i recipeGuiArea) {
        List<IRecipeCategory<?>> categories = this.recipeGuiLogic.getRecipeCategories();
        if (categories.isEmpty()) {
            return;
        }
        ImmutableRect2i tabsArea = recipeGuiArea.keepTop(24).moveUp(21).cropLeft(2).cropRight(2);
        this.categoriesPerPage = Math.min(tabsArea.getWidth() / 24, categories.size());
        int tabsWidth = this.categoriesPerPage * 24;
        this.area = tabsArea.keepLeft(tabsWidth);
        this.pageCount = MathUtil.divideCeil(categories.size(), this.categoriesPerPage);
        IRecipeCategory<?> currentCategory = this.recipeGuiLogic.getSelectedRecipeCategory();
        int categoryIndex = categories.indexOf(currentCategory);
        this.pageNumber = categoryIndex / this.categoriesPerPage;
        ImmutableRect2i navigationArea = tabsArea.keepTop(20).moveUp(22);
        this.pageNavigation.updateBounds(navigationArea);
        this.updateLayout();
    }

    private void updateLayout() {
        int index;
        this.tabs.clear();
        ArrayList<IUserInputHandler> inputHandlers = new ArrayList<IUserInputHandler>();
        List<IRecipeCategory<?>> categories = this.recipeGuiLogic.getRecipeCategories();
        int tabX = this.area.getX();
        int startIndex = this.pageNumber * this.categoriesPerPage;
        for (int i = 0; i < this.categoriesPerPage && (index = i + startIndex) < categories.size(); ++i) {
            IRecipeCategory<?> category = categories.get(index);
            RecipeCategoryTab tab = new RecipeCategoryTab(this.recipeGuiLogic, category, tabX, this.area.getY(), this.recipeManager, this.guiHelper);
            this.tabs.add(tab);
            inputHandlers.add(tab);
            tabX += 24;
        }
        inputHandlers.add(this.pageNavigation.createInputHandler());
        this.inputHandler = new CombinedInputHandler("RecipeGuiTabs", inputHandlers);
        this.pageNavigation.updatePageNumber();
    }

    public void draw(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        IRecipeCategory<?> selectedCategory = this.recipeGuiLogic.getSelectedRecipeCategory();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RecipeGuiTab hovered = null;
        RenderSystem.disableDepthTest();
        for (RecipeGuiTab tab : this.tabs) {
            boolean selected = tab.isSelected(selectedCategory);
            tab.draw(selected, guiGraphics, mouseX, mouseY);
            if (!tab.isMouseOver(mouseX, mouseY)) continue;
            hovered = tab;
        }
        RenderSystem.enableDepthTest();
        this.pageNavigation.draw(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
        if (hovered != null) {
            JeiTooltip tooltip = hovered.getTooltip();
            tooltip.draw(guiGraphics, mouseX, mouseY);
        }
    }

    public IUserInputHandler createInputHandler() {
        return new ProxyInputHandler(() -> this.inputHandler);
    }

    @Override
    public boolean nextPage() {
        this.pageNumber = this.hasNext() ? ++this.pageNumber : 0;
        this.updateLayout();
        return true;
    }

    @Override
    public boolean hasNext() {
        return this.pageNumber + 1 < this.pageCount;
    }

    @Override
    public boolean previousPage() {
        this.pageNumber = this.hasPrevious() ? --this.pageNumber : this.pageCount - 1;
        this.updateLayout();
        return true;
    }

    @Override
    public boolean hasPrevious() {
        return this.pageNumber > 0;
    }

    @Override
    public int getPageCount() {
        return this.pageCount;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }
}

