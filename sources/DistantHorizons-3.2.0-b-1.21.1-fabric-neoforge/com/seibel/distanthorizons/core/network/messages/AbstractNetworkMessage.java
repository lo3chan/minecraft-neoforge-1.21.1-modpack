package com.seibel.distanthorizons.core.network.messages;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.network.INetworkObject;
import com.seibel.distanthorizons.core.network.session.NetworkSession;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;

public abstract class AbstractNetworkMessage implements INetworkObject {
   private NetworkSession networkSession = null;

   public NetworkSession getSession() {
      return this.networkSession;
   }

   public void setSession(NetworkSession networkSession) {
      this.networkSession = networkSession;
   }

   public IServerPlayerWrapper serverPlayer() {
      return this.networkSession.serverPlayer;
   }

   @Override
   public String toString() {
      return this.toStringHelper().toString();
   }

   public ToStringHelper toStringHelper() {
      return MoreObjects.toStringHelper(this);
   }
}
