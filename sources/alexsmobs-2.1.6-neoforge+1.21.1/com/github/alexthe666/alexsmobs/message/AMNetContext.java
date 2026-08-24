package com.github.alexthe666.alexsmobs.message;

import net.minecraft.server.level.ServerPlayer;

public interface AMNetContext {
   void setPacketHandled(boolean var1);

   void enqueueWork(Runnable var1);

   ServerPlayer getSender();

   boolean isClientSide();
}
