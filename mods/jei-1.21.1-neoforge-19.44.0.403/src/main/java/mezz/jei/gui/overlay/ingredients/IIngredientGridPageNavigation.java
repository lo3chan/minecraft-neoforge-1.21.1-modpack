/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Nullable;

public interface IIngredientGridPageNavigation {
    @Nullable
    public IElement<?> getPageAnchorElement();

    public void updateLayoutKeepingPageAnchorVisible(@Nullable IElement<?> var1);

    public void updateLayoutToFirstPage();
}

