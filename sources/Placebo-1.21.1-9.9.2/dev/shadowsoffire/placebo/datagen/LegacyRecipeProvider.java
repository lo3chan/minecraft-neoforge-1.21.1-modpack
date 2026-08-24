package dev.shadowsoffire.placebo.datagen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import org.jetbrains.annotations.Nullable;

public abstract class LegacyRecipeProvider extends RecipeProvider {
   private final String modid;
   protected final Set<String> usedPaths = new HashSet<>();
   @Nullable
   protected RecipeOutput recipeOutput;

   public LegacyRecipeProvider(PackOutput output, CompletableFuture<Provider> registries, String modid) {
      super(output, registries);
      this.modid = modid;
   }

   protected abstract void genRecipes(RecipeOutput var1, Provider var2);

   public void addShaped(ResourceLocation key, String group, Object output, int width, int height, Object... input) {
      if (width * height != input.length) {
         throw new UnsupportedOperationException("Attempted to create invalid shaped recipe. Expected " + width * height + " inputs, but got " + input.length);
      } else {
         ShapedRecipe recipe = new ShapedRecipe(group, CraftingBookCategory.MISC, toPattern(width, height, createInput(true, input)), makeStack(output));
         this.recipeOutput.accept(key, recipe, null);
      }
   }

   public void addShapeless(ResourceLocation key, String group, Object output, Object... inputs) {
      ShapelessRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, makeStack(output), createInput(false, inputs));
      this.recipeOutput.accept(key, recipe, null);
   }

   public void addShaped(ResourceLocation key, Object output, int width, int height, Object... input) {
      this.addShaped(key, this.modid, output, width, height, input);
   }

   public void addShapeless(ResourceLocation key, Object output, Object... inputs) {
      this.addShapeless(key, this.modid, output, inputs);
   }

   public void addShaped(Object output, int width, int height, Object... input) {
      ItemStack out = makeStack(output);
      String path = this.resolvePath(out);
      this.addShaped(ResourceLocation.fromNamespaceAndPath(this.modid, path), this.modid, out, width, height, input);
   }

   public void addShapeless(Object output, Object... inputs) {
      ItemStack out = makeStack(output);
      String path = this.resolvePath(out);
      this.addShapeless(ResourceLocation.fromNamespaceAndPath(this.modid, path), this.modid, out, inputs);
   }

   public static Ingredient potionIngredient(Holder<Potion> type) {
      HolderSet<Item> items = HolderSet.direct(new Holder[]{BuiltInRegistries.ITEM.wrapAsHolder(Items.POTION)});
      DataComponentPredicate predicate = DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(type)).build();
      return new Ingredient(new DataComponentIngredient(items, predicate, false));
   }

   protected final void buildRecipes(RecipeOutput recipeOutput, Provider registries) {
      this.recipeOutput = recipeOutput;
      this.genRecipes(recipeOutput, registries);
      this.recipeOutput = null;
   }

   protected final void buildRecipes(RecipeOutput recipeOutput) {
   }

   protected String resolvePath(ItemStack output) {
      String path = BuiltInRegistries.ITEM.getKey(output.getItem()).getPath();

      while (this.usedPaths.contains(path)) {
         path = path + "_";
      }

      this.usedPaths.add(path);
      return path;
   }

   protected static ItemStack makeStack(Object thing) {
      if (thing instanceof ItemStack stack) {
         return stack;
      } else if (thing instanceof ItemLike il) {
         return new ItemStack(il);
      } else if (thing instanceof Holder<?> h) {
         return new ItemStack((ItemLike)h.value());
      } else {
         throw new IllegalArgumentException("Attempted to create an ItemStack from something that cannot be converted: " + thing);
      }
   }

   protected static NonNullList<Ingredient> createInput(boolean allowEmpty, Object... inputArr) {
      NonNullList<Ingredient> inputL = NonNullList.create();

      for (int i = 0; i < inputArr.length; i++) {
         Object input = inputArr[i];
         if (input instanceof TagKey tag) {
            inputL.add(i, Ingredient.of(tag));
         } else if (input instanceof String str) {
            inputL.add(i, Ingredient.of(ItemTags.create(ResourceLocation.parse(str))));
         } else if (input instanceof ItemStack stack && !stack.isEmpty()) {
            inputL.add(i, Ingredient.of(new ItemStack[]{stack}));
         } else if (!(input instanceof ItemLike) && !(input instanceof Holder)) {
            if (input instanceof Ingredient ing && !ing.isEmpty()) {
               inputL.add(i, ing);
            } else {
               if (!allowEmpty || input != null && input != ItemStack.EMPTY && input != Ingredient.EMPTY) {
                  throw new UnsupportedOperationException("Attempted to add invalid recipe. Input " + input + " not allowed.");
               }

               inputL.add(i, Ingredient.EMPTY);
            }
         } else {
            inputL.add(i, Ingredient.of(new ItemStack[]{makeStack(input)}));
         }
      }

      return inputL;
   }

   protected static ShapedRecipePattern toPattern(int width, int height, NonNullList<Ingredient> input) {
      Map<Character, Ingredient> key = new HashMap<>();
      Map<Ingredient, Character> chars = new HashMap<>();
      List<String> rows = new ArrayList<>(height);

      for (int h = 0; h < height; h++) {
         String row = "";

         for (int w = 0; w < width; w++) {
            Ingredient ing = (Ingredient)input.get(h * width + w);
            if (chars.containsKey(ing)) {
               row = row + chars.get(ing);
            } else {
               Character c = getFirstChar(chars.values(), ing);
               key.put(c, ing);
               chars.put(ing, c);
               row = row + c;
            }
         }

         rows.add(row);
      }

      key.remove(' ');
      return ShapedRecipePattern.of(key, rows);
   }

   protected static Character getFirstChar(Collection<Character> inUse, Ingredient ing) {
      if (ing == Ingredient.EMPTY) {
         return ' ';
      } else {
         String path;
         if (ing.isCustom()) {
            ICustomIngredient custom = ing.getCustomIngredient();
            Item item = custom.getItems().findFirst().<Item>map(ItemStack::getItem).orElse(Items.AIR);
            path = BuiltInRegistries.ITEM.getKey(item).getPath();
         } else {
            Value v = ing.getValues()[0];
            if (v instanceof TagValue t) {
               path = t.tag().location().getPath();
            } else {
               if (!(v instanceof ItemValue i)) {
                  throw new UnsupportedOperationException("Unknown Ingredient$Value type: " + v.getClass().getCanonicalName());
               }

               path = BuiltInRegistries.ITEM.getKey(i.item().getItem()).getPath();
            }
         }

         path = path.toUpperCase(Locale.ROOT);

         for (char c : path.toCharArray()) {
            if (!inUse.contains(c)) {
               return c;
            }
         }

         throw new UnsupportedOperationException("Failed to find any unused characters for ingredient: " + ing);
      }
   }
}
