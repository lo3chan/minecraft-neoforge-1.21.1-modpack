package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;

public class InducerAdditionalGenerationConditionProcedure {
   public static boolean execute() {
      return (Boolean)MobsabilityConfiguration.INDUCER.get();
   }
}
