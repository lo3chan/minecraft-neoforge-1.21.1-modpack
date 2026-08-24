package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class ServerEntityLevelEvents {
   public static final EventInvoker<ServerEntityLevelEvents.Load> LOAD = EventInvoker.lookup(ServerEntityLevelEvents.Load.class);
   public static final EventInvoker<ServerEntityLevelEvents.Spawn> SPAWN = EventInvoker.lookup(ServerEntityLevelEvents.Spawn.class);
   public static final EventInvoker<ServerEntityLevelEvents.Unload> UNLOAD = EventInvoker.lookup(ServerEntityLevelEvents.Unload.class);

   private ServerEntityLevelEvents() {
   }

   @FunctionalInterface
   public interface Load {
      EventResult onEntityLoad(Entity var1, ServerLevel var2);
   }

   @FunctionalInterface
   public interface Spawn {
      EventResult onEntitySpawn(Entity var1, ServerLevel var2, @Nullable MobSpawnType var3);
   }

   @FunctionalInterface
   public interface Unload {
      void onEntityUnload(Entity var1, ServerLevel var2);
   }
}
