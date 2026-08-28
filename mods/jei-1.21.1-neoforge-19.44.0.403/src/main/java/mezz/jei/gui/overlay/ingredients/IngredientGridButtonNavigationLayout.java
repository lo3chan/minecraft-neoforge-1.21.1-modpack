/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import mezz.jei.common.config.IIngredientGridConfig;
import mezz.jei.common.config.IngredientGridLayoutMode;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.NavigationVisibility;
import mezz.jei.gui.overlay.ingredients.IngredientGridLayout;
import mezz.jei.gui.overlay.ingredients.IngredientGridPageState;
import mezz.jei.gui.overlay.ingredients.IngredientGridWithNavigationLayout;
import org.jetbrains.annotations.Nullable;

public final class IngredientGridButtonNavigationLayout {
    private static final int NAVIGATION_MIN_WIDTH = 27;

    private IngredientGridButtonNavigationLayout() {
    }

    public static IngredientGridWithNavigationLayout calculate(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, int ingredientCount) {
        return switch (gridConfig.navigationVisibility().getValue()) {
            default -> throw new MatchException(null, null);
            case NavigationVisibility.ENABLED -> IngredientGridButtonNavigationLayout.calculateForNavigation(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true);
            case NavigationVisibility.DISABLED -> IngredientGridButtonNavigationLayout.calculateForNavigation(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false);
            case NavigationVisibility.AUTO_HIDE -> IngredientGridButtonNavigationLayout.calculateAutoHide(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, ingredientCount);
        };
    }

    private static IngredientGridWithNavigationLayout calculateAutoHide(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, int ingredientCount) {
        boolean navigationEnabled;
        IngredientGridWithNavigationLayout layoutWithoutNavigation = IngredientGridButtonNavigationLayout.calculateForNavigation(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, false);
        int pageCountWithoutNavigation = IngredientGridPageState.getPageCount(ingredientCount, layoutWithoutNavigation.availableSlotCount());
        boolean bl = navigationEnabled = layoutWithoutNavigation.hasRoom() && pageCountWithoutNavigation > 1;
        if (navigationEnabled) {
            return IngredientGridButtonNavigationLayout.calculateForNavigation(gridConfig, availableArea, guiExclusionAreas, mouseExclusionPoint, true);
        }
        return layoutWithoutNavigation;
    }

    private static IngredientGridWithNavigationLayout calculateForNavigation(IIngredientGridConfig gridConfig, ImmutableRect2i availableArea, Set<ImmutableRect2i> guiExclusionAreas, @Nullable ImmutablePoint2i mouseExclusionPoint, boolean navigationEnabled) {
        int shiftY;
        ImmutableRect2i effectiveArea = availableArea;
        ImmutableRect2i availableGridArea = IngredientGridWithNavigationLayout.getAvailableGridArea(gridConfig, effectiveArea);
        ImmutableRect2i ingredientGridArea = IngredientGridLayout.calculateBounds(gridConfig, availableGridArea);
        int availableSlotCount = IngredientGridLayout.calculateAvailableSlotCount(ingredientGridArea, guiExclusionAreas, mouseExclusionPoint);
        ImmutableRect2i slotBackgroundArea = IngredientGridWithNavigationLayout.calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
        ImmutableRect2i defaultNavigationArea = IngredientGridWithNavigationLayout.calculateNavigationArea(slotBackgroundArea, navigationEnabled);
        ImmutableRect2i navigationArea = IngredientGridButtonNavigationLayout.calculateNavigationArea(defaultNavigationArea, slotBackgroundArea, guiExclusionAreas, gridConfig);
        if (navigationEnabled && navigationArea.isEmpty() && !defaultNavigationArea.isEmpty() && (shiftY = IngredientGridButtonNavigationLayout.calculateNavigationShiftY(effectiveArea, slotBackgroundArea, guiExclusionAreas, gridConfig)) > effectiveArea.y()) {
            int effectiveAreaBottom = effectiveArea.y() + effectiveArea.height();
            shiftY = Math.min(shiftY, effectiveAreaBottom);
            effectiveArea = new ImmutableRect2i(effectiveArea.x(), shiftY, effectiveArea.width(), effectiveAreaBottom - shiftY);
            availableGridArea = IngredientGridWithNavigationLayout.getAvailableGridArea(gridConfig, effectiveArea);
            ingredientGridArea = IngredientGridLayout.calculateBounds(gridConfig, availableGridArea);
            availableSlotCount = IngredientGridLayout.calculateAvailableSlotCount(ingredientGridArea, guiExclusionAreas, mouseExclusionPoint);
            slotBackgroundArea = IngredientGridWithNavigationLayout.calculateSlotBackgroundArea(ingredientGridArea, gridConfig);
            defaultNavigationArea = IngredientGridWithNavigationLayout.calculateNavigationArea(slotBackgroundArea, navigationEnabled);
            navigationArea = IngredientGridButtonNavigationLayout.calculateNavigationArea(defaultNavigationArea, slotBackgroundArea, guiExclusionAreas, gridConfig);
        }
        ImmutableRect2i backgroundNavigationArea = navigationArea.isEmpty() ? ImmutableRect2i.EMPTY : defaultNavigationArea;
        return IngredientGridWithNavigationLayout.fromGridArea(gridConfig, ingredientGridArea, availableSlotCount, navigationArea, backgroundNavigationArea, navigationEnabled, ImmutableRect2i.EMPTY, false);
    }

