/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.runtime;

import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IBookmarkOverlay {
    public Optional<ITypedIngredient<?>> getIngredientUnderMouse();

    @Nullable
    public <T> T getIngredientUnderMouse(IIngredientType<T> var1);

    @Nullable
    default public ItemStack getItemStackUnderMouse() {
        return (ItemStack)this.getIngredientUnderMouse(VanillaTypes.ITEM_STACK);
    }
}

