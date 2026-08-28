/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import java.util.List;

public enum BookmarkTooltipFeature {
    PREVIEW,
    INGREDIENTS;

    public static final List<BookmarkTooltipFeature> DEFAULT_BOOKMARK_TOOLTIP_FEATURES;

    static {
        DEFAULT_BOOKMARK_TOOLTIP_FEATURES = List.of(PREVIEW);
    }
}

