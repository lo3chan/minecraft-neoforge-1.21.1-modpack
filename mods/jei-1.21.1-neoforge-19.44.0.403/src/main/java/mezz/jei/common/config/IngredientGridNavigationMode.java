/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

public enum IngredientGridNavigationMode {
    PAGED,
    SCROLLING,
    SMOOTH_SCROLLING;


    public boolean usesScrollbar() {
        return this != PAGED;
    }

    public boolean usesSmoothScrolling() {
        return this == SMOOTH_SCROLLING;
    }
}

