/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.recipes;

final class RecipeGuiSizing {
    private RecipeGuiSizing() {
    }

    static Size calculateInitialSize(int screenHeight, boolean centerSearchBarEnabled, int maxHeight) {
        int ySize = centerSearchBarEnabled ? screenHeight - 76 : screenHeight - 58;
        if (ySize < 175) {
            ySize = 175;
        }
        int extraSpace = 0;
        if (ySize > maxHeight) {
            extraSpace = ySize - maxHeight;
            ySize = maxHeight;
        }
        return new Size(ySize, extraSpace);
    }

    record Size(int ySize, int extraSpace) {
    }
}

