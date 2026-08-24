package com.aetherteam.aether.block;

import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.event.FreezeEvent;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

public interface FreezingBlock extends FreezingBehavior<BlockState> {
   float SQRT_8 = Mth.sqrt(8.0F);
   Table<Block, BlockPropertyPair, IcestoneFreezableRecipe> cachedBlocks = HashBasedTable.create();
   List<Block> cachedResults = new ArrayList<>();

   default int freezeFromRecipe(Level level, BlockPos pos, BlockPos origin, BlockState source, int flag) {
      if (!level.isClientSide()) {
         BlockState oldBlockState = level.getBlockState(pos);
         Block oldBlock = oldBlockState.getBlock();
         FluidState fluidState = level.getFluidState(pos);
         if (fluidState.isEmpty() || oldBlockState.is(fluidState.createLegacyBlock().getBlock())) {
            BlockPropertyPair pair = matchesCache(oldBlock, oldBlockState);
            if (pair != null) {
               IcestoneFreezableRecipe freezableRecipe = (IcestoneFreezableRecipe)cachedBlocks.get(oldBlock, pair);
               if (freezableRecipe != null) {
                  BlockState newBlockState = freezableRecipe.getResultState(oldBlockState);
                  Optional<CacheableFunction> function = freezableRecipe.getFunction();
                  return this.freezeBlockAt(level, pos, origin, oldBlockState, newBlockState, function, source, flag);
               }
            }
         } else if (!oldBlockState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            oldBlockState = fluidState.createLegacyBlock();
            oldBlock = fluidState.createLegacyBlock().getBlock();
            BlockPropertyPair pair = matchesCache(oldBlock, oldBlockState);
            if (pair != null) {
               IcestoneFreezableRecipe freezableRecipe = (IcestoneFreezableRecipe)cachedBlocks.get(oldBlock, pair);
               if (freezableRecipe != null) {
                  level.destroyBlock(pos, true);
                  BlockState newBlockState = freezableRecipe.getResultState(oldBlockState);
                  Optional<CacheableFunction> function = freezableRecipe.getFunction();
                  return this.freezeBlockAt(level, pos, origin, oldBlockState, newBlockState, function, source, flag);
               }
            }
         }
      }

      return 0;
   }

   default FreezeEvent onFreeze(LevelAccessor level, BlockPos pos, BlockPos origin, BlockState oldBlockState, BlockState newBlockState, BlockState source) {
      return AetherEventDispatch.onBlockFreezeFluid(level, pos, origin, oldBlockState, newBlockState, source);
   }

   static void cacheRecipes(Level level) {
      if (cachedBlocks.isEmpty()) {
         for (RecipeHolder<IcestoneFreezableRecipe> recipe : level.getRecipeManager().getAllRecipesFor((RecipeType)AetherRecipeTypes.ICESTONE_FREEZABLE.get())) {
            IcestoneFreezableRecipe freezableRecipe = (IcestoneFreezableRecipe)recipe.value();
            BlockPropertyPair[] pairs = freezableRecipe.getIngredient().getPairs();
            if (pairs != null) {
               Arrays.stream(pairs).forEach(pair -> cachedBlocks.put(pair.block(), pair, freezableRecipe));
            }

            cachedResults.add(freezableRecipe.getResult().block());
         }
      }
   }

   @Nullable
   static BlockPropertyPair matchesCache(Block block, BlockState blockState) {
      if (cachedBlocks.containsRow(block)) {
         BlockPropertyPair pair = null;

         for (Entry<BlockPropertyPair, IcestoneFreezableRecipe> entry : cachedBlocks.row(block).entrySet()) {
            if (entry.getKey().matches(blockState)) {
               pair = entry.getKey();
            }
         }

         return pair;
      } else {
         return null;
      }
   }
}
