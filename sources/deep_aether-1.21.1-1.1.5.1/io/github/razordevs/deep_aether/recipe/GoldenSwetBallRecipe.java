package io.github.razordevs.deep_aether.recipe;

import com.aetherteam.aether.recipe.recipes.block.MatchEventRecipe;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.aetherteam.nitrogen.recipe.serializer.BlockStateRecipeSerializer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GoldenSwetBallRecipe extends AbstractBlockStateRecipe implements MatchEventRecipe {
   public GoldenSwetBallRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> functionId) {
      super((RecipeType)DARecipeTypes.GOLDEN_SWET_BALL_RECIPE.get(), ingredient, result, functionId);
   }

   public boolean matches(Player player, Level level, BlockPos pos, ItemStack stack, BlockState oldState, BlockState newState, RecipeType recipeType) {
      return this.matches(level, pos, oldState) && super.matches(player, level, pos, stack, oldState, newState, recipeType);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)DARecipeSerializers.GOLDEN_SWET_BALL_RECIPE.get();
   }

   public static class Serializer extends BlockStateRecipeSerializer<GoldenSwetBallRecipe> {
      public Serializer() {
         super(GoldenSwetBallRecipe::new);
      }
   }
}
