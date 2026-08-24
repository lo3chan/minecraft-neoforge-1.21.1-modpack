package net.sourceforge.jaad.mp4.boxes.impl;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.BoxImpl;

public class BitRateBox extends BoxImpl {
   private long decodingBufferSize;
   private long maxBitrate;
   private long avgBitrate;

   public BitRateBox() {
      super("Bitrate Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      this.decodingBufferSize = in.readBytes(4);
      this.maxBitrate = in.readBytes(4);
      this.avgBitrate = in.readBytes(4);
   }

   public long getDecodingBufferSize() {
      return this.decodingBufferSize;
   }

   public long getMaximumBitrate() {
      return this.maxBitrate;
   }

   public long getAverageBitrate() {
      return this.avgBitrate;
   }
}
