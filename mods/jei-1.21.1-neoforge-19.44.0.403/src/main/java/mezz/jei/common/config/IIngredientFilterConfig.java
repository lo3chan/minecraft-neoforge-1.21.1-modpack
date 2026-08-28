/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import mezz.jei.api.runtime.config.IJeiConfigValue;
import mezz.jei.common.search.SearchMode;

public interface IIngredientFilterConfig {
    public IJeiConfigValue<SearchMode> modNameSearchMode();

    public IJeiConfigValue<SearchMode> tooltipSearchMode();

    public IJeiConfigValue<SearchMode> tagSearchMode();

    public IJeiConfigValue<SearchMode> colorSearchMode();

    public IJeiConfigValue<SearchMode> resourceLocationSearchMode();

    public IJeiConfigValue<SearchMode> creativeTabSearchMode();

    public IJeiConfigValue<Boolean> searchAdvancedTooltips();

    public IJeiConfigValue<Boolean> searchModIds();

    public IJeiConfigValue<Boolean> searchModAliases();

    public IJeiConfigValue<Boolean> searchIngredientAliases();

    public IJeiConfigValue<Boolean> searchShortModNames();
}

