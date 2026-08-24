package com.seibel.distanthorizons.core.multiplayer.fullData;

import com.google.common.base.MoreObjects;
import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.network.INetworkObject;
import com.seibel.distanthorizons.core.sql.dto.BeaconBeamDTO;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV2DTO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

public class FullDataPayload implements INetworkObject {
   private static final AtomicInteger lastBufferId = new AtomicInteger();
   public int dtoBufferId;
   public ByteBuf dtoBuffer;
   public List<BeaconBeamDTO> beaconBeams;

   public FullDataPayload() {
   }

   public FullDataPayload(@NotNull FullDataSourceV2 fullDataSource, List<BeaconBeamDTO> beaconBeams) {
      Objects.requireNonNull(fullDataSource);
      this.dtoBufferId = lastBufferId.getAndIncrement();

      try {
         EDhApiDataCompressionMode compressionMode = Config.Common.LodBuilding.dataCompression.get();
         FullDataSourceV2DTO dataSourceDto = FullDataSourceV2DTO.CreateFromDataSource(fullDataSource, compressionMode);

         try {
            this.dtoBuffer = Unpooled.buffer();
            dataSourceDto.encode(this.dtoBuffer);
         } catch (Throwable var8) {
            if (dataSourceDto != null) {
               try {
                  dataSourceDto.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (dataSourceDto != null) {
            dataSourceDto.close();
         }
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      }

      this.beaconBeams = beaconBeams;
   }

   @Override
   public void encode(ByteBuf out) {
      out.writeInt(this.dtoBufferId);
      this.writeCollection(out, this.beaconBeams);
   }

   @Override
   public void decode(ByteBuf in) {
      this.dtoBufferId = in.readInt();
      this.beaconBeams = this.readCollection(in, new ArrayList<>(), () -> new BeaconBeamDTO(null, null));
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("dtoBufferId", this.dtoBufferId)
         .add("dtoBuffer", this.dtoBuffer)
         .add("beaconBeams", this.beaconBeams)
         .toString();
   }
}
