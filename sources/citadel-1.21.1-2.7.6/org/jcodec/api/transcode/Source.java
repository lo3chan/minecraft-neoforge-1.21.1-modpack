package org.jcodec.api.transcode;

import java.io.IOException;
import org.jcodec.common.AudioCodecMeta;
import org.jcodec.common.VideoCodecMeta;

public interface Source {
   void init(PixelStore var1) throws IOException;

   void seekFrames(int var1) throws IOException;

   VideoFrameWithPacket getNextVideoFrame() throws IOException;

   AudioFrameWithPacket getNextAudioFrame() throws IOException;

   void finish();

   boolean haveAudio();

   void setOption(Options var1, Object var2);

   VideoCodecMeta getVideoCodecMeta();

   AudioCodecMeta getAudioCodecMeta();

   boolean isVideo();

   boolean isAudio();
}
