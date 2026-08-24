package org.jcodec.api.specific;

import java.nio.ByteBuffer;
import org.jcodec.api.MediaInfo;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.codecs.h264.io.model.SeqParameterSet;
import org.jcodec.common.DemuxerTrackMeta;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Packet;

public class AVCMP4Adaptor implements ContainerAdaptor {
   private H264Decoder decoder;
   private int curENo;
   private Size size;
   private DemuxerTrackMeta meta;

   public AVCMP4Adaptor(DemuxerTrackMeta meta) {
      this.meta = meta;
      this.curENo = -1;
      this.calcBufferSize();
   }

   private void calcBufferSize() {
      int w = -2147483648;
      int h = -2147483648;
      ByteBuffer bb = this.meta.getCodecPrivate().duplicate();

      ByteBuffer b;
      while ((b = H264Utils.nextNALUnit(bb)) != null) {
         NALUnit nu = NALUnit.read(b);
         if (nu.type == NALUnitType.SPS) {
            SeqParameterSet sps = H264Utils.readSPS(b);
            int ww = sps.picWidthInMbsMinus1 + 1;
            if (ww > w) {
               w = ww;
            }

            int hh = SeqParameterSet.getPicHeightInMbs(sps);
            if (hh > h) {
               h = hh;
            }
         }
      }

      this.size = new Size(w << 4, h << 4);
   }

   @Override
   public Picture decodeFrame(Packet packet, byte[][] data) {
      this.updateState(packet);
      Picture pic = this.decoder.decodeFrame(packet.getData(), data);
      Rational pasp = this.meta.getVideoCodecMeta().getPixelAspectRatio();
      if (pasp != null) {
      }

      return pic;
   }

   private void updateState(Packet packet) {
      int eNo = ((MP4Packet)packet).getEntryNo();
      if (eNo != this.curENo) {
         this.curENo = eNo;
      }

      if (this.decoder == null) {
         this.decoder = H264Decoder.createH264DecoderFromCodecPrivate(this.meta.getCodecPrivate());
      }
   }

   @Override
   public boolean canSeek(Packet pkt) {
      this.updateState(pkt);
      return H264Utils.idrSlice(H264Utils.splitFrame(pkt.getData()));
   }

   @Override
   public byte[][] allocatePicture() {
      return Picture.create(this.size.getWidth(), this.size.getHeight(), ColorSpace.YUV444).getData();
   }

   @Override
   public MediaInfo getMediaInfo() {
      return new MediaInfo(this.size);
   }
}
