package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface AfterChangeDimensionCallback {
   EventInvoker<AfterChangeDimensionCallback> EVENT = EventInvoker.lookup(AfterChangeDimensionCallback.class);

   void onAfterChangeDimension(ServerPlayer var1, ServerLevel var2, ServerLevel var3);
}
