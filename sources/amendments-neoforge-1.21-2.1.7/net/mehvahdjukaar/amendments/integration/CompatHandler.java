package net.mehvahdjukaar.amendments.integration;

import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

public class CompatHandler {
   public static final boolean FARMERS_DELIGHT;
   public static final boolean SUPPLEMENTARIES = PlatHelper.isModLoaded("supplementaries");
   public static final boolean SUPPSQUARED = PlatHelper.isModLoaded("suppsquared");
   public static final boolean QUARK = PlatHelper.isModLoaded("quark");
   public static final boolean TORCHSLAB = PlatHelper.isModLoaded("torch_slab");
   public static final boolean BUZZIER_BEES = PlatHelper.isModLoaded("buzzier_bees");
   public static final boolean SHIMMER = PlatHelper.isModLoaded("shimmer");
   public static final boolean SOUL_FIRED;
   public static final boolean CAVE_ENHANCEMENTS;
   public static final boolean FLAN;
   public static final boolean BLUEPRINT;
   public static final boolean CONFIGURED;
   public static final boolean ALEX_CAVES;
   public static final boolean RATS;
   public static final boolean THIN_AIR;
   public static final boolean CAVERNS_AND_CHASMS;
   public static final boolean FLYWHEEL;

   static {
      if (PlatHelper.isModLoaded("soul_fire_d")) {
      }

      SOUL_FIRED = false;
      CAVE_ENHANCEMENTS = PlatHelper.isModLoaded("cave_enhancements");
      FLAN = PlatHelper.isModLoaded("flan");
      BLUEPRINT = PlatHelper.isModLoaded("blueprint");
      CONFIGURED = PlatHelper.isModLoaded("configured");
      ALEX_CAVES = PlatHelper.isModLoaded("alexscaves");
      RATS = PlatHelper.isModLoaded("rats");
      THIN_AIR = PlatHelper.isModLoaded("thinair");
      CAVERNS_AND_CHASMS = PlatHelper.isModLoaded("caverns_and_chasms");
      if (PlatHelper.isModLoaded("flywheel")) {
      }

      FLYWHEEL = false;
      boolean fd = false;
      if (PlatHelper.isModLoaded("farmersdelight")) {
         try {
            Class.forName("vectorwing.farmersdelight.FarmersDelight");
            fd = true;
         } catch (Exception var2) {
            Amendments.LOGGER.error("Farmers Delight Refabricated is not installed. Disabling Farmers Delight Module");
         }
      }

      FARMERS_DELIGHT = fd;
   }
}
