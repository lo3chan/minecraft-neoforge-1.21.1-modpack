package net.joefoxe.hexerei.data.datagen;

import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class WoodcutterRecipeBuilder implements RecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final int ingredient_count;
   private final Builder advancement = Builder.advancement();
   private final String type;

   public WoodcutterRecipeBuilder(ItemLike ingredient, ItemLike result, int count, int ingredient_count, String type) {
      this.ingredient = Ingredient.of(new ItemLike[]{ingredient});
      this.result = result.asItem();
      this.count = count;
      this.ingredient_count = ingredient_count;
      this.type = type;
   }

   public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
      this.advancement.addCriterion(name, criterion);
      return this;
   }

   public RecipeBuilder group(@Nullable String pGroupName) {
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      String path = "recipes/" + id.getPath();
      recipeOutput.accept(
         HexereiUtil.getResource(
            "woodcutting/"
               + this.type
               + "/"
               + BuiltInRegistries.ITEM.getKey(this.result).getPath()
               + "_from_"
               + BuiltInRegistries.ITEM.getKey(this.ingredient.getItems()[0].getItem()).getPath()
               + "_woodcutting"
         ),
         new WoodcutterRecipe(this.type, this.ingredient, BuiltInRegistries.ITEM.getKey(this.result).toString(), this.count, this.ingredient_count),
         this.advancement.build(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path.trim()))
      );
   }
}
