package org.jcodec.containers.mps.index;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.containers.mps.MPSUtils;
import org.jcodec.containers.mps.PESPacket;

public class MPSIndexer extends BaseIndexer {
   private long predFileStart;

   public void index(File source, NIOUtils.FileReaderListener listener) throws IOException {
      this.newReader().readFile(source, 65536, listener);
   }

   public void indexChannel(SeekableByteChannel source, NIOUtils.FileReaderListener listener) throws IOException {
      this.newReader().readChannel(source, 65536, listener);
   }

   private NIOUtils.FileReader newReader() {
      final MPSIndexer self = this;
      return new NIOUtils.FileReader() {
         @Override
         protected void data(ByteBuffer data, long filePos) {
            self.analyseBuffer(data, filePos);
         }

         @Override
         protected void done() {
            self.finishAnalyse();
         }
      };
   }

   @Override
   protected void pes(ByteBuffer pesBuffer, long start, int pesLen, int stream) {
      if (MPSUtils.mediaStream(stream)) {
         PESPacket pesHeader = MPSUtils.readPESHeader(pesBuffer, start);
         int leading = 0;
         if (this.predFileStart != start) {
            leading += (int)(start - this.predFileStart);
         }

         this.predFileStart = start + pesLen;
         this.savePESMeta(stream, MPSIndex.makePESToken(leading, pesLen, pesBuffer.remaining()));
         this.getAnalyser(stream).pkt(pesBuffer, pesHeader);
      }
   }

   public static void main1(String[] args) throws IOException {
      MPSIndexer indexer = new MPSIndexer();
      indexer.index(new File(args[0]), new NIOUtils.FileReaderListener() {
         @Override
         public void progress(int percentDone) {
            System.out.println(percentDone);
         }
      });
      ByteBuffer index = ByteBuffer.allocate(65536);
      indexer.serialize().serializeTo(index);
      NIOUtils.writeTo(index, new File(args[1]));
   }
}
