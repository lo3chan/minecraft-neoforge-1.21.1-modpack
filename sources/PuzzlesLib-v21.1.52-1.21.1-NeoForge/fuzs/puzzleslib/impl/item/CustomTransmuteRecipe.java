package fuzs.puzzleslib.impl.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface CustomTransmuteRecipe {
   String TRANSMUTE_SHAPED_RECIPE_SERIALIZER_ID = "crafting_transmute_shaped";
   String TRANSMUTE_SHAPELESS_RECIPE_SERIALIZER_ID = "crafting_transmute_shapeless";

   static RecipeSerializer<?> getModSerializer(String modId, String recipeSerializerId) {
      RecipeSerializer<?> recipeSerializer = (RecipeSerializer<?>)BuiltInRegistries.RECIPE_SERIALIZER
         .get(ResourceLocation.fromNamespaceAndPath(modId, recipeSerializerId));
      Objects.requireNonNull(recipeSerializer, "recipe serializer '" + ResourceLocation.fromNamespaceAndPath(modId, recipeSerializerId) + "' not registered");
      return recipeSerializer;
   }

   static void registerSerializers(BiConsumer<String, Supplier<RecipeSerializer<?>>> registrar) {
      registrar.accept(
         "crafting_transmute_shaped",
         () -> new CustomTransmuteRecipe.Serializer(new net.minecraft.world.item.crafting.ShapedRecipe.Serializer(), TransmuteShapedRecipe::new)
      );
      registrar.accept(
         "crafting_transmute_shapeless",
         () -> new CustomTransmuteRecipe.Serializer(new net.minecraft.world.item.crafting.ShapelessRecipe.Serializer(), TransmuteShapelessRecipe::new)
      );
   }

   Ingredient getInput();

   default void transmuteInput(ItemStack result, CraftingInput craftingInput) {
      for (int i = 0; i < craftingInput.size(); i++) {
         ItemStack itemStack = craftingInput.getItem(i);
         if (this.getInput().test(itemStack)) {
            result.applyComponents(itemStack.getComponentsPatch());
            return;
         }
      }
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe, S extends CraftingRecipe & CustomTransmuteRecipe> {
      S apply(RecipeSerializer<?> var1, T var2, Ingredient var3);
   }

   public record Serializer<R1 extends CraftingRecipe, R2 extends CraftingRecipe & CustomTransmuteRecipe>(
      RecipeSerializer<R1> serializer, CustomTransmuteRecipe.Factory<R1, R2> factory
   ) implements RecipeSerializer<R2> {
      public MapCodec<R2> codec() {
         return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                  this.serializer.codec().forGetter(arg -> arg), Ingredient.CODEC.fieldOf("input").forGetter(rec$ -> ((CustomTransmuteRecipe)rec$).getInput())
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
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getInput());
      }
   }
}
