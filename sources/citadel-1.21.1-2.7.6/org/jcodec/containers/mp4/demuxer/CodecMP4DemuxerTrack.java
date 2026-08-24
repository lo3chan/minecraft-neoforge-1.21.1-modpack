package org.jcodec.containers.mp4.demuxer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jcodec.codecs.aac.AACUtils;
import org.jcodec.codecs.aac.ADTSParser;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.common.Codec;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.boxes.VideoSampleEntry;

public class CodecMP4DemuxerTrack extends MP4DemuxerTrack {
   private ByteBuffer codecPrivate;
   private AvcCBox avcC;

   public CodecMP4DemuxerTrack(MovieBox mov, TrakBox trak, SeekableByteChannel input) {
      super(mov, trak, input);
      if (Codec.codecByFourcc(this.getFourcc()) == Codec.H264) {
         this.avcC = H264Utils.parseAVCC((VideoSampleEntry)this.getSampleEntries()[0]);
      }

      this.codecPrivate = MP4DemuxerTrackMeta.getCodecPrivate(this);
   }

   @Override
   public ByteBuffer convertPacket(ByteBuffer result) {
      if (this.codecPrivate != null) {
         if (Codec.codecByFourcc(this.getFourcc()) == Codec.H264) {
            ByteBuffer annexbCoded = H264Utils.decodeMOVPacket(result, this.avcC);
            if (H264Utils.isByteBufferIDRSlice(annexbCoded)) {
               return NIOUtils.combineBuffers(Arrays.asList(this.codecPrivate, annexbCoded));
            }

            return annexbCoded;
         }

         if (Codec.codecByFourcc(this.getFourcc()) == Codec.AAC) {
            ADTSParser.Header adts = AACUtils.streamInfoToADTS(this.codecPrivate, true, 1, result.remaining());
            ByteBuffer adtsRaw = ByteBuffer.allocate(7);
            ADTSParser.write(adts, adtsRaw);
            return NIOUtils.combineBuffers(Arrays.asList(adtsRaw, result));
         }
      }

      return result;
   }
}
