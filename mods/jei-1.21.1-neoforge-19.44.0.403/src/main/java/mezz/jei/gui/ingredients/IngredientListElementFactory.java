/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.gui.ingredients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.ingredients.ListElementInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class IngredientListElementFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    private IngredientListElementFactory() {
    }

    public static List<IListElementInfo<?>> createBaseList(IIngredientManager ingredientManager, IModIdHelper modIdHelper) {
        ArrayList ingredientListElements = new ArrayList();
        for (IIngredientType<?> ingredientType : ingredientManager.getRegisteredIngredientTypes()) {
            IngredientListElementFactory.addToBaseList(ingredientListElements, ingredientManager, ingredientType, modIdHelper);
        }
        return ingredientListElements;
    }

    public static <V> List<IListElementInfo<V>> createTestList(IIngredientManager ingredientManager, IIngredientType<V> ingredientType, Collection<V> ingredients, IModIdHelper modIdHelper) {
        return ingredients.stream().map(i -> ingredientManager.createTypedIngredient(ingredientType, i, false)).flatMap(Optional::stream).map(i -> ListElementInfo.create(i, ingredientManager, modIdHelper)).filter(Objects::nonNull).toList();
    }

    public static List<IListElementInfo<?>> rebuildList(IIngredientManager ingredientManager, Collection<IListElement<?>> elements, IModIdHelper modIdHelper) {
        ArrayList results = new ArrayList();
        for (IListElement<?> element : elements) {
            IListElementInfo<?> orderedElement = ListElementInfo.createFromElement(element, ingredientManager, modIdHelper);
            if (orderedElement == null) continue;
            results.add(orderedElement);
        }
        return results;
    }

    private static <V> void addToBaseList(List<IListElementInfo<?>> baseList, IIngredientManager ingredientManager, IIngredientType<V> ingredientType, IModIdHelper modIdHelper) {
        Collection<ITypedIngredient<V>> typedIngredients = ingredientManager.getAllTypedIngredients(ingredientType);
        LOGGER.debug("Registering ingredients: {}", (Object)ingredientType.getIngredientClass().getSimpleName());
        for (ITypedIngredient<V> typedIngredient : typedIngredients) {
            IListElementInfo<V> orderedElement = ListElementInfo.create(typedIngredient, ingredientManager, modIdHelper);
            if (orderedElement == null) continue;
            baseList.add(orderedElement);
        }
    }
}

