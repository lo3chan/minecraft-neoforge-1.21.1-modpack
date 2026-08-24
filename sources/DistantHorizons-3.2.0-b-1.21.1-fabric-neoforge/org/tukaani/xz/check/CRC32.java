package org.tukaani.xz.check;

public class CRC32 extends Check {
   private final java.util.zip.CRC32 state = new java.util.zip.CRC32();

   public CRC32() {
      this.size = 4;
      this.name = "CRC32";
   }

   @Override
   public void update(byte[] bs, int i, int j) {
      this.state.update(bs, i, j);
   }

   @Override
   public byte[] finish() {
      long var1 = this.state.getValue();
      byte[] var3 = new byte[]{(byte)var1, (byte)(var1 >>> 8), (byte)(var1 >>> 16), (byte)(var1 >>> 24)};
      this.state.reset();
      return var3;
   }
}
