package com.alonie.brbe.loaders;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.brewingstand.BrewableResult;
import com.alonie.brbe.brewingstand.PlatformPotionUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;

public class PotionLoader {
   public static List<BrewableResult> POTIONS = new ArrayList<>();

   public static void init() {
   }

   public static void load(ClientLevel level) {
      clearNoLog();

      for (Object potionRecipe : PlatformPotionUtil.getPotionMixes(level)) {
         POTIONS.add(new BrewableResult(potionRecipe));
      }

      BetterRecipeBook.LOGGER.info("Loaded %d potions.".formatted(POTIONS.size()));
   }

   public static void clear() {
      BetterRecipeBook.LOGGER.info("Clearing potions...");
      clearNoLog();
   }

   private static void clearNoLog() {
      POTIONS.clear();
   }
}
