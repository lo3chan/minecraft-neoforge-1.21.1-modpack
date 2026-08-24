package com.iafenvoy.jupiter;

import com.iafenvoy.jupiter.compat.ExtraConfigManager;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import com.iafenvoy.jupiter.util.CopyOnWriteHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerConfigManager implements ResourceManagerReloadListener {
   private static final Map<ResourceLocation, ServerConfigManager.ServerConfigHolder> CONFIGS = new CopyOnWriteHashMap<>();

   public static void registerServerConfig(AbstractConfigContainer data, ServerConfigManager.PermissionChecker checker) {
      registerServerConfig(data, checker, false);
   }

   public static void registerServerConfig(AbstractConfigContainer data, ServerConfigManager.PermissionChecker checker, boolean allowManualSync) {
      CONFIGS.put(data.getConfigId(), new ServerConfigManager.ServerConfigHolder(data, checker, allowManualSync));
   }

   @Nullable
   public static AbstractConfigContainer getConfig(ResourceLocation id) {
      ServerConfigManager.ServerConfigHolder holder = CONFIGS.get(id);
      return holder == null ? null : holder.data;
   }

   public static boolean checkPermission(ResourceLocation id, MinecraftServer server, ServerPlayer player, boolean modify) {
      ServerConfigManager.ServerConfigHolder holder = CONFIGS.get(id);
      return holder == null ? false : !modify && holder.allowManualSync || holder.checker.check(server, player);
   }

   public static List<AbstractConfigContainer> getServerConfigs() {
      return CONFIGS.values().stream().map(ServerConfigManager.ServerConfigHolder::data).toList();
   }

   public void onResourceManagerReload(@NotNull ResourceManager manager) {
      CONFIGS.values().forEach(x -> x.data.load());
      ExtraConfigManager.scanConfigs();
      Jupiter.LOGGER.info("Successfully reload {} server config(s).", CONFIGS.size());
   }

   @FunctionalInterface
   public interface PermissionChecker {
      ServerConfigManager.PermissionChecker ALWAYS_TRUE = (server, player) -> true;
      ServerConfigManager.PermissionChecker ALWAYS_FALSE = (server, player) -> false;
      ServerConfigManager.PermissionChecker IS_DEDICATE_SERVER = (server, player) -> server.isDedicatedServer();
      ServerConfigManager.PermissionChecker IS_LOCAL_GAME = (server, player) -> server.isSingleplayerOwner(player.getGameProfile());
      ServerConfigManager.PermissionChecker IS_OPERATOR = (server, player) -> IS_LOCAL_GAME.check(server, player)
         || player.hasPermissions(server.getOperatorUserPermissionLevel());

      boolean check(MinecraftServer var1, ServerPlayer var2);
   }

   private record ServerConfigHolder(AbstractConfigContainer data, ServerConfigManager.PermissionChecker checker, boolean allowManualSync) {
   }
}
