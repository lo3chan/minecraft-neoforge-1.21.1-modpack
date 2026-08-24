package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThebidyEntityDiesProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity.isPassenger() && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:bullseyes"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         UndeadRevamp2Mod.queueServerWork(
            35,
            () -> {
               Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator != entity && entityiterator instanceof LivingEntity) {
                     entityiterator.getPersistentData().putDouble("aoe_x", entity.getX() - entityiterator.getX());
                     entityiterator.getPersistentData()
                        .putDouble("aoe_y", entity.getY() + entity.getBbHeight() - (entityiterator.getY() + entityiterator.getBbHeight()));
                     entityiterator.getPersistentData().putDouble("aoe_z", entity.getZ() - entityiterator.getZ());
                     entityiterator.getPersistentData().putDouble("distance", 0.0);
                     UndeadRevamp2Mod.queueServerWork(
                        1,
                        () -> {
                           for (int index0 = 0; index0 < 20; index0++) {
                              if (world.isEmptyBlock(
                                 BlockPos.containing(
                                    entity.getX()
                                       + entityiterator.getPersistentData().getDouble("aoe_x") * entityiterator.getPersistentData().getDouble("distance"),
                                    entity.getY()
                                       + entity.getBbHeight()
                                       + entityiterator.getPersistentData().getDouble("aoe_y") * entityiterator.getPersistentData().getDouble("distance"),
                                    entity.getZ()
                                       + entityiterator.getPersistentData().getDouble("aoe_z") * entityiterator.getPersistentData().getDouble("distance")
                                 )
                              )) {
                                 entityiterator.getPersistentData().putBoolean("behind_wall", false);
                                 entityiterator.getPersistentData().putDouble("distance", entityiterator.getPersistentData().getDouble("distance") - 0.05);
                              } else {
                                 entityiterator.getPersistentData().putBoolean("behind_wall", true);
                              }

                              UndeadRevamp2Mod.queueServerWork(1, () -> {
                                 if (!entityiterator.getPersistentData().getBoolean("behind_wall") && entity.getVehicle() == entityiterator) {
                                    if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                                       _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 80, 0));
                                    }

                                    entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.PLAYER_EXPLOSION), entity, sourceentity), 4.0F);
                                 }
                              });
                           }
                        }
                     );
                  }
               }
            }
         );
      }
   }
}
