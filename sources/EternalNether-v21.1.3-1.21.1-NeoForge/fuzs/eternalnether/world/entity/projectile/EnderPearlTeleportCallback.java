package fuzs.eternalnether.world.entity.projectile;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Deprecated(
   forRemoval = true
)
public interface EnderPearlTeleportCallback {
   EventInvoker<EnderPearlTeleportCallback> EVENT = EventInvoker.lookup(EnderPearlTeleportCallback.class);

   EventResult onEnderPearlTeleport(ServerPlayer var1, Vec3 var2, ThrownEnderpearl var3, MutableFloat var4, HitResult var5);
}
