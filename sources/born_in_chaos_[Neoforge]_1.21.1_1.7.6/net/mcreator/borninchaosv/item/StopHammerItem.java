package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.StopHammerPriUdariePoSushchnostiPriedmietomProcedure;
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

public class StopHammerItem extends Item {
   public StopHammerItem() {
      super(
         new Properties()
            .stacksTo(1)
            .fireResistant()
            .rarity(Rarity.COMMON)
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, -0.5, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .build()
            )
      );
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      StopHammerPriUdariePoSushchnostiPriedmietomProcedure.execute(entity);
      return retval;
   }
}
