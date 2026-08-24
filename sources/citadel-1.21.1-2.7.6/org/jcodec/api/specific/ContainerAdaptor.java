package org.jcodec.api.specific;

import org.jcodec.api.MediaInfo;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture;

public interface ContainerAdaptor {
   Picture decodeFrame(Packet var1, byte[][] var2);

   boolean canSeek(Packet var1);

   byte[][] allocatePicture();

   MediaInfo getMediaInfo();
}
