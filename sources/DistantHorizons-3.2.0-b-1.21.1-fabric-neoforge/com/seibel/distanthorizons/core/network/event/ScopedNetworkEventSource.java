package com.seibel.distanthorizons.core.network.event;

import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import java.util.function.Consumer;

public final class ScopedNetworkEventSource extends AbstractNetworkEventSource {
   public final AbstractNetworkEventSource parent;
   private boolean isClosed = false;
   private final Consumer<AbstractNetworkMessage> actualHandleMessageStable = this::handleMessage;

   public ScopedNetworkEventSource(AbstractNetworkEventSource parent) {
      this.parent = parent;
   }

   @Override
   public <T extends AbstractNetworkMessage> void registerHandler(Class<T> handlerClass, Consumer<T> handlerImplementation) {
      if (!this.isClosed) {
         this.parent.registerHandler(this, handlerClass, (Consumer<T>)this.actualHandleMessageStable);
         super.registerHandler(this, handlerClass, handlerImplementation);
      }
   }

   @Override
   public void close() {
      this.isClosed = true;
      this.parent.removeAllHandlers(this);
   }
}
