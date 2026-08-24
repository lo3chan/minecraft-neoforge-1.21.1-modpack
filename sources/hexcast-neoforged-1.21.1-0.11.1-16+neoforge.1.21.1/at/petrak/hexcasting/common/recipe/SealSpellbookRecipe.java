package at.petrak.hexcasting.common.recipe;

import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class SealSpellbookRecipe extends ShapelessRecipe {
   public static final SimpleCraftingRecipeSerializer<SealSpellbookRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer(SealSpellbookRecipe::new);

   private static ItemStack getSealedStack() {
      ItemStack output = new ItemStack(HexItems.SPELLBOOK);
      ItemSpellbook.setSealed(output, true);
      NBTHelper.putString(output, "VisualOverride", "any");
      return output;
   }

   private static NonNullList<Ingredient> createIngredients() {
      NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(2);
      ingredients.add(IXplatAbstractions.INSTANCE.getUnsealedIngredient(new ItemStack(HexItems.SPELLBOOK)));
      ingredients.add(Ingredient.of(new ItemLike[]{Items.HONEYCOMB}));
      return ingredients;
   }

   public SealSpellbookRecipe(CraftingBookCategory category) {
      super("", category, getSealedStack(), createIngredients());
   }

   @NotNull
   public ItemStack assemble(CraftingInput inv, Provider access) {
      ItemStack out = ItemStack.EMPTY;

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         if (stack.is(HexItems.SPELLBOOK)) {
            out = stack.copy();
            break;
         }
      }

      if (!out.isEmpty()) {
         ItemSpellbook.setSealed(out, true);
         out.setCount(1);
      }

      return out;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return SERIALIZER;
   }
}
