package fuzs.puzzleslib.impl.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

@Deprecated
public interface CopyComponentsRecipe {
   String SHAPED_RECIPE_SERIALIZER_ID = "copy_components_shaped_recipe";
   String SHAPELESS_RECIPE_SERIALIZER_ID = "copy_components_shapeless_recipe";

   static RecipeSerializer<?> getModSerializer(String modId, String recipeSerializerId) {
      RecipeSerializer<?> recipeSerializer = (RecipeSerializer<?>)BuiltInRegistries.RECIPE_SERIALIZER
         .get(ResourceLocationHelper.fromNamespaceAndPath(modId, recipeSerializerId));
      if (recipeSerializer == null) {
         ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.COPY_RECIPES);
      }

      return recipeSerializer;
   }

   static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
      registrar.accept(
         "copy_components_shaped_recipe",
         () -> new CopyComponentsRecipe.Serializer(new net.minecraft.world.item.crafting.ShapedRecipe.Serializer(), CopyComponentsShapedRecipe::new)
      );
      registrar.accept(
         "copy_components_shapeless_recipe",
         () -> new CopyComponentsRecipe.Serializer(new net.minecraft.world.item.crafting.ShapelessRecipe.Serializer(), CopyComponentsShapelessRecipe::new)
      );
   }

   Ingredient getComponentsSource();

   default void copyComponentsToResult(ItemStack result, CraftingInput craftingInput) {
      for (int i = 0; i < craftingInput.size(); i++) {
         ItemStack itemStack = craftingInput.getItem(i);
         if (this.getComponentsSource().test(itemStack)) {
            result.applyComponents(itemStack.getComponents());
            return;
         }
      }
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe, S extends CraftingRecipe & CopyComponentsRecipe> {
      S apply(RecipeSerializer<?> var1, T var2, Ingredient var3);
   }

   public record Serializer<R1 extends CraftingRecipe, R2 extends CraftingRecipe & CopyComponentsRecipe>(
      RecipeSerializer<R1> serializer, CopyComponentsRecipe.Factory<R1, R2> factory
   ) implements RecipeSerializer<R2> {
      public MapCodec<R2> codec() {
         return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                  this.serializer.codec().forGetter(arg -> arg),
                  Ingredient.CODEC.fieldOf("copy_from").forGetter(rec$ -> ((CopyComponentsRecipe)rec$).getComponentsSource())
               )
               .apply(instance, (craftingRecipe, ingredient) -> this.factory.apply(this, (R1)craftingRecipe, ingredient))
         );
      }

      public StreamCodec<RegistryFriendlyByteBuf, R2> streamCodec() {
         return StreamCodec.of((x$0, x$1) -> this.toNetwork(x$0, (R2)x$1), this::fromNetwork);
      }

      private R2 fromNetwork(RegistryFriendlyByteBuf buffer) {
         R1 recipe = (R1)this.serializer.streamCodec().decode(buffer);
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         return this.factory.apply(this, recipe, ingredient);
      }

      private void toNetwork(RegistryFriendlyByteBuf buffer, R2 recipe) {
         this.serializer.streamCodec().encode(buffer, recipe);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getComponentsSource());
      }
   }
}
