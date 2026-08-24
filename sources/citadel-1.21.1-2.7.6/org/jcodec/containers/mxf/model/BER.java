package org.jcodec.containers.mxf.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;

public class BER {
   public static final byte ASN_LONG_LEN = -128;

   public static final long decodeLength(SeekableByteChannel is) throws IOException {
      long length = 0L;
      int lengthbyte = NIOUtils.readByte(is) & 255;
      if ((lengthbyte & -128) > 0) {
         lengthbyte &= 127;
         if (lengthbyte == 0) {
            throw new IOException("Indefinite lengths are not supported");
         }

         if (lengthbyte > 8) {
            throw new IOException("Data length > 4 bytes are not supported!");
         }

         byte[] bb = NIOUtils.readNByte(is, lengthbyte);

         for (int i = 0; i < lengthbyte; i++) {
            length = length << 8 | bb[i] & 255;
         }

         if (length < 0L) {
            throw new IOException("mxflib does not support data lengths > 2^63");
         }
      } else {
         length = lengthbyte & 0xFF;
      }

      return length;
   }

   public static long decodeLengthBuf(ByteBuffer buffer) {
      long length = 0L;
      int lengthbyte = buffer.get() & 255;
      if ((lengthbyte & -128) > 0) {
         lengthbyte &= 127;
         if (lengthbyte == 0) {
            throw new RuntimeException("Indefinite lengths are not supported");
         }

         if (lengthbyte > 8) {
            throw new RuntimeException("Data length > 8 bytes are not supported!");
         }

         for (int i = 0; i < lengthbyte; i++) {
            length = length << 8 | buffer.get() & 255;
         }

         if (length < 0L) {
            throw new RuntimeException("mxflib does not support data lengths > 2^63");
         }
      } else {
         length = lengthbyte & 0xFF;
      }

      return length;
   }
}
