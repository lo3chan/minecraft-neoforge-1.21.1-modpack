/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.recipes.layout.builder;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.drawable.TilingDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.elements.OffsetDrawable;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.Pair;
import mezz.jei.library.gui.ingredients.ICycler;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.gui.ingredients.RendererOverrides;
import mezz.jei.library.gui.recipes.layout.builder.LegacyTooltipCallbackAdapter;
import mezz.jei.library.ingredients.DisplayIngredientAcceptor;
import mezz.jei.library.render.FluidTankRenderer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class RecipeSlotBuilder
implements IRecipeSlotBuilder {
    private final DisplayIngredientAcceptor ingredients;
    private final RecipeIngredientRole role;
    private final List<IRecipeSlotRichTooltipCallback> tooltipCallbacks = new ArrayList<IRecipeSlotRichTooltipCallback>();
    private final int slotIndex;
    private ImmutableRect2i rect;
    @Nullable
    private RendererOverrides rendererOverrides;
    @Nullable
    private OffsetDrawable background;
    @Nullable
    private IDrawable overlay;
    @Nullable
    private String slotName;
    @Nullable
    private ISlottedWidgetFactory<?> assignedWidgetFactory;

    public RecipeSlotBuilder(IIngredientManager ingredientManager, int slotIndex, RecipeIngredientRole role) {
        this.ingredients = new DisplayIngredientAcceptor(ingredientManager);
        this.rect = new ImmutableRect2i(0, 0, 16, 16);
        this.role = role;
        this.slotIndex = slotIndex;
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredients(IIngredientType<I> ingredientType, List<@Nullable I> ingredients) {
        this.ingredients.addIngredients((IIngredientType)ingredientType, (List)ingredients);
        return this;
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredient(IIngredientType<I> ingredientType, I ingredient) {
        this.ingredients.addIngredient((IIngredientType)ingredientType, (Object)ingredient);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid) {
        this.ingredients.addFluidStack(fluid);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount) {
        this.ingredients.addFluidStack(fluid, amount);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount, DataComponentPatch componentPatch) {
        this.ingredients.addFluidStack(fluid, amount, componentPatch);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addIngredientsUnsafe(List<?> ingredients) {
        this.ingredients.addIngredientsUnsafe((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
        this.ingredients.addTypedIngredients((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
        this.ingredients.addOptionalTypedIngredients((List)ingredients);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setStandardSlotBackground() {
        IGuiHelper guiHelper = Internal.getJeiRuntime().getJeiHelpers().getGuiHelper();
        IDrawableStatic background = guiHelper.getSlotDrawable();
        this.background = new OffsetDrawable(background, -1, -1);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setOutputSlotBackground() {
        IGuiHelper guiHelper = Internal.getJeiRuntime().getJeiHelpers().getGuiHelper();
        IDrawableStatic background = guiHelper.getOutputSlot();
        this.background = new OffsetDrawable(background, -5, -5);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setBackground(IDrawable background, int xOffset, int yOffset) {
        ErrorUtil.checkNotNull(background, "background");
        this.background = new OffsetDrawable(background, xOffset, yOffset);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setOverlay(IDrawable overlay, int xOffset, int yOffset) {
        ErrorUtil.checkNotNull(overlay, "overlay");
        this.overlay = OffsetDrawable.create(overlay, xOffset, yOffset);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height) {
        return this.setFluidRenderer(capacity, showCapacity, width, height, TilingDirection.UP_RIGHT);
    }

    @Override
    public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height, TilingDirection tilingDirection) {
        Preconditions.checkArgument((capacity > 0L ? 1 : 0) != 0, (Object)"capacity must be > 0");
        ErrorUtil.checkNotNull(tilingDirection, "tilingDirection");
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.setFluidRenderer(fluidHelper, capacity, showCapacity, width, height, tilingDirection);
    }

    private <T> IRecipeSlotBuilder setFluidRenderer(IPlatformFluidHelperInternal<T> fluidHelper, long capacity, boolean showCapacity, int width, int height, TilingDirection tilingDirection) {
        IIngredientTypeWithSubtypes type = fluidHelper.getFluidIngredientType();
        IIngredientRenderer<T> renderer = RecipeSlotBuilder.createFluidRenderer(fluidHelper, type, capacity, showCapacity, width, height, tilingDirection);
        this.addRenderOverride(type, renderer);
        return this;
    }

    private static <T> IIngredientRenderer<T> createFluidRenderer(IPlatformFluidHelperInternal<T> fluidHelper, IIngredientTypeWithSubtypes<Fluid, T> type, long capacity, boolean showCapacity, int width, int height, TilingDirection tilingDirection) {
        return new FluidTankRenderer<T>(fluidHelper, type, capacity, showCapacity, width, height, tilingDirection);
    }

    @Override
    public <T> IRecipeSlotBuilder setCustomRenderer(IIngredientType<T> ingredientType, IIngredientRenderer<T> ingredientRenderer) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredientRenderer, "ingredientRenderer");
        this.addRenderOverride(ingredientType, ingredientRenderer);
        return this;
    }

    @Override
    public IRecipeSlotBuilder addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
        ErrorUtil.checkNotNull(tooltipCallback, "tooltipCallback");
        this.tooltipCallbacks.add(new LegacyTooltipCallbackAdapter(tooltipCallback));
        return this;
    }

    @Override
    public IRecipeSlotBuilder addRichTooltipCallback(IRecipeSlotRichTooltipCallback tooltipCallback) {
        ErrorUtil.checkNotNull(tooltipCallback, "tooltipCallback");
        this.tooltipCallbacks.add(tooltipCallback);
        return this;
    }

    @Override
    public IRecipeSlotBuilder setSlotName(String slotName) {
        ErrorUtil.checkNotNull(slotName, "slotName");
        this.slotName = slotName;
        return this;
    }

    @Override
    public int getWidth() {
        return this.rect.width();
    }

    @Override
    public int getHeight() {
        return this.rect.height();
    }

    @Override
    public IRecipeSlotBuilder setPosition(int xPos, int yPos) {
        this.rect = this.rect.setPosition(xPos, yPos);
        return this;
    }

    public RecipeSlotBuilder assignToWidgetFactory(ISlottedWidgetFactory<?> widgetFactory) {
        ErrorUtil.checkNotNull(widgetFactory, "widgetFactory");
        this.assignedWidgetFactory = widgetFactory;
        return this;
    }

    @Nullable
    public ISlottedWidgetFactory<?> getAssignedWidget() {
        return this.assignedWidgetFactory;
    }

    public Pair<Integer, IRecipeSlotDrawable> build(IFocusGroup focusGroup, ICycler cycler) {
        IntSet focusMatches = this.getMatches(focusGroup);
        return this.build((Set<Integer>)focusMatches, cycler);
    }

    public Pair<Integer, IRecipeSlotDrawable> build(Set<Integer> focusMatches, ICycler cycler) {
        List<@Nullable ITypedIngredient<?>> allIngredients = this.ingredients.getAllIngredients();
        ArrayList focusedIngredients = null;
        if (!focusMatches.isEmpty()) {
            focusedIngredients = new ArrayList();
            for (Integer i : focusMatches) {
                if (i >= allIngredients.size()) continue;
                @Nullable ITypedIngredient<?> ingredient = allIngredients.get(i);
                focusedIngredients.add(ingredient);
            }
        }
        RecipeSlot recipeSlot = new RecipeSlot(this.role, this.rect, cycler, this.tooltipCallbacks, allIngredients, focusedIngredients, this.background, this.overlay, this.slotName, this.rendererOverrides);
        return new Pair<Integer, IRecipeSlotDrawable>(this.slotIndex, recipeSlot);
    }

    public IntSet getMatches(IFocusGroup focuses) {
        return this.ingredients.getMatches(focuses, this.role);
    }

    public DisplayIngredientAcceptor getIngredientAcceptor() {
        return this.ingredients;
    }

    public RecipeIngredientRole getRole() {
        return this.role;
    }

    private <T> void addRenderOverride(IIngredientType<T> ingredientType, IIngredientRenderer<T> ingredientRenderer) {
        if (this.rendererOverrides == null) {
            this.rendererOverrides = new RendererOverrides();
        }
        this.rendererOverrides.addOverride(ingredientType, ingredientRenderer);
        this.rect = new ImmutableRect2i(this.rect.getX(), this.rect.getY(), this.rendererOverrides.getIngredientWidth(), this.rendererOverrides.getIngredientHeight());
    }
}

