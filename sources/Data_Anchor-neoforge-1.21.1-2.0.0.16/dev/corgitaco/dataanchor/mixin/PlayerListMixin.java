package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.PlayerTrackedData;
import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerList.class})
public class PlayerListMixin {
   @Inject(
      method = {"respawn(Lnet/minecraft/server/level/ServerPlayer;ZLnet/minecraft/world/entity/Entity$RemovalReason;)Lnet/minecraft/server/level/ServerPlayer;"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$restoreFrom(ServerPlayer player, boolean keepInventory, RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
      if (cir.getReturnValue() instanceof TrackedDataContainer access) {
         Collection<TrackedDataKey<PlayerTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
         keys.forEach(key -> access.dataAnchor$getTrackedData((TrackedDataKey<PlayerTrackedData>)key).ifPresent(data -> {
            if (data instanceof PlayerTrackedData playerTrackedData) {
               playerTrackedData.respawn(player, keepInventory);
            }
         }));
      }
   }
}
