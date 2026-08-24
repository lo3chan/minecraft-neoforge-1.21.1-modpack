package net.joefoxe.hexerei.item.custom;

import java.util.List;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetDyed;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class WaxingKitItem extends Item {
   boolean isCreative;

   public WaxingKitItem(Properties pProperties, boolean isCreative) {
      super(pProperties);
      this.isCreative = isCreative;
   }

   public boolean isBarVisible(ItemStack pStack) {
      return true;
   }

   public int getBarColor(ItemStack pStack) {
      CustomData data = (CustomData)pStack.get(DataComponents.CUSTOM_DATA);
      if (this.isCreative) {
         return 16733695;
      } else if (data != null && data.contains("waxCount") && data.copyTag().getInt("waxCount") > 0) {
         float f = Math.max(0.0F, this.getBarWidth(pStack) / 13.0F);
         return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
      } else {
         return 3487800;
      }
   }

   public int getBarWidth(ItemStack pStack) {
      CustomData data = (CustomData)pStack.get(DataComponents.CUSTOM_DATA);
      if (data == null || !data.contains("waxCount") || data.copyTag().getInt("waxCount") <= 0) {
         return 13;
      } else {
         return data.contains("waxCount") ? (int)(data.copyTag().getInt("waxCount") / 256.0F * 13.0F) : 0;
      }
   }

   public InteractionResult useOn(UseOnContext pContext) {
      Level level = pContext.getLevel();
      BlockPos blockpos = pContext.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      Player player = pContext.getPlayer();
      ItemStack itemstack = pContext.getItemInHand();
      InteractionResult result = InteractionResult.PASS;
      CustomData data = (CustomData)itemstack.get(DataComponents.CUSTOM_DATA);
      if (this.isCreative || data != null && data.contains("waxCount") && data.copyTag().getInt("waxCount") > 0) {
         result = WaxBlendItem.getWaxed(blockstate)
            .map(
               newBlockstate -> {
                  if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR)) {
                     newBlockstate.setValue(ConnectingCarpetDyed.COLOR, (DyeColor)blockstate.getValue(ConnectingCarpetDyed.COLOR));
                  }

                  if (player instanceof ServerPlayer) {
                     CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
                  }

                  if (!this.isCreative) {
                     CompoundTag tag = data.copyTag();
                     tag.putInt("waxCount", tag.getInt("waxCount") - 1);
                     itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                  }

                  if (blockstate.getBlock() instanceof CrossCollisionBlock) {
                     BlockState changeTo = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)newBlockstate.setValue(
                                    CrossCollisionBlock.NORTH, (Boolean)blockstate.getValue(CrossCollisionBlock.NORTH)
                                 ))
                                 .setValue(CrossCollisionBlock.SOUTH, (Boolean)blockstate.getValue(CrossCollisionBlock.SOUTH)))
                              .setValue(CrossCollisionBlock.EAST, (Boolean)blockstate.getValue(CrossCollisionBlock.EAST)))
                           .setValue(CrossCollisionBlock.WEST, (Boolean)blockstate.getValue(CrossCollisionBlock.WEST)))
                        .setValue(CrossCollisionBlock.WATERLOGGED, (Boolean)blockstate.getValue(CrossCollisionBlock.WATERLOGGED));
                     if (blockstate.hasProperty(ConnectingCarpetDyed.COLOR)) {
                        changeTo.setValue(ConnectingCarpetDyed.COLOR, (DyeColor)blockstate.getValue(ConnectingCarpetDyed.COLOR));
                     }

                     level.setBlockAndUpdate(blockpos, changeTo);
                  } else {
                     level.setBlock(blockpos, newBlockstate, 11);
                  }

                  level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, Context.of(player, newBlockstate));
                  level.levelEvent(player, 3003, blockpos, 0);
                  return InteractionResult.sidedSuccess(level.isClientSide);
               }
            )
            .orElse(InteractionResult.PASS);
      }

      if (result == InteractionResult.PASS) {
         BlockState cleanedState = CleaningClothItem.getCleanedState(blockstate);
         if (cleanedState != null) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
            }

            if (blockstate.hasProperty(ConnectingCarpetStairs.COLOR)) {
               cleanedState = (BlockState)cleanedState.trySetValue(ConnectingCarpetStairs.COLOR, (DyeColor)blockstate.getValue(ConnectingCarpetStairs.COLOR));
            }

            level.setBlock(blockpos, cleanedState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, Context.of(player, cleanedState));
            if (player != null) {
               itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pContext.getHand()));
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      } else {
         return result;
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         Component wax_blend = Component.translatable(((Item)ModItems.WAX_BLEND.get()).getDescription().getString())
            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
         tooltipComponents.add(
            Component.translatable("tooltip.hexerei.waxing_kit", new Object[]{wax_blend}).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.waxing_kit_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      int count = 0;
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      if (data != null && data.contains("waxCount")) {
         count = data.copyTag().getInt("waxCount");
      }

      if (!this.isCreative) {
         tooltipComponents.add(
            Component.translatable(
                  "%s: " + count + " / 256",
                  new Object[]{Component.translatable("tooltip.hexerei.wax").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10071705)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      } else {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.infinite_wax").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10071705))));
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
