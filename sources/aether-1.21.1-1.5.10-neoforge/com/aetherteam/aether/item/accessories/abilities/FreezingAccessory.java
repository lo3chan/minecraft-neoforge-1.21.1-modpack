package com.aetherteam.aether.item.accessories.abilities;

import com.aetherteam.aether.block.FreezingBehavior;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.event.FreezeEvent;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotReference;
import java.util.Optional;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

public interface FreezingAccessory extends FreezingBehavior<ItemStack> {
   default void freezeTick(SlotReference context, ItemStack stack) {
      LivingEntity livingEntity = context.entity();
      if (!(livingEntity instanceof Player player && (player.getAbilities().flying || player.isSpectator()))) {
         int damage = this.freezeBlocks(livingEntity.level(), livingEntity.blockPosition(), stack, 1.9F);
         if (livingEntity.level() instanceof ServerLevel serverLevel) {
            context.getStack().hurtAndBreak(damage / 3, serverLevel, livingEntity, item -> AccessoriesAPI.breakStack(context));
         }
      }
   }

   default int freezeFromRecipe(Level level, BlockPos pos, BlockPos origin, ItemStack source, int flag) {
      if (!level.isClientSide()) {
         BlockState oldBlockState = level.getBlockState(pos);
         FluidState fluidState = level.getFluidState(pos);

         for (RecipeHolder<AccessoryFreezableRecipe> recipe : level.getRecipeManager()
            .getAllRecipesFor((RecipeType)AetherRecipeTypes.ACCESSORY_FREEZABLE.get())) {
            AccessoryFreezableRecipe freezableRecipe = (AccessoryFreezableRecipe)recipe.value();
            if (!fluidState.isEmpty() && !oldBlockState.is(fluidState.createLegacyBlock().getBlock())) {
               if (!oldBlockState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                  oldBlockState = fluidState.createLegacyBlock();
                  if (freezableRecipe.matches(level, pos, oldBlockState)) {
                     level.destroyBlock(pos, true);
                     BlockState newBlockState = freezableRecipe.getResultState(oldBlockState);
                     Optional<CacheableFunction> function = freezableRecipe.getFunction();
                     return this.freezeBlockAt(level, pos, origin, oldBlockState, newBlockState, function, source, flag);
                  }
               }
            } else if (freezableRecipe.matches(level, pos, oldBlockState)) {
               BlockState newBlockState = freezableRecipe.getResultState(oldBlockState);
               Optional<CacheableFunction> function = freezableRecipe.getFunction();
               return this.freezeBlockAt(level, pos, origin, oldBlockState, newBlockState, function, source, flag);
            }
         }
      }

      return 0;
   }

   default FreezeEvent onFreeze(LevelAccessor level, BlockPos pos, BlockPos origin, BlockState oldBlockState, BlockState newBlockState, ItemStack source) {
      return AetherEventDispatch.onItemFreezeFluid(level, pos, oldBlockState, newBlockState, source);
   }
}
