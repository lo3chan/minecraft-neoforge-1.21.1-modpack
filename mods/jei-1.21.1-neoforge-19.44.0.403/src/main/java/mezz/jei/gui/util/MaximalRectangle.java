/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;

public class MaximalRectangle {
    private final ImmutableRect2i area;
    private final int rows;
    private final int columns;
    private final int samplingScale;
    private final boolean[][] blockedAreas;

    public static Stream<ImmutableRect2i> getLargestRectangles(ImmutableRect2i area, Collection<ImmutableRect2i> exclusionAreas, int samplingScale) {
        if ((exclusionAreas = (Collection)exclusionAreas.stream().filter(area::intersects).collect(Collectors.toUnmodifiableSet())).isEmpty()) {
            return Stream.of(area);
        }
        MaximalRectangle maximalRectangle = new MaximalRectangle(area, samplingScale, exclusionAreas);
        return maximalRectangle.getLargestRectangles();
    }

    private MaximalRectangle(ImmutableRect2i area, int samplingScale, Collection<ImmutableRect2i> exclusionAreas) {
        this.area = area;
        this.samplingScale = samplingScale;
        this.rows = area.getHeight() / samplingScale;
        this.columns = area.getWidth() / samplingScale;
        this.blockedAreas = new boolean[this.rows][this.columns];
        for (int row = 0; row < this.rows; ++row) {
            for (int column = 0; column < this.columns; ++column) {
                boolean intersects;
                ImmutableRect2i rect = this.getRect(row, column, 1, 1);
                this.blockedAreas[row][column] = intersects = MathUtil.intersects(exclusionAreas, rect);
            }
        }
    }

    private ImmutableRect2i getRect(int row, int column, int width, int height) {
        if (width == 0 || height == 0) {
            return ImmutableRect2i.EMPTY;
        }
        return new ImmutableRect2i(this.area.getX() + column * this.samplingScale, this.area.getY() + row * this.samplingScale, width * this.samplingScale, height * this.samplingScale);
    }

    private Stream<ImmutableRect2i> getLargestRectangles() {
        if (this.rows == 0 || this.columns == 0) {
            return Stream.empty();
        }
        int[] heights = new int[this.columns];
        int[] leftIndexes = new int[this.columns];
        int[] rightIndexes = new int[this.columns];
        Arrays.fill(rightIndexes, this.columns);
        return IntStream.range(0, this.rows).boxed().flatMap(row -> {
            int currentLeftIndex = 0;
            for (int column2 = 0; column2 < this.columns; ++column2) {
                if (this.blockedAreas[row][column2]) {
                    heights[column2] = 0;
                    leftIndexes[column2] = 0;
                    currentLeftIndex = column2 + 1;
                    continue;
                }
                int n = column2;
                heights[n] = heights[n] + 1;
                leftIndexes[column2] = Math.max(leftIndexes[column2], currentLeftIndex);
            }
            int currentRightIndex = this.columns;
            for (int column3 = this.columns - 1; column3 >= 0; --column3) {
                if (this.blockedAreas[row][column3]) {
                    rightIndexes[column3] = this.columns;
                    currentRightIndex = column3;
                    continue;
                }
                rightIndexes[column3] = Math.min(rightIndexes[column3], currentRightIndex);
            }
            return IntStream.range(0, this.columns).mapToObj(column -> {
                int rightIndex = rightIndexes[column];
                int leftIndex = leftIndexes[column];
                int width = rightIndex - leftIndex;
                int height = heights[column];
                return this.getRect(row - height + 1, leftIndex, width, height);
            }).filter(r -> !r.isEmpty());
        });
    }
}

