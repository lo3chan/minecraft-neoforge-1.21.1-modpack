package com.github.alexthe666.alexsmobs.misc;

import com.github.alexthe666.alexsmobs.citadel.client.model.container.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Type;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CapsidRecipe {
   private final NonNullList<Ingredient> ingredients;
   private ItemStack result = ItemStack.EMPTY;
   private int time = 0;

   public CapsidRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int time) {
      this.result = result;
      this.ingredients = ingredients;
      this.time = time;
   }

   private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();

      for (int i = 0; i < ingredientArray.size(); i++) {
         Ingredient ingredient = (Ingredient)Ingredient.CODEC
            .parse(JsonOps.INSTANCE, ingredientArray.get(i))
            .result()
            .orElseThrow(() -> new JsonParseException("Malformed capsid recipe ingredient"));
         if (!ingredient.isEmpty()) {
            nonnulllist.add(ingredient);
         }
      }

      return nonnulllist;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public int getTime() {
      return this.time;
   }

   public boolean matches(ItemStack... stacks) {
      IntList taken = new IntArrayList();
      ItemStack[] copy = new ItemStack[stacks.length];

      for (int j = 0; j < copy.length; j++) {
         copy[j] = stacks[j].copy();

         for (int i = 0; i < this.ingredients.size(); i++) {
            if (((Ingredient)this.ingredients.get(i)).test(copy[j])) {
               taken.add(j);
               copy[j].shrink(1);
            }
         }
      }

      return taken.size() >= this.ingredients.size();
   }

   public static class Deserializer implements JsonDeserializer<CapsidRecipe> {
      public CapsidRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonobject = json.getAsJsonObject();
         int time = JsonUtils.getInt(jsonobject, "time");
         ItemStack result = ItemStack.EMPTY;
         if (jsonobject.has("result")) {
            result = (ItemStack)ItemStack.CODEC
               .parse(JsonOps.INSTANCE, JsonUtils.getJsonObject(jsonobject, "result"))
               .result()
               .orElseThrow(() -> new JsonParseException("Malformed capsid recipe result"));
         }

         NonNullList<Ingredient> nonnulllist = CapsidRecipe.readIngredients(JsonUtils.getJsonArray(jsonobject, "ingredients"));
         return new CapsidRecipe(nonnulllist, result, time);
      }
   }
}
