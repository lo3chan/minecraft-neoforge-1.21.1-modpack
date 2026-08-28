/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.platform;

import mezz.jei.common.platform.IPlatformBrewingHelper;
import mezz.jei.common.platform.IPlatformConfigHelper;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.IPlatformInputHelper;
import mezz.jei.common.platform.IPlatformItemStackHelper;
import mezz.jei.common.platform.IPlatformModHelper;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.IPlatformWorldHelper;

public interface IPlatformHelper {
    public IPlatformItemStackHelper getItemStackHelper();

    public IPlatformFluidHelperInternal<?> getFluidHelper();

    public IPlatformRenderHelper getRenderHelper();

    public IPlatformRecipeHelper getRecipeHelper();

    public IPlatformBrewingHelper getBrewingHelper();

    public IPlatformConfigHelper getConfigHelper();

    public IPlatformInputHelper getInputHelper();

    public IPlatformScreenHelper getScreenHelper();

    public IPlatformIngredientHelper getIngredientHelper();

    public IPlatformModHelper getModHelper();

    public IPlatformWorldHelper getWorldHelper();
}

