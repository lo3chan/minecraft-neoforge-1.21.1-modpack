package org.jcodec.containers.mkv.boxes;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class EbmlFloat extends EbmlBin {
   public EbmlFloat(byte[] id) {
      super(id);
   }

   public void setDouble(double value) {
      if (value < 3.4028234663852886E38) {
         ByteBuffer bb = ByteBuffer.allocate(4);
         bb.putFloat((float)value);
         ((Buffer)bb).flip();
         this.data = bb;
      } else if (value < 1.7976931348623157E308) {
         ByteBuffer bb = ByteBuffer.allocate(8);
         bb.putDouble(value);
         ((Buffer)bb).flip();
         this.data = bb;
      }
   }

   public double getDouble() {
      return this.data.limit() == 4 ? this.data.duplicate().getFloat() : this.data.duplicate().getDouble();
   }
}
