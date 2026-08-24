package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.miscellaneous.CloudMinion;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CloudStaffItem extends Item {
   public CloudStaffItem() {
      super(new Properties().durability(60).rarity(AetherItems.AETHER_LOOT));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldItem = player.getItemInHand(hand);
      AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
      if (data.getCloudMinions().isEmpty()) {
         player.swing(hand);
         if (!level.isClientSide()) {
            if (!player.getAbilities().instabuild) {
               heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            CloudMinion cloudMinionRight = new CloudMinion(level, player, HumanoidArm.RIGHT);
            CloudMinion cloudMinionLeft = new CloudMinion(level, player, HumanoidArm.LEFT);
            level.addFreshEntity(cloudMinionRight);
            level.addFreshEntity(cloudMinionLeft);
            data.setCloudMinions(player, cloudMinionRight, cloudMinionLeft);
         }

         this.spawnExplosionParticles(player);
      } else if (player.isShiftKeyDown()) {
         player.swing(hand);

         for (CloudMinion cloudMinion : data.getCloudMinions()) {
            cloudMinion.setLifeSpan(0);
         }
      }

      return InteractionResultHolder.pass(heldItem);
   }

   public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
      if (entity instanceof Player player) {
         AetherPlayerAttachment data = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if (!player.getCooldowns().isOnCooldown(this) && data.isHitting()) {
            boolean hasMinions = false;

            for (int i = 0; i < data.getCloudMinions().size(); i++) {
               CloudMinion cloudMinion = data.getCloudMinions().get(i);
               if (cloudMinion != null) {
                  cloudMinion.setShouldShoot(true);
                  hasMinions = true;
               }
            }

            if (hasMinions && !player.getAbilities().instabuild) {
               player.getCooldowns().addCooldown(this, (Integer)AetherConfig.SERVER.cloud_staff_cooldown.get());
            }
         }
      }

      return super.onEntitySwing(stack, entity, hand);
   }

   private void spawnExplosionParticles(Player player) {
      if (player.level().isClientSide()) {
         EntityUtil.spawnSummoningExplosionParticles(player);
      }
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      return !player.isCreative();
   }
}
