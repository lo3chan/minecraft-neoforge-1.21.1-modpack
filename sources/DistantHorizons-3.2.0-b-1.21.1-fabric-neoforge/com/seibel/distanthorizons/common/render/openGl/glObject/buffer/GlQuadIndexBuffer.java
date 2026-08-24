package com.seibel.distanthorizons.common.render.openGl.glObject.buffer;

import com.seibel.distanthorizons.api.enums.config.EDhApiGpuUploadMethod;
import com.seibel.distanthorizons.common.render.openGl.glObject.enums.GLEnums;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.nio.ByteBuffer;

public class GlQuadIndexBuffer extends GLIndexBuffer {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public GlQuadIndexBuffer() {
      super(false);
   }

   public void upload(ByteBuffer buffer, int quadCount) {
      if (quadCount < 0) {
         throw new IllegalArgumentException("quadCount must be greater than 0");
      } else if (quadCount != 0) {
         this.indicesCount = quadCount * 6;
         if (this.indicesCount < this.getCapacity() || !(this.indicesCount < this.getCapacity() * 1.6900000000000002)) {
            this.glType = 5125;
            super.uploadBuffer(buffer, EDhApiGpuUploadMethod.DATA, this.indicesCount * GLEnums.getTypeSize(this.glType), 35044);
         }
      }
   }

   public int getCapacity() {
      return super.getSize() / GLEnums.getTypeSize(this.getGlType());
   }
}
