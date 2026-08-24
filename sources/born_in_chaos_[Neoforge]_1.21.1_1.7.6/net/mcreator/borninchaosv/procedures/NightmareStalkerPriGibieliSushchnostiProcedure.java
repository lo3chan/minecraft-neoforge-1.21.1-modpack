package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;

public class NightmareStalkerPriGibieliSushchnostiProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof Player
            && !(
               sourceentity instanceof ServerPlayer _plr1
                  && _plr1.level() instanceof ServerLevel
                  && _plr1.getAdvancements()
                     .getOrStartProgress(_plr1.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:fighting_nightmares")))
                     .isDone()
            )
            && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:fighting_nightmares"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }

         if (Calendar.getInstance().get(5) >= 13 && Calendar.getInstance().get(5) < 20 && Calendar.getInstance().get(2) == 1) {
            for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 1, 3); index0++) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.CHOCOLATE_HEART.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            }
         }
      }
   }
}
