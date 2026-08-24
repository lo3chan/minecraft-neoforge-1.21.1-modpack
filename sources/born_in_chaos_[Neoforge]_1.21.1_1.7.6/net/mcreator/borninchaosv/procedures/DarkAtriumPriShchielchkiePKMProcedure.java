package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class DarkAtriumPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.DARK_WARD))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.DARK_WARD, 72000, 0));
            }

            itemstack.shrink(1);
            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown(itemstack.getItem(), 100);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:charm_of_protection_use")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     0.7F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:charm_of_protection_use")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     0.7F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.DARKMATTER.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.2
               );
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }
            } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()
               && entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.OFF_HAND, true);
            }
         }
      }
   }
}
