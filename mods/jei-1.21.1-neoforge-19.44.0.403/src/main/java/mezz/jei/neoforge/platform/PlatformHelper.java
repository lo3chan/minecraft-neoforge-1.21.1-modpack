/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.neoforge.platform;

import java.util.function.Supplier;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.IPlatformHelper;
import mezz.jei.common.util.function.LazySupplier;
import mezz.jei.neoforge.platform.BrewingHelper;
import mezz.jei.neoforge.platform.ConfigHelper;
import mezz.jei.neoforge.platform.FluidHelper;
import mezz.jei.neoforge.platform.IngredientHelper;
import mezz.jei.neoforge.platform.InputHelper;
import mezz.jei.neoforge.platform.ItemStackHelper;
import mezz.jei.neoforge.platform.ModHelper;
import mezz.jei.neoforge.platform.RecipeHelper;
import mezz.jei.neoforge.platform.RenderHelper;
import mezz.jei.neoforge.platform.ScreenHelper;
import mezz.jei.neoforge.platform.WorldHelper;

public class PlatformHelper
implements IPlatformHelper {
    private final Supplier<ItemStackHelper> itemStackHelper = new LazySupplier<ItemStackHelper>(ItemStackHelper::new);
    private final Supplier<FluidHelper> fluidHelper = new LazySupplier<FluidHelper>(FluidHelper::new);
    private final Supplier<RenderHelper> renderHelper = new LazySupplier<RenderHelper>(RenderHelper::new);
    private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<RecipeHelper>(RecipeHelper::new);
    private final Supplier<BrewingHelper> brewingHelper = new LazySupplier<BrewingHelper>(BrewingHelper::new);
    private final Supplier<ConfigHelper> configHelper = new LazySupplier<ConfigHelper>(ConfigHelper::new);
    private final Supplier<InputHelper> inputHelper = new LazySupplier<InputHelper>(InputHelper::new);
    private final Supplier<ScreenHelper> screenHelper = new LazySupplier<ScreenHelper>(ScreenHelper::new);
    private final Supplier<IngredientHelper> ingredientHelper = new LazySupplier<IngredientHelper>(IngredientHelper::new);
    private final Supplier<ModHelper> modHelper = new LazySupplier<ModHelper>(ModHelper::new);
    private final Supplier<WorldHelper> worldHelper = new LazySupplier<WorldHelper>(WorldHelper::new);

    @Override
    public ItemStackHelper getItemStackHelper() {
        return this.itemStackHelper.get();
    }

    @Override
    public IPlatformFluidHelperInternal<?> getFluidHelper() {
        return this.fluidHelper.get();
    }

    @Override
    public RenderHelper getRenderHelper() {
        return this.renderHelper.get();
    }

    @Override
    public RecipeHelper getRecipeHelper() {
        return this.recipeHelper.get();
    }

    @Override
    public BrewingHelper getBrewingHelper() {
        return this.brewingHelper.get();
    }

    @Override
    public ConfigHelper getConfigHelper() {
        return this.configHelper.get();
    }

    @Override
    public InputHelper getInputHelper() {
        return this.inputHelper.get();
    }

    @Override
    public ScreenHelper getScreenHelper() {
        return this.screenHelper.get();
    }

    @Override
    public IngredientHelper getIngredientHelper() {
        return this.ingredientHelper.get();
    }

    @Override
    public ModHelper getModHelper() {
        return this.modHelper.get();
    }

    @Override
    public WorldHelper getWorldHelper() {
        return this.worldHelper.get();
    }
}

