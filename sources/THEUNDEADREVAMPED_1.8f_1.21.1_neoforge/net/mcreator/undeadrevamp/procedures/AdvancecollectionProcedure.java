package net.mcreator.undeadrevamp.procedures;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class AdvancecollectionProcedure {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      execute(event, event.getEntity());
   }

   public static void execute(Entity entity) {
      execute(null, entity);
   }

   private static void execute(@Nullable Event event, Entity entity) {
      if (entity != null) {
         if ((
               entity instanceof ServerPlayer _plr0
                     && _plr0.level() instanceof ServerLevel
                     && _plr0.getAdvancements()
                        .getOrStartProgress(_plr0.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:mygothgf")))
                        .isDone()
                  || entity instanceof ServerPlayer _plr1
                     && _plr1.level() instanceof ServerLevel
                     && _plr1.getAdvancements().getOrStartProgress(_plr1.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:mommy"))).isDone()
                  || entity instanceof ServerPlayer _plr2
                     && _plr2.level() instanceof ServerLevel
                     && _plr2.getAdvancements().getOrStartProgress(_plr2.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:daddy"))).isDone()
                  || entity instanceof ServerPlayer _plr3
                     && _plr3.level() instanceof ServerLevel
                     && _plr3.getAdvancements()
                        .getOrStartProgress(_plr3.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:thineded")))
                        .isDone()
                  || entity instanceof ServerPlayer _plr4
                     && _plr4.level() instanceof ServerLevel
                     && _plr4.getAdvancements()
                        .getOrStartProgress(_plr4.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:cloggeradvance")))
                        .isDone()
                  || entity instanceof ServerPlayer _plr5
                     && _plr5.level() instanceof ServerLevel
                     && _plr5.getAdvancements()
                        .getOrStartProgress(_plr5.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:nicehousebro")))
                        .isDone()
            )
            && entity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:defeatundeadbosses"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         if (entity instanceof ServerPlayer _plr7
            && _plr7.level() instanceof ServerLevel
            && _plr7.getAdvancements().getOrStartProgress(_plr7.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:mygothgf"))).isDone()
            && entity instanceof ServerPlayer _plr8
            && _plr8.level() instanceof ServerLevel
            && _plr8.getAdvancements().getOrStartProgress(_plr8.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:mommy"))).isDone()
            && entity instanceof ServerPlayer _plr9
            && _plr9.level() instanceof ServerLevel
            && _plr9.getAdvancements().getOrStartProgress(_plr9.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:daddy"))).isDone()
            && entity instanceof ServerPlayer _plr10
            && _plr10.level() instanceof ServerLevel
            && _plr10.getAdvancements().getOrStartProgress(_plr10.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:thineded"))).isDone()
            && entity instanceof ServerPlayer _plr11
            && _plr11.level() instanceof ServerLevel
            && _plr11.getAdvancements()
               .getOrStartProgress(_plr11.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:cloggeradvance")))
               .isDone()
            && entity instanceof ServerPlayer _plr12
            && _plr12.level() instanceof ServerLevel
            && _plr12.getAdvancements().getOrStartProgress(_plr12.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:nicehousebro"))).isDone()
            && entity instanceof ServerPlayer _playerx) {
            AdvancementHolder _adv = _playerx.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:allbosseskilled"));
            if (_adv != null) {
               AdvancementProgress _ap = _playerx.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _playerx.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
