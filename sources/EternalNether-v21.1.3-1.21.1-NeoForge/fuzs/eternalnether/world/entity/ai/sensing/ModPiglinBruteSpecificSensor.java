package fuzs.eternalnether.world.entity.ai.sensing;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import fuzs.eternalnether.world.entity.monster.piglin.ModPiglinBruteAi;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.PiglinBruteSpecificSensor;
import net.minecraft.world.entity.player.Player;

public class ModPiglinBruteSpecificSensor extends PiglinBruteSpecificSensor {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.builder().addAll(super.requires()).add(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).build();
   }

   protected void doTick(ServerLevel level, LivingEntity entity) {
      super.doTick(level, entity);
      Brain<?> brain = entity.getBrain();
      NearestVisibleLivingEntities nearestVisibleLivingEntities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
         .orElse(NearestVisibleLivingEntities.empty());
      Optional<Player> optional = Optional.empty();

      for (LivingEntity livingEntity : nearestVisibleLivingEntities.findAll(Predicates.alwaysTrue())) {
         if (livingEntity instanceof Player player && optional.isEmpty() && !ModPiglinBruteAi.isWearingGold(player) && entity.canAttack(livingEntity)) {
            optional = Optional.of(player);
         }
      }

      brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional);
   }
}
