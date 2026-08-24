package tallestegg.guardvillagers.common.entities.ai.tasks;

import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class VillagerHelp extends Behavior<Villager> {
   private final List<? extends String> allowedProfessions;

   public VillagerHelp(Map<MemoryModuleType<?>, MemoryStatus> entryCondition, List<? extends String> allowedProfessions) {
      super(entryCondition);
      this.allowedProfessions = allowedProfessions;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, Villager owner) {
      Activity activity = (Activity)owner.getBrain().getActiveNonCoreActivity().orElse(null);
      return !this.checkIfDayHavePassedFromLastActivity(owner)
         ? false
         : this.allowedProfessions.contains(owner.getVillagerData().getProfession().name())
            && !owner.isSleeping()
            && activity != Activity.AVOID
            && activity != Activity.HIDE
            && activity != Activity.PANIC;
   }

   protected boolean checkIfDayHavePassedFromLastActivity(LivingEntity owner) {
      long gameTime = owner.level().getDayTime();
      if (this.timeToCheck(owner) > 0L && gameTime - this.timeToCheck(owner) < 24000L) {
         return false;
      } else {
         return this.timeToCheck(owner) <= 0L ? true : true;
      }
   }

   protected long timeToCheck(LivingEntity owner) {
      return 0L;
   }
}
