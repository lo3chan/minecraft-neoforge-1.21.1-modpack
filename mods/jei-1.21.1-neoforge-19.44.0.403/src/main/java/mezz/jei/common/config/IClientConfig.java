/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import java.util.List;
import mezz.jei.api.runtime.config.IJeiConfigValue;
import mezz.jei.common.config.BookmarkTooltipFeature;
import mezz.jei.common.config.GiveMode;
import mezz.jei.common.config.HistoryDisplaySide;
import mezz.jei.common.config.IngredientSortStage;
import mezz.jei.common.config.RecipeSorterStage;

public interface IClientConfig {
    public static final int minRecipeGuiHeight = 175;
    public static final int defaultRecipeGuiHeight = 350;
    public static final boolean defaultCenterSearchBar = false;

    public IJeiConfigValue<Boolean> centerSearchBarEnabled();

    public IJeiConfigValue<Integer> maxRecipeGuiHeight();

    public IJeiConfigValue<Boolean> toastReflowEnabled();

    public IJeiConfigValue<GiveMode> giveMode();

    public IJeiConfigValue<Boolean> cheatToHotbarUsingHotkeysEnabled();

    public IJeiConfigValue<Boolean> showHiddenIngredients();

    public IJeiConfigValue<Boolean> showTagRecipesEnabled();

    public IJeiConfigValue<Boolean> addBookmarksToFrontEnabled();

    public IJeiConfigValue<Boolean> bookmarkOutputAsRecipe();

    public IJeiConfigValue<List<BookmarkTooltipFeature>> bookmarkTooltipFeatures();

    public IJeiConfigValue<Boolean> holdShiftToShowBookmarkTooltipFeaturesEnabled();

    public IJeiConfigValue<Boolean> dragToRearrangeBookmarksEnabled();

    public IJeiConfigValue<Boolean> lookupHistoryEnabled();

    public IJeiConfigValue<Integer> maxLookupHistoryRows();

    public IJeiConfigValue<Integer> maxLookupHistoryIngredients();

    public IJeiConfigValue<HistoryDisplaySide> lookupHistoryDisplaySide();

    public IJeiConfigValue<Boolean> ingredientsSummaryEnabled();

    public IJeiConfigValue<Boolean> lowMemorySlowSearchEnabled();

    public IJeiConfigValue<Boolean> catchRenderErrorsEnabled();

    public IJeiConfigValue<Boolean> lookupFluidContentsEnabled();

    public IJeiConfigValue<Boolean> lookupBlockTagsEnabled();

    public IJeiConfigValue<Boolean> showCreativeTabNamesEnabled();

    public IJeiConfigValue<Integer> dragDelayMs();

    public IJeiConfigValue<Integer> smoothScrollRate();

    public IJeiConfigValue<List<IngredientSortStage>> ingredientSorterStages();

    public IJeiConfigValue<List<RecipeSorterStage>> recipeSorterStages();

    public IJeiConfigValue<Boolean> tagContentTooltipEnabled();

    public IJeiConfigValue<Boolean> hideSingleTagContentTooltipEnabled();
}

