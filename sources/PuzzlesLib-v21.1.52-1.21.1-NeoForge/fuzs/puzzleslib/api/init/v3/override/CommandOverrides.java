package fuzs.puzzleslib.api.init.v3.override;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.ApiStatus.Internal;

@Deprecated
public final class CommandOverrides {
   private static final String KEY_PLAYER_JOINED_WORLD = PuzzlesLibMod.id("has_seen_world").toLanguageKey();
   private static final Map<CommandOverrides.CommandEnvironment, Collection<String>> COMMAND_OVERRIDES = Maps.newEnumMap(
      CommandOverrides.CommandEnvironment.class
   );

   private CommandOverrides() {
   }

   public static void registerServerCommand(String command, boolean onlyDedicated) {
      CommandOverrides.CommandEnvironment commandEnvironment = onlyDedicated
         ? CommandOverrides.CommandEnvironment.DEDICATED_SERVER
         : CommandOverrides.CommandEnvironment.SERVER;
      COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
   }

   public static void registerEffectCommand(Holder<MobEffect> holder) {
      registerPlayerCommand("effect give @s " + holder.getRegisteredName() + " infinite 127 true", false);
   }

   public static void registerPlayerCommand(String command, boolean onlyDedicated) {
      CommandOverrides.CommandEnvironment commandEnvironment = onlyDedicated
         ? CommandOverrides.CommandEnvironment.DEDICATED_PLAYER
         : CommandOverrides.CommandEnvironment.PLAYER;
      COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
   }

   @Internal
   public static void registerEventHandlers() {
      ServerLifecycleEvents.STARTED
         .register(
            minecraftServer -> {
               if (minecraftServer.getWorldData().overworldData().getGameTime() == 0L && minecraftServer.getWorldData().isAllowCommands()) {
                  executeCommandOverrides(
                     minecraftServer,
                     CommandOverrides.CommandEnvironment.SERVER,
                     CommandOverrides.CommandEnvironment.DEDICATED_SERVER,
                     UnaryOperator.identity()
                  );
               }
            }
         );
      ServerEntityEvents.LOAD
         .register(
            (entity, serverLevel, isLoadedFromDisk, entitySpawnReason) -> {
               if (!isLoadedFromDisk && entity instanceof ServerPlayer serverPlayer && !serverPlayer.getTags().contains(KEY_PLAYER_JOINED_WORLD)) {
                  serverPlayer.addTag(KEY_PLAYER_JOINED_WORLD);
                  if (serverLevel.getServer().isDedicatedServer() || serverLevel.getServer().getWorldData().isAllowCommands()) {
                     serverLevel.getServer()
                        .tell(
                           new TickTask(
                              serverLevel.getServer().getTickCount(),
                              () -> {
                                 String playerName = serverPlayer.getGameProfile().getName();
                                 executeCommandOverrides(
                                    serverLevel.getServer(),
                                    CommandOverrides.CommandEnvironment.PLAYER,
                                    CommandOverrides.CommandEnvironment.DEDICATED_PLAYER,
                                    s -> s.replaceAll("@[sp]", playerName)
                                 );
                              }
                           )
                        );
                  }
               }
            }
         );
      PlayerCopyEvents.COPY.register((originalServerPlayer, newServerPlayer, originalStillAlive) -> {
         if (!originalStillAlive) {
            originalServerPlayer.removeTag(KEY_PLAYER_JOINED_WORLD);
         }
      });
   }

   private static void executeCommandOverrides(
      MinecraftServer minecraftServer,
      CommandOverrides.CommandEnvironment commandEnvironment,
      CommandOverrides.CommandEnvironment dedicatedCommandEnvironment,
      UnaryOperator<String> formatter
   ) {
      for (String command : COMMAND_OVERRIDES.getOrDefault(commandEnvironment, Collections.emptySet())) {
         minecraftServer.getCommands().performPrefixedCommand(minecraftServer.createCommandSourceStack(), formatter.apply(command));
      }

      if (minecraftServer instanceof DedicatedServer) {
         for (String command : COMMAND_OVERRIDES.getOrDefault(dedicatedCommandEnvironment, Collections.emptySet())) {
            minecraftServer.getCommands().performPrefixedCommand(minecraftServer.createCommandSourceStack(), formatter.apply(command));
         }
      }
   }

   private static enum CommandEnvironment {
      DEDICATED_SERVER,
      SERVER,
      DEDICATED_PLAYER,
      PLAYER;
   }
}
