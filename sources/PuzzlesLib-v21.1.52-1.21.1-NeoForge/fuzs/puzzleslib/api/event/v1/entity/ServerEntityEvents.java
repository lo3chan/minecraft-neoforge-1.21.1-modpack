package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public final class ServerEntityEvents {
   public static final EventInvoker<ServerEntityEvents.Join> JOIN = EventInvoker.lookup(ServerEntityEvents.Join.class);
   public static final EventInvoker<ServerEntityEvents.Load> LOAD = EventInvoker.lookup(ServerEntityEvents.Load.class);
   public static final EventInvoker<ServerEntityEvents.Unload> UNLOAD = EventInvoker.lookup(ServerEntityEvents.Unload.class);

   private ServerEntityEvents() {
   }

   @FunctionalInterface
   public interface Join {
      EventResult onEntityJoin(Entity var1, ServerLevel var2, boolean var3, @Nullable MobSpawnType var4);
   }

   @FunctionalInterface
   public interface Load {
      void onEntityLoad(Entity var1, ServerLevel var2, boolean var3, @Nullable MobSpawnType var4);
   }

   @FunctionalInterface
   public interface Unload {
      void onEntityUnload(Entity var1, ServerLevel var2);
   }
}
