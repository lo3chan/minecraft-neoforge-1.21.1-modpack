package io.github.razordevs.deep_aether.recipe.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.recipe.DABookCategory;
import io.github.razordevs.deep_aether.recipe.DARecipeSerializers;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class CombinerRecipe implements Recipe<CombinerRecipeInput> {
   private final String group;
   private final DABookCategory category;
   public final NonNullList<Ingredient> inputItems = NonNullList.create();
   public final ItemStack output;
   protected final float experience;
   protected final int processingTime;

   public CombinerRecipe(String group, DABookCategory category, List<Ingredient> inputItems, ItemStack output, float experience, int processingTime) {
      this.group = group;
      this.inputItems.addAll(inputItems);
      this.output = output;
      this.category = category;
      this.experience = experience;
      this.processingTime = processingTime;
   }

   public boolean matches(CombinerRecipeInput input, Level pLevel) {
      return this.testEachSlot(input, (Ingredient)this.inputItems.get(0))
         && this.testEachSlot(input, (Ingredient)this.inputItems.get(1))
         && this.testEachSlot(input, (Ingredient)this.inputItems.get(2));
   }

   public ItemStack assemble(CombinerRecipeInput input, Provider provider) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.inputItems;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.output;
   }

   public ItemStack getResult() {
      return this.output;
   }

   public String getGroup() {
      return this.group;
   }

   public float getExperience() {
      return this.experience;
   }

   public int getProcessingTime() {
      return this.processingTime;
   }

   public DABookCategory daCategory() {
      return this.category;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)DABlocks.COMBINER.get());
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)DARecipeTypes.COMBINING.get();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)DARecipeSerializers.COMBINING.get();
   }

   private boolean testEachSlot(CombinerRecipeInput input, Ingredient ingredient) {
      return ingredient.test(input.getItem(0)) ^ ingredient.test(input.getItem(1)) ^ ingredient.test(input.getItem(2));
   }

   public static class Serializer implements RecipeSerializer<CombinerRecipe> {
      private final MapCodec<CombinerRecipe> codec = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(CombinerRecipe::getGroup),
               DABookCategory.CODEC.fieldOf("category").orElse(DABookCategory.COMBINEABLE_MISC).forGetter(CombinerRecipe::daCategory),
               Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
               ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
               Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(recipe -> recipe.experience),
               Codec.INT.fieldOf("processing_time").orElse(200).forGetter(recipe -> recipe.processingTime)
            )
            .apply(instance, CombinerRecipe::new)
      );
      private final StreamCodec<RegistryFriendlyByteBuf, CombinerRecipe> streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);

      public CombinerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf();
         DABookCategory daBookCategory = (DABookCategory)buffer.readEnum(DABookCategory.class);
         List<Ingredient> ingredients = new ArrayList<>();

         for (int i = 0; i < 3; i++) {
            ingredients.add((Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
         }

         ItemStack itemstack = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
         float experience = buffer.readFloat();
         int processTime = buffer.readInt();
         return new CombinerRecipe(group, daBookCategory, ingredients, itemstack, experience, processTime);
      }

      public void toNetwork(RegistryFriendlyByteBuf buffer, CombinerRecipe recipe) {
         buffer.writeUtf(recipe.getGroup());
         buffer.writeEnum(recipe.daCategory());

         for (Ingredient ingredient : recipe.inputItems) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
         buffer.writeFloat(recipe.getExperience());
         buffer.writeInt(recipe.getProcessingTime());
      }

      public MapCodec<CombinerRecipe> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CombinerRecipe> streamCodec() {
         return this.streamCodec;
      }
   }
}
