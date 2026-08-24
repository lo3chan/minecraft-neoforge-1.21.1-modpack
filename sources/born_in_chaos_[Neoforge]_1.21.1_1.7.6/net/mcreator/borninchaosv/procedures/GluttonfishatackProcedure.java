package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.GluttonFishEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class GluttonfishatackProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingIncomingDamageEvent event) {
      if (event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity(),
            event.getSource().getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof GluttonFishEntity
            && (entity instanceof Mob || entity instanceof Monster || entity instanceof WaterAnimal || entity instanceof Player)) {
            if (entity instanceof LivingEntity _livEnt5 && _livEnt5.isBlocking()) {
               if (entity instanceof LivingEntity _livEnt8 && _livEnt8.isBlocking() && entity instanceof Player) {
                  if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STUN, 60, 0, false, false));
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(), x, y, z, 9, 1.0, 1.0, 1.0, 0.1);
                  }

                  if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.SHIELD) {
                     if (world instanceof ServerLevel _level) {
                        (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).hurtAndBreak(20, _level, null, _stkprov -> {});
                     }
                  } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.SHIELD
                     && world instanceof ServerLevel _level) {
                     (entity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).hurtAndBreak(20, _level, null, _stkprov -> {});
                  }
               }
            } else {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_FRACTURE, 100, 0));
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:glutton_fish_attack")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:glutton_fish_attack")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }
      }
   }
}
