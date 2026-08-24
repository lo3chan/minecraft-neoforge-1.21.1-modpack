package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PickupExperienceCallback {
   EventInvoker<PickupExperienceCallback> EVENT = EventInvoker.lookup(PickupExperienceCallback.class);

   EventResult onPickupExperience(Player var1, ExperienceOrb var2);
}
