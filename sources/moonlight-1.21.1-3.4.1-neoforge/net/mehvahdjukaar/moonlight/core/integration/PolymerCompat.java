package net.mehvahdjukaar.moonlight.core.integration;

public class PolymerCompat {
   private static final Class<?> POLYMER_SYNCED_OBJ;

   public static boolean isPolymerObj(Object obj) {
      return POLYMER_SYNCED_OBJ.isInstance(obj);
   }

   static {
      try {
         POLYMER_SYNCED_OBJ = Class.forName("eu.pb4.polymer.core.api.utils.PolymerSyncedObject");
      } catch (ClassNotFoundException var1) {
         throw new RuntimeException(var1);
      }
   }
}
