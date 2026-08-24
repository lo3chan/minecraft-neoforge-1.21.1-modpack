package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherGameEvents;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.FreezingBlock;
import com.aetherteam.aether.blockentity.IcestoneBlockEntity;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;

public class RecipeHooks {
   public static boolean checkInteractionBanned(
      Player player, Level level, BlockPos pos, Direction face, ItemStack stack, BlockState state, boolean spawnParticles
   ) {
      if (isItemPlacementBanned(level, pos, face, stack, spawnParticles)) {
         player.displayClientMessage(Component.translatable("aether.banned_item", new Object[]{stack.getItem().getName(stack)}), true);
         return true;
      } else if (level.getBiome(pos).is(AetherTags.Biomes.ULTRACOLD)
         && (Boolean)AetherConfig.SERVER.enable_bed_explosions.get()
         && state.is(BlockTags.BEDS)
         && state.getBlock() != AetherBlocks.SKYROOT_BED.get()) {
         if (!level.isClientSide()) {
            if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
               pos = pos.relative((Direction)state.getValue(BedBlock.FACING));
               state = level.getBlockState(pos);
            }

            BlockPos blockpos = pos.relative(((Direction)state.getValue(BedBlock.FACING)).getOpposite());
            if (level.getBlockState(blockpos).is(BlockTags.BEDS) && level.getBlockState(blockpos).getBlock() != AetherBlocks.SKYROOT_BED.get()) {
               level.removeBlock(blockpos, false);
            }

            Vec3 vec3 = pos.getCenter();
            level.explode(
               null,
               level.damageSources().badRespawnPointExplosion(vec3),
               null,
               pos.getX() + 0.5,
               pos.getY() + 0.5,
               pos.getZ() + 0.5,
               5.0F,
               true,
               ExplosionInteraction.BLOCK
            );
         }

         player.swing(InteractionHand.MAIN_HAND);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isItemPlacementBanned(Level level, BlockPos pos, Direction face, ItemStack stack, boolean spawnParticles) {
      for (RecipeHolder<ItemBanRecipe> recipe : level.getRecipeManager().getAllRecipesFor((RecipeType)AetherRecipeTypes.ITEM_PLACEMENT_BAN.get())) {
         if (((ItemBanRecipe)recipe.value()).banItem(level, pos, face, stack, spawnParticles)) {
            return true;
         }
      }

      return false;
   }

   public static void checkExistenceBanned(LevelAccessor levelAccessor, BlockPos pos) {
      if (levelAccessor instanceof Level level) {
         BlockState state = levelAccessor.getBlockState(pos);
         if (isBlockPlacementBanned(level, pos, state)) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            if (state.getBlock().asItem() != Items.AIR) {
               Block.dropResources(state, level, pos);
            }
         } else {
            isBlockPlacementConvertable(level, pos, state);
         }
      }
   }

   private static boolean isBlockPlacementBanned(Level level, BlockPos pos, BlockState state) {
      if (!level.isClientSide()) {
         for (RecipeHolder<BlockBanRecipe> recipe : level.getRecipeManager().getAllRecipesFor((RecipeType)AetherRecipeTypes.BLOCK_PLACEMENT_BAN.get())) {
            if (((BlockBanRecipe)recipe.value()).banBlock(level, pos, state)) {
               return true;
            }
         }
      }

      return false;
   }

   private static void isBlockPlacementConvertable(Level level, BlockPos pos, BlockState state) {
      if (!level.isClientSide()) {
         for (RecipeHolder<PlacementConversionRecipe> recipe : level.getRecipeManager()
            .getAllRecipesFor((RecipeType)AetherRecipeTypes.PLACEMENT_CONVERSION.get())) {
            if (((PlacementConversionRecipe)recipe.value()).convert(level, pos, state)) {
               return;
            }
         }
      }
   }

   public static void banOrConvert(LevelAccessor accessor, BlockPos pos) {
      if (accessor instanceof ServerLevel serverLevel) {
         double x = pos.getX() + 0.5;
         double y = pos.getY() + 1.0;
         double z = pos.getZ() + 0.5;

         for (int i = 0; i < 10; i++) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         }

         serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public static void sendIcestoneFreezableUpdateEvent(LevelAccessor accessor, BlockPos pos) {
      if (accessor instanceof Level level && !level.isClientSide()) {
         BlockState oldBlockState = level.getBlockState(pos);
         FreezingBlock.cacheRecipes(level);
         if (FreezingBlock.matchesCache(oldBlockState.getBlock(), oldBlockState) != null) {
            level.gameEvent(AetherGameEvents.ICESTONE_FREEZABLE_UPDATE, pos, Context.of(oldBlockState));
         }
      }
   }

   public static boolean preventBlockFreezing(LevelAccessor accessor, BlockPos sourcePos, BlockPos pos) {
      if (accessor.getBlockEntity(sourcePos) instanceof IcestoneBlockEntity blockEntity) {
         for (Entry<BlockPos, Integer> entry : blockEntity.getLastBrokenPositions().entrySet()) {
            if (entry.getKey().equals(pos) && entry.getValue() > 0) {
               return true;
            }
         }
      }

      return false;
   }
}
