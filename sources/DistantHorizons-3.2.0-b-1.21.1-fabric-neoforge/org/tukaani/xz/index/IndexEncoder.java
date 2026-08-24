package org.tukaani.xz.index;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.common.EncoderUtil;

public class IndexEncoder extends IndexBase {
   private final ArrayList<IndexRecord> records = new ArrayList<>();

   public IndexEncoder() {
      super(new XZIOException("XZ Stream or its Index has grown too big"));
   }

   @Override
   public void add(long l, long m) throws XZIOException {
      super.add(l, m);
      this.records.add(new IndexRecord(l, m));
   }

   public void encode(OutputStream outputStream) throws IOException {
      CRC32 var2 = new CRC32();
      CheckedOutputStream var3 = new CheckedOutputStream(outputStream, var2);
      var3.write(0);
      EncoderUtil.encodeVLI(var3, this.recordCount);

      for (IndexRecord var5 : this.records) {
         EncoderUtil.encodeVLI(var3, var5.unpadded);
         EncoderUtil.encodeVLI(var3, var5.uncompressed);
      }

      for (int var7 = this.getIndexPaddingSize(); var7 > 0; var7--) {
         var3.write(0);
      }

      long var8 = var2.getValue();

      for (int var6 = 0; var6 < 4; var6++) {
         outputStream.write((byte)(var8 >>> var6 * 8));
      }
   }
}
