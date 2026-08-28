/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.ingredients;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IngredientSortStage;
import mezz.jei.gui.config.IngredientTypeSortingConfig;
import mezz.jei.gui.config.ModNameSortingConfig;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.ingredients.IngredientSorterComparators;

public final class IngredientSorter {
    private static final Comparator<IListElement<?>> COMPARE_SORT_INDEX = Comparator.comparing(IListElement::getSortedIndex);

    public static Comparator<IListElement<?>> sortIngredients(IClientConfig clientConfig, ModNameSortingConfig modNameSortingConfig, IngredientTypeSortingConfig ingredientTypeSortingConfig, IIngredientManager ingredientManager, List<IListElementInfo<?>> ingredients) {
        Set<String> modNames = ingredients.stream().map(IListElementInfo::getModNameForSorting).collect(Collectors.toSet());
        IngredientSorterComparators comparators = new IngredientSorterComparators(ingredientManager, modNameSortingConfig, ingredientTypeSortingConfig, modNames);
        List<IngredientSortStage> ingredientSorterStages = clientConfig.ingredientSorterStages().getValue();
        Comparator<IListElementInfo<?>> completeComparator = comparators.getComparator(ingredientSorterStages);
        ingredients.sort(completeComparator);
        int size = ingredients.size();
        for (int i = 0; i < size; ++i) {
            IListElementInfo<?> elementInfo = ingredients.get(i);
            IListElement<?> element = elementInfo.getElement();
            element.setSortedIndex(i);
        }
        return COMPARE_SORT_INDEX;
    }
}

