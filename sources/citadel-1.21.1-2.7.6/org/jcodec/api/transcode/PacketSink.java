package org.jcodec.api.transcode;

import java.io.IOException;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.VideoCodecMeta;
import org.jcodec.common.model.Packet;

public interface PacketSink {
   void outputVideoPacket(Packet var1, VideoCodecMeta var2) throws IOException;

   void outputAudioPacket(Packet var1, AudioCodecMeta var2) throws IOException;
}
