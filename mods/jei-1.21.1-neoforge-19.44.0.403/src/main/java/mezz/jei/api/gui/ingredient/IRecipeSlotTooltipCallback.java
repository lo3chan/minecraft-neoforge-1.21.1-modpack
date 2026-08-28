/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.api.gui.ingredient;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;

@Deprecated(since="19.8.5", forRemoval=true)
@FunctionalInterface
public interface IRecipeSlotTooltipCallback {
    @Deprecated(since="19.5.4", forRemoval=true)
    public void onTooltip(IRecipeSlotView var1, List<Component> var2);

    @Deprecated(since="19.8.5", forRemoval=true)
    default public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
        List<Component> components = tooltip.toLegacyToComponents();
        ArrayList<Component> changedComponents = new ArrayList<Component>(components);
        this.onTooltip(recipeSlotView, changedComponents);
        if (!components.equals(changedComponents)) {
            tooltip.removeAll(components);
            tooltip.addAll(changedComponents);
        }
    }
}

