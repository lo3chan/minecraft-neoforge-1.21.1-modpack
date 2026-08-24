package com.github.alexthe666.citadel.server.entity;

import com.github.alexthe666.citadel.server.message.DanceJukeboxMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public interface IDancesToJukebox {
   void setDancing(boolean var1);

   void setJukeboxPos(BlockPos var1);

   default void onClientPlayMusicDisc(int entityId, BlockPos pos, boolean dancing) {
      PacketDistributor.sendToServer(new DanceJukeboxMessage(entityId, dancing, pos), new CustomPacketPayload[0]);
      this.setDancing(dancing);
      if (dancing) {
         this.setJukeboxPos(pos);
      } else {
         this.setJukeboxPos(null);
      }
   }
}
