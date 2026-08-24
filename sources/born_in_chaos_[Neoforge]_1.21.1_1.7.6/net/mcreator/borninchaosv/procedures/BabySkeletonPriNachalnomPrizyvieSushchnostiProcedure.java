package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.entity.BabySkeletonEntity;
import net.minecraft.world.entity.Entity;

public class BabySkeletonPriNachalnomPrizyvieSushchnostiProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.4 && entity instanceof BabySkeletonEntity animatable) {
            animatable.setTexture("baby_skeleton_alternative");
         }
      }
   }
}
