package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.BloodyGadflyEntity;
import net.mcreator.borninchaosv.entity.CorpseFlyEntity;
import net.mcreator.borninchaosv.entity.DoorKnightEntity;
import net.mcreator.borninchaosv.entity.DoorKnightNotDespawnEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherEntity;
import net.mcreator.borninchaosv.entity.SkeletonThrasherNotDespawnEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public class SpinyShellTrapPriStolknovieniiSushchnostiSBlokomProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if ((
               entity instanceof Mob
                  || entity instanceof Monster
                  || entity instanceof Player
                     && (
                        (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                              != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get()
                           || (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                              != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get()
                     )
            )
            && (!(entity instanceof CorpseFlyEntity) || !(entity instanceof BloodyGadflyEntity))) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.CACTUS)), 2.0F);
            if ((
                  entity instanceof DoorKnightEntity
                     || entity instanceof SkeletonThrasherEntity
                     || entity instanceof SkeletonThrasherNotDespawnEntity
                     || entity instanceof DoorKnightNotDespawnEntity
               )
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 60, 0, false, false));
            }
         }
      }
   }
}
