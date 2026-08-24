package fuzs.puzzleslib.api.data.v2.recipes;

import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.impl.item.CopyComponentsRecipe;
import fuzs.puzzleslib.impl.item.CopyComponentsShapedRecipe;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class CopyComponentsShapedRecipeBuilder extends ShapedRecipeBuilder {
   private Ingredient copyFrom;

   public CopyComponentsShapedRecipeBuilder(RecipeCategory recipeCategory, ItemLike result, int count) {
      super(recipeCategory, result, count);
   }

   public static CopyComponentsShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
      return shaped(category, result, 1);
   }

   public static CopyComponentsShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
      return new CopyComponentsShapedRecipeBuilder(category, result, count);
   }

   public CopyComponentsShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
      super.define(symbol, tag);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder define(Character symbol, ItemLike item) {
      super.define(symbol, item);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
      super.define(symbol, ingredient);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder pattern(String pattern) {
      super.pattern(pattern);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
      super.unlockedBy(criterionName, criterionTrigger);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder group(@Nullable String groupName) {
      super.group(groupName);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder showNotification(boolean bl) {
      super.showNotification(bl);
      return this;
   }

   public CopyComponentsShapedRecipeBuilder copyFrom(ItemLike copyFrom) {
      return this.copyFrom(Ingredient.of(new ItemLike[]{copyFrom}));
   }

   public CopyComponentsShapedRecipeBuilder copyFrom(Ingredient copyFrom) {
      this.copyFrom = copyFrom;
      return this;
   }

   public void save(RecipeOutput recipeOutput, ResourceLocation id) {
      super.save(
         new RecipeOutput() {
            public Builder advancement() {
               return recipeOutput.advancement();
            }

            public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
               String modId = recipeOutput instanceof AbstractRecipeProvider.IdentifiableRecipeOutput identifiableRecipeOutput
                  ? identifiableRecipeOutput.getModId()
                  : id.getNamespace();
               RecipeSerializer<?> recipeSerializer = CopyComponentsRecipe.getModSerializer(modId, "copy_components_shaped_recipe");
               Recipe<?> var6 = new CopyComponentsShapedRecipe(recipeSerializer, (ShapedRecipe)recipe, CopyComponentsShapedRecipeBuilder.this.copyFrom);
               recipeOutput.accept(location, var6, advancement);
            }
         },
         id
      );
   }
}
