/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input;

import java.util.stream.Stream;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.ClickableIngredientInternal;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.overlay.elements.IngredientElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class GuiContainerWrapper
implements IRecipeFocusSource {
    private final IScreenHelper screenHelper;

    public GuiContainerWrapper(IScreenHelper screenHelper) {
        this.screenHelper = screenHelper;
    }

    @Override
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double mouseX, double mouseY) {
        Screen guiScreen = Minecraft.getInstance().screen;
        if (guiScreen == null) {
            return Stream.empty();
        }
        return this.screenHelper.getClickableIngredientUnderMouse(guiScreen, mouseX, mouseY).map(clickableSlot -> {
            ITypedIngredient typedIngredient = clickableSlot.getTypedIngredient();
            ImmutableRect2i area = new ImmutableRect2i(clickableSlot.getArea());
            IngredientElement element = new IngredientElement(typedIngredient);
            return new ClickableIngredientInternal(element, area::contains, false, false);
        });
    }

    @Override
    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double mouseX, double mouseY) {
        return Stream.empty();
    }
}

