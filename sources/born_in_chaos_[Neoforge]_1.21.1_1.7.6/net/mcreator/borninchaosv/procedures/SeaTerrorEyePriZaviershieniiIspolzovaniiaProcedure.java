package net.mcreator.borninchaosv.procedures;

import net.minecraft.world.entity.Entity;

public class SeaTerrorEyePriZaviershieniiIspolzovaniiaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setAirSupply(300);
      }
   }
}
