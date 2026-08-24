package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.entity.ThornshellCrabEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;

@EventBusSubscriber
public class ThornshellCrabPriShchielchkiePKMPoSushchnostiProcedure {
   @SubscribeEvent
   public static void onRightClickEntity(EntityInteract event) {
      if (event.getHand() == event.getEntity().getUsedItemHand()) {
         execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getTarget(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof ThornshellCrabEntity
            && !(sourceentity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK))
            && (
               (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.ROTTEN_FISH.get()
                  || (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.ROTTEN_FLESH
            )
            && !(entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(MobEffects.SATURATION))
            && !(entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.BARBEDATTACK))) {
            if (sourceentity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:waste_recycler"));
               if (_adv != null) {
                  AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                  if (!_ap.isDone()) {
                     for (String criteria : _ap.getRemainingCriteria()) {
                        _player.getAdvancements().award(_adv, criteria);
                     }
                  }
               }
            }

            Scoreboard _sc = entity.level().getScoreboard();
            Objective _so = _sc.getObjective("eat");
            if (_so == null) {
               _so = _sc.addObjective("eat", ObjectiveCriteria.DUMMY, Component.literal("eat"), RenderType.INTEGER, true, null);
            }

            _sc.getOrCreatePlayerScore(ScoreHolder.forNameOnly(entity.getScoreboardName()), _so).set(1);
            if (sourceentity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.MAIN_HAND, true);
            }

            (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
            if (sourceentity instanceof Player _playerx) {
               _playerx.getCooldowns().addCooldown((Item)BornInChaosV1ModItems.ROTTEN_FISH.get(), 15);
            }

            if (sourceentity instanceof Player _playerx) {
               _playerx.getCooldowns().addCooldown(Items.ROTTEN_FLESH, 15);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                     SoundSource.NEUTRAL,
                     0.1F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.turtle.egg_crack")),
                     SoundSource.NEUTRAL,
                     0.1F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), entity.getX(), entity.getY() + 0.5, entity.getZ(), 3, 0.2, 0.2, 0.2, 0.1
               );
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 15, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15, 5, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15, 5, false, false));
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BARBEDATTACK, 15, 0, false, false));
            }

            if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BARBEDATTACK, 15, 0, false, false));
            }
         }
      }
   }
}
