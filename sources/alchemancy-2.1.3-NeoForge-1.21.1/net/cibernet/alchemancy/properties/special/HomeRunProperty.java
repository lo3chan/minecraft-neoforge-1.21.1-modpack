package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.BouncyProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import org.jetbrains.annotations.Nullable;

public class HomeRunProperty extends Property {
   private static final AttributeModifier SPEED_MOD = new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.799999952316284, Operation.ADD_VALUE);

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 9.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.799999952316284, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   @Override
   public void applyAttributes(ItemAttributeModifierEvent event) {
      event.removeModifier(Attributes.ATTACK_SPEED, Item.BASE_ATTACK_SPEED_ID);
      event.addModifier(Attributes.ATTACK_SPEED, SPEED_MOD, EquipmentSlotGroup.MAINHAND);
   }

   @Override
   public void modifyAttackDamage(Entity user, ItemStack weapon, Pre event) {
      DamageSource damageSource = event.getSource();
      LivingEntity target = event.getEntity();
      if ((user instanceof LivingEntity living ? living.getAttributeValue(Attributes.ATTACK_DAMAGE) : getItemAttackDamage(weapon))
         > event.getNewDamage() * 1.25F) {
         user.level().playSound(null, user, (SoundEvent)AlchemancySoundEvents.HOME_RUN_FAIL.value(), user.getSoundSource(), 1.0F, 1.0F);
      } else {
         Vec3 attackPos = damageSource.getSourcePosition();
         if (attackPos == null && damageSource.getDirectEntity() != null) {
            attackPos = damageSource.getDirectEntity().position();
         }

         if (attackPos == null) {
            attackPos = user.position();
         }

         BouncyProperty.knockBack(target, attackPos, 15.0F);
         user.level().playSound(null, user, (SoundEvent)AlchemancySoundEvents.HOME_RUN_HIT.value(), user.getSoundSource(), 1.0F, 1.0F);
         this.damage(user.level(), user, weapon);
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (target != null && !target.level().isClientSide()) {
         if (source == target && source instanceof Player user && CommonUtils.calculateHitResult(user).getType() != Type.MISS) {
            BouncyProperty.knockBack(user, user.position().add(user.getLookAngle()), 10.0F);
            user.level().playSound(null, user, (SoundEvent)AlchemancySoundEvents.HOME_RUN_HIT.value(), user.getSoundSource(), 1.0F, 1.0F);
            this.damage(user.level(), user, stack);
            user.getCooldowns().addCooldown(stack.getItem(), 80);
         }
      }
   }

   private void damage(Level level, @Nullable Entity user, ItemStack stack) {
      this.damageOrConsumeItem(level, user, stack, EquipmentSlot.MAINHAND, 10);
   }

   @Override
   public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack weapon) {
      BouncyProperty.knockBack(target, position.below().getBottomCenter(), 4.0F);
      this.damage(level, null, weapon);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16775498;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
   }
}
