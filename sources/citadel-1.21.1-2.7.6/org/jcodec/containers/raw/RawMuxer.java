package org.jcodec.containers.raw;

import java.io.IOException;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.Codec;
import org.jcodec.common.Muxer;
import org.jcodec.common.MuxerTrack;
import org.jcodec.common.VideoCodecMeta;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;

public class RawMuxer implements Muxer, MuxerTrack {
   private SeekableByteChannel ch;
   private boolean hasVideo;
   private boolean hasAudio;

   public RawMuxer(SeekableByteChannel destStream) {
      this.ch = destStream;
   }

   @Override
   public MuxerTrack addVideoTrack(Codec codec, VideoCodecMeta meta) {
      if (this.hasAudio) {
         throw new RuntimeException("Raw muxer supports either video or audio track but not both.");
      } else {
         this.hasVideo = true;
         return this;
      }
   }

   @Override
   public MuxerTrack addAudioTrack(Codec codec, AudioCodecMeta meta) {
      if (this.hasVideo) {
         throw new RuntimeException("Raw muxer supports either video or audio track but not both.");
      } else {
         this.hasAudio = true;
         return this;
      }
   }

   @Override
   public void finish() throws IOException {
   }

   @Override
   public void addFrame(Packet outPacket) throws IOException {
      this.ch.write(outPacket.getData().duplicate());
   }
}
