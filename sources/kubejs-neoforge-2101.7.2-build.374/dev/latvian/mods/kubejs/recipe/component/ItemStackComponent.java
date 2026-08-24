package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.InvalidRecipeComponentValueException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemStackComponent(RecipeComponentType<?> type, Codec<ItemStack> codec, boolean allowEmpty, Ingredient filter)
   implements RecipeComponent<ItemStack> {
   public static final RecipeComponentType<ItemStack> ITEM_STACK = RecipeComponentType.unit(
      KubeJS.id("item_stack"), type -> new ItemStackComponent(type, false, Ingredient.EMPTY)
   );
   public static final RecipeComponentType<ItemStack> OPTIONAL_ITEM_STACK = RecipeComponentType.unit(
      KubeJS.id("optional_item_stack"), type -> new ItemStackComponent(type, true, Ingredient.EMPTY)
   );
   public static final RecipeComponentType<?> FILTERED_ITEM_STACK = RecipeComponentType.dynamic(
      KubeJS.id("filtered_item_stack"),
      (RecipeComponentCodecFactory)((type, ctx) -> RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.BOOL.optionalFieldOf("allow_empty", false).forGetter(ItemStackComponent::allowEmpty),
               Ingredient.CODEC.optionalFieldOf("filter", Ingredient.EMPTY).forGetter(ItemStackComponent::filter)
            )
            .apply(instance, (allowEmpty, filter) -> new ItemStackComponent(type, allowEmpty, filter))
      ))
   );

   public ItemStackComponent(RecipeComponentType<?> type, boolean allowEmpty, Ingredient filter) {
      this(type, allowEmpty ? ItemStack.OPTIONAL_CODEC : ItemStack.STRICT_CODEC, allowEmpty, filter);
   }

   @Override
   public TypeInfo typeInfo() {
      return ItemWrapper.TYPE_INFO;
   }

   @Override
   public boolean hasPriority(RecipeMatchContext cx, Object from) {
      return ItemWrapper.isItemStackLike(from);
   }

   public boolean matches(RecipeMatchContext cx, ItemStack value, ReplacementMatchInfo match) {
      return match.match() instanceof ItemMatch m && !value.isEmpty() && m.matches(cx, value, match.exact());
   }

   public boolean isEmpty(ItemStack value) {
      return value.isEmpty();
   }

   public void buildUniqueId(UniqueIdBuilder builder, ItemStack value) {
      if (!value.isEmpty()) {
         builder.append(value.kjs$getIdLocation());
      }
   }

   @Override
   public String toString() {
      return this.type.toString();
   }

   public String toString(OpsContainer ops, ItemStack value) {
      return value.kjs$toItemString0(ops.nbt());
   }

   public void validate(RecipeValidationContext ctx, ItemStack value) {
      RecipeComponent.super.validate(ctx, value);
      if (!this.filter.isEmpty() && !this.filter.test(value)) {
         throw new InvalidRecipeComponentValueException(
            "Item " + value.kjs$toItemString0(ctx.ops().nbt()) + " doesn't match filter " + this.filter.kjs$toIngredientString(ctx.ops().nbt()), this, value
         );
      }
   }

   public List<ItemStack> spread(ItemStack value) {
      int count = value.getCount();
      if (count <= 0) {
         return List.of();
      } else if (count == 1) {
         return List.of(value.copyWithCount(1));
      } else {
         ArrayList<ItemStack> list = new ArrayList<>(count);

         for (int i = 0; i < count; i++) {
            list.add(value.copyWithCount(1));
         }

         return list;
      }
   }
}
