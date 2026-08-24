package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SweetSwordPriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.getFoodData().setFoodLevel((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) + 3);
         }

         if (entity instanceof Player _player) {
            _player.getFoodData().setSaturation((entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0.0F) + 2.0F);
         }

         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(itemstack.getItem(), 200);
         }

         if (world instanceof ServerLevel _level) {
            itemstack.hurtAndBreak(6, _level, null, _stkprov -> {});
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.CHIT.get(), entity.getX(), entity.getY() + 1.7, entity.getZ(), 10, 0.2, 0.2, 0.2, 0.1
            );
         }

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
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.eat")), SoundSource.NEUTRAL, 1.0F, 1.0F, false
               );
            }
         }

         if (itemstack.getItem() == (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }
         } else if (itemstack.getItem() == (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem()
            && entity instanceof LivingEntity _entity) {
            _entity.swing(InteractionHand.OFF_HAND, true);
         }
      }
   }
}
