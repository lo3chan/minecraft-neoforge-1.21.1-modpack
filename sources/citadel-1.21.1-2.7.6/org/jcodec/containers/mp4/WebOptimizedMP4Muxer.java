package org.jcodec.containers.mp4;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.logging.Logger;
import org.jcodec.containers.mp4.boxes.ChunkOffsets64Box;
import org.jcodec.containers.mp4.boxes.ChunkOffsetsBox;
import org.jcodec.containers.mp4.boxes.Header;
import org.jcodec.containers.mp4.boxes.MovieBox;
import org.jcodec.containers.mp4.boxes.SampleToChunkBox;
import org.jcodec.containers.mp4.boxes.TrakBox;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

public class WebOptimizedMP4Muxer extends MP4Muxer {
   private ByteBuffer header;
   private long headerPos;

   public static WebOptimizedMP4Muxer withOldHeader(SeekableByteChannel output, Brand brand, MovieBox oldHeader) throws IOException {
      int size = (int)oldHeader.getHeader().getSize();
      TrakBox vt = oldHeader.getVideoTrack();
      SampleToChunkBox stsc = vt.getStsc();
      size -= stsc.getSampleToChunk().length * 12;
      size += 12;
      ChunkOffsetsBox stco = vt.getStco();
      if (stco != null) {
         size -= stco.getChunkOffsets().length << 2;
         size += vt.getFrameCount() << 3;
      } else {
         ChunkOffsets64Box co64 = vt.getCo64();
         size -= co64.getChunkOffsets().length << 3;
         size += vt.getFrameCount() << 3;
      }

      return new WebOptimizedMP4Muxer(output, brand, size + (size >> 1));
   }

   public WebOptimizedMP4Muxer(SeekableByteChannel output, Brand brand, int headerSize) throws IOException {
      super(output, brand.getFileTypeBox());
      this.headerPos = output.position() - 24L;
      output.setPosition(this.headerPos);
      this.header = ByteBuffer.allocate(headerSize);
      output.write(this.header);
      ((Buffer)this.header).clear();
      Header.createHeader("wide", 8L).writeChannel(output);
      Header.createHeader("mdat", 1L).writeChannel(output);
      this.mdatOffset = output.position();
      NIOUtils.writeLong(output, 0L);
   }

   @Override
   public void storeHeader(MovieBox movie) throws IOException {
      long mdatEnd = this.out.position();
      long mdatSize = mdatEnd - this.mdatOffset + 8L;
      this.out.setPosition(this.mdatOffset);
      NIOUtils.writeLong(this.out, mdatSize);
      this.out.setPosition(this.headerPos);

      try {
         movie.write(this.header);
         ((Buffer)this.header).flip();
         int rem = this.header.capacity() - this.header.limit();
         if (rem < 8) {
            this.header.duplicate().putInt(this.header.capacity());
         }

         this.out.write(this.header);
         if (rem >= 8) {
            Header.createHeader("free", rem).writeChannel(this.out);
         }
      } catch (ArrayIndexOutOfBoundsException var7) {
         Logger.warn("Could not web-optimize, header is bigger then allocated space.");
         Header.createHeader("free", this.header.remaining()).writeChannel(this.out);
         this.out.setPosition(mdatEnd);
         MP4Util.writeMovie(this.out, movie);
      }
   }
}
