package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;

public class HunthuntinglolProcedure {
   public static boolean execute() {
      return (Boolean)MobsabilityConfiguration.HUNT_ANI.get();
   }
}
