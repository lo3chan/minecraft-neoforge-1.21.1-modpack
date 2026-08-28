/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.api.registration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ISubtypeRegistration {
    public <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> var1, B var2, ISubtypeInterpreter<I> var3);

    default public void registerSubtypeInterpreter(Item item, ISubtypeInterpreter<ItemStack> interpreter) {
        this.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
    }

    @Deprecated(since="19.9.0", forRemoval=true)
    public <B, I> void registerSubtypeInterpreter(IIngredientTypeWithSubtypes<B, I> var1, B var2, IIngredientSubtypeInterpreter<I> var3);

    @Deprecated(since="19.9.0", forRemoval=true)
    default public void registerSubtypeInterpreter(Item item, IIngredientSubtypeInterpreter<ItemStack> interpreter) {
        this.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, interpreter);
    }
}

