package net.joefoxe.hexerei.data.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
   public ModRecipeProvider(PackOutput packOutput, CompletableFuture<Provider> registries) {
      super(packOutput, registries);
   }

   public static String getItemName(ItemLike pItemLike) {
      return BuiltInRegistries.ITEM.getKey(pItemLike.asItem()).getPath();
   }

   public static String getAddCandleRecipeName(ItemLike pResult) {
      return "add_to_candle/" + getItemName(pResult) + "_add_to_candle";
   }

   public static String getWoodcuttingRecipeName(String type, Item result, Item input) {
      return "woodcutting/"
         + type
         + "/"
         + BuiltInRegistries.ITEM.getKey(result).getPath()
         + "_from_"
         + BuiltInRegistries.ITEM.getKey(input).getPath()
         + "_woodcutting";
   }

   protected void buildRecipes(RecipeOutput p_recipeOutput) {
      File add_to_candle_file = new File("recipe-builder/add_to_candle.json");

      JsonArray recipesAddToCandle;
      try {
         JsonElement jsonElement = JsonParser.parseReader(new FileReader(add_to_candle_file.getAbsolutePath()));
         recipesAddToCandle = GsonHelper.getAsJsonArray(jsonElement.getAsJsonObject(), "values");
      } catch (FileNotFoundException var5) {
         throw new RuntimeException(var5);
      }

      BuiltInRegistries.BLOCK
         .forEach(
            block -> recipesAddToCandle.forEach(
               recipeBlock -> {
                  if (BuiltInRegistries.BLOCK.getKey(block).toString().equals(recipeBlock.getAsString())) {
                     ItemStack stack = new ItemStack((ItemLike)ModItems.CANDLE.get());
                     CandleItem.setLayerFromBlock(stack, BuiltInRegistries.BLOCK.getKey(block).toString(), "base");
                     new AddToCandleRecipeBuilder(block.asItem(), stack)
                        .unlockedBy(
                           "has_candle", inventoryTrigger(new ItemPredicate[]{Builder.item().of(new ItemLike[]{(ItemLike)ModItems.CANDLE.get()}).build()})
                        )
                        .save(p_recipeOutput, getAddCandleRecipeName(block));
                  }
               }
            )
         );
   }
}
