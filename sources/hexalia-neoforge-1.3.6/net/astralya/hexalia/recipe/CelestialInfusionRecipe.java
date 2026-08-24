package net.astralya.hexalia.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CelestialInfusionRecipe(Ingredient inputItem, ItemStack output) implements Recipe<CelestialInfusionRecipeInput> {
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> list = NonNullList.create();
      list.add(this.inputItem);
      return list;
   }

   public boolean matches(CelestialInfusionRecipeInput input, Level level) {
      return !level.isClientSide() && this.inputItem.test(input.getItem(0));
   }

   public ItemStack assemble(CelestialInfusionRecipeInput input, Provider registries) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return this.output;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.CELESTIAL_INFUSION_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipeTypes.CELESTIAL_INFUSION.get();
   }

   public static final class Serializer implements RecipeSerializer<CelestialInfusionRecipe> {
      public static final MapCodec<CelestialInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CelestialInfusionRecipe::inputItem),
               BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").xmap(ItemStack::new, ItemStack::getItem).forGetter(CelestialInfusionRecipe::output)
            )
            .apply(instance, CelestialInfusionRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, CelestialInfusionRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         CelestialInfusionRecipe::inputItem,
         ItemStack.STREAM_CODEC,
         CelestialInfusionRecipe::output,
         CelestialInfusionRecipe::new
      );

      public MapCodec<CelestialInfusionRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CelestialInfusionRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
