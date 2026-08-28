/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.gui.navigation.ScreenPosition
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.renderer.Rect2i
 */
package mezz.jei.library.gui.recipes;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.util.MathUtil;
import mezz.jei.library.gui.recipes.RecipeLayout;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;

public class RecipeLayoutInputHandler<T>
implements IJeiInputHandler {
    private final RecipeLayout<T> recipeLayout;
    private final List<IJeiInputHandler> inputHandlers;
    private final List<IJeiGuiEventListener> guiEventListeners;

    public RecipeLayoutInputHandler(RecipeLayout<T> recipeLayout) {
        this.recipeLayout = recipeLayout;
        this.inputHandlers = new ArrayList<IJeiInputHandler>();
        this.guiEventListeners = new ArrayList<IJeiGuiEventListener>();
    }

    @Override
    public ScreenRectangle getArea() {
        Rect2i area = this.recipeLayout.getRect();
        return new ScreenRectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }

    @Override
    public boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
        double relativeMouseY;
        ScreenPosition position;
        double relativeMouseX;
        ScreenRectangle widgetArea;
        if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        Rect2i area = this.recipeLayout.getRect();
        double recipeMouseX = mouseX - (double)area.getX();
        double recipeMouseY = mouseY - (double)area.getY();
        for (IJeiInputHandler inputHandler : this.inputHandlers) {
            widgetArea = inputHandler.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !inputHandler.handleInput(relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), userInput)) continue;
            return true;
        }
        for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            widgetArea = guiEventListener.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !RecipeLayoutInputHandler.handleInput(guiEventListener, relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), userInput)) continue;
            return true;
        }
        if (userInput.isSimulate()) {
            return true;
        }
        IRecipeCategory<T> recipeCategory = this.recipeLayout.getRecipeCategory();
        T recipe = this.recipeLayout.getRecipe();
        boolean legacyResult = recipeCategory.handleInput(recipe, recipeMouseX, recipeMouseY, userInput.getKey());
        return legacyResult;
    }

    private static boolean handleInput(IJeiGuiEventListener guiEventListener, double relativeMouseX, double relativeMouseY, IJeiUserInput userInput) {
        InputConstants.Key key = userInput.getKey();
        switch (key.getType()) {
            case MOUSE: {
                if (userInput.isSimulate()) {
                    return guiEventListener.mouseClicked(relativeMouseX, relativeMouseY, key.getValue());
                }
                return guiEventListener.mouseReleased(relativeMouseX, relativeMouseY, key.getValue());
            }
            case KEYSYM: {
                if (userInput.isSimulate()) break;
                return guiEventListener.keyPressed(relativeMouseX, relativeMouseY, key.getValue(), 0, userInput.getModifiers());
            }
            default: {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        double relativeMouseY;
        ScreenPosition position;
        double relativeMouseX;
        ScreenRectangle widgetArea;
        if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        Rect2i area = this.recipeLayout.getRect();
        double recipeMouseX = mouseX - (double)area.getX();
        double recipeMouseY = mouseY - (double)area.getY();
        for (IJeiInputHandler inputHandler : this.inputHandlers) {
            widgetArea = inputHandler.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !inputHandler.handleMouseDragged(relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), mouseKey, dragX, dragY)) continue;
            return true;
        }
        for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            widgetArea = guiEventListener.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !guiEventListener.mouseDragged(relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), mouseKey.getValue(), dragX, dragY)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        double relativeMouseY;
        ScreenPosition position;
        double relativeMouseX;
        ScreenRectangle widgetArea;
        if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        Rect2i area = this.recipeLayout.getRect();
        double recipeMouseX = mouseX - (double)area.getX();
        double recipeMouseY = mouseY - (double)area.getY();
        for (IJeiInputHandler inputHandler : this.inputHandlers) {
            widgetArea = inputHandler.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !inputHandler.handleMouseScrolled(relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), scrollDeltaX, scrollDeltaY)) continue;
            return true;
        }
        for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            widgetArea = guiEventListener.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY) || !guiEventListener.mouseScrolled(relativeMouseX = recipeMouseX - (double)(position = widgetArea.position()).x(), relativeMouseY = recipeMouseY - (double)position.y(), scrollDeltaX, scrollDeltaY)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void handleMouseMoved(double mouseX, double mouseY) {
        double relativeMouseY;
        double relativeMouseX;
        ScreenPosition position;
        ScreenRectangle widgetArea;
        if (!this.recipeLayout.isMouseOver(mouseX, mouseY)) {
            return;
        }
        Rect2i area = this.recipeLayout.getRect();
        double recipeMouseX = mouseX - (double)area.getX();
        double recipeMouseY = mouseY - (double)area.getY();
        for (IJeiInputHandler inputHandler : this.inputHandlers) {
            widgetArea = inputHandler.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) continue;
            position = widgetArea.position();
            relativeMouseX = recipeMouseX - (double)position.x();
            relativeMouseY = recipeMouseY - (double)position.y();
            inputHandler.handleMouseMoved(relativeMouseX, relativeMouseY);
        }
        for (IJeiGuiEventListener guiEventListener : this.guiEventListeners) {
            widgetArea = guiEventListener.getArea();
            if (!MathUtil.contains(widgetArea, recipeMouseX, recipeMouseY)) continue;
            position = widgetArea.position();
            relativeMouseX = recipeMouseX - (double)position.x();
            relativeMouseY = recipeMouseY - (double)position.y();
            guiEventListener.mouseMoved(relativeMouseX, relativeMouseY);
        }
    }

    public void addInputHandler(IJeiInputHandler inputHandler) {
        this.inputHandlers.add(inputHandler);
    }

    public void addGuiEventListener(IJeiGuiEventListener guiEventListener) {
        this.guiEventListeners.add(guiEventListener);
    }
}

