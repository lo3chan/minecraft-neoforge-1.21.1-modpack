package io.github.razordevs.deep_aether.item.misc;

import com.aetherteam.aether.entity.passive.Moa;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.MoaFodder;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class FodderItem extends Item {
   public FodderItem(Properties properties) {
      super(properties);
   }

   public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
      if (!(livingEntity instanceof Moa)) {
         return InteractionResult.FAIL;
      } else if (this.applyMoaEffect(livingEntity, itemStack)) {
         if (!player.isCreative()) {
            itemStack.shrink(1);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      if (!pPlayer.isPassenger()) {
         return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
      } else if (pPlayer.getVehicle() instanceof Moa moa) {
         if (!pPlayer.isCreative()) {
            pPlayer.getItemInHand(pUsedHand).shrink(1);
         }

         this.applyMoaEffect(moa, pPlayer.getItemInHand(pUsedHand));
         return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
      } else {
         return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      MoaFodder fodder = (MoaFodder)stack.get(DADataComponentTypes.MOA_FODDER);
      if (fodder != null) {
         PotionContents.addPotionTooltip(List.of(fodder.effect()), tooltipComponents::add, 1.0F, context.level() == null ? 20.0F : context.tickRate());
      }
   }

   private boolean applyMoaEffect(LivingEntity livingEntity, ItemStack stack) {
      MoaFodder fodder = (MoaFodder)stack.get(DADataComponentTypes.MOA_FODDER);
      if (fodder == null) {
         return false;
      } else if (livingEntity.addEffect(fodder.effect())) {
         livingEntity.level().playLocalSound(livingEntity, SoundEvents.PLAYER_BURP, SoundSource.AMBIENT, 1.0F, 0.2F);
         return true;
      } else {
         return false;
      }
   }

   public static MoaFodder getFodderEffect(ItemStack itemStack) {
      return (MoaFodder)itemStack.get(DADataComponentTypes.MOA_FODDER);
   }
}
