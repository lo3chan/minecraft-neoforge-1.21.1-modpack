package org.jcodec.common;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Picture;

public abstract class VideoDecoder {
   private byte[][] byteBuffer;

   public abstract Picture decodeFrame(ByteBuffer var1, byte[][] var2);

   public abstract VideoCodecMeta getCodecMeta(ByteBuffer var1);

   protected byte[][] getSameSizeBuffer(int[][] buffer) {
      if (this.byteBuffer == null || this.byteBuffer.length != buffer.length || this.byteBuffer[0].length != buffer[0].length) {
         this.byteBuffer = ArrayUtil.create2D(buffer[0].length, buffer.length);
      }

      return this.byteBuffer;
   }

   public VideoDecoder downscaled(int ratio) {
      return ratio == 1 ? this : null;
   }
}
