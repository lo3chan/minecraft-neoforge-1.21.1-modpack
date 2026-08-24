package com.github.alexthe666.alexsmobs.citadel.client.gui;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class BookRecipe {
   private static final Map<String, BookRecipe> CACHE = new HashMap<>();
   private final List<ItemStack[]> ingredients;
   private final ItemStack result;
   private final boolean shapeless;

   private BookRecipe(List<ItemStack[]> ingredients, ItemStack result, boolean shapeless) {
      this.ingredients = ingredients;
      this.result = result;
      this.shapeless = shapeless;
   }

   public List<ItemStack[]> getIngredients() {
      return this.ingredients;
   }

   public ItemStack getResult() {
      return this.result;
   }

   public boolean isShapeless() {
      return this.shapeless;
   }

   public static BookRecipe get(String id) {
      if (CACHE.containsKey(id)) {
         return CACHE.get(id);
      } else {
         BookRecipe recipe = null;

         try {
            recipe = load(id);
         } catch (Exception var3) {
            AlexsMobs.LOGGER.warn("Could not read book recipe {}", id, var3);
         }

         CACHE.put(id, recipe);
         return recipe;
      }
   }

   private static BookRecipe load(String id) throws Exception {
      ResourceLocation res = AMCompat.rl(id);
      InputStream stream = BookRecipe.class.getResourceAsStream("/data/" + res.getNamespace() + "/recipe/" + res.getPath() + ".json");
      if (stream == null) {
         stream = BookRecipe.class.getResourceAsStream("/data/" + res.getNamespace() + "/recipes/" + res.getPath() + ".json");
      }

      if (stream == null) {
         return null;
      } else {
         InputStream in = stream;

         BookRecipe var18;
         label90: {
            try {
               label102: {
                  JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                  String type = json.has("type") ? json.get("type").getAsString() : "";
                  List<ItemStack[]> ingredients = new ArrayList<>();
                  boolean shapeless;
                  if (!"minecraft:crafting_shaped".equals(type)) {
                     if (!"minecraft:crafting_shapeless".equals(type)) {
                        var18 = null;
                        break label102;
                     }

                     shapeless = true;

                     for (JsonElement element : json.getAsJsonArray("ingredients")) {
                        ingredients.add(resolve(element));
                     }
                  } else {
                     shapeless = false;
                     JsonArray pattern = json.getAsJsonArray("pattern");
                     JsonObject key = json.getAsJsonObject("key");

                     for (JsonElement rowElement : pattern) {
                        String row = rowElement.getAsString();

                        for (int i = 0; i < row.length(); i++) {
                           char c = row.charAt(i);
                           ingredients.add(c == ' ' ? new ItemStack[0] : resolve(key.get(String.valueOf(c))));
                        }
                     }
                  }

                  var18 = new BookRecipe(ingredients, readResult(json.get("result")), shapeless);
                  break label90;
               }
            } catch (Throwable var16) {
               if (stream != null) {
                  try {
                     in.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }
               }

               throw var16;
            }

            if (stream != null) {
               stream.close();
            }

            return var18;
         }

         if (stream != null) {
            stream.close();
         }

         return var18;
      }
   }

   private static ItemStack readResult(JsonElement element) {
      if (element == null) {
         return ItemStack.EMPTY;
      } else if (element.isJsonPrimitive()) {
         return stackOf(element.getAsString(), 1);
      } else {
         JsonObject object = element.getAsJsonObject();
         String itemId = object.has("id") ? object.get("id").getAsString() : object.get("item").getAsString();
         int count = object.has("count") ? object.get("count").getAsInt() : 1;
         return stackOf(itemId, count);
      }
   }

   private static ItemStack[] resolve(JsonElement element) {
      List<ItemStack> stacks = new ArrayList<>();
      collect(element, stacks);
      return stacks.toArray(new ItemStack[0]);
   }

   private static void collect(JsonElement element, List<ItemStack> out) {
      if (element != null && !element.isJsonNull()) {
         if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
               collect(child, out);
            }
         } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("item")) {
               collect(object.get("item"), out);
            } else if (object.has("tag")) {
               collect(object.get("tag"), out);
            } else if (object.has("id")) {
               collect(object.get("id"), out);
            }
         } else {
            String value = element.getAsString();
            if (value.startsWith("#")) {
               TagKey<Item> tag = TagKey.create(Registries.ITEM, AMCompat.rl(value.substring(1)));

               for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                  out.add(new ItemStack((ItemLike)holder.value()));
               }
            } else {
               ItemStack stack = stackOf(value, 1);
               if (!stack.isEmpty()) {
                  out.add(stack);
               }
            }
         }
      }
   }

   private static ItemStack stackOf(String id, int count) {
      Item item = (Item)BuiltInRegistries.ITEM.get(AMCompat.rl(id));
      return new ItemStack(item, count);
   }
}
