package com.seibel.distanthorizons.core.network.event.internal;

public class IncompatibleMessageInternalEvent extends AbstractInternalEvent {
   public final int protocolVersion;

   public IncompatibleMessageInternalEvent(int protocolVersion) {
      this.protocolVersion = protocolVersion;
   }
}
