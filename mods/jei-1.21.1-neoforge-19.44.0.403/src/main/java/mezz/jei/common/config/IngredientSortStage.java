/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import java.util.List;

public enum IngredientSortStage {
    MOD_NAME,
    INGREDIENT_TYPE,
    ALPHABETICAL,
    CREATIVE_MENU,
    TAG,
    ARMOR,
    MAX_DURABILITY;

    public static final List<IngredientSortStage> defaultStages;

    static {
        defaultStages = List.of(MOD_NAME, INGREDIENT_TYPE, CREATIVE_MENU);
    }
}

