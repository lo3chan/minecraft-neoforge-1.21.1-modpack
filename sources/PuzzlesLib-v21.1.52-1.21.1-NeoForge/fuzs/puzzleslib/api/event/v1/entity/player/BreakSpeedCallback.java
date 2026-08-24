package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BreakSpeedCallback {
   EventInvoker<BreakSpeedCallback> EVENT = EventInvoker.lookup(BreakSpeedCallback.class);

   EventResult onBreakSpeed(Player var1, BlockState var2, DefaultedFloat var3);
}
