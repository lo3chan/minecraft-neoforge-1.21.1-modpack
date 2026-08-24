package com.aetherteam.aether.item.miscellaneous.bucket;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class SkyrootBucketItem extends BucketItem {
   public static final Map<Supplier<? extends Item>, Supplier<? extends Item>> REPLACEMENTS = new HashMap<>();

   public SkyrootBucketItem(Fluid supplier, Properties properties) {
      super(supplier, properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      BlockHitResult blockhitResult = getPlayerPOVHitResult(
         level,
         player,
         this.content == Fluids.EMPTY ? net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY : net.minecraft.world.level.ClipContext.Fluid.NONE
      );
      if (blockhitResult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(heldStack);
      } else if (blockhitResult.getType() != Type.BLOCK) {
         return InteractionResultHolder.pass(heldStack);
      } else {
         BlockPos blockPos = blockhitResult.getBlockPos();
         Direction direction = blockhitResult.getDirection();
         BlockPos relativePos = blockPos.relative(direction);
         if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativePos, direction, heldStack)) {
            return InteractionResultHolder.fail(heldStack);
         } else if (this.content != Fluids.EMPTY) {
            BlockState blockState = level.getBlockState(blockPos);
            BlockPos newPos = this.canBlockContainFluid(player, level, blockPos, blockState) ? blockPos : relativePos;
            if (this.emptyContents(player, level, newPos, blockhitResult, heldStack)) {
               this.checkExtraContent(player, level, heldStack, newPos);
               if (player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, newPos, heldStack);
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(heldStack, player), level.isClientSide());
            } else {
               return InteractionResultHolder.fail(heldStack);
            }
         } else {
            BlockState blockState = level.getBlockState(blockPos);
            FluidState fluidState = level.getFluidState(blockPos);
            if (blockState.getBlock() instanceof BucketPickup bucketPickup
               && (blockState.is(AetherTags.Blocks.ALLOWED_BUCKET_PICKUP) || fluidState.is(AetherTags.Fluids.ALLOWED_BUCKET_PICKUP))) {
               ItemStack bucketStack = bucketPickup.pickupBlock(player, level, blockPos, blockState);
               bucketStack = swapBucketType(bucketStack);
               if (!bucketStack.isEmpty()) {
                  player.awardStat(Stats.ITEM_USED.get(this));
                  bucketPickup.getPickupSound(blockState).ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
                  level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
                  ItemStack resultStack = ItemUtils.createFilledResult(heldStack, player, bucketStack);
                  if (!level.isClientSide()) {
                     CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, bucketStack);
                  }

                  return InteractionResultHolder.sidedSuccess(resultStack, level.isClientSide());
               }
            }

            return InteractionResultHolder.fail(heldStack);
         }
      }
   }

   public static ItemStack swapBucketType(ItemStack filledStack) {
      Supplier<? extends Item> filledItem = filledStack::getItem;

      for (Entry<Supplier<? extends Item>, Supplier<? extends Item>> entry : REPLACEMENTS.entrySet()) {
         if (filledItem.get() == entry.getKey().get()) {
            Item replacedItem = entry.getValue().get();
            ItemStack newStack = new ItemStack(replacedItem, 1);
            newStack.applyComponents(filledStack.getComponentsPatch());
            return newStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static ItemStack getEmptySuccessItem(ItemStack bucketStack, Player player) {
      return !player.getAbilities().instabuild ? new ItemStack((ItemLike)AetherItems.SKYROOT_BUCKET.get()) : bucketStack;
   }

   protected boolean canBlockContainFluid(Player player, Level level, BlockPos pos, BlockState state) {
      return state.getBlock() instanceof LiquidBlockContainer liquidBlockContainer
         && liquidBlockContainer.canPlaceLiquid(player, level, pos, state, this.content);
   }
}
