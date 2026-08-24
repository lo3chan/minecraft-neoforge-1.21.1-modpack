package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl;

import java.util.HashMap;
import java.util.Map;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks.FavoriteBlockQuirk;

public class QuirkRegistry {
   private static final Map<String, Class<? extends Quirk>> QUIRKS = new HashMap<>();

   public static Quirk getQuirkByName(String name) {
      Class<? extends Quirk> quirkClass = QUIRKS.get(name);
      if (quirkClass != null) {
         try {
            return quirkClass.getDeclaredConstructor().newInstance();
         } catch (ReflectiveOperationException var3) {
            var3.printStackTrace();
         }
      }

      return null;
   }

   static {
      QUIRKS.put("FavoriteBlockQuirk", FavoriteBlockQuirk.class);
   }
}
