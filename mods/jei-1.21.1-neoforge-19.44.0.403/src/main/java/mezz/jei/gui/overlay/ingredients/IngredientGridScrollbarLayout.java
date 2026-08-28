/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.Set;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.ImmutableSize2i;
import mezz.jei.common.util.NavigationVisibility;
import mezz.jei.gui.overlay.ingredients.IngredientGridLayout;
import mezz.jei.gui.overlay.ingredients.IngredientGridPageState;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationLayout;
import mezz.jei.gui.util.AlignmentUtil;
import org.jetbrains.annotations.Nullable;

public final class IngredientGridScrollbarLayout {
    private IngredientGridScrollbarLayout() {
    }

    public static IngredientGridWithNavigationLayout calculate(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, int ingredientCount) {
        return switch (gridConfig.navigationVisibility().getValue()) {
            default -> throw new MatchException(null, null);
            case NavigationVisibility.ENABLED -> IngredientGridScrollbarLayout.calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true);
            case NavigationVisibility.DISABLED -> IngredientGridScrollbarLayout.calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false);
            case NavigationVisibility.AUTO_HIDE -> IngredientGridScrollbarLayout.calculateAutoHideScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, ingredientCount);
        };
    }

    private static IngredientGridWithNavigationLayout calculateAutoHideScrollbar(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, int ingredientCount) {
        boolean scrollbarEnabled;
        IngredientGridWithNavigationLayout layoutWithoutScrollbar = IngredientGridScrollbarLayout.calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false);
        int pageCountWithoutScrollbar = IngredientGridPageState.getPageCount(ingredientCount, layoutWithoutScrollbar.availableSlotCount());
        boolean bl = scrollbarEnabled = layoutWithoutScrollbar.hasRoom() && pageCountWithoutScrollbar > 1;
        if (scrollbarEnabled) {
            return IngredientGridScrollbarLayout.calculateForScrollbar(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true);
        }
        return layoutWithoutScrollbar;
    }

    private static IngredientGridWithNavigationLayout calculateForScrollbar(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, boolean scrollbarEnabled) {
        ImmutableRect2i availableGridArea = IngredientGridWithNavigationLayout.getAvailableGridArea(gridConfig, availableArea, false);
        ImmutableRect2i ingredientGridArea = scrollbarEnabled ? IngredientGridScrollbarLayout.calculateScrollbarGridArea(gridConfig, availableGridArea) : IngredientGridLayout.calculateBounds(gridConfig, availableGridArea);
        int availableSlotCount = IngredientGridLayout.calculateAvailableSlotCount(ingredientGridArea, guiExclusionAreas, mouseExclusionPoint);
        ImmutableRect2i slotBackgroundArea = IngredientGridWithNavigationLayout.calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
        return IngredientGridWithNavigationLayout.fromGridArea(gridConfig, ingredientGridArea, availableSlotCount, ImmutableRect2i.EMPTY, ImmutableRect2i.EMPTY, false, IngredientGridScrollbarLayout.calculateScrollbarArea(gridConfig, ingredientGridArea, slotBackgroundArea, scrollbarEnabled), scrollbarEnabled);
    }

    private static ImmutableRect2i calculateScrollbarGridArea(IIngredientGridConfig gridConfig, ImmutableRect2i availableGridArea) {
        if (availableGridArea.isEmpty()) {
            return ImmutableRect2i.EMPTY;
        }
        ImmutableRect2i availableAreaWithoutScrollbar = availableGridArea.cropRight(IngredientGridScrollbarLayout.calculateScrollbarReservedGridWidth(gridConfig));
        ImmutableSize2i ingredientGridSize = IngredientGridLayout.calculateSize(gridConfig, availableAreaWithoutScrollbar);
        if (ingredientGridSize.equals(ImmutableSize2i.EMPTY)) {
            return ImmutableRect2i.EMPTY;
        }
        return AlignmentUtil.align(ingredientGridSize, availableAreaWithoutScrollbar, gridConfig.horizontalAlignment().getValue(), gridConfig.verticalAlignment().getValue());
    }

    private static int calculateScrollbarExtraWidth(IIngredientGridConfig gridConfig) {
        return IngredientGridScrollbarLayout.calculateScrollbarOffsetFromGrid(gridConfig) + 14;
    }

    private static int calculateScrollbarReservedGridWidth(IIngredientGridConfig gridConfig) {
        int reservedGridWidth = IngredientGridScrollbarLayout.calculateScrollbarExtraWidth(gridConfig);
        if (gridConfig.drawBackground().getValue().booleanValue()) {
            return reservedGridWidth - 2;
        }
        return reservedGridWidth;
    }

    private static int calculateScrollbarOffsetFromGrid(IIngredientGridConfig gridConfig) {
        if (gridConfig.drawBackground().getValue().booleanValue()) {
            return 4;
        }
        return 0;
    }

    private static ImmutableRect2i calculateScrollbarArea(IIngredientGridConfig gridConfig, ImmutableRect2i ingredientGridArea, ImmutableRect2i slotBackgroundArea, boolean scrollbarEnabled) {
        if (!scrollbarEnabled || ingredientGridArea.isEmpty()) {
            return ImmutableRect2i.EMPTY;
        }
        return new ImmutableRect2i(ingredientGridArea.x() + ingredientGridArea.width() + IngredientGridScrollbarLayout.calculateScrollbarOffsetFromGrid(gridConfig), slotBackgroundArea.y(), 14, slotBackgroundArea.height());
    }
}

