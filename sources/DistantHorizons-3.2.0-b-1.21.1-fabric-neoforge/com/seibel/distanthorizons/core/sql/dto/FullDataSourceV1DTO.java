package com.seibel.distanthorizons.core.sql.dto;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.core.pos.DhSectionPos;

public class FullDataSourceV1DTO implements IBaseDTO<Long> {
   public long pos;
   public int checksum;
   public byte dataDetailLevel;
   public EDhApiWorldGenerationStep worldGenStep;
   public String dataType;
   public byte binaryDataFormatVersion;
   public final byte[] dataArray;

   public FullDataSourceV1DTO(
      long pos, int checksum, byte dataDetailLevel, EDhApiWorldGenerationStep worldGenStep, String dataType, byte binaryDataFormatVersion, byte[] dataArray
   ) {
      this.pos = pos;
      this.checksum = checksum;
      this.dataDetailLevel = dataDetailLevel;
      this.worldGenStep = worldGenStep;
      this.dataType = dataType;
      this.binaryDataFormatVersion = binaryDataFormatVersion;
      this.dataArray = dataArray;
   }

   public Long getKey() {
      return this.pos;
   }

   @Override
   public String getKeyDisplayString() {
      return DhSectionPos.toString(this.pos);
   }

   @Override
   public void close() {
   }
}
