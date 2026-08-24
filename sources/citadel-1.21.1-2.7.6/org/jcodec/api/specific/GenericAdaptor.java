package org.jcodec.api.specific;

import org.jcodec.api.MediaInfo;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;

public class GenericAdaptor implements ContainerAdaptor {
   private VideoDecoder decoder;

   public GenericAdaptor(VideoDecoder detect) {
      this.decoder = detect;
   }

   @Override
   public Picture decodeFrame(Packet packet, byte[][] data) {
      return this.decoder.decodeFrame(packet.getData(), data);
   }

   @Override
   public boolean canSeek(Packet data) {
      return true;
   }

   @Override
   public MediaInfo getMediaInfo() {
      return new MediaInfo(new Size(0, 0));
   }

   @Override
   public byte[][] allocatePicture() {
      return Picture.create(1920, 1088, ColorSpace.YUV444).getData();
   }
}
