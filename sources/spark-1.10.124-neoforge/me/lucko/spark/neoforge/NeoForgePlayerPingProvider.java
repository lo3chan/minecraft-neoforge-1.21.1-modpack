package me.lucko.spark.neoforge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class NeoForgePlayerPingProvider implements PlayerPingProvider {
   private final MinecraftServer server;

   public NeoForgePlayerPingProvider(MinecraftServer server) {
      this.server = server;
   }

   @Override
   public Map<String, Integer> poll() {
      Builder<String, Integer> builder = ImmutableMap.builder();

      for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
         builder.put(player.getGameProfile().getName(), player.connection.latency());
      }

      return builder.build();
   }
}
