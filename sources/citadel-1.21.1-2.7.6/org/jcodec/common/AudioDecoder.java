package org.jcodec.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.model.AudioBuffer;

public interface AudioDecoder {
   AudioBuffer decodeFrame(ByteBuffer var1, ByteBuffer var2) throws IOException;

   AudioCodecMeta getCodecMeta(ByteBuffer var1) throws IOException;
}
