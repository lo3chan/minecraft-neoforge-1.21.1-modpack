package net.astralya.hexalia.gameplay.censer;

import java.util.HashMap;
import java.util.Map;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;

public final class CenserEffectRegistry {
   private static Map<HerbCombination, CenserEffectRegistry.Entry> entries;

   private CenserEffectRegistry() {
   }

   public static boolean isValid(HerbCombination combination) {
      return registry().containsKey(combination);
   }

   public static String getMessageKey(HerbCombination combination) {
      CenserEffectRegistry.Entry entry = registry().get(combination);
      return entry == null ? "message.hexalia.invalid_herb_combination" : entry.messageKey();
   }

   public static void apply(ServerLevel level, BlockPos pos, HerbCombination combination) {
      CenserEffectRegistry.Entry entry = registry().get(combination);
      if (entry != null) {
         entry.effect().apply(level, pos);
      }
   }

   private static Map<HerbCombination, CenserEffectRegistry.Entry> registry() {
      if (entries == null) {
         entries = new HashMap<>();
         register((Item)ModItems.SIREN_KELP.get(), (Item)ModItems.SPIRIT_BLOOM.get(), "message.hexalia.censer.tidewarden", CenserEffectHandler::applyTidewarden);
         register(
            (Item)ModItems.GHOST_FERN.get(),
            (Item)ModItems.SIREN_KELP.get(),
            "message.hexalia.censer.ethereal_grazing",
            CenserEffectHandler::applyEtherealGrazing
         );
         register(
            (Item)ModItems.DREAMSHROOM.get(), (Item)ModItems.SIREN_KELP.get(), "message.hexalia.censer.tides_memory", CenserEffectHandler::applyTidesMemory
         );
         register(
            (Item)ModItems.DREAMSHROOM.get(),
            (Item)ModItems.SPIRIT_BLOOM.get(),
            "message.hexalia.censer.miners_respite",
            CenserEffectHandler::applyMinersRespite
         );
         register(
            (Item)ModItems.DREAMSHROOM.get(), (Item)ModItems.GHOST_FERN.get(), "message.hexalia.censer.phantom_drift", CenserEffectHandler::applyPhantomDrift
         );
         register(
            (Item)ModItems.GHOST_FERN.get(), (Item)ModItems.SPIRIT_BLOOM.get(), "message.hexalia.censer.undead_veil", CenserEffectHandler::applyUndeadVeil
         );
         register(
            (Item)ModItems.WITCHWEED.get(), (Item)ModItems.GHOST_FERN.get(), "message.hexalia.censer.withering_calm", CenserEffectHandler::applyWitheringCalm
         );
         register((Item)ModItems.WITCHWEED.get(), (Item)ModItems.SPIRIT_BLOOM.get(), "message.hexalia.censer.hollow_aura", CenserEffectHandler::applyHollowAura);
         register(
            (Item)ModItems.WITCHWEED.get(), (Item)ModItems.DREAMSHROOM.get(), "message.hexalia.censer.blighted_bloom", CenserEffectHandler::applyBlightedBloom
         );
         register((Item)ModItems.WITCHWEED.get(), (Item)ModItems.SIREN_KELP.get(), "message.hexalia.censer.tidal_pull", CenserEffectHandler::applyTidalPull);
      }

      return entries;
   }

   private static void register(Item first, Item second, String messageKey, CenserEffectRegistry.Effect effect) {
      entries.put(new HerbCombination(first, second), new CenserEffectRegistry.Entry(messageKey, effect));
   }

   @FunctionalInterface
   public interface Effect {
      void apply(ServerLevel var1, BlockPos var2);
   }

   public record Entry(String messageKey, CenserEffectRegistry.Effect effect) {
   }
}
