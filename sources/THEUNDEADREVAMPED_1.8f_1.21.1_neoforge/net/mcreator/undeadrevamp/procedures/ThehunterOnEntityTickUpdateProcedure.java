package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.SlavemanEntity;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThehunterOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         boolean found = false;
         double sx = 0.0;
         double sy = 0.0;
         double sz = 0.0;
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.HUNGER))) {
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
               entity.getPersistentData().putDouble("gaszz_spead", entity.getPersistentData().getDouble("gaszz_spead") - 1.0);
            }

            if (entity.getPersistentData().getDouble("gaszz_spead") <= 0.0
               && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
               entity.getPersistentData().putDouble("gaszz_spead", 150.0);
               entity.getPersistentData().putDouble("gaszz_sped", 1.0);
            }

            if (entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(UndeadRevamp2ModMobEffects.BOMBEREXPLODING)) {
               entity.getPersistentData().putBoolean("inflat_anim", true);
            } else {
               entity.getPersistentData().putBoolean("inflat_anim", false);
            }

            if (entity.getPersistentData().getDouble("horned") == 1.0 && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, false));
            }

            if (entity.getPersistentData().getDouble("horned") == 2.0 && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, false, false));
            }

            if ((Boolean)MobsabilityConfiguration.HUNT_EAT.get()) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator instanceof ItemEntity
                     && (entityiterator instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY)
                        .is(ItemTags.create(ResourceLocation.parse("forge:hunterfood")))) {
                     if (world instanceof Level _level) {
                        if (!_level.isClientSide()) {
                           _level.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _level.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     if (!entityiterator.level().isClientSide()) {
                        entityiterator.discard();
                     }

                     if (entity instanceof LivingEntity _entity) {
                        _entity.setHealth(30.0F);
                     }
                  }
               }
            }
         }

         if (world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl27
               && _lvl27.isDay()
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

         if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            && world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.HUNTERNIBLING)
            && (Boolean)MobsabilityConfiguration.HUNT_FEN.get()) {
            if (Math.random() < 0.5
               && (
                  world.getBlockState(BlockPos.containing(x + 1.0, y, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_fences")))
                     || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_fences")))
               )) {
               if (entity instanceof ThehunterEntity) {
                  ((ThehunterEntity)entity).setAnimation("claw");
               }

               if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_fences")))) {
                  world.destroyBlock(BlockPos.containing(x, y, z + 1.0), false);
               }

               if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_fences")))) {
                  world.destroyBlock(BlockPos.containing(x + 1.0, y, z), false);
               }

               world.levelEvent(2001, BlockPos.containing(x, y, z + 1.0), Block.getId(world.getBlockState(BlockPos.containing(x, y, z + 1.0))));
            }

            if (Math.random() < 0.5
               && (
                  world.getBlockState(BlockPos.containing(x + 1.0, y, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_doors")))
                     || world.getBlockState(BlockPos.containing(x, y, z + 1.0)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_doors")))
               )) {
               if (entity instanceof ThehunterEntity) {
                  ((ThehunterEntity)entity).setAnimation("claw");
               }

               if (world.getBlockState(BlockPos.containing(x + 1.0, y, z)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_doors")))) {
                  world.destroyBlock(BlockPos.containing(x + 1.0, y, z), false);
               }

               if (world.getBlockState(BlockPos.containing(x, y, z + 1.0)).is(BlockTags.create(ResourceLocation.parse("minecraft:wooden_doors")))) {
                  world.destroyBlock(BlockPos.containing(x, y, z + 1.0), false);
               }

               world.levelEvent(2001, BlockPos.containing(x, y, z + 1.0), Block.getId(world.getBlockState(BlockPos.containing(x, y, z + 1.0))));
               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.3F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                        SoundSource.NEUTRAL,
                        0.3F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }

         if (!entity.isAlive()) {
            entity.setDeltaMovement(new Vec3(0.0, -1.0, 0.0));
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx.getVehicle() == entity) {
                  entityiteratorx.stopRiding();
               }
            }
         }

         if (entity.getPersistentData().getDouble("gaszz_sped") != 1.0 && entity.getPersistentData().getDouble("gaszz_sped") != 0.0) {
            entity.getPersistentData().putDouble("gaszz_spead", 0.0);
            entity.getPersistentData().putDouble("gaszz_sped", 0.0);
         }

         if (entity.onGround()
            && (((ThehunterEntity)entity).animationprocedure.equals("fly") || ((ThehunterEntity)entity).animationprocedure.equals("turbine"))
            && entity instanceof ThehunterEntity) {
            ((ThehunterEntity)entity).setAnimation("empty");
         }

         if ((Boolean)MobsabilityConfiguration.HUNT_SOAR.get()) {
            if (!(entity instanceof LivingEntity _livEnt83 && _livEnt83.hasEffect(MobEffects.HUNGER))
               && ((ThehunterEntity)entity).animationprocedure.equals("soar")
               && entity instanceof ThehunterEntity) {
               ((ThehunterEntity)entity).setAnimation("empty");
            }

            if (!entity.isAlive() && ((ThehunterEntity)entity).animationprocedure.equals("soar") && entity instanceof ThehunterEntity) {
               ((ThehunterEntity)entity).setAnimation("empty");
            }

            if (((ThehunterEntity)entity).animationprocedure.equals("soar") && Math.random() < 0.05 && world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _lvl91 && _lvl91.isDay() && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
               UndeadRevamp2Mod.queueServerWork(1, () -> {
                  if (entity.isAlive()) {
                     if (entity instanceof LivingEntity _entityxx && !_entityxx.level().isClientSide()) {
                        _entityxx.addEffect(new MobEffectInstance(MobEffects.HUNGER, 150, 0, false, false));
                     }

                     if (entity instanceof ThehunterEntity) {
                        ((ThehunterEntity)entity).setAnimation("soar");
                     }

                     if (entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                        _entityx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 50, false, false));
                     }
                  }
               });
               UndeadRevamp2Mod.queueServerWork(42, () -> {
                  if (entity.isAlive() && entity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                     _entityx.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 90, 15, false, false));
                  }
               });
               UndeadRevamp2Mod.queueServerWork(
                  110,
                  () -> {
                     if (entity.isAlive() && !entity.level().isClientSide()) {
                        entity.discard();
                     }

                     Vec3 _center = new Vec3(x, y, z);

                     for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
                        .stream()
                        .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                        .toList()) {
                        if (entityiteratorxx instanceof SlavemanEntity && !entityiteratorxx.level().isClientSide()) {
                           entityiteratorxx.discard();
                        }
                     }
                  }
               );
            }
         }
      }
   }
}
