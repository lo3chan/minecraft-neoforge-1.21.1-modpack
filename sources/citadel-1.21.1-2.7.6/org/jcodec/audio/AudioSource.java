package org.jcodec.audio;

import java.io.IOException;
import java.nio.FloatBuffer;
import org.jcodec.common.AudioFormat;

public interface AudioSource {
   AudioFormat getFormat();

   int readFloat(FloatBuffer var1) throws IOException;
}
