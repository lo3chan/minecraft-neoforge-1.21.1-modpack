package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.UUIDWrapper;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlayerSelector {
   TypeInfo TYPE_INFO = TypeInfo.of(PlayerSelector.class);

   static PlayerSelector wrap(Context cx, Object o) {
      return switch (o) {
         case null -> throw new KubeRuntimeException("PlayerSelector cannot be null!").source(SourceLine.of(cx));
         case ServerPlayer sp -> identity(sp);
         case UUID uuid -> uuid(uuid);
         case BaseFunction fn -> (PlayerSelector)Cast.to(cx.createInterfaceAdapter(TYPE_INFO, fn));
         default -> fromString(cx, String.valueOf(o).trim().toLowerCase(Locale.ROOT));
      };
   }

   private static PlayerSelector fromString(Context cx, String name) {
      if (name.isEmpty()) {
         throw new KubeRuntimeException("PlayerSelector cannot be blank!").source(SourceLine.of(cx));
      } else {
         UUID uuid = UUIDWrapper.fromString(cx, name);
         return uuid != null ? uuid(uuid) : server -> {
            ServerPlayer player = name(name).or(fuzzyName(name)).getPlayer(server);
            if (player != null) {
               return player;
            } else {
               throw new KubeRuntimeException("No player matched selector '%s'".formatted(name)).source(SourceLine.of(cx));
            }
         };
      }
   }

   @Nullable
   ServerPlayer getPlayer(MinecraftServer server);

   static PlayerSelector identity(ServerPlayer player) {
      return server -> player;
   }

   static PlayerSelector uuid(UUID uuid) {
      return server -> server.getPlayerList().getPlayer(uuid);
   }

   static PlayerSelector name(String name) {
      return server -> server.getPlayerList().getPlayerByName(name);
   }

   static PlayerSelector fuzzyName(String name) {
      return server -> {
         for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (p.getScoreboardName().toLowerCase(Locale.ROOT).contains(name)) {
               return p;
            }
         }

         return null;
      };
   }

   default PlayerSelector or(PlayerSelector fallback) {
      return server -> {
         ServerPlayer p = this.getPlayer(server);
         return p == null ? fallback.getPlayer(server) : p;
      };
   }
}
