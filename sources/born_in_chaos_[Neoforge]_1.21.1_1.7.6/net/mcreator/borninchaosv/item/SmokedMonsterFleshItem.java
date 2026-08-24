package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.MonsterFleshPriUdariePoSushchnostiPriedmietomProcedure;
import net.mcreator.borninchaosv.procedures.SmokedFleshPriZaviershieniiIspolzovaniiaProcedure;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class SmokedMonsterFleshItem extends Item {
   public SmokedMonsterFleshItem() {
      super(
         new Properties()
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food(new Builder().nutrition(6).saturationModifier(0.5F).build())
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .build()
            )
      );
   }

   public int getEnchantmentValue() {
      return 5;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 40;
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = new ItemStack(Items.BONE);
      super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      SmokedFleshPriZaviershieniiIspolzovaniiaProcedure.execute(entity);
      if (itemstack.isEmpty()) {
         return retval;
      } else {
         if (entity instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().add(retval)) {
            player.drop(retval, false);
         }

         return itemstack;
      }
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      MonsterFleshPriUdariePoSushchnostiPriedmietomProcedure.execute(sourceentity);
      return retval;
   }
}
