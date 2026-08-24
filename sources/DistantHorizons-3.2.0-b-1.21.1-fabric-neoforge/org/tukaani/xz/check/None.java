package org.tukaani.xz.check;

public class None extends Check {
   public None() {
      this.size = 0;
      this.name = "None";
   }

   @Override
   public void update(byte[] bs, int i, int j) {
   }

   @Override
   public byte[] finish() {
      return new byte[0];
   }
}
