package org.jcodec.common;

import java.io.IOException;
import org.jcodec.common.model.Packet;

public interface MuxerTrack {
   void addFrame(Packet var1) throws IOException;
}
