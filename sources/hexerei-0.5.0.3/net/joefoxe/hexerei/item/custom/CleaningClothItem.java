package net.joefoxe.hexerei.item.custom;

import java.util.List;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.neoforged.neoforge.common.ItemAbility;

public class CleaningClothItem extends Item {
   public static final ItemAbility CLOTH_WAX_OFF = ItemAbility.get("cloth_wax_off");

   public CleaningClothItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResult useOn(UseOnContext pContext) {
      Level level = pContext.getLevel();
      BlockPos blockpos = pContext.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      Player player = pContext.getPlayer();
      ItemStack itemstack = pContext.getItemInHand();
      BlockState cleanedState = getCleanedState(blockstate);
      if (cleanedState != null) {
         level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.levelEvent(player, 3004, blockpos, 0);
         if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
         }

         if (blockstate.hasProperty(ConnectingCarpetStairs.COLOR)) {
            cleanedState = (BlockState)cleanedState.trySetValue(ConnectingCarpetStairs.COLOR, (DyeColor)blockstate.getValue(ConnectingCarpetStairs.COLOR));
         }

         level.setBlockAndUpdate(blockpos, cleanedState);
         level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, Context.of(player, cleanedState));
         if (player != null) {
            itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pContext.getHand()));
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
      return (BlockState)to.setValue(property, from.getValue(property));
   }

   public static BlockState copyProperties(BlockState from, BlockState to) {
      for (Property<?> property : from.getProperties()) {
         if (to.hasProperty(property)) {
            to = copyProperty(from, to, property);
         }
      }

      return to;
   }

   public static BlockState getCleanedState(BlockState originalState) {
      if (WaxBlendItem.WAX_OFF_BY_BLOCK.get().containsKey(originalState.getBlock())) {
         Block block = (Block)WaxBlendItem.WAX_OFF_BY_BLOCK.get().get(originalState.getBlock());
         BlockState toReturn = block.defaultBlockState();
         return copyProperties(originalState, toReturn);
      } else {
         return null;
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("tooltip.hexerei.cloth").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
