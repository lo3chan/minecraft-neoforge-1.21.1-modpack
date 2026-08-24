package com.aetherteam.aether.item.combat.abilities.weapon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;

public interface HolystoneWeapon {
   default void dropAmbrosium(LivingEntity target, LivingEntity attacker) {
      if (EquipmentUtil.isFullStrength(attacker) && !target.getType().is(AetherTags.Entities.NO_AMBROSIUM_DROPS) && target.level().getRandom().nextInt(25) == 0
         )
       {
         target.spawnAtLocation((ItemLike)AetherItems.AMBROSIUM_SHARD.get());
      }
   }
}
