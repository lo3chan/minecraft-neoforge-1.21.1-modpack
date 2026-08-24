package com.mrcrayfish.configured.util;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.configured.Config;
import com.mrcrayfish.configured.api.Environment;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientConfigHelper;
import com.mrcrayfish.configured.client.ClientSessionData;
import com.mrcrayfish.configured.platform.Services;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ConfigHelper {
   public static List<IConfigEntry> gatherAllConfigEntries(IConfigEntry entry) {
      List<IConfigEntry> entries = new ObjectArrayList();
      Queue<IConfigEntry> queue = new ArrayDeque<>(entry.getChildren());

      while (!queue.isEmpty()) {
         IConfigEntry e = queue.poll();
         entries.add(e);
         if (!e.isLeaf()) {
            queue.addAll(e.getChildren());
         }
      }

      return entries;
   }

   public static List<IConfigValue<?>> gatherAllConfigValues(IConfigEntry entry) {
      List<IConfigValue<?>> values = new ObjectArrayList();
      gatherValuesFromForgeConfig(entry, values);
      return ImmutableList.copyOf(values);
   }

   private static void gatherValuesFromForgeConfig(IConfigEntry entry, List<IConfigValue<?>> values) {
      if (entry.isLeaf()) {
         IConfigValue<?> value = entry.getValue();
         if (value != null) {
            values.add(value);
         }
      } else {
         for (IConfigEntry children : entry.getChildren()) {
            gatherValuesFromForgeConfig(children, values);
         }
      }
   }

   public static boolean isServerConfig(IModConfig config) {
      return config.getType().isServer();
   }

   public static boolean isConfiguredInstalledOnServer() {
      return Services.PLATFORM.getEnvironment() == Environment.DEDICATED_SERVER ? true : ClientConfigHelper.isConfiguredInstalledRemotely();
   }

   public static Set<IConfigValue<?>> getChangedValues(IConfigEntry entry) {
      Set<IConfigValue<?>> changed = new HashSet<>();
      Queue<IConfigEntry> found = new ArrayDeque<>();
      found.add(entry);

      while (!found.isEmpty()) {
         IConfigEntry toSave = found.poll();
         if (!toSave.isLeaf()) {
            found.addAll(toSave.getChildren());
         } else {
            IConfigValue<?> value = toSave.getValue();
            if (value != null && value.isChanged()) {
               changed.add(value);
            }
         }
      }

      return changed;
   }

   public static boolean isPlayingGame() {
      return Services.PLATFORM.getEnvironment() != Environment.CLIENT ? false : ClientConfigHelper.isPlayingGame();
   }

   public static boolean isPlayingLan() {
      return Services.PLATFORM.getEnvironment() != Environment.CLIENT
         ? false
         : ClientConfigHelper.isLan() || !isIntegratedServer() && ClientSessionData.isLan();
   }

   public static boolean isSingleplayer() {
      return Services.PLATFORM.getEnvironment() == Environment.DEDICATED_SERVER ? false : ClientConfigHelper.isSingleplayer();
   }

   public static boolean isServerOwnedByPlayer(@Nullable Player player) {
      return Services.PLATFORM.getEnvironment() == Environment.DEDICATED_SERVER ? false : ClientConfigHelper.isServerOwnedByPlayer(player);
   }

   public static boolean isOperator(@Nullable Player player) {
      if (player != null) {
         if (Services.PLATFORM.getEnvironment() != Environment.DEDICATED_SERVER) {
            return player.hasPermissions(4);
         } else {
            MinecraftServer server = player.getServer();
            return server != null && server.getPlayerList().isOp(player.getGameProfile());
         }
      } else {
         return false;
      }
   }

   public static Player getClientPlayer() {
      return Services.PLATFORM.getEnvironment() != Environment.CLIENT ? null : ClientConfigHelper.getClientPlayer();
   }

   public static boolean isIntegratedServer() {
      return Services.PLATFORM.getEnvironment() != Environment.CLIENT ? false : ClientConfigHelper.isIntegratedServer();
   }

   public static boolean isDeveloper(@Nullable Player player) {
      if (player == null) {
         return false;
      } else {
         return Services.PLATFORM.getEnvironment() == Environment.DEDICATED_SERVER
            ? Config.isDeveloperEnabled() && Config.getDevelopers().contains(player.getUUID())
            : player.isLocalPlayer() && ClientSessionData.isDeveloper() || isServerOwnedByPlayer(player);
      }
   }

   public static boolean isPlayingOnRemoteServer() {
      return Services.PLATFORM.getEnvironment() == Environment.DEDICATED_SERVER ? true : ClientConfigHelper.isPlayingRemotely();
   }

   public static boolean canRestoreConfig(IModConfig config, Player player) {
      return config.restoreDefaultsTask().isPresent() && config.canPlayerEdit(player).asBoolean();
   }
}
