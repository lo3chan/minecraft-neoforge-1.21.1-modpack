package net.Pandarix.item;

import java.util.List;
import net.Pandarix.config.BAConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TorrentTotemItem extends Item {
   private static final double DASH_SPEED = 2.25;
   private static final double GROUND_LIFT = 1.1999999284744263;

   public TorrentTotemItem(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
      if (BAConfig.torrentTotemEnabled && BAConfig.totemsEnabled) {
         Vec3 direction = pPlayer.getLookAngle();
         if (!BAConfig.torrentTotemUpwardsBoost) {
            direction = new Vec3(direction.x, 0.0, direction.z);
         }

         Vec3 dash = direction.normalize().scale(2.25 * BAConfig.torrentTotemBoost);
         pPlayer.setDeltaMovement(pPlayer.getDeltaMovement().add(dash));
         pPlayer.startAutoSpinAttack(8, 2.0F, itemStack);
         if (pPlayer.onGround()) {
            pPlayer.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
         }

         pLevel.playSound(null, pPlayer, SoundEvents.WATER_AMBIENT, SoundSource.NEUTRAL, 0.1F, (float)pLevel.getRandom().nextDouble() * 0.5F + 0.5F);
         pLevel.playSound(
            null, pPlayer, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.NEUTRAL, 0.25F, 0.35F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
         );
         pPlayer.getCooldowns().addCooldown(this, 120);
         itemStack.hurtAndBreak(1, pPlayer, pPlayer.getEquipmentSlotForItem(itemStack));
         return InteractionResultHolder.consume(itemStack);
      } else {
         if (pLevel.isClientSide()) {
            pPlayer.displayClientMessage(Component.translatableWithFallback("config.notify.disabled", "This feature has been disabled in the config!"), true);
         }

         return InteractionResultHolder.pass(itemStack);
      }
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
      pTooltipComponents.add(Component.translatable("item.betterarcheology.torrent_totem_description").withStyle(ChatFormatting.DARK_AQUA));
   }

   public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
      return 0;
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
      return UseAnim.BOW;
   }
}
