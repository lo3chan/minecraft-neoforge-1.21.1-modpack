package org.jcodec.containers.mps;

import java.nio.ByteBuffer;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.TapeTimecode;

public class MPEGPacket extends Packet {
   private long offset;
   private ByteBuffer seq;
   private int gop;
   private int timecode;

   public MPEGPacket(ByteBuffer data, long pts, int timescale, long duration, long frameNo, Packet.FrameType keyFrame, TapeTimecode tapeTimecode) {
      super(data, pts, timescale, duration, frameNo, keyFrame, tapeTimecode, 0);
   }

   public long getOffset() {
      return this.offset;
   }

   public ByteBuffer getSeq() {
      return this.seq;
   }

   public int getGOP() {
      return this.gop;
   }

   public int getTimecode() {
      return this.timecode;
   }
}
