/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.runtime.config.IJeiConfigValue;
import mezz.jei.common.config.IngredientGridLayoutMode;
import mezz.jei.common.config.IngredientGridNavigationMode;
import mezz.jei.common.util.NavigationVisibility;

public interface IIngredientGridConfig {
    public IJeiConfigValue<Integer> maxColumns();

    public int getMinColumns();

    public IJeiConfigValue<Integer> maxRows();

    public int getMinRows();

    public IJeiConfigValue<Boolean> drawBackground();

    public IJeiConfigValue<IngredientGridLayoutMode> layoutMode();

    public IJeiConfigValue<IngredientGridNavigationMode> navigationMode();

    public IJeiConfigValue<HorizontalAlignment> horizontalAlignment();

    public IJeiConfigValue<VerticalAlignment> verticalAlignment();

    public IJeiConfigValue<NavigationVisibility> navigationVisibility();
}

