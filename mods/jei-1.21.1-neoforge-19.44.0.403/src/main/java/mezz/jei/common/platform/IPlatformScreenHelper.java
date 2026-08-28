/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.gui.screens.recipebook.RecipeBookComponent
 *  net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton
 *  net.minecraft.world.inventory.Slot
 */
package mezz.jei.common.platform;

import java.util.List;
import java.util.Optional;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.inventory.Slot;

public interface IPlatformScreenHelper {
    public Optional<Slot> getSlotUnderMouse(AbstractContainerScreen<?> var1);

    public int getGuiLeft(AbstractContainerScreen<?> var1);

    public int getGuiTop(AbstractContainerScreen<?> var1);

    public int getXSize(AbstractContainerScreen<?> var1);

    public int getYSize(AbstractContainerScreen<?> var1);

    public ImmutableRect2i getBookArea(RecipeBookComponent var1);

    public ImmutableRect2i getToastsArea();

    public List<RecipeBookTabButton> getTabButtons(RecipeBookComponent var1);

    public boolean canLoseFocus(EditBox var1);
}

