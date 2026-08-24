package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class RestoreClayMoldSmeltingRecipe extends SmeltingRecipe {
   public static final ItemStack DEFAULT_RESULT = Items.CLAY_BALL.getDefaultInstance();

   public RestoreClayMoldSmeltingRecipe(String group, float experience, int cookingTime) {
      super(group, CookingBookCategory.MISC, Ingredient.of(new ItemLike[]{AlchemancyItems.UNSHAPED_CLAY}), DEFAULT_RESULT, experience, cookingTime);
      InfusedPropertiesHelper.addProperty(DEFAULT_RESULT, AlchemancyProperties.HARDENED);
   }

   public boolean matches(SingleRecipeInput input, Level level) {
      return InfusedPropertiesHelper.hasProperty(input.item(), AlchemancyProperties.CLAY_MOLD);
   }

   public ItemStack assemble(SingleRecipeInput input, Provider registries) {
      ItemStack storedItem = ((ClayMoldProperty)AlchemancyProperties.CLAY_MOLD.get()).getData(input.item());
      if (storedItem.isDamageableItem()) {
         storedItem.setDamageValue(0);
      }

      InfusedPropertiesHelper.removeProperty(storedItem, AlchemancyProperties.MALLEABLE);
      InfusedPropertiesHelper.addProperty(storedItem, AlchemancyProperties.HARDENED);
      return storedItem;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.RESTORE_CLAY_MOLD_SMELTING.get();
   }

   public static class Serializer implements RecipeSerializer<RestoreClayMoldSmeltingRecipe> {
      private static final MapCodec<RestoreClayMoldSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         p_300831_ -> p_300831_.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::getGroup),
               Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::getExperience),
               Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(AbstractCookingRecipe::getCookingTime)
            )
            .apply(p_300831_, RestoreClayMoldSmeltingRecipe::new)
      );
      private static final StreamCodec<RegistryFriendlyByteBuf, RestoreClayMoldSmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         AbstractCookingRecipe::getGroup,
         ByteBufCodecs.FLOAT,
         AbstractCookingRecipe::getExperience,
         ByteBufCodecs.INT,
         AbstractCookingRecipe::getCookingTime,
         RestoreClayMoldSmeltingRecipe::new
      );

      public MapCodec<RestoreClayMoldSmeltingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, RestoreClayMoldSmeltingRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
