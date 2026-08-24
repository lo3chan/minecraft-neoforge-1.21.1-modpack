package org.jcodec.codecs.wav;

import java.io.IOException;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.AudioFormat;
import org.jcodec.common.Codec;
import org.jcodec.common.Muxer;
import org.jcodec.common.MuxerTrack;
import org.jcodec.common.VideoCodecMeta;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;

public class WavMuxer implements Muxer, MuxerTrack {
   protected SeekableByteChannel out;
   protected WavHeader header;
   protected int written;
   private AudioFormat format;

   public WavMuxer(SeekableByteChannel out) throws IOException {
      this.out = out;
   }

   @Override
   public void addFrame(Packet outPacket) throws IOException {
      this.written = this.written + this.out.write(outPacket.getData());
   }

   public void close() throws IOException {
      this.out.setPosition(0L);
      WavHeader.createWavHeader(this.format, this.format.bytesToFrames(this.written)).write(this.out);
      NIOUtils.closeQuietly(this.out);
   }

   @Override
   public MuxerTrack addVideoTrack(Codec codec, VideoCodecMeta meta) {
      return null;
   }

   @Override
   public MuxerTrack addAudioTrack(Codec codec, AudioCodecMeta meta) {
      this.header = WavHeader.createWavHeader(meta.getFormat(), 0);
      this.format = meta.getFormat();

      try {
         this.header.write(this.out);
         return this;
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   @Override
   public void finish() throws IOException {
   }
}
