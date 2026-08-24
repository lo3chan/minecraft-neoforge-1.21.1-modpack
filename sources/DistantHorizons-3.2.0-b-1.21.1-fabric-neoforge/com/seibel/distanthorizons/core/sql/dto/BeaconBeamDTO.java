package com.seibel.distanthorizons.core.sql.dto;

import com.seibel.distanthorizons.core.network.INetworkObject;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import io.netty.buffer.ByteBuf;
import java.awt.Color;

public class BeaconBeamDTO implements IBaseDTO<DhBlockPos>, INetworkObject {
   public DhBlockPos blockPos;
   public Color color;

   public BeaconBeamDTO(DhBlockPos blockPos, Color color) {
      this.blockPos = blockPos;
      this.color = color;
   }

   @Override
   public void encode(ByteBuf out) {
      this.blockPos.encode(out);
      out.writeInt(this.color.getRGB());
   }

   @Override
   public void decode(ByteBuf in) {
      this.blockPos = INetworkObject.decodeToInstance(new DhBlockPos(), in);
      this.color = new Color(in.readInt());
   }

   public DhBlockPos getKey() {
      return this.blockPos;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj != null && obj.getClass() == this.getClass()) {
         BeaconBeamDTO that = (BeaconBeamDTO)obj;
         return this.blockPos.equals(that.blockPos) && this.color.equals(that.color);
      } else {
         return false;
      }
   }

   @Override
   public void close() {
   }

   @Override
   public String toString() {
      return this.blockPos + " " + this.color;
   }
}
