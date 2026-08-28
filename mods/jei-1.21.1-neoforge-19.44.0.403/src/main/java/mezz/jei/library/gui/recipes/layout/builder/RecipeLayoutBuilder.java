/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.recipes.layout.builder;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import mezz.jei.common.collect.ListMultiMap;
import mezz.jei.common.gui.elements.HighResolutionDrawable;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.Pair;
import mezz.jei.library.gui.ingredients.CycleTicker;
import mezz.jei.library.gui.ingredients.ICycler;
import mezz.jei.library.gui.recipes.IngredientsTooltipCallback;
import mezz.jei.library.gui.recipes.OutputSlotTooltipCallback;
import mezz.jei.library.gui.recipes.RecipeLayout;
import mezz.jei.library.gui.recipes.ShapelessIcon;
import mezz.jei.library.gui.recipes.layout.builder.RecipeSlotBuilder;
import mezz.jei.library.ingredients.DisplayIngredientAcceptor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RecipeLayoutBuilder<T>
implements IRecipeLayoutBuilder {
    private final List<RecipeSlotBuilder> visibleSlots = new ArrayList<RecipeSlotBuilder>();
    private final List<List<RecipeSlotBuilder>> focusLinkedSlots = new ArrayList<List<RecipeSlotBuilder>>();
    private final IIngredientManager ingredientManager;
    private final IRecipeCategory<T> recipeCategory;
    private final T recipe;
    private boolean shapeless = false;
    private int shapelessX = -1;
    private int shapelessY = -1;
    private int recipeTransferX = -1;
    private int recipeTransferY = -1;
    private int nextSlotIndex = 0;

    public RecipeLayoutBuilder(IRecipeCategory<T> recipeCategory, T recipe, IIngredientManager ingredientManager) {
        this.recipeCategory = recipeCategory;
        this.recipe = recipe;
        this.ingredientManager = ingredientManager;
    }

    @Override
    public IRecipeSlotBuilder addSlot(RecipeIngredientRole role) {
        RecipeSlotBuilder slot = new RecipeSlotBuilder(this.ingredientManager, this.nextSlotIndex++, role);
        if (role == RecipeIngredientRole.OUTPUT) {
            this.addOutputSlotTooltipCallback(slot);
        }
        this.visibleSlots.add(slot);
        return slot;
    }

    @Override
    @Deprecated
    public IRecipeSlotBuilder addSlotToWidget(RecipeIngredientRole role, ISlottedWidgetFactory<?> widgetFactory) {
        RecipeSlotBuilder slot = new RecipeSlotBuilder(this.ingredientManager, this.nextSlotIndex++, role).assignToWidgetFactory(widgetFactory);
        if (role == RecipeIngredientRole.OUTPUT) {
            this.addOutputSlotTooltipCallback(slot);
        }
        this.visibleSlots.add(slot);
        return slot;
    }

    private void addOutputSlotTooltipCallback(RecipeSlotBuilder slot) {
        ResourceLocation recipeName = this.recipeCategory.getRegistryName(this.recipe);
        if (recipeName != null) {
            RecipeType<T> recipeType = this.recipeCategory.getRecipeType();
            OutputSlotTooltipCallback callback = new OutputSlotTooltipCallback(recipeName, recipeType);
            slot.addRichTooltipCallback(callback);
        }
    }

    @Override
    public IIngredientAcceptor<?> addInvisibleIngredients(RecipeIngredientRole role) {
        return new RecipeSlotBuilder(this.ingredientManager, this.nextSlotIndex++, role);
    }

    @Override
    public void moveRecipeTransferButton(int posX, int posY) {
        this.recipeTransferX = posX;
        this.recipeTransferY = posY;
    }

    @Override
    public void setShapeless() {
        this.shapeless = true;
    }

    @Override
    public void setShapeless(int posX, int posY) {
        this.shapeless = true;
        this.shapelessX = posX;
        this.shapelessY = posY;
    }

    @Override
    public void createFocusLink(IIngredientAcceptor<?> ... slots) {
        ArrayList<RecipeSlotBuilder> builders = new ArrayList<RecipeSlotBuilder>();
        int count = -1;
        for (IIngredientAcceptor<?> slot : slots) {
            RecipeSlotBuilder builder = (RecipeSlotBuilder)slot;
            builders.add(builder);
            DisplayIngredientAcceptor displayIngredientAcceptor = builder.getIngredientAcceptor();
            List<@Nullable ITypedIngredient<?>> allIngredients = displayIngredientAcceptor.getAllIngredients();
            int ingredientCount = allIngredients.size();
            if (count == -1) {
                count = ingredientCount;
                continue;
            }
            if (count == ingredientCount) continue;
            IntSummaryStatistics stats = Arrays.stream(slots).map(RecipeSlotBuilder.class::cast).map(RecipeSlotBuilder::getIngredientAcceptor).map(DisplayIngredientAcceptor::getAllIngredients).mapToInt(Collection::size).summaryStatistics();
            throw new IllegalArgumentException("All slots must have the same number of ingredients in order to create a focus link. " + String.format("slot stats: %s", stats));
        }
        this.focusLinkedSlots.add(builders);
    }

    public RecipeLayout<T> buildRecipeLayout(IFocusGroup focuses, Collection<IRecipeCategoryDecorator<T>> decorators, IScalableDrawable recipeBackground, int recipeBorderPadding) {
        ShapelessIcon shapelessIcon = this.createShapelessIcon(this.recipeCategory);
        ImmutablePoint2i recipeTransferButtonPosition = this.getRecipeTransferButtonPosition(this.recipeCategory, recipeBorderPadding);
        ArrayList<Pair<Integer, IRecipeSlotDrawable>> recipeCategorySlots = new ArrayList<Pair<Integer, IRecipeSlotDrawable>>();
        ArrayList<Pair<Integer, IRecipeSlotDrawable>> allSlots = new ArrayList<Pair<Integer, IRecipeSlotDrawable>>();
        ListMultiMap widgetSlots = new ListMultiMap();
        CycleTicker cycleTicker = CycleTicker.createWithRandomOffset();
        HashSet<RecipeSlotBuilder> focusLinkedSlots = new HashSet<RecipeSlotBuilder>();
        for (List<RecipeSlotBuilder> list : this.focusLinkedSlots) {
            IntArraySet focusMatches = new IntArraySet();
            for (RecipeSlotBuilder slot : list) {
                focusMatches.addAll((IntCollection)slot.getMatches(focuses));
            }
            for (RecipeSlotBuilder slotBuilder : list) {
                if (!this.visibleSlots.contains(slotBuilder)) continue;
                ISlottedWidgetFactory<?> assignedWidget = slotBuilder.getAssignedWidget();
                Pair<Integer, IRecipeSlotDrawable> slotDrawable = slotBuilder.build((Set<Integer>)focusMatches, (ICycler)cycleTicker);
                if (assignedWidget == null) {
                    recipeCategorySlots.add(slotDrawable);
                } else {
                    widgetSlots.put(assignedWidget, slotDrawable);
                }
                allSlots.add(slotDrawable);
            }
            focusLinkedSlots.addAll(list);
        }
        class LayoutSupplier
        implements Supplier<IRecipeLayoutDrawable<?>> {
            @Nullable
            private IRecipeLayoutDrawable<?> drawable;

            LayoutSupplier(RecipeLayoutBuilder this$0) {
            }

            @Override
            @Nullable
            public IRecipeLayoutDrawable<?> get() {
                return this.drawable;
            }
        }
        LayoutSupplier layoutSupplier = new LayoutSupplier(this);
        for (RecipeSlotBuilder slotBuilder : this.visibleSlots) {
            if (focusLinkedSlots.contains(slotBuilder)) continue;
            ISlottedWidgetFactory<?> assignedWidget = slotBuilder.getAssignedWidget();
            if (slotBuilder.getRole() == RecipeIngredientRole.OUTPUT) {
                slotBuilder.addRichTooltipCallback(new IngredientsTooltipCallback(layoutSupplier));
            }
            Pair<Integer, IRecipeSlotDrawable> slotDrawable = slotBuilder.build(focuses, (ICycler)cycleTicker);
            if (assignedWidget == null) {
                recipeCategorySlots.add(slotDrawable);
            } else {
                widgetSlots.put(assignedWidget, slotDrawable);
            }
            allSlots.add(slotDrawable);
        }
        RecipeLayout<T> recipeLayout = new RecipeLayout<T>(this.recipeCategory, decorators, this.recipe, recipeBackground, recipeBorderPadding, shapelessIcon, recipeTransferButtonPosition, RecipeLayoutBuilder.sortSlots(recipeCategorySlots), RecipeLayoutBuilder.sortSlots(allSlots), cycleTicker, focuses);
        layoutSupplier.drawable = recipeLayout;
        for (Map.Entry e : widgetSlots.entrySet()) {
            ISlottedWidgetFactory factory = (ISlottedWidgetFactory)e.getKey();
            List<IRecipeSlotDrawable> slots = RecipeLayoutBuilder.sortSlots((List)e.getValue());
            factory.createWidgetForSlots(recipeLayout, this.recipe, slots);
        }
        return recipeLayout;
    }

    private static List<IRecipeSlotDrawable> sortSlots(List<Pair<Integer, IRecipeSlotDrawable>> indexedSlots) {
        ArrayList<Pair<Integer, IRecipeSlotDrawable>> sortedPairs = new ArrayList<Pair<Integer, IRecipeSlotDrawable>>(indexedSlots);
        sortedPairs.sort(Comparator.comparingInt(Pair::first));
        ArrayList<IRecipeSlotDrawable> iRecipeSlotDrawables = new ArrayList<IRecipeSlotDrawable>(sortedPairs.size());
        for (Pair pair : sortedPairs) {
            IRecipeSlotDrawable second = (IRecipeSlotDrawable)pair.second();
            iRecipeSlotDrawables.add(second);
        }
        return iRecipeSlotDrawables;
    }

    @Nullable
    private ShapelessIcon createShapelessIcon(IRecipeCategory<?> recipeCategory) {
        int y;
        int x;
        if (!this.shapeless) {
            return null;
        }
        HighResolutionDrawable icon = Internal.getTextures().getShapelessIcon();
        if (this.shapelessX >= 0 && this.shapelessY >= 0) {
            x = this.shapelessX;
            y = this.shapelessY;
        } else {
            x = recipeCategory.getWidth() - icon.getWidth();
            y = 0;
        }
        return new ShapelessIcon(icon, x, y);
    }

    private ImmutablePoint2i getRecipeTransferButtonPosition(IRecipeCategory<?> recipeCategory, int recipeBorderPadding) {
        if (this.recipeTransferX >= 0 && this.recipeTransferY >= 0) {
            return new ImmutablePoint2i(this.recipeTransferX, this.recipeTransferY);
        }
        return new ImmutablePoint2i(recipeCategory.getWidth() + recipeBorderPadding + 2, recipeCategory.getHeight() + recipeBorderPadding - 13);
    }
}

