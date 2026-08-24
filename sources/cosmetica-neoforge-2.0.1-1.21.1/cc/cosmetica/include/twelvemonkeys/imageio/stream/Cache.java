package cc.cosmetica.include.twelvemonkeys.imageio.stream;

import java.nio.channels.SeekableByteChannel;

interface Cache extends SeekableByteChannel {
   void flushBefore(long var1);
}
