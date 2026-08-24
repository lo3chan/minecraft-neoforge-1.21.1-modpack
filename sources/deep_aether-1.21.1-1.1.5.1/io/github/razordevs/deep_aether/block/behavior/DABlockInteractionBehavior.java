package io.github.razordevs.deep_aether.block.behavior;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber(
   modid = "deep_aether",
   bus = Bus.GAME
)
public class DABlockInteractionBehavior {
   @SubscribeEvent
   public static void bonemealEvent(BonemealEvent event) {
      if (event.getState().is(DATags.Blocks.HAS_GLOWING_SPORES)) {
         Block.popResource(event.getLevel(), event.getPos(), new ItemStack((ItemLike)DABlocks.GLOWING_SPORES.get()));
         event.getStack().consume(1, event.getPlayer());
         event.setSuccessful(true);
      }
   }

   @SubscribeEvent
   public static void onRightClick(RightClickBlock event) {
      ItemStack itemstack = event.getItemStack();
      BlockPos pos = event.getPos();
      Level level = event.getLevel();
      BlockState state = level.getBlockState(pos);
      Player player = event.getEntity();
      if (itemstack.is(Items.TOOLS_SHEAR)) {
         handleShears(event, itemstack, pos, level, state, player);
      } else if (event.getFace() != Direction.DOWN
         && ((PotionContents)itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).is(Potions.WATER)) {
         handleWatterBottle(event, itemstack, pos, level, state, player);
      } else if (itemstack.is((Item)AetherItems.SKYROOT_POISON_BUCKET.get())) {
         handleSkyrootPoisonBucket(event, itemstack, level, player);
      }
   }

   private static void handleShears(RightClickBlock event, ItemStack itemstack, BlockPos pos, Level level, BlockState state, Player player) {
      if (state.getBlock().equals(DABlocks.GLOWING_VINE.get())) {
         Block.popResource(level, pos, new ItemStack((ItemLike)DABlocks.GLOWING_SPORES.get()));
         level.setBlock(
            pos,
            (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)Blocks.VINE
                           .defaultBlockState()
                           .setValue(PipeBlock.UP, (Boolean)state.getValue(PipeBlock.UP)))
                        .setValue(PipeBlock.NORTH, (Boolean)state.getValue(PipeBlock.NORTH)))
                     .setValue(PipeBlock.EAST, (Boolean)state.getValue(PipeBlock.EAST)))
                  .setValue(PipeBlock.SOUTH, (Boolean)state.getValue(PipeBlock.SOUTH)))
               .setValue(PipeBlock.WEST, (Boolean)state.getValue(PipeBlock.WEST)),
            18
         );
         level.playSound(player, pos, SoundEvents.BOGGED_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
         if (!level.isClientSide()) {
            itemstack.hurtAndBreak(1, (ServerLevel)level, player, item -> {});
         }

         event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
      } else if (state.getBlock().equals(DABlocks.TALL_GLOWING_GRASS.get())
         && ((DoubleBlockHalf)state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)).equals(DoubleBlockHalf.UPPER)) {
         Block.popResource(level, pos, new ItemStack((ItemLike)DABlocks.GLOWING_SPORES.get()));
         level.setBlock(
            pos.below(1), (BlockState)Blocks.TALL_GRASS.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 18
         );
         level.setBlock(pos, (BlockState)Blocks.TALL_GRASS.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 18);
         level.playSound(player, pos, SoundEvents.BOGGED_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
         if (!level.isClientSide()) {
            itemstack.hurtAndBreak(1, (ServerLevel)level, player, item -> {});
         }

         event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
      }
   }

   private static void handleWatterBottle(RightClickBlock event, ItemStack itemstack, BlockPos pos, Level level, BlockState state, Player player) {
      if (state.getBlock() == AetherBlocks.AETHER_DIRT.get()) {
         BlockState newState = ((Block)DABlocks.AETHER_MUD.get()).defaultBlockState();
         level.setBlockAndUpdate(pos, newState);
         player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
         if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
            ItemStack bottleStack = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE);
            if (!player.addItem(bottleStack)) {
               Containers.dropItemStack(player.level(), player.getX(), player.getY(), player.getZ(), bottleStack);
            }
         }

         if (!level.isClientSide) {
            ServerLevel serverlevel = (ServerLevel)level;

            for (int i = 0; i < 5; i++) {
               serverlevel.sendParticles(
                  ParticleTypes.SPLASH, pos.getX() + level.random.nextDouble(), pos.getY() + 1, pos.getZ() + level.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0
               );
            }
         }

         level.playSound(player, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5F, 1.0F);
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   private static void handleSkyrootPoisonBucket(RightClickBlock event, ItemStack itemstack, Level level, Player player) {
      BlockHitResult blockRayTraceResult = Item.getPlayerPOVHitResult(level, player, Fluid.NONE);
      BlockState blockHitState = level.getBlockState(blockRayTraceResult.getBlockPos());
      if (blockRayTraceResult.getType() != Type.MISS
         && blockRayTraceResult.getType() == Type.BLOCK
         && blockHitState.getBlock() != Blocks.CAULDRON
         && (player.isShiftKeyDown() || !blockHitState.hasBlockEntity())) {
         BlockPos blockpos = blockRayTraceResult.getBlockPos();
         Direction direction = blockRayTraceResult.getDirection();
         BlockPos relativePos = blockpos.relative(direction);
         if (level.mayInteract(player, blockpos) && player.mayUseItemAt(relativePos, direction, itemstack)) {
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, relativePos, itemstack);
            }

            player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            if (!player.getAbilities().instabuild) {
               player.setItemInHand(
                  player.getUsedItemHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get()))
               );
            }

            level.setBlockAndUpdate(relativePos, ((LiquidBlock)DABlocks.POISON_BLOCK.get()).defaultBlockState());
            level.playSound(null, relativePos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, relativePos);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      } else {
         event.setCancellationResult(InteractionResult.PASS);
      }
   }
}
