/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 */
package mezz.jei.api.gui.builder;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IJeiKeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface ITooltipBuilder {
    public void add(FormattedText var1);

    public void addAll(Collection<? extends FormattedText> var1);

    public void add(TooltipComponent var1);

    public void addKeyUsageComponent(String var1, IJeiKeyMapping var2);

    public void setIngredient(ITypedIngredient<?> var1);

    default public void clear() {
        this.clearIngredient();
        this.getLines().clear();
    }

    public void clearIngredient();

    public List<Either<FormattedText, TooltipComponent>> getLines();

    @Deprecated(since="19.8.4", forRemoval=true)
    public List<Component> toLegacyToComponents();

    @Deprecated(since="19.8.4", forRemoval=true)
    public void removeAll(List<Component> var1);
}

