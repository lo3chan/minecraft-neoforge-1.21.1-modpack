package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.SizedIngredientWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DifferenceIngredient;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface IngredientKJS extends ItemPredicate, Replaceable, WithCodec, ItemMatch {
   default Ingredient kjs$self() {
      throw new NoMixinException();
   }

   @Override
   default ItemStack[] kjs$getStackArray() {
      return this.kjs$self().getItems();
   }

   default Ingredient kjs$and(Ingredient ingredient) {
      return ingredient == Ingredient.EMPTY
         ? this.kjs$self()
         : (this == Ingredient.EMPTY ? ingredient : IntersectionIngredient.of(new Ingredient[]{this.kjs$self(), ingredient}));
   }

   default Ingredient kjs$or(Ingredient ingredient) {
      return ingredient == Ingredient.EMPTY
         ? this.kjs$self()
         : (this == Ingredient.EMPTY ? ingredient : CompoundIngredient.of(new Ingredient[]{this.kjs$self(), ingredient}));
   }

   default Ingredient kjs$except(Ingredient subtracted) {
      return DifferenceIngredient.of(this.kjs$self(), subtracted);
   }

   default SizedIngredient kjs$asStack() {
      return this.kjs$self().isEmpty() ? SizedIngredientWrapper.empty : new SizedIngredient(this.kjs$self(), 1);
   }

   default SizedIngredient kjs$withCount(int count) {
      return new SizedIngredient(this.kjs$self(), count);
   }

   @Override
   default boolean kjs$isWildcard() {
      return this.kjs$self().getCustomIngredient() == WildcardIngredient.INSTANCE;
   }

   @Override
   default Ingredient kjs$asIngredient() {
      return this.kjs$self();
   }

   @Override
   default Codec<?> getCodec(Context cx) {
      return Ingredient.CODEC;
   }

   @Override
   default Object replaceThisWith(RecipeScriptContext cx, Object with) {
      Ingredient t = this.kjs$self();
      Ingredient r = IngredientWrapper.wrap(cx.cx(), with);
      return !r.equals(t) ? r : this;
   }

   @Override
   default boolean matches(RecipeMatchContext cx, ItemStack item, boolean exact) {
      if (item.isEmpty()) {
         return false;
      } else if (!exact) {
         return this.test(item);
      } else {
         ItemStackSet stacks = this.kjs$getStacks();
         return stacks.size() == 1 && ItemStack.isSameItemSameComponents(stacks.getFirst(), item);
      }
   }

   @Override
   default boolean matches(RecipeMatchContext cx, Ingredient in, boolean exact) {
      if (in == Ingredient.EMPTY) {
         return false;
      } else if (exact) {
         TagKey<Item> t1 = IngredientWrapper.tagKeyOf(this.kjs$self());
         TagKey<Item> t2 = IngredientWrapper.tagKeyOf(in);
         return t1 != null && t2 != null ? t1 == t2 : this.equals(in);
      } else {
         try {
            for (ItemStack stack : in.getItems()) {
               if (this.test(stack)) {
                  return true;
               }
            }

            return false;
         } catch (Exception var8) {
            throw new KubeRuntimeException("Failed to test ingredient " + in, var8);
         }
      }
   }

   @Nullable
   default TagKey<Item> kjs$getTagKey() {
      return IngredientWrapper.tagKeyOf(this.kjs$self());
   }

   default boolean kjs$containsAnyTag() {
      return IngredientWrapper.containsAnyTag(this.kjs$self());
   }

   default String kjs$toIngredientString(@Nullable DynamicOps<Tag> ops) {
      Ingredient in = this.kjs$self();
      if (in.isEmpty()) {
         return "air";
      } else {
         ItemStack[] items = this.kjs$getStackArray();
         if (items.length == 0) {
            return "air";
         } else {
            return items.length == 1 ? items[0].kjs$toItemString0(null) : ((Tag)Ingredient.CODEC.encodeStart(ops, in).getOrThrow()).toString();
         }
      }
   }
}
