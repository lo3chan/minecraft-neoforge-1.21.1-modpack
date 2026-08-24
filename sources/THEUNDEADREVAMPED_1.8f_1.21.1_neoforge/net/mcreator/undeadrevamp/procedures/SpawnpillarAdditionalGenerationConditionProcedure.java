package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;

public class SpawnpillarAdditionalGenerationConditionProcedure {
   public static boolean execute() {
      return (Boolean)MobsabilityConfiguration.DUNZHONG.get();
   }
}
