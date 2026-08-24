package net.sourceforge.jaad.mp4.boxes.impl.fd;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class FECReservoirBox extends FullBox {
   private int[] itemIDs;
   private long[] symbolCounts;

   public FECReservoirBox() {
      super("FEC Reservoir Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      int entryCount = (int)in.readBytes(2);
      this.itemIDs = new int[entryCount];
      this.symbolCounts = new long[entryCount];

      for (int i = 0; i < entryCount; i++) {
         this.itemIDs[i] = (int)in.readBytes(2);
         this.symbolCounts[i] = in.readBytes(4);
      }
   }

   public int[] getItemIDs() {
      return this.itemIDs;
   }

   public long[] getSymbolCounts() {
      return this.symbolCounts;
   }
}
