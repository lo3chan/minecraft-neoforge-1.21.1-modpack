package org.jcodec.common;

import java.io.IOException;
import org.jcodec.common.model.Packet;

public interface DemuxerTrack {
   Packet nextFrame() throws IOException;

   DemuxerTrackMeta getMeta();
}
