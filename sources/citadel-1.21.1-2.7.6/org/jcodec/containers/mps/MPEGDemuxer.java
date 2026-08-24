package org.jcodec.containers.mps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jcodec.common.Demuxer;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.model.Packet;

public interface MPEGDemuxer extends Demuxer {
   @Override
   List<? extends MPEGDemuxer.MPEGDemuxerTrack> getTracks();

   @Override
   List<? extends MPEGDemuxer.MPEGDemuxerTrack> getVideoTracks();

   @Override
   List<? extends MPEGDemuxer.MPEGDemuxerTrack> getAudioTracks();

   public interface MPEGDemuxerTrack extends DemuxerTrack {
      Packet nextFrameWithBuffer(ByteBuffer var1) throws IOException;

      @Override
      DemuxerTrackMeta getMeta();

      int getSid();

      List<PESPacket> getPending();

      void ignore();
   }
}
