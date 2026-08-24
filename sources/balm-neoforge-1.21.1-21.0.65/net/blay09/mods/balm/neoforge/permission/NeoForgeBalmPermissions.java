package net.blay09.mods.balm.neoforge.permission;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.blay09.mods.balm.api.permission.PermissionContext;
import net.blay09.mods.balm.common.permission.CommonBalmPermissions;
import net.blay09.mods.balm.common.permission.OfflinePermissionContext;
import net.blay09.mods.balm.common.permission.PlayerPermissionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent.Nodes;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

public class NeoForgeBalmPermissions extends CommonBalmPermissions {
   private final Map<ResourceLocation, PermissionNode<?>> nodes = new ConcurrentHashMap<>();

   public NeoForgeBalmPermissions() {
      NeoForge.EVENT_BUS.addListener(this::registerNodes);
   }

   private void registerNodes(Nodes event) {
      event.addNodes(this.nodes.values());
   }

   @Override
   public void registerPermission(ResourceLocation permission, Function<PermissionContext, Boolean> defaultResolver) {
      super.registerPermission(permission, defaultResolver);
      this.nodes
         .put(
            permission,
            new PermissionNode(
               permission,
               PermissionTypes.BOOLEAN,
               (serverPlayer, uuid, permissionDynamicContexts) -> defaultResolver.apply(
                  (PermissionContext)(serverPlayer != null ? new PlayerPermissionContext(serverPlayer) : new OfflinePermissionContext(uuid))
               ),
               new PermissionDynamicContextKey[0]
            )
         );
   }

   @Override
   public boolean hasPermission(ServerPlayer player, ResourceLocation permission) {
      PermissionNode<Boolean> node = (PermissionNode<Boolean>)this.nodes.get(permission);
      return node == null ? false : (Boolean)PermissionAPI.getPermission(player, node, new PermissionDynamicContext[0]);
   }

   @Override
   public boolean hasPermission(CommandSourceStack source, ResourceLocation permission) {
      PermissionNode<Boolean> node = (PermissionNode<Boolean>)this.nodes.get(permission);
      if (node == null) {
         return false;
      } else {
         ServerPlayer player = source.getPlayer();
         return player != null ? (Boolean)PermissionAPI.getPermission(player, node, new PermissionDynamicContext[0]) : super.hasPermission(source, permission);
      }
   }
}
