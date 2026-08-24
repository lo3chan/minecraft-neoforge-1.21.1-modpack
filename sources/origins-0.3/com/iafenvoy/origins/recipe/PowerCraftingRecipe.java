package com.iafenvoy.origins.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.builtin.regular.RecipePower;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record PowerCraftingRecipe(ResourceLocation powerId, CraftingRecipe delegate) implements CraftingRecipe {
   @NotNull
   public CraftingBookCategory category() {
      return this.delegate().category();
   }

   public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
      return input instanceof PowerCraftingObject pco
         && pco.origins$getPlayer().flatMap(OriginDataHolder::optional).map(h -> h.hasActivePower(this.powerId(), RecipePower.class)).orElse(false)
         && level.getRecipeManager()
            .byKey(this.powerId())
            .filter(entry -> Objects.equals(this, entry.value()))
            .map(entry -> this.delegate().matches(input, level))
            .orElse(false);
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput input, @NotNull Provider lookup) {
      return this.delegate().assemble(input, lookup);
   }

   public boolean canCraftInDimensions(int width, int height) {
      return this.delegate().canCraftInDimensions(width, height);
   }

   @NotNull
   public ItemStack getResultItem(@NotNull Provider registriesLookup) {
      return this.delegate().getResultItem(registriesLookup);
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)OriginsRecipeSerializers.POWER.get();
   }

   public boolean isIncomplete() {
      return this.delegate().isIncomplete();
   }

   @NotNull
   public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
      return this.delegate().getRemainingItems(input);
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.delegate().getIngredients();
   }

   public boolean isSpecial() {
      return this.delegate().isSpecial();
   }

   public boolean showNotification() {
      return this.delegate().showNotification();
   }

   @NotNull
   public String getGroup() {
      return this.delegate().getGroup();
   }

   public static class Serializer implements RecipeSerializer<PowerCraftingRecipe> {
      public static final MapCodec<PowerCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ResourceLocation.CODEC.fieldOf("power").forGetter(PowerCraftingRecipe::powerId),
               MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(PowerCraftingRecipe::delegate)
            )
            .apply(instance, PowerCraftingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PowerCraftingRecipe> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

      @NotNull
      public MapCodec<PowerCraftingRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, PowerCraftingRecipe> streamCodec() {
         return PACKET_CODEC;
      }
   }
}
