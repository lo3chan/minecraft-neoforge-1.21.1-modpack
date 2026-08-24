package org.jcodec.common.model;

import java.nio.ByteBuffer;
import org.jcodec.common.AudioFormat;

public class AudioBuffer {
   protected ByteBuffer data;
   protected AudioFormat format;
   protected int nFrames;

   public AudioBuffer(ByteBuffer data, AudioFormat format, int nFrames) {
      this.data = data;
      this.format = format;
      this.nFrames = nFrames;
   }

   public ByteBuffer getData() {
      return this.data;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public int getNFrames() {
      return this.nFrames;
   }
}
