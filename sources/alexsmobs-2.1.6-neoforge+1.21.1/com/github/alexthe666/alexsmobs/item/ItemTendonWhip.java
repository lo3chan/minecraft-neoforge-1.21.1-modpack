package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import com.github.alexthe666.alexsmobs.entity.util.TendonWhipUtil;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemTendonWhip extends SwordItem implements ILeftClick {
   public ItemTendonWhip(Properties props) {
      super(
         Tiers.IRON,
         props.attributes(
            ItemAttributeModifiers.builder()
               .add(
                  Attributes.ATTACK_DAMAGE,
                  AMCompat.attributeModifier(AMCompat.BASE_ATTACK_DAMAGE_ID, "Weapon modifier", 4.0, Operation.ADD_VALUE),
                  EquipmentSlotGroup.MAINHAND
               )
               .add(
                  Attributes.ATTACK_SPEED,
                  AMCompat.attributeModifier(AMCompat.BASE_ATTACK_SPEED_ID, "Weapon modifier", -3.0, Operation.ADD_VALUE),
                  EquipmentSlotGroup.MAINHAND
               )
               .build()
         )
      );
   }

   public static boolean isActive(ItemStack stack, LivingEntity holder) {
      return holder != null && (holder.getMainHandItem() == stack || holder.getOffhandItem() == stack)
         ? !TendonWhipUtil.canLaunchTendons(holder.level(), holder)
         : false;
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player) {
      this.launchTendonsAt(stack, player, entity);
      return super.hurtEnemy(stack, entity, player);
   }

   private boolean isCharged(Player player, ItemStack stack) {
      return player.getAttackStrengthScale(0.5F) > 0.9F;
   }

   @Override
   public boolean onLeftClick(ItemStack stack, LivingEntity playerIn) {
      if (stack.is(AMItemRegistry.TENDON_WHIP.get()) && (!(playerIn instanceof Player) || this.isCharged((Player)playerIn, stack))) {
         Level worldIn = playerIn.level();
         Entity closestValid = null;
         Vec3 playerEyes = playerIn.getEyePosition(1.0F);
         HitResult hitresult = worldIn.clip(
            new ClipContext(playerEyes, playerEyes.add(playerIn.getLookAngle().scale(12.0)), Block.VISUAL, Fluid.NONE, playerIn)
         );
         if (hitresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult)hitresult).getEntity();
            if (!entity.equals(playerIn)
               && !playerIn.isAlliedTo(entity)
               && !entity.isAlliedTo(playerIn)
               && entity instanceof Mob
               && playerIn.hasLineOfSight(entity)) {
               closestValid = entity;
            }
         } else {
            for (Entity entity : worldIn.getEntitiesOfClass(LivingEntity.class, playerIn.getBoundingBox().inflate(12.0))) {
               if (!entity.equals(playerIn)
                  && !playerIn.isAlliedTo(entity)
                  && !entity.isAlliedTo(playerIn)
                  && entity instanceof Mob
                  && playerIn.hasLineOfSight(entity)
                  && (closestValid == null || playerIn.distanceTo(entity) < playerIn.distanceTo(closestValid))) {
                  closestValid = entity;
               }
            }
         }

         if (closestValid != null) {
            AMCompat.hurtAndBreak(stack, 1, playerIn, playerIn.getUsedItemHand());
         }

         return this.launchTendonsAt(stack, playerIn, closestValid);
      } else {
         return false;
      }
   }

   public boolean launchTendonsAt(ItemStack stack, LivingEntity playerIn, Entity closestValid) {
      Level worldIn = playerIn.level();
      if (TendonWhipUtil.canLaunchTendons(worldIn, playerIn)) {
         TendonWhipUtil.retractFarTendons(worldIn, playerIn);
         if (!worldIn.isClientSide() && closestValid != null) {
            EntityTendonSegment segment = AMCompat.create(AMEntityRegistry.TENDON_SEGMENT.get(), worldIn);
            segment.copyPosition(playerIn);
            worldIn.addFreshEntity(segment);
            segment.setCreatorEntityUUID(playerIn.getUUID());
            segment.setFromEntityID(playerIn.getId());
            segment.setToEntityID(closestValid.getId());
            segment.copyPosition(playerIn);
            segment.setProgress(0.0F);
            segment.setHasGlint(stack.hasFoil());
            TendonWhipUtil.setLastTendon(playerIn, segment);
            return true;
         }
      }

      return false;
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
      return toolAction != ItemAbilities.SWORD_SWEEP && super.canPerformAction(stack, toolAction);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.isSameItem(oldStack, newStack);
   }

   public int getMaxDamage(ItemStack stack) {
      return 450;
   }

   public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
      return stack.is(AMItemRegistry.ELASTIC_TENDON.get());
   }
}
