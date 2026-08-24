package dev.corgitaco.enhancedcelestials2core.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.Optional;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Player.class})
public abstract class PlayerMixin extends LivingEntity {
   protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @WrapMethod(
      method = {"giveExperiencePoints"}
   )
   private void modifyXPPoints(int xpPoints, Operation<Void> original) {
      Optional<LunarForecast> enhancedCelestialsLunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(this.level());
      if (enhancedCelestialsLunarForecastWorldData.isEmpty()) {
         original.call(new Object[]{xpPoints});
      } else {
         LunarForecast data = enhancedCelestialsLunarForecastWorldData.orElseThrow();
         double xp = data.currentLunarEvent().xpAmplifier();
         original.call(new Object[]{(int)(xp * xpPoints)});
      }
   }
}
