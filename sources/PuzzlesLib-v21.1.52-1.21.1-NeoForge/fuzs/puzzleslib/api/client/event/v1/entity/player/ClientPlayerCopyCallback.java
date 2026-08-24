package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;

@FunctionalInterface
public interface ClientPlayerCopyCallback {
   EventInvoker<ClientPlayerCopyCallback> EVENT = EventInvoker.lookup(ClientPlayerCopyCallback.class);

   void onCopy(LocalPlayer var1, LocalPlayer var2, MultiPlayerGameMode var3, Connection var4);
}
