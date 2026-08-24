package org.tukaani.xz.check;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 extends Check {
   private final MessageDigest sha256;

   public SHA256() throws NoSuchAlgorithmException {
      this.size = 32;
      this.name = "SHA-256";
      this.sha256 = MessageDigest.getInstance("SHA-256");
   }

   @Override
   public void update(byte[] bs, int i, int j) {
      this.sha256.update(bs, i, j);
   }

   @Override
   public byte[] finish() {
      byte[] var1 = this.sha256.digest();
      this.sha256.reset();
      return var1;
   }
}
