package com.aetherteam.aether.item.materials.behavior;

import com.aetherteam.aether.recipe.recipes.block.MatchEventRecipe;
import com.aetherteam.nitrogen.recipe.recipes.BlockStateRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ItemUseConversion<R extends MatchEventRecipe & BlockStateRecipe> {
   default <T extends R> InteractionResult convertBlock(RecipeType<T> recipeType, UseOnContext context) {
      Player player = context.getPlayer();
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      ItemStack heldItem = context.getItemInHand();
      BlockState oldBlockState = level.getBlockState(pos);

      for (RecipeHolder<T> recipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
         if (recipe != null) {
            BlockState newState = ((BlockStateRecipe)((MatchEventRecipe)recipe.value())).getResultState(oldBlockState);
            if (((MatchEventRecipe)recipe.value()).matches(player, level, pos, heldItem, oldBlockState, newState, recipeType)) {
               if (!level.isClientSide()
                  && ((MatchEventRecipe)recipe.value()).convert(level, pos, newState, ((BlockStateRecipe)((MatchEventRecipe)recipe.value())).getFunction())) {
                  if (player != null && !player.getAbilities().instabuild) {
                     heldItem.shrink(1);
                  }

                  return InteractionResult.CONSUME;
               }

               if (level.isClientSide()) {
                  return InteractionResult.SUCCESS;
               }
            }
         }
      }

      return InteractionResult.PASS;
   }

   default <T extends R> boolean convertBlockWithoutContext(RecipeType<T> recipeType, Level level, BlockPos pos, ItemStack stack) {
      if (!level.isClientSide()) {
         BlockState oldBlockState = level.getBlockState(pos);

         for (RecipeHolder<T> recipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
            if (recipe != null) {
               BlockState newState = ((BlockStateRecipe)((MatchEventRecipe)recipe.value())).getResultState(oldBlockState);
               if (((MatchEventRecipe)recipe.value()).matches(null, level, pos, null, oldBlockState, newState, recipeType)
                  && ((MatchEventRecipe)recipe.value()).convert(level, pos, newState, ((BlockStateRecipe)((MatchEventRecipe)recipe.value())).getFunction())) {
                  stack.shrink(1);
                  return true;
               }
            }
         }
      }

      return false;
   }
}
