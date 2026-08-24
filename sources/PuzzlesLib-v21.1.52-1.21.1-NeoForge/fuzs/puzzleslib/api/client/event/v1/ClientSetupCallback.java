package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface ClientSetupCallback {
   EventInvoker<ClientSetupCallback> EVENT = EventInvoker.lookup(ClientSetupCallback.class);

   void onClientSetup();
}
