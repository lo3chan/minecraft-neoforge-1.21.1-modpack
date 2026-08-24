package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.PumpkinBombEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PumpkinBombPriObnovlieniiTikaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(MobEffects.DAMAGE_RESISTANCE)
            && entity instanceof LivingEntity _livEnt1
            && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), x, y + 1.0, z, 1, 0.2, 0.2, 0.2, 0.1);
            }

            if (entity instanceof PumpkinBombEntity) {
               ((PumpkinBombEntity)entity).setAnimation("boom");
            }

            if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.BLOCK_BREAK).getDuration()
                     : 0
               )
               <= 7) {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }

               if (world instanceof Level _level && !_level.isClientSide()) {
                  _level.explode(null, x, y, z, 3.0F, ExplosionInteraction.NONE);
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_EXPLOSION.get(), x, y + 1.0, z, 3, 0.6, 0.6, 0.6, 0.1);
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y, z, 16, 2.0, 0.3, 2.0, 0.1);
               }

               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.5), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (!(entityiterator instanceof LivingEntity _livEnt9 && _livEnt9.isBlocking())
                     && entityiterator instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 240, 0));
                  }
               }

               if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).canOcclude()
                  && world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() != Blocks.BEDROCK) {
                  BlockPos _bp = BlockPos.containing(x, y - 1.0, z);
                  BlockState _bs = ((Block)BornInChaosV1ModBlocks.FEL_SOIL.get()).defaultBlockState();
                  BlockState _bso = world.getBlockState(_bp);

                  for (Property<?> _propertyOld : _bso.getProperties()) {
                     Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
                     if (_propertyNew != null && _bs.getValue(_propertyNew) != null) {
                        try {
                           _bs = (BlockState)_bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
                        } catch (Exception var18) {
                        }
                     }
                  }

                  world.setBlock(_bp, _bs, 3);
               }
            }
         }
      }
   }
}
