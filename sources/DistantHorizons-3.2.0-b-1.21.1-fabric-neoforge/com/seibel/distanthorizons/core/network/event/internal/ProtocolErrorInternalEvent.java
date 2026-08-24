package com.seibel.distanthorizons.core.network.event.internal;

import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import org.jetbrains.annotations.Nullable;

public class ProtocolErrorInternalEvent extends AbstractInternalEvent {
   public final Throwable reason;
   @Nullable
   public final AbstractNetworkMessage message;
   public final boolean replyWithCloseReason;

   public ProtocolErrorInternalEvent(Throwable reason, @Nullable AbstractNetworkMessage message, boolean replyWithCloseReason) {
      this.reason = reason;
      this.message = message;
      this.replyWithCloseReason = replyWithCloseReason;
   }
}
