package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ThespitterOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl2
               && _lvl2.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if ((world.getLevelData().isRaining() || world.getLevelData().isThundering()) && !world.isClientSide()) {
               entity.clearFire();
            }
         }

         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:dirt")))
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:animals_spawnable_on")))
            || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:sand")))) {
            if (!((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity)
               && entity.getPersistentData().getDouble("spitter_hid") <= 0.0
               && !(entity instanceof LivingEntity _livEnt22 && _livEnt22.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST))
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.ANIMATIONTEST, 100, 0, false, false));
            }

            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
               && entity.getPersistentData().getDouble("spitter_hid") > 0.0
               && entity instanceof LivingEntity _livEnt27
               && _livEnt27.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST);
               }

               if (entity instanceof LivingEntity _entity) {
                  _entity.removeAllEffects();
               }
            }
         }

         if (entity.getPersistentData().getDouble("spitter_hid") >= 1.0) {
            entity.getPersistentData().putDouble("spitter_hid", entity.getPersistentData().getDouble("spitter_hid") - 1.0);
         }
      }
   }
}
