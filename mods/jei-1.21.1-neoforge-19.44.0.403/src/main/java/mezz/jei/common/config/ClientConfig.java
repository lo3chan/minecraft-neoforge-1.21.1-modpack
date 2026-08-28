/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.config;

import com.google.common.base.Preconditions;
import java.util.List;
import mezz.jei.common.config.BookmarkTooltipFeature;
import mezz.jei.common.config.GiveMode;
import mezz.jei.common.config.HistoryDisplaySide;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IngredientSortStage;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import mezz.jei.common.config.file.serializers.EnumSerializer;
import mezz.jei.common.config.file.serializers.ListSerializer;
import mezz.jei.common.platform.Services;
import org.jetbrains.annotations.Nullable;

public final class ClientConfig
implements IClientConfig {
    @Nullable
    private static IClientConfig instance;
    private final ConfigValue<Boolean> centerSearchBarEnabled;
    private final ConfigValue<Integer> maxRecipeGuiHeight;
    private final ConfigValue<Boolean> toastReflowEnabled;
    private final ConfigValue<GiveMode> giveMode;
    private final ConfigValue<Boolean> cheatToHotbarUsingHotkeysEnabled;
    private final ConfigValue<Boolean> showHiddenIngredients;
    private final ConfigValue<Boolean> addBookmarksToFrontEnabled;
    private final ConfigValue<Boolean> bookmarkOutputAsRecipe;
    private final ConfigValue<List<BookmarkTooltipFeature>> bookmarkTooltipFeatures;
    private final ConfigValue<Boolean> holdShiftToShowBookmarkTooltipFeaturesEnabled;
    private final ConfigValue<Boolean> dragToRearrangeBookmarksEnabled;
    private final ConfigValue<Boolean> lookupHistoryEnabled;
    private final ConfigValue<Integer> maxLookupHistoryRows;
    private final ConfigValue<Integer> maxLookupHistoryIngredients;
    private final ConfigValue<HistoryDisplaySide> lookupHistoryDisplaySide;
    private final ConfigValue<Boolean> ingredientsSummaryEnabled;
    private final ConfigValue<Boolean> lowMemorySlowSearchEnabled;
    private final ConfigValue<Boolean> catchRenderErrorsEnabled;
    private final ConfigValue<Boolean> lookupFluidContentsEnabled;
    private final ConfigValue<Boolean> lookupBlockTagsEnabled;
    private final ConfigValue<Boolean> showTagRecipesEnabled;
    private final ConfigValue<Boolean> showCreativeTabNamesEnabled;
    private final ConfigValue<Integer> dragDelayMs;
    private final ConfigValue<Integer> smoothScrollRate;
    private final ConfigValue<List<IngredientSortStage>> ingredientSorterStages;
    private final ConfigValue<List<RecipeSorterStage>> recipeSorterStages;
    private final ConfigValue<Boolean> tagContentTooltipEnabled;
    private final ConfigValue<Boolean> hideSingleTagContentTooltipEnabled;

    public ClientConfig(IConfigSchemaBuilder schema) {
        instance = this;
        boolean isDev = Services.PLATFORM.getModHelper().isInDev();
        IConfigCategoryBuilder appearance = schema.addCategory("appearance");
        this.centerSearchBarEnabled = appearance.addBoolean("centerSearch", false);
        this.maxRecipeGuiHeight = appearance.addInteger("recipeGuiHeight", 350, 175, Integer.MAX_VALUE);
        this.toastReflowEnabled = appearance.addBoolean("toastReflowEnabled", true);
        IConfigCategoryBuilder cheating = schema.addCategory("cheating");
        this.giveMode = cheating.addEnum("giveMode", GiveMode.defaultGiveMode);
        this.cheatToHotbarUsingHotkeysEnabled = cheating.addBoolean("cheatToHotbarUsingHotkeysEnabled", false);
        this.showHiddenIngredients = cheating.addBoolean("showHiddenIngredients", false);
        this.showTagRecipesEnabled = cheating.addBoolean("showTagRecipesEnabled", isDev);
        IConfigCategoryBuilder bookmarks = schema.addCategory("bookmarks");
        this.addBookmarksToFrontEnabled = bookmarks.addBoolean("addBookmarksToFrontEnabled", false);
        this.bookmarkOutputAsRecipe = bookmarks.addBoolean("bookmarkOutputAsRecipe", true);
        this.dragToRearrangeBookmarksEnabled = bookmarks.addBoolean("dragToRearrangeBookmarksEnabled", true);
        IConfigCategoryBuilder tooltips = schema.addCategory("tooltips");
        this.bookmarkTooltipFeatures = tooltips.addList("bookmarkTooltipFeatures", BookmarkTooltipFeature.DEFAULT_BOOKMARK_TOOLTIP_FEATURES, new ListSerializer<BookmarkTooltipFeature>(new EnumSerializer<BookmarkTooltipFeature>(BookmarkTooltipFeature.class)));
        this.holdShiftToShowBookmarkTooltipFeaturesEnabled = tooltips.addBoolean("holdShiftToShowBookmarkTooltipFeatures", true);
        this.showCreativeTabNamesEnabled = tooltips.addBoolean("showCreativeTabNamesEnabled", false);
        this.tagContentTooltipEnabled = tooltips.addBoolean("tagContentTooltipEnabled", true);
        this.hideSingleTagContentTooltipEnabled = tooltips.addBoolean("hideSingleTagContentTooltipEnabled", true);
        this.ingredientsSummaryEnabled = tooltips.addBoolean("enableRecipesGuiIngredientsSummary", false);
        IConfigCategoryBuilder performance = schema.addCategory("performance");
        this.lowMemorySlowSearchEnabled = performance.addBoolean("lowMemorySlowSearchEnabled", false);
        IConfigCategoryBuilder lookups = schema.addCategory("lookups");
        this.lookupFluidContentsEnabled = lookups.addBoolean("lookupFluidContentsEnabled", false);
        this.lookupBlockTagsEnabled = lookups.addBoolean("lookupBlockTagsEnabled", true);
        IConfigCategoryBuilder lookupHistory = schema.addCategory("lookupHistory");
        this.lookupHistoryEnabled = lookupHistory.addBoolean("enabled", false);
        this.maxLookupHistoryRows = lookupHistory.addInteger("maxRows", 2, 1, 7);
        this.maxLookupHistoryIngredients = lookupHistory.addInteger("maxIngredients", 100, 10, 1000);
        this.lookupHistoryDisplaySide = lookupHistory.addEnum("displaySide", HistoryDisplaySide.LEFT);
        IConfigCategoryBuilder advanced = schema.addCategory("advanced");
        this.catchRenderErrorsEnabled = advanced.addBoolean("catchRenderErrorsEnabled", !isDev);
        IConfigCategoryBuilder input = schema.addCategory("input");
        this.dragDelayMs = input.addInteger("dragDelayInMilliseconds", 150, 0, 1000);
        this.smoothScrollRate = input.addInteger("smoothScrollRate", 9, 1, 50);
        IConfigCategoryBuilder sorting = schema.addCategory("sorting");
        this.ingredientSorterStages = sorting.addList("ingredientSortStages", IngredientSortStage.defaultStages, new ListSerializer<IngredientSortStage>(new EnumSerializer<IngredientSortStage>(IngredientSortStage.class)));
        this.recipeSorterStages = sorting.addList("recipeSorterStages", RecipeSorterStage.defaultStages, new ListSerializer<RecipeSorterStage>(new EnumSerializer<RecipeSorterStage>(RecipeSorterStage.class)));
    }

    @Deprecated
    public static IClientConfig getInstance() {
        Preconditions.checkNotNull((Object)instance);
        return instance;
    }

    public ConfigValue<Boolean> centerSearchBarEnabled() {
        return this.centerSearchBarEnabled;
    }

    public ConfigValue<Integer> maxRecipeGuiHeight() {
        return this.maxRecipeGuiHeight;
    }

    public ConfigValue<Boolean> toastReflowEnabled() {
        return this.toastReflowEnabled;
    }

    public ConfigValue<GiveMode> giveMode() {
        return this.giveMode;
    }

    public ConfigValue<Boolean> cheatToHotbarUsingHotkeysEnabled() {
        return this.cheatToHotbarUsingHotkeysEnabled;
    }

    public ConfigValue<Boolean> showHiddenIngredients() {
        return this.showHiddenIngredients;
    }

    public ConfigValue<Boolean> showTagRecipesEnabled() {
        return this.showTagRecipesEnabled;
    }

    public ConfigValue<Boolean> addBookmarksToFrontEnabled() {
        return this.addBookmarksToFrontEnabled;
    }

    public ConfigValue<Boolean> bookmarkOutputAsRecipe() {
        return this.bookmarkOutputAsRecipe;
    }

    public ConfigValue<List<BookmarkTooltipFeature>> bookmarkTooltipFeatures() {
        return this.bookmarkTooltipFeatures;
    }

    public ConfigValue<Boolean> holdShiftToShowBookmarkTooltipFeaturesEnabled() {
        return this.holdShiftToShowBookmarkTooltipFeaturesEnabled;
    }

    public ConfigValue<Boolean> dragToRearrangeBookmarksEnabled() {
        return this.dragToRearrangeBookmarksEnabled;
    }

    public ConfigValue<Boolean> lookupHistoryEnabled() {
        return this.lookupHistoryEnabled;
    }

    public ConfigValue<Integer> maxLookupHistoryRows() {
        return this.maxLookupHistoryRows;
    }

    public ConfigValue<Integer> maxLookupHistoryIngredients() {
        return this.maxLookupHistoryIngredients;
    }

    public ConfigValue<HistoryDisplaySide> lookupHistoryDisplaySide() {
        return this.lookupHistoryDisplaySide;
    }

    public ConfigValue<Boolean> ingredientsSummaryEnabled() {
        return this.ingredientsSummaryEnabled;
    }

    public ConfigValue<Boolean> lowMemorySlowSearchEnabled() {
        return this.lowMemorySlowSearchEnabled;
    }

    public ConfigValue<Boolean> catchRenderErrorsEnabled() {
        return this.catchRenderErrorsEnabled;
    }

    public ConfigValue<Boolean> lookupFluidContentsEnabled() {
        return this.lookupFluidContentsEnabled;
    }

    public ConfigValue<Boolean> lookupBlockTagsEnabled() {
        return this.lookupBlockTagsEnabled;
    }

    public ConfigValue<Boolean> showCreativeTabNamesEnabled() {
        return this.showCreativeTabNamesEnabled;
    }

    public ConfigValue<Integer> dragDelayMs() {
        return this.dragDelayMs;
    }

    public ConfigValue<Integer> smoothScrollRate() {
        return this.smoothScrollRate;
    }

    public ConfigValue<List<IngredientSortStage>> ingredientSorterStages() {
        return this.ingredientSorterStages;
    }

    public ConfigValue<List<RecipeSorterStage>> recipeSorterStages() {
        return this.recipeSorterStages;
    }

    public ConfigValue<Boolean> tagContentTooltipEnabled() {
        return this.tagContentTooltipEnabled;
    }

    public ConfigValue<Boolean> hideSingleTagContentTooltipEnabled() {
        return this.hideSingleTagContentTooltipEnabled;
    }
}

