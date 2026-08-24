package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RidingFelsteedPriNazhatiiPravoiKnopkiNaSushchnostProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.ETHEREAL_SPIRIT.get()
            && !(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(MobEffects.MOVEMENT_SPEED))) {
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (sourceentity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.ETHEREAL_SPIRIT.get(), 20);
            }

            if (sourceentity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.horse.eat")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     0.8F
                  );
               } else {
                  _level.playLocalSound(
                     x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.horse.eat")), SoundSource.NEUTRAL, 0.6F, 0.8F, false
                  );
               }
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 2, false, false));
            }
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.EMPTY_FEL_LAMP.get()
            && sourceentity.isShiftKeyDown()) {
            if (sourceentity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.lantern.place")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.8F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.lantern.place")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.8F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton_horse.ambient")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     0.6F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.skeleton_horse.ambient")),
                     SoundSource.NEUTRAL,
                     0.6F,
                     0.6F,
                     false
                  );
               }
            }

            if (sourceentity instanceof LivingEntity _entity) {
               ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.FEL_LAMP.get()).copy();
               _setstack.setCount(1);
               _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
               if (_entity instanceof Player _player) {
                  _player.getInventory().setChanged();
               }
            }

            if (world instanceof ServerLevel _levelxxx) {
               _levelxxx.sendParticles(ParticleTypes.SMOKE, x + 0.5, y, z + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
            }

            if (sourceentity instanceof Player _player) {
               _player.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.FEL_LAMP.get(), 30);
            }

            if (sourceentity instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
               _entityx.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 30, 0));
            }
         }
      }
   }
}
