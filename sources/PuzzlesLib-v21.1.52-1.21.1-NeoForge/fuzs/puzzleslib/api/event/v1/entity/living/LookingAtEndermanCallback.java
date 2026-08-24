package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface LookingAtEndermanCallback {
   EventInvoker<LookingAtEndermanCallback> EVENT = EventInvoker.lookup(LookingAtEndermanCallback.class);

   EventResult onLookingAtEnderManCallback(EnderMan var1, Player var2);
}
