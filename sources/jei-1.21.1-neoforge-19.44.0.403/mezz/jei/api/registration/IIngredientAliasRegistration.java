package mezz.jei.api.registration;

import java.util.Collection;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IIngredientAliasRegistration {
   default void addAlias(ItemStack itemStack, String alias) {
      this.addAlias(VanillaTypes.ITEM_STACK, itemStack, alias);
   }

   default void addAlias(Item item, String alias) {
      this.addAlias(VanillaTypes.ITEM_STACK, item, alias);
   }

   void addAlias(Fluid var1, String var2);

   <I> void addAlias(IIngredientType<I> var1, I var2, String var3);

   <B, I> void addAlias(IIngredientTypeWithSubtypes<B, I> var1, B var2, String var3);

   <I> void addAlias(ITypedIngredient<I> var1, String var2);

   <I> void addAliases(IIngredientType<I> var1, I var2, Collection<String> var3);

   default void addAliases(Item item, Collection<String> aliases) {
      this.addAliases(VanillaTypes.ITEM_STACK, item, aliases);
   }

   void addAliases(Fluid var1, Collection<String> var2);

   <B, I> void addAliases(IIngredientTypeWithSubtypes<B, I> var1, B var2, Collection<String> var3);

   <I> void addAliases(ITypedIngredient<I> var1, Collection<String> var2);

   <I> void addAliases(IIngredientType<I> var1, Collection<I> var2, String var3);

   <I> void addAliases(Collection<ITypedIngredient<I>> var1, String var2);

   <I> void addAliases(IIngredientType<I> var1, Collection<I> var2, Collection<String> var3);

   <I> void addAliases(Collection<ITypedIngredient<I>> var1, Collection<String> var2);
}
