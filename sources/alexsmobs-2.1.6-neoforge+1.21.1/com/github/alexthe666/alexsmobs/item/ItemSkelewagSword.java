package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemSkelewagSword extends SwordItem implements IClientExtensionItem {
   public ItemSkelewagSword(Properties props) {
      super(
         Tiers.IRON,
         props.attributes(
            ItemAttributeModifiers.builder()
               .add(
                  Attributes.ATTACK_DAMAGE,
                  AMCompat.attributeModifier(AMCompat.BASE_ATTACK_DAMAGE_ID, "Weapon modifier", 3.5, Operation.ADD_VALUE),
                  EquipmentSlotGroup.MAINHAND
               )
               .add(
                  Attributes.ATTACK_SPEED,
                  AMCompat.attributeModifier(AMCompat.BASE_ATTACK_SPEED_ID, "Weapon modifier", 0.0, Operation.ADD_VALUE),
                  EquipmentSlotGroup.MAINHAND
               )
               .build()
         )
      );
   }

   public float getDamage() {
      return 3.5F;
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
      return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BLOCK;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return this.getUseDuration(stack);
   }

   public int getUseDuration(ItemStack stack) {
      return 72000;
   }

   @Override
   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept((IClientItemExtensions)AlexsMobs.PROXY.getISTERProperties());
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack lvt_4_1_ = player.getItemInHand(hand);
      player.startUsingItem(hand);
      return AMCompat.consume(lvt_4_1_);
   }

   public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
      return repairStack.is(Items.BONE);
   }
}
