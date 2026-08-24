package org.jcodec.api.transcode;

import java.io.IOException;
import org.jcodec.common.model.ColorSpace;

public interface Sink {
   void init() throws IOException;

   void outputVideoFrame(VideoFrameWithPacket var1) throws IOException;

   void outputAudioFrame(AudioFrameWithPacket var1) throws IOException;

   void finish() throws IOException;

   ColorSpace getInputColor();

   void setOption(Options var1, Object var2);

   boolean isVideo();

   boolean isAudio();
}
