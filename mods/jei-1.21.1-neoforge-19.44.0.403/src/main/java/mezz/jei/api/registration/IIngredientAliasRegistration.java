/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.registration;

import java.util.Collection;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IIngredientAliasRegistration {
    default public void addAlias(ItemStack itemStack, String alias) {
        this.addAlias((IIngredientType)VanillaTypes.ITEM_STACK, (Object)itemStack, alias);
    }

    default public void addAlias(Item item, String alias) {
        this.addAlias(VanillaTypes.ITEM_STACK, (Object)item, alias);
    }

    public void addAlias(Fluid var1, String var2);

    public <I> void addAlias(IIngredientType<I> var1, I var2, String var3);

    public <B, I> void addAlias(IIngredientTypeWithSubtypes<B, I> var1, B var2, String var3);

    public <I> void addAlias(ITypedIngredient<I> var1, String var2);

    public <I> void addAliases(IIngredientType<I> var1, I var2, Collection<String> var3);

    default public void addAliases(Item item, Collection<String> aliases) {
        this.addAliases(VanillaTypes.ITEM_STACK, (Object)item, aliases);
    }

    public void addAliases(Fluid var1, Collection<String> var2);

    public <B, I> void addAliases(IIngredientTypeWithSubtypes<B, I> var1, B var2, Collection<String> var3);

    public <I> void addAliases(ITypedIngredient<I> var1, Collection<String> var2);

    public <I> void addAliases(IIngredientType<I> var1, Collection<I> var2, String var3);

    public <I> void addAliases(Collection<ITypedIngredient<I>> var1, String var2);

    public <I> void addAliases(IIngredientType<I> var1, Collection<I> var2, Collection<String> var3);

    public <I> void addAliases(Collection<ITypedIngredient<I>> var1, Collection<String> var2);
}

