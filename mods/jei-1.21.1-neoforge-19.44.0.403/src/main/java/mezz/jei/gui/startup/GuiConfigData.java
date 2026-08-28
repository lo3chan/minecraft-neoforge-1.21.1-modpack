/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.startup;

import java.nio.file.Path;
import mezz.jei.common.platform.Services;
import mezz.jei.gui.config.BookmarkJsonConfig;
import mezz.jei.gui.config.IBookmarkConfig;
import mezz.jei.gui.config.ILookupHistoryConfig;
import mezz.jei.gui.config.IngredientTypeSortingConfig;
import mezz.jei.gui.config.LookupHistoryJsonConfig;
import mezz.jei.gui.config.ModNameSortingConfig;

public record GuiConfigData(IBookmarkConfig bookmarkConfig, ILookupHistoryConfig lookupHistoryConfig, ModNameSortingConfig modNameSortingConfig, IngredientTypeSortingConfig ingredientTypeSortingConfig) {
    public static GuiConfigData create() {
        Path configDir = Services.PLATFORM.getConfigHelper().createJeiConfigDir();
        BookmarkJsonConfig bookmarkConfig = new BookmarkJsonConfig(configDir);
        LookupHistoryJsonConfig lookupHistoryConfig = new LookupHistoryJsonConfig(configDir);
        ModNameSortingConfig ingredientModNameSortingConfig = new ModNameSortingConfig(configDir.resolve("ingredient-list-mod-sort-order.ini"));
        IngredientTypeSortingConfig ingredientTypeSortingConfig = new IngredientTypeSortingConfig(configDir.resolve("ingredient-list-type-sort-order.ini"));
        return new GuiConfigData(bookmarkConfig, lookupHistoryConfig, ingredientModNameSortingConfig, ingredientTypeSortingConfig);
    }
}

