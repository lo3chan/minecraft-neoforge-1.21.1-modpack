package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.blocks.blockentities.EssenceExtractorBlockEntity;
import net.cibernet.alchemancy.essence.Essence;
import net.cibernet.alchemancy.registries.AlchemancyEssence;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class EssenceExtractionRecipe implements Recipe<EssenceExtractorBlockEntity> {
   final Ingredient ingredient;
   final EssenceContainer essence = new EssenceContainer(1000);

   public EssenceExtractionRecipe(Ingredient ingredient, Holder<Essence> essence, int amount) {
      this.ingredient = ingredient;
      this.essence.replace((Essence)essence.value(), amount);
   }

   public boolean matches(EssenceExtractorBlockEntity input, Level level) {
      return this.ingredient.test(input.getItem(0)) && input.storedEssence.canAdd(this.essence, true);
   }

   public ItemStack assemble(EssenceExtractorBlockEntity input, Provider registries) {
      input.removeItem(0, 1);
      input.storedEssence.add(this.essence.getEssence(), this.essence.getAmount());
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   public RecipeSerializer<?> getSerializer() {
      return null;
   }

   public RecipeType<?> getType() {
      return null;
   }

   public static class Serializer implements RecipeSerializer<EssenceExtractionRecipe> {
      private static final MapCodec<EssenceExtractionRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
               Essence.CODEC.fieldOf("essence").forGetter(recipe -> Holder.direct(recipe.essence.getEssence())),
               Codec.INT.fieldOf("amount").forGetter(recipe -> recipe.essence.getAmount())
            )
            .apply(instance, EssenceExtractionRecipe::new)
      );
      private static final StreamCodec<RegistryFriendlyByteBuf, EssenceExtractionRecipe> STREAM_CODEC = StreamCodec.of(
         (encode, recipe) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(encode, recipe.ingredient);
            ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey())
               .encode(
                  encode,
                  (Holder)encode.registryAccess()
                     .holder(ResourceKey.create(AlchemancyEssence.REGISTRY.getRegistryKey(), recipe.essence.getEssence().getKey()))
                     .get()
               );
            encode.writeInt(recipe.essence.getAmount());
         },
         decode -> new EssenceExtractionRecipe(
            (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(decode),
            (Holder<Essence>)ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey()).decode(decode),
            decode.readInt()
         )
      );

      public MapCodec<EssenceExtractionRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, EssenceExtractionRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
