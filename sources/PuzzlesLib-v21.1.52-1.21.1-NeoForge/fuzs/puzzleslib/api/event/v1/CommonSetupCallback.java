package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;

@FunctionalInterface
public interface CommonSetupCallback {
   EventInvoker<CommonSetupCallback> EVENT = EventInvoker.lookup(CommonSetupCallback.class);

   void onCommonSetup();
}
