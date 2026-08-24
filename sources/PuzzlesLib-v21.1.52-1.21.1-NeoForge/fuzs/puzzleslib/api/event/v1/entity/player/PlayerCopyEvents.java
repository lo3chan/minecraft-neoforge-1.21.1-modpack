package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerCopyEvents {
   public static final EventInvoker<PlayerCopyEvents.Copy> COPY = EventInvoker.lookup(PlayerCopyEvents.Copy.class);
   public static final EventInvoker<PlayerCopyEvents.Respawn> RESPAWN = EventInvoker.lookup(PlayerCopyEvents.Respawn.class);

   private PlayerCopyEvents() {
   }

   @FunctionalInterface
   public interface Copy {
      void onCopy(ServerPlayer var1, ServerPlayer var2, boolean var3);
   }

   @FunctionalInterface
   public interface Respawn {
      void onRespawn(ServerPlayer var1, boolean var2);
   }
}
