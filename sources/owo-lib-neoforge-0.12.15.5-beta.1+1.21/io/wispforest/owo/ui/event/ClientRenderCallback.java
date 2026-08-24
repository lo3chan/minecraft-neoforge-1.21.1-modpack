package io.wispforest.owo.ui.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

public interface ClientRenderCallback {
   Event<ClientRenderCallback> BEFORE = EventFactory.createArrayBacked(ClientRenderCallback.class, callbacks -> client -> {
      for (ClientRenderCallback callback : callbacks) {
         callback.onRender(client);
      }
   });
   Event<ClientRenderCallback> AFTER = EventFactory.createArrayBacked(ClientRenderCallback.class, callbacks -> client -> {
      for (ClientRenderCallback callback : callbacks) {
         callback.onRender(client);
      }
   });

   void onRender(Minecraft var1);
}
