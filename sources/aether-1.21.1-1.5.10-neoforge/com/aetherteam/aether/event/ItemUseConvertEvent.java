package com.aetherteam.aether.event;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ItemUseConvertEvent extends PlayerEvent implements ICancellableEvent {
   private final LevelAccessor level;
   private final BlockPos pos;
   @Nullable
   private final ItemStack itemStack;
   private final RecipeType<?> recipeType;
   private final BlockState oldBlockState;
   private BlockState newBlockState;

   public ItemUseConvertEvent(
      @Nullable Player player,
      LevelAccessor level,
      BlockPos pos,
      @Nullable ItemStack itemStack,
      BlockState oldBlockState,
      BlockState newBlockState,
      RecipeType<?> recipe
   ) {
      super(player);
      this.level = level;
      this.pos = pos;
      this.itemStack = itemStack;
      this.oldBlockState = oldBlockState;
      this.newBlockState = newBlockState;
      this.recipeType = recipe;
   }

   public LevelAccessor getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   @Nullable
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public RecipeType<?> getRecipeType() {
      return this.recipeType;
   }

   public BlockState getOldBlockState() {
      return this.oldBlockState;
   }

   public BlockState getNewBlockState() {
      return this.newBlockState;
   }

   public void setNewBlockState(BlockState newBlockState) {
      this.newBlockState = newBlockState;
   }
}
