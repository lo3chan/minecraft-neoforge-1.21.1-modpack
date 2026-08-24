package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PumpkinStaffSirHitProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(entity.getX(), entity.getY() + 1.0, entity.getZ());

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entityiterator instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  != BornInChaosV1ModItems.LORD_PUMPKINHEADS_HAT_HELMET.get()
               && (entityiterator instanceof Mob || entityiterator instanceof Monster || entityiterator instanceof Animal || entityiterator instanceof Player)) {
               entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.EXPLOSION)), 3.0F);
            }
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(entity.getX(), entity.getY() + 1.0, entity.getZ()),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkin_hit")),
                  SoundSource.NEUTRAL,
                  1.6F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:pumpkin_hit")),
                  SoundSource.NEUTRAL,
                  1.6F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_EXPLOSION.get(),
               entity.getX(),
               entity.getY() + 1.0,
               entity.getZ(),
               4,
               0.8,
               0.8,
               0.8,
               0.1
            );
         }
      }
   }
}
