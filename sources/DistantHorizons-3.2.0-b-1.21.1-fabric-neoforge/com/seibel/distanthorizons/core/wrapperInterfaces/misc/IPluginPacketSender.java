package com.seibel.distanthorizons.core.wrapperInterfaces.misc;

import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IPluginPacketSender extends IBindable {
   void sendToServer(AbstractNetworkMessage abstractNetworkMessage);

   void sendToClient(IServerPlayerWrapper iServerPlayerWrapper, AbstractNetworkMessage abstractNetworkMessage);
}
