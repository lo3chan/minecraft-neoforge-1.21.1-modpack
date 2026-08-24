package dev.corgitaco.enhancedcelestials2core.mixin;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.ai.village.VillageSiege.State;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VillageSiege.class})
public class VillageSiegeMixin {
   @Shadow
   private State siegeState;

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/world/entity/ai/village/VillageSiege;siegeState:Lnet/minecraft/world/entity/ai/village/VillageSiege$State;",
         ordinal = 1,
         opcode = 181
      )}
   )
   public void tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies, CallbackInfoReturnable<Integer> cir) {
      Optional<LunarForecast> lunarForecast = EnhancedCelestials.lunarForecastWorldData(level);
      if (lunarForecast.isPresent()) {
         Optional<Double> siegeProbability = lunarForecast.get().currentLunarEvent().siegeProbability();
         siegeProbability.ifPresent(probability -> this.siegeState = probability > level.random.nextDouble() ? State.SIEGE_TONIGHT : State.SIEGE_DONE);
      }
   }
}
