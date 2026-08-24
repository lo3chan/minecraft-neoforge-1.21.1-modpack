package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SquireoftheDarkLordPProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (!(
               entity instanceof ServerPlayer _plr0
                  && _plr0.level() instanceof ServerLevel
                  && _plr0.getAdvancements()
                     .getOrStartProgress(_plr0.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:squireofthe_dark_lord")))
                     .isDone()
            )
            && entity instanceof Player _playerHasItemxxxxxx
            && _playerHasItemxxxxxx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.NIGHTMARE_SCYTHE.get()))
            && entity instanceof Player _playerHasItemxxxxx
            && _playerHasItemxxxxx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.GREAT_REAPER_AXE.get()))
            && entity instanceof Player _playerHasItemxxxx
            && _playerHasItemxxxx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.SKULLBREAKER_HAMMER.get()))
            && entity instanceof Player _playerHasItemxxx
            && _playerHasItemxxx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.SHARPENED_DARK_METAL_SWORD.get()))
            && entity instanceof Player _playerHasItemxx
            && _playerHasItemxx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.INTOXICATING_DAGGER.get()))
            && entity instanceof Player _playerHasItemx
            && _playerHasItemx.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.SOUL_CUTLASS.get()))
            && entity instanceof Player _playerHasItem
            && _playerHasItem.getInventory().contains(new ItemStack((ItemLike)BornInChaosV1ModItems.DARKWARBLADE.get()))
            && entity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:squireofthe_dark_lord"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