    private static ImmutableRect2i calculateNavigationArea(ImmutableRect2i defaultNavigationArea, ImmutableRect2i slotBackgroundArea, Set<ImmutableRect2i> guiExclusionAreas, IIngredientGridConfig gridConfig) {
        if (gridConfig.layoutMode().getValue() == IngredientGridLayoutMode.RECTANGULAR) {
            boolean blocked = guiExclusionAreas.stream().anyMatch(defaultNavigationArea::intersects);
            if (blocked) {
                return ImmutableRect2i.EMPTY;
            }
            return defaultNavigationArea;
        }
        return IngredientGridButtonNavigationLayout.calculateNavigationAreaAvoidingExclusions(defaultNavigationArea, slotBackgroundArea, guiExclusionAreas, gridConfig);
    }

    private static int calculateNavigationShiftY(ImmutableRect2i availableArea, ImmutableRect2i slotBackgroundArea, Set<ImmutableRect2i> guiExclusionAreas, IIngredientGridConfig gridConfig) {
        int padding = gridConfig.drawBackground().getValue() != false ? 7 : 0;
        int stripTop = availableArea.y() + 6;
        int stripHeight = 22 + 2 * padding;
        ImmutableRect2i navigationStripArea = IngredientGridButtonNavigationLayout.calculateNavigationStripArea(slotBackgroundArea, stripTop, stripHeight, gridConfig);
        int shiftY = availableArea.y();
        for (ImmutableRect2i exclusion : guiExclusionAreas) {
            if (!exclusion.intersects(navigationStripArea)) continue;
            shiftY = Math.max(shiftY, exclusion.getY() + exclusion.getHeight());
        }
        return shiftY;
    }

    private static ImmutableRect2i calculateNavigationAreaAvoidingExclusions(ImmutableRect2i defaultNavigationArea, ImmutableRect2i slotBackgroundArea, Set<ImmutableRect2i> guiExclusionAreas, IIngredientGridConfig gridConfig) {
        int originalWidth;
        int originalRight;
        if (defaultNavigationArea.isEmpty()) {
            return ImmutableRect2i.EMPTY;
        }
        if (guiExclusionAreas.stream().noneMatch(defaultNavigationArea::intersects)) {
            return defaultNavigationArea;
        }
        ImmutableRect2i navigationStripArea = IngredientGridButtonNavigationLayout.calculateNavigationStripArea(slotBackgroundArea, defaultNavigationArea.y(), defaultNavigationArea.height(), gridConfig);
        int stripX = navigationStripArea.x();
        int stripWidth = navigationStripArea.width();
        ArrayList<int[]> excludedRanges = new ArrayList<int[]>();
        for (ImmutableRect2i exclusion : guiExclusionAreas) {
            int exclEnd;
            int exclStart;
            if (!exclusion.intersects(navigationStripArea) || (exclStart = Math.max(exclusion.getX(), stripX)) >= (exclEnd = Math.min(exclusion.getX() + exclusion.getWidth(), stripX + stripWidth))) continue;
            excludedRanges.add(new int[]{exclStart, exclEnd});
        }
        if (excludedRanges.isEmpty()) {
            return defaultNavigationArea;
        }
        excludedRanges.sort(Comparator.comparingInt(a -> a[0]));
        ArrayList<int[]> gaps = new ArrayList<int[]>();
        int currentX = stripX;
        for (int[] range : excludedRanges) {
            if (range[0] > currentX) {
                gaps.add(new int[]{currentX, range[0]});
            }
            currentX = Math.max(currentX, range[1]);
        }
        if (currentX < stripX + stripWidth) {
            gaps.add(new int[]{currentX, stripX + stripWidth});
        }
        if (gaps.isEmpty()) {
            return ImmutableRect2i.EMPTY;
        }
        int originalX = defaultNavigationArea.x();
        ImmutableRect2i navigationArea = IngredientGridButtonNavigationLayout.calculateNavigationAreaInGaps(gaps, originalX, originalRight = originalX + (originalWidth = defaultNavigationArea.width()), originalX, originalWidth, navigationStripArea.y(), navigationStripArea.height());
        if (!navigationArea.isEmpty()) {
            return navigationArea;
        }
        return ImmutableRect2i.EMPTY;
    }

    private static ImmutableRect2i calculateNavigationAreaInGaps(List<int[]> gaps, int minX, int maxX, int originalX, int originalWidth, int y, int height) {
        int bestGapStart = -1;
        int bestGapWidth = 0;
        int bestDistance = Integer.MAX_VALUE;
        for (int[] gap : gaps) {
            int navWidthInGap;
            int navStart;
            int distance;
            int gapStart = Math.max(gap[0], minX);
            int gapEnd = Math.min(gap[1], maxX);
            int gapWidth = gapEnd - gapStart;
            if (gapWidth < 27 || (distance = Math.abs((navStart = Math.clamp((long)originalX, (int)gapStart, (int)(gapEnd - (navWidthInGap = Math.min(originalWidth, gapWidth))))) - originalX)) >= bestDistance) continue;
            bestGapStart = navStart;
            bestGapWidth = navWidthInGap;
            bestDistance = distance;
        }
        if (bestGapStart < 0) {
            return ImmutableRect2i.EMPTY;
        }
        return new ImmutableRect2i(bestGapStart, y, bestGapWidth, height);
    }

    private static ImmutableRect2i calculateNavigationStripArea(ImmutableRect2i slotBackgroundArea, int y, int height, IIngredientGridConfig gridConfig) {
        int x = slotBackgroundArea.x();
        int right = slotBackgroundArea.x() + slotBackgroundArea.width();
        if (gridConfig.drawBackground().getValue().booleanValue()) {
            x -= 5;
            right += 5;
        }
        return new ImmutableRect2i(x, y, right - x, height);
    }
}

