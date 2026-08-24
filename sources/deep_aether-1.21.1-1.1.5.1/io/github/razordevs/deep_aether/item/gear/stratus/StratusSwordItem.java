package io.github.razordevs.deep_aether.item.gear.stratus;

import com.aetherteam.aether.item.combat.abilities.weapon.GravititeWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class StratusSwordItem extends SwordItem implements GravititeWeapon {
   public StratusSwordItem(Tier tier, Properties properties) {
      super(tier, properties);
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      this.launchEntity(target, attacker);
      return super.hurtEnemy(stack, target, attacker);
   }
}
