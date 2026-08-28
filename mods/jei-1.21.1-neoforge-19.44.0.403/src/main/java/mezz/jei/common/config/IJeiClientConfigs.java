/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config;

import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.common.config.IIngredientGridConfig;

public interface IJeiClientConfigs {
    public IClientConfig getClientConfig();

    public IIngredientFilterConfig getIngredientFilterConfig();

    public IIngredientGridConfig getIngredientListConfig();

    public IIngredientGridConfig getBookmarkListConfig();

    public void onRuntimeStopped();
}

