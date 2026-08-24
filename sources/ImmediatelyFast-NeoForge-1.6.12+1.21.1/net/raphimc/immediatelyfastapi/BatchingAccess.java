package net.raphimc.immediatelyfastapi;

@Deprecated
public interface BatchingAccess {
   @Deprecated
   void beginHudBatching();

   @Deprecated
   void endHudBatching();

   @Deprecated
   boolean isHudBatching();

   @Deprecated
   boolean hasDataToDraw();

   @Deprecated
   void forceDrawBuffers();
}
