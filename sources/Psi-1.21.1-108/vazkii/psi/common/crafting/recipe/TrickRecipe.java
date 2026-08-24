package vazkii.psi.common.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.recipe.ITrickRecipe;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.piece.PieceCraftingTrick;
import vazkii.psi.common.crafting.ModCraftingRecipes;
import vazkii.psi.common.item.base.ModItems;

public class TrickRecipe implements ITrickRecipe {
   @Nullable
   protected final PieceCraftingTrick piece;
   protected final Ingredient input;
   protected final ItemStack output;
   protected final ItemStack cad;

   public TrickRecipe(@Nullable PieceCraftingTrick piece, Ingredient input, ItemStack output, ItemStack cad) {
      this.piece = piece;
      this.input = input;
      this.output = output;
      this.cad = cad;
   }

   @Nullable
   @Override
   public PieceCraftingTrick getPiece() {
      return this.piece;
   }

   @NotNull
   @Override
   public Ingredient getInput() {
      return this.input;
   }

   @NotNull
   @Override
   public ItemStack getResultItem(Provider pRegistries) {
      return this.output;
   }

   @Override
   public ItemStack getAssembly() {
      return this.cad;
   }

   public boolean matches(SingleRecipeInput inv, Level world) {
      return this.getInput().test(inv.getItem(0));
   }

   @NotNull
   public ItemStack assemble(SingleRecipeInput inv, Provider pRegistries) {
      return this.getResultItem(pRegistries);
   }

   @NotNull
   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)ModItems.cad.get());
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModCraftingRecipes.TRICK_RECIPE_SERIALIZER.get();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   @Override
   public RecipeType<?> getType() {
      return (RecipeType<?>)ModCraftingRecipes.TRICK_RECIPE_TYPE.get();
   }

   public boolean isSpecial() {
      return true;
   }

   public static class Serializer implements RecipeSerializer<TrickRecipe> {
      public static final StreamCodec<RegistryFriendlyByteBuf, TrickRecipe> STREAM_CODEC = StreamCodec.of(
         TrickRecipe.Serializer::toNetwork, TrickRecipe.Serializer::fromNetwork
      );
      private static final MapCodec<TrickRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC.fieldOf("input").forGetter(d -> d.input),
               ItemStack.CODEC.fieldOf("output").forGetter(d -> d.output),
               ItemStack.CODEC.fieldOf("cad").forGetter(d -> d.cad),
               ResourceLocation.CODEC.optionalFieldOf("piece").forGetter(d -> d.piece != null ? Optional.of(d.piece.registryKey) : Optional.empty())
            )
            .apply(
               instance,
               (ingredient, output, cadAssembly, trick) -> new TrickRecipe(
                  (PieceCraftingTrick)SpellPiece.create((ResourceLocation)trick.orElse(null)), ingredient, output, cadAssembly
               )
            )
      );

      private static TrickRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
         ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
         ItemStack cadAssembly = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
         PieceCraftingTrick trick = null;
         if ((Boolean)ByteBufCodecs.BOOL.decode(buf)) {
            trick = (PieceCraftingTrick)SpellPiece.create((ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buf));
         }

         return new TrickRecipe(trick, ingredient, output, cadAssembly);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, TrickRecipe recipe) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
         ItemStack.STREAM_CODEC.encode(buf, recipe.output);
         ItemStack.STREAM_CODEC.encode(buf, recipe.cad);
         if (recipe.piece != null) {
            ByteBufCodecs.BOOL.encode(buf, true);
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.piece.registryKey);
         } else {
            ByteBufCodecs.BOOL.encode(buf, false);
         }
      }

      @NotNull
      public MapCodec<TrickRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, TrickRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
