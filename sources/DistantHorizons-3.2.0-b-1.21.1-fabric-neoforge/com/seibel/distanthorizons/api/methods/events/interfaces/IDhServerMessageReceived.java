package com.seibel.distanthorizons.api.methods.events.interfaces;

@Deprecated
public interface IDhServerMessageReceived<T> extends IDhApiEvent<T> {
   void serverMessageReceived(String string, byte[] bs);
}
