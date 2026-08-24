package com.iafenvoy.origins.recipe;

import com.iafenvoy.origins.accessor.PowerCraftingInventory;
import com.iafenvoy.origins.accessor.PowerCraftingObject;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.mixin.recipe.CraftingMenuAccessor;
import com.iafenvoy.origins.mixin.recipe.TransientCraftingContainerAccessor;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ModifiedCraftingRecipe(ResourceLocation id, CraftingRecipe delegate) implements CraftingRecipe {
   @NotNull
   public CraftingBookCategory category() {
      return this.delegate().category();
   }

   public boolean matches(@NotNull CraftingInput input, @NotNull Level world) {
      return this.delegate().matches(input, world);
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingInput input, @NotNull Provider lookup) {
      if (input instanceof PowerCraftingInventory pci && pci.origins$getPlayer().isPresent()) {
         Pair<ItemStack, Collection<ModifyCraftingPower>> result = this.getModifiedResult(lookup, pci.origins$getPlayer().get());
         pci.origins$setPowerTypes((Collection<? extends Power>)result.getSecond());
         return ((ItemStack)result.getFirst()).copy();
      } else {
         return this.getResultItem(lookup).copy();
      }
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
      return (RecipeSerializer<?>)OriginsRecipeSerializers.MODIFIED.get();
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

   public Pair<ItemStack, Collection<ModifyCraftingPower>> getModifiedResult(Provider registriesLookup, @NotNull Player player) {
      return getModifiedResult(this.id(), this.delegate(), registriesLookup, player);
   }

   public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, RecipeBook recipeBook) {
      return recipeBook instanceof PowerCraftingObject pco && pco.origins$getPlayer().map(p -> canModify(id, craftingRecipe, p)).orElse(false);
   }

   public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, RecipeInput recipeInput) {
      return recipeInput instanceof PowerCraftingObject pco && pco.origins$getPlayer().map(p -> canModify(id, craftingRecipe, p)).orElse(false);
   }

   public static boolean canModify(ResourceLocation id, CraftingRecipe craftingRecipe, @Nullable Player player) {
      return player != null
         && PowerHelper.get(player)
            .anyActive(ModifyCraftingPower.class, mcpt -> mcpt.doesApply(player, id, craftingRecipe.getResultItem(player.registryAccess())));
   }

   public static Pair<ItemStack, Collection<ModifyCraftingPower>> getModifiedResult(
      ResourceLocation id, CraftingRecipe craftingRecipe, Provider registriesLookup, @NotNull Player player
   ) {
      ItemStack resultStack = craftingRecipe.getResultItem(registriesLookup).copy();
      SlotAccess newStackRef = Mutable.stack(resultStack).toSlotAccess();
      List<ModifyCraftingPower> powers = PowerHelper.get(player).listActive(ModifyCraftingPower.class, p -> p.doesApply(player, id, resultStack));
      powers.forEach(mcpt -> mcpt.getNewResult(player, newStackRef));
      return Pair.of(newStackRef.get(), powers);
   }

   public static Optional<BlockPos> getBlockFromInventory(TransientCraftingContainer craftingInventory) {
      return ((TransientCraftingContainerAccessor)craftingInventory).getMenu() instanceof CraftingMenu craftingScreenHandler
         ? ((CraftingMenuAccessor)craftingScreenHandler).getAccess().evaluate((world, pos) -> pos)
         : Optional.empty();
   }

   public static class Serializer implements RecipeSerializer<ModifiedCraftingRecipe> {
      public static final MapCodec<ModifiedCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ResourceLocation.CODEC.fieldOf("id").forGetter(ModifiedCraftingRecipe::id),
               MiscCodecs.DATAPACK_RECIPES_ONLY_CODEC.fieldOf("recipe").forGetter(ModifiedCraftingRecipe::delegate)
            )
            .apply(instance, ModifiedCraftingRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ModifiedCraftingRecipe> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

      @NotNull
      public MapCodec<ModifiedCraftingRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, ModifiedCraftingRecipe> streamCodec() {
         return PACKET_CODEC;
      }
   }
}
