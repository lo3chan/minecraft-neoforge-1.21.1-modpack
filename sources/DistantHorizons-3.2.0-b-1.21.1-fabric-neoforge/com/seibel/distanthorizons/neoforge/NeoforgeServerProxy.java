package com.seibel.distanthorizons.neoforge;

import com.seibel.distanthorizons.common.AbstractModInitializer$IEventProxy_neoforge;
import com.seibel.distanthorizons.common.util.ProxyUtil_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.misc.ServerPlayerWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.core.api.internal.ServerApi;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class NeoforgeServerProxy implements AbstractModInitializer$IEventProxy_neoforge {
   private final ServerApi serverApi = ServerApi.INSTANCE;
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private final boolean isDedicated;
   public static Supplier<Boolean> isGenerationThreadChecker = null;

   private static LevelAccessor GetEventLevel(LevelEvent e) {
      return e.getLevel();
   }

   public NeoforgeServerProxy(boolean isDedicated) {
      this.isDedicated = isDedicated;
      isGenerationThreadChecker = BatchGenerationEnvironment_neoforge::isThisDhWorldGenThread;
   }

   @Override
   public void registerEvents() {
      NeoForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void dedicatedWorldLoadEvent(ServerAboutToStartEvent event) {
      this.serverApi.serverLoadEvent(this.isDedicated);
   }

   @SubscribeEvent
   public void serverWorldUnloadEvent(ServerStoppingEvent event) {
      this.serverApi.serverUnloadEvent();
   }

   @SubscribeEvent
   public void serverLevelLoadEvent(Load event) {
      if (GetEventLevel(event) instanceof ServerLevel) {
         this.serverApi.serverLevelLoadEvent(getServerLevelWrapper((ServerLevel)GetEventLevel(event)));
      }
   }

   @SubscribeEvent
   public void serverLevelUnloadEvent(Unload event) {
      if (GetEventLevel(event) instanceof ServerLevel) {
         this.serverApi.serverLevelUnloadEvent(getServerLevelWrapper((ServerLevel)GetEventLevel(event)));
      }
   }

   @SubscribeEvent
   public void serverChunkLoadEvent(net.neoforged.neoforge.event.level.ChunkEvent.Load event) {
      ILevelWrapper levelWrapper = ProxyUtil_neoforge.getLevelWrapper(GetEventLevel(event));
      IChunkWrapper chunk = new ChunkWrapper_neoforge(event.getChunk(), levelWrapper);
      this.serverApi.serverChunkLoadEvent(chunk, levelWrapper);
   }

   @SubscribeEvent
   public void playerLoggedInEvent(PlayerLoggedInEvent event) {
      this.serverApi.serverPlayerJoinEvent(getServerPlayerWrapper(event));
   }

   @SubscribeEvent
   public void playerLoggedOutEvent(PlayerLoggedOutEvent event) {
      this.serverApi.serverPlayerDisconnectEvent(getServerPlayerWrapper(event));
   }

   @SubscribeEvent
   public void playerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
      this.serverApi
         .serverPlayerLevelChangeEvent(
            getServerPlayerWrapper(event), getServerLevelWrapper(event.getFrom(), event), getServerLevelWrapper(event.getTo(), event)
         );
   }

   private static ServerLevelWrapper_neoforge getServerLevelWrapper(ServerLevel level) {
      return ServerLevelWrapper_neoforge.getWrapper(level);
   }

   private static ServerLevelWrapper_neoforge getServerLevelWrapper(ResourceKey<Level> resourceKey, PlayerEvent event) {
      return getServerLevelWrapper(event.getEntity().getServer().getLevel(resourceKey));
   }

   private static ServerPlayerWrapper_neoforge getServerPlayerWrapper(PlayerEvent event) {
      return ServerPlayerWrapper_neoforge.getWrapper((ServerPlayer)event.getEntity());
   }
}
