package org.jcodec.containers.mkv.muxer;

import java.util.ArrayList;
import java.util.List;
import org.jcodec.common.MuxerTrack;
import org.jcodec.common.VideoCodecMeta;
import org.jcodec.common.model.Packet;
import org.jcodec.containers.mkv.boxes.MkvBlock;

public class MKVMuxerTrack implements MuxerTrack {
   public MKVMuxerTrack.MKVMuxerTrackType type;
   public VideoCodecMeta videoMeta;
   public String codecId;
   public int trackNo;
   private int frameDuration;
   List<MkvBlock> trackBlocks = new ArrayList<>();
   static final int DEFAULT_TIMESCALE = 1000000000;
   static final int NANOSECONDS_IN_A_MILISECOND = 1000000;
   static final int MULTIPLIER = 1000;

   public MKVMuxerTrack() {
      this.type = MKVMuxerTrack.MKVMuxerTrackType.VIDEO;
   }

   public int getTimescale() {
      return 1000000;
   }

   @Override
   public void addFrame(Packet outPacket) {
      MkvBlock frame = MkvBlock.keyFrame(this.trackNo, 0, outPacket.getData());
      frame.absoluteTimecode = outPacket.getPts() - 1L;
      this.trackBlocks.add(frame);
   }

   public long getTrackNo() {
      return this.trackNo;
   }

   public static enum MKVMuxerTrackType {
      VIDEO;
   }
}
