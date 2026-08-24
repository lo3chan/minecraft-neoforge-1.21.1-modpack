package org.tukaani.xz.index;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CheckedInputStream;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.check.CRC32;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.check.SHA256;
import org.tukaani.xz.common.DecoderUtil;

public class IndexHash extends IndexBase {
   private Check hash;

   public IndexHash() {
      super(new CorruptedInputException());

      try {
         this.hash = new SHA256();
      } catch (NoSuchAlgorithmException var2) {
         this.hash = new CRC32();
      }
   }

   @Override
   public void add(long l, long m) throws XZIOException {
      super.add(l, m);
      ByteBuffer var5 = ByteBuffer.allocate(16);
      var5.putLong(l);
      var5.putLong(m);
      this.hash.update(var5.array());
   }

   public void validate(InputStream inputStream) throws IOException {
      java.util.zip.CRC32 var2 = new java.util.zip.CRC32();
      var2.update(0);
      CheckedInputStream var3 = new CheckedInputStream(inputStream, var2);
      long var4 = DecoderUtil.decodeVLI(var3);
      if (var4 != this.recordCount) {
         throw new CorruptedInputException("XZ Block Header or the start of XZ Index is corrupt");
      } else {
         IndexHash var6 = new IndexHash();

         for (long var7 = 0L; var7 < this.recordCount; var7++) {
            long var9 = DecoderUtil.decodeVLI(var3);
            long var11 = DecoderUtil.decodeVLI(var3);

            try {
               var6.add(var9, var11);
            } catch (XZIOException var14) {
               throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (var6.blocksSum > this.blocksSum || var6.uncompressedSum > this.uncompressedSum || var6.indexListSize > this.indexListSize) {
               throw new CorruptedInputException("XZ Index is corrupt");
            }
         }

         if (var6.blocksSum == this.blocksSum
            && var6.uncompressedSum == this.uncompressedSum
            && var6.indexListSize == this.indexListSize
            && Arrays.equals(var6.hash.finish(), this.hash.finish())) {
            DataInputStream var15 = new DataInputStream(var3);

            for (int var8 = this.getIndexPaddingSize(); var8 > 0; var8--) {
               if (var15.readUnsignedByte() != 0) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }

            long var16 = var2.getValue();

            for (int var10 = 0; var10 < 4; var10++) {
               if ((var16 >>> var10 * 8 & 255L) != var15.readUnsignedByte()) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }

            return;
         } else {
            throw new CorruptedInputException("XZ Index is corrupt");
         }
      }
   }
}
