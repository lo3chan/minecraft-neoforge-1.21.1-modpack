package com.alonie.brbe.brewingstand;

import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;

public class PlatformPotionUtil {
   private static PlatformPotionUtil.PotionUtilProvider provider;

   public static void setProvider(PlatformPotionUtil.PotionUtilProvider p) {
      provider = p;
   }

   public static Ingredient getIngredient(Object recipe) {
      if (provider == null) {
         throw new IllegalStateException("PlatformPotionUtil provider not set");
      } else {
         return provider.getIngredient(recipe);
      }
   }

   public static Potion getTo(Object recipe) {
      if (provider == null) {
         throw new IllegalStateException("PlatformPotionUtil provider not set");
      } else {
         return provider.getTo(recipe);
      }
   }

   public static Potion getFrom(Object recipe) {
      if (provider == null) {
         throw new IllegalStateException("PlatformPotionUtil provider not set");
      } else {
         return provider.getFrom(recipe);
      }
   }

   public static List<?> getPotionMixes(ClientLevel level) {
      if (provider == null) {
         throw new IllegalStateException("PlatformPotionUtil provider not set");
      } else {
         return provider.getPotionMixes(level);
      }
   }

   public interface PotionUtilProvider {
      Ingredient getIngredient(Object var1);

      Potion getTo(Object var1);

      Potion getFrom(Object var1);

      List<?> getPotionMixes(ClientLevel var1);
   }
}
