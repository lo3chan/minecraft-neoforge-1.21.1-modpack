/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.api.helpers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.network.chat.Component;

public interface IModIdHelper {
    public String getModNameForModId(String var1);

    public boolean isDisplayingModNameEnabled();

    @Deprecated(since="19.31.0", forRemoval=true)
    public String getFormattedModNameForModId(String var1);

    default public Component getFormattedModNameComponentForModId(String modId) {
        return Component.literal((String)this.getFormattedModNameForModId(modId));
    }

    public Set<String> getModAliases(String var1);

    @Deprecated(since="19.5.4", forRemoval=true)
    public <T> List<Component> addModNameToIngredientTooltip(List<Component> var1, T var2, IIngredientHelper<T> var3);

    @Deprecated(since="19.5.4", forRemoval=true)
    public <T> List<Component> addModNameToIngredientTooltip(List<Component> var1, ITypedIngredient<T> var2);

    public <T> Optional<Component> getModNameForTooltip(ITypedIngredient<T> var1);
}

