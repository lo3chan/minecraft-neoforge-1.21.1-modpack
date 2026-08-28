/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.gui.input.IDragHandler;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.overlay.ingredients.IIngredientGridPageNavigation;
import mezz.jei.gui.overlay.ingredients.IIngredientGridView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public interface IIngredientListOverlayContents
extends IIngredientGridView,
IIngredientGridPageNavigation,
IRecipeFocusSource {
    public boolean isEmpty();

    public void drawBackground(GuiGraphics var1);

    public void drawForeground(Minecraft var1, GuiGraphics var2, int var3, int var4, float var5);

    public void drawTooltips(Minecraft var1, GuiGraphics var2, int var3, int var4);

    public void drawOnForeground(GuiGraphics var1, int var2, int var3);

    public void tick();

    public IUserInputHandler createInputHandler();

    public IDragHandler createDragHandler();

    public <T> Stream<T> getVisibleIngredients(IIngredientType<T> var1);
}

