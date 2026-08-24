package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.SpiderMandiblePriUdariePoSushchnostiPriedmietomProcedure;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;

public class SpiderMandibleItem extends Item {
   public SpiderMandibleItem() {
      super(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 0.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .build()
            )
      );
   }

   public float getDestroySpeed(ItemStack itemstack, BlockState state) {
      return 1.5F;
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      SpiderMandiblePriUdariePoSushchnostiPriedmietomProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, sourceentity);
      return retval;
   }
}
