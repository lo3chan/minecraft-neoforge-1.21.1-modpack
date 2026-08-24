package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.ThornshellCrabEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThornshellCrabPriRanieniiSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof ThornshellCrabEntity
            && (
               sourceentity instanceof Player
                     && (
                        (sourceentity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                              != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_HELMET.get()
                           || (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                              != BornInChaosV1ModItems.SPINY_SHELL_ARMOR_CHESTPLATE.get()
                     )
                  || sourceentity instanceof Monster
                  || sourceentity instanceof Mob
            )) {
            if (world.getDifficulty() == Difficulty.HARD) {
               sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 2.0F);
            } else {
               sourceentity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 1.0F);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPIKERELEASE.get(), x, y + 1.0, z, 7, 0.4, 0.4, 0.4, 0.1);
            }
         }
      }
   }
}
