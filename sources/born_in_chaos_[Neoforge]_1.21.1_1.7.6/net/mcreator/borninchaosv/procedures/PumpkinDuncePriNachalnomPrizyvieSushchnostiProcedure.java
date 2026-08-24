package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.PumpkinDunceEntity;
import net.minecraft.world.entity.Entity;

public class PumpkinDuncePriNachalnomPrizyvieSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.35) {
            if (entity instanceof PumpkinDunceEntity animatable) {
               animatable.setTexture("pumpkindunce_ww");
            }
         } else if (Math.random() < 0.35) {
            if (entity instanceof PumpkinDunceEntity animatable) {
               animatable.setTexture("pumpkindunce_hh");
            }
         } else if (entity instanceof PumpkinDunceEntity animatable) {
            animatable.setTexture("pumpkindunce");
         }
      }
   }
}
