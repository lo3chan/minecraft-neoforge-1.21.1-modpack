package net.sourceforge.jaad.mp4.boxes.impl.oma;

import java.io.IOException;
import net.sourceforge.jaad.mp4.MP4InputStream;
import net.sourceforge.jaad.mp4.boxes.FullBox;

public class OMATransactionTrackingBox extends FullBox {
   private String transactionID;

   public OMATransactionTrackingBox() {
      super("OMA DRM Transaction Tracking Box");
   }

   @Override
   public void decode(MP4InputStream in) throws IOException {
      super.decode(in);
      this.transactionID = in.readString(16);
   }

   public String getTransactionID() {
      return this.transactionID;
   }
}
