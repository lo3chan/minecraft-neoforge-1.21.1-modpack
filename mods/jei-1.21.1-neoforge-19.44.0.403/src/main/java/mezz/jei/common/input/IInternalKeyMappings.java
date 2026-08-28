/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.input;

import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.api.runtime.IJeiKeyMappings;
import mezz.jei.common.input.keys.IJeiKeyMappingInternal;

public interface IInternalKeyMappings
extends IJeiKeyMappings {
    public IJeiKeyMapping getToggleOverlay();

    public IJeiKeyMapping getFocusSearch();

    public IJeiKeyMapping getToggleCheatMode();

    public IJeiKeyMapping getToggleEditMode();

    public IJeiKeyMapping getToggleCheatModeConfigButton();

    public IJeiKeyMapping getRecipeBack();

    public IJeiKeyMapping getPreviousCategory();

    public IJeiKeyMapping getNextCategory();

    public IJeiKeyMapping getPreviousRecipePage();

    public IJeiKeyMapping getNextRecipePage();

    public IJeiKeyMappingInternal getPauseRecipeCycling();

    public IJeiKeyMapping getPreviousPage();

    public IJeiKeyMapping getNextPage();

    public IJeiKeyMapping getCloseRecipeGui();

    public IJeiKeyMapping getBookmark();

    public IJeiKeyMapping getToggleBookmarkOverlay();

    @Override
    public IJeiKeyMapping getShowRecipe();

    @Override
    public IJeiKeyMapping getShowUses();

    public IJeiKeyMapping getTransferRecipeBookmark();

    public IJeiKeyMapping getMaxTransferRecipeBookmark();

    public IJeiKeyMappingInternal getShowBookmarkTooltipFeatures();

    public IJeiKeyMapping getQuickMove();

    public IJeiKeyMapping getShareToChat();

    public IJeiKeyMapping getCheatOneItem();

    public IJeiKeyMapping getCheatItemStack();

    public IJeiKeyMapping getToggleHideIngredient();

    public IJeiKeyMapping getToggleWildcardHideIngredient();

    public IJeiKeyMapping getHoveredClearSearchBar();

    public IJeiKeyMapping getPreviousSearch();

    public IJeiKeyMapping getNextSearch();

    public IJeiKeyMapping getCopyRecipeId();

    public IJeiKeyMapping getEscapeKey();

    public IJeiKeyMapping getLeftClick();

    public IJeiKeyMapping getRightClick();

    public IJeiKeyMapping getEnterKey();
}

