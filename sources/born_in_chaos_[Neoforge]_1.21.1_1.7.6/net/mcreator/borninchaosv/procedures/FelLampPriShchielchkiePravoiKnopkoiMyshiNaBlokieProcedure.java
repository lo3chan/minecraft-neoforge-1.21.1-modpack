package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class FelLampPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.FEL_LAMP.get()
            && !(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))
            && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x, y + 2.0, z)).canOcclude()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (entity instanceof LivingEntity _entity) {
               ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.EMPTY_FEL_LAMP.get()).copy();
               _setstack.setCount(1);
               _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
               if (_entity instanceof Player _player) {
                  _player.getInventory().setChanged();
               }
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.RIDING_FELSTEED.get())
                  .spawn(_level, BlockPos.containing(x + 0.5, y + 1.0, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.SMOKE, x + 0.5, y + 1.0, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.ANIM_FIRE.get(), x + 0.5, y + 1.0, z + 0.5, 12, 0.3, 0.3, 0.3, 0.1);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y + 1.0, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton_horse.ambient")),
                     SoundSource.NEUTRAL,
                     1.3F,
                     0.8F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y + 1.0,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton_horse.ambient")),
                     SoundSource.NEUTRAL,
                     1.3F,
                     0.8F,
                     false
                  );
               }
            }

            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.EMPTY_FEL_LAMP.get(), 30);
            }
         }
      }
   }
}
