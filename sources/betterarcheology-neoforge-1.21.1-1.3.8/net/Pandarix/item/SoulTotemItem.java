package net.Pandarix.item;

import java.util.List;
import java.util.function.Predicate;
import net.Pandarix.config.BAConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;

public class SoulTotemItem extends Item {
   public SoulTotemItem(Properties pProperties) {
      super(pProperties);
   }

   public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
      return 1;
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BLOCK;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (BAConfig.soulTotemEnabled && BAConfig.totemsEnabled) {
         pPlayer.startUsingItem(pHand);
         return InteractionResultHolder.consume(itemstack);
      } else {
         if (pLevel.isClientSide()) {
            pPlayer.displayClientMessage(Component.translatableWithFallback("config.notify.disabled", "This feature has been disabled in the config!"), true);
         }

         return InteractionResultHolder.pass(itemstack);
      }
   }

   @NotNull
   public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
      if (!(pLivingEntity instanceof Player player)) {
         return pStack;
      } else {
         if (ProjectileUtil.getHitResultOnViewVector(player, Predicate.not(Entity::isSpectator), player.blockInteractionRange()) instanceof EntityHitResult entityHitResult
            && entityHitResult.getType() == Type.ENTITY) {
            Entity entity = entityHitResult.getEntity();
            if (entity.isAttackable()) {
               if (pLevel.isClientSide()) {
                  Vec3 playerPos = player.position();
                  Vec3 targetPos = entity.position();
                  Vec3 toPlayerPos = playerPos.subtract(targetPos);

                  for (float f = 0.0F; f <= 1.0F; f = (float)(f + 0.05)) {
                     pLevel.addParticle(
                        ParticleTypes.SCULK_SOUL,
                        this.lerp(playerPos.x, targetPos.x, f),
                        this.lerp(playerPos.y, targetPos.y, f) + 1.0,
                        this.lerp(playerPos.z, targetPos.z, f),
                        toPlayerPos.x * f / 15.0,
                        toPlayerPos.y * f / 15.0,
                        toPlayerPos.z * f / 15.0
                     );
                  }
               } else {
                  pLevel.playSound(null, player, SoundEvents.MULE_EAT, SoundSource.PLAYERS, 0.5F, 1.0F);
                  pLevel.playSound(null, player, SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.1F, 0.25F);
                  entity.hurt(entity.damageSources().playerAttack(player), 4.0F);
                  player.heal(4.0F);
                  player.getCooldowns().addCooldown(this, 180);
                  pStack.hurtAndBreak(1, player, pLivingEntity.getEquipmentSlotForItem(pStack));
               }
            }
         }

         return super.finishUsingItem(pStack, pLevel, pLivingEntity);
      }
   }

   private double lerp(double a, double b, float f) {
      return a + f * (b - a);
   }

   public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
      super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
      pTooltipComponents.add(Component.translatable("item.betterarcheology.soul_totem_description").withStyle(ChatFormatting.DARK_AQUA));
   }
}
