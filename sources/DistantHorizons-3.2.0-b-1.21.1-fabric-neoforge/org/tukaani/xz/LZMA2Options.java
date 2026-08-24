package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public class LZMA2Options extends FilterOptions {
   public static final int PRESET_MIN = 0;
   public static final int PRESET_MAX = 9;
   public static final int PRESET_DEFAULT = 6;
   public static final int DICT_SIZE_MIN = 4096;
   public static final int DICT_SIZE_MAX = 805306368;
   public static final int DICT_SIZE_DEFAULT = 8388608;
   public static final int LC_LP_MAX = 4;
   public static final int LC_DEFAULT = 3;
   public static final int LP_DEFAULT = 0;
   public static final int PB_MAX = 4;
   public static final int PB_DEFAULT = 2;
   public static final int MODE_UNCOMPRESSED = 0;
   public static final int MODE_FAST = 1;
   public static final int MODE_NORMAL = 2;
   public static final int NICE_LEN_MIN = 8;
   public static final int NICE_LEN_MAX = 273;
   public static final int MF_HC4 = 4;
   public static final int MF_BT4 = 20;
   private static final int[] presetToDictSize = new int[]{262144, 1048576, 2097152, 4194304, 4194304, 8388608, 8388608, 16777216, 33554432, 67108864};
   private static final int[] presetToDepthLimit = new int[]{4, 8, 24, 48};
   private int dictSize;
   private byte[] presetDict = null;
   private int lc;
   private int lp;
   private int pb;
   private int mode;
   private int niceLen;
   private int mf;
   private int depthLimit;

   public LZMA2Options() {
      try {
         this.setPreset(6);
      } catch (UnsupportedOptionsException var2) {
         assert false;

         throw new RuntimeException();
      }
   }

   public LZMA2Options(int i) throws UnsupportedOptionsException {
      this.setPreset(i);
   }

   public LZMA2Options(int i, int j, int k, int l, int m, int n, int o, int p) throws UnsupportedOptionsException {
      this.setDictSize(i);
      this.setLcLp(j, k);
      this.setPb(l);
      this.setMode(m);
      this.setNiceLen(n);
      this.setMatchFinder(o);
      this.setDepthLimit(p);
   }

   public void setPreset(int i) throws UnsupportedOptionsException {
      if (i >= 0 && i <= 9) {
         this.lc = 3;
         this.lp = 0;
         this.pb = 2;
         this.dictSize = presetToDictSize[i];
         if (i <= 3) {
            this.mode = 1;
            this.mf = 4;
            this.niceLen = i <= 1 ? 128 : 273;
            this.depthLimit = presetToDepthLimit[i];
         } else {
            this.mode = 2;
            this.mf = 20;
            this.niceLen = i == 4 ? 16 : (i == 5 ? 32 : 64);
            this.depthLimit = 0;
         }
      } else {
         throw new UnsupportedOptionsException("Unsupported preset: " + i);
      }
   }

   public void setDictSize(int i) throws UnsupportedOptionsException {
      if (i < 4096) {
         throw new UnsupportedOptionsException("LZMA2 dictionary size must be at least 4 KiB: " + i + " B");
      } else if (i > 805306368) {
         throw new UnsupportedOptionsException("LZMA2 dictionary size must not exceed 768 MiB: " + i + " B");
      } else {
         this.dictSize = i;
      }
   }

   public int getDictSize() {
      return this.dictSize;
   }

   public void setPresetDict(byte[] bs) {
      this.presetDict = bs;
   }

   public byte[] getPresetDict() {
      return this.presetDict;
   }

   public void setLcLp(int i, int j) throws UnsupportedOptionsException {
      if (i >= 0 && j >= 0 && i <= 4 && j <= 4 && i + j <= 4) {
         this.lc = i;
         this.lp = j;
      } else {
         throw new UnsupportedOptionsException("lc + lp must not exceed 4: " + i + " + " + j);
      }
   }

   public void setLc(int i) throws UnsupportedOptionsException {
      this.setLcLp(i, this.lp);
   }

   public void setLp(int i) throws UnsupportedOptionsException {
      this.setLcLp(this.lc, i);
   }

   public int getLc() {
      return this.lc;
   }

   public int getLp() {
      return this.lp;
   }

   public void setPb(int i) throws UnsupportedOptionsException {
      if (i >= 0 && i <= 4) {
         this.pb = i;
      } else {
         throw new UnsupportedOptionsException("pb must not exceed 4: " + i);
      }
   }

   public int getPb() {
      return this.pb;
   }

   public void setMode(int i) throws UnsupportedOptionsException {
      if (i >= 0 && i <= 2) {
         this.mode = i;
      } else {
         throw new UnsupportedOptionsException("Unsupported compression mode: " + i);
      }
   }

   public int getMode() {
      return this.mode;
   }

   public void setNiceLen(int i) throws UnsupportedOptionsException {
      if (i < 8) {
         throw new UnsupportedOptionsException("Minimum nice length of matches is 8 bytes: " + i);
      } else if (i > 273) {
         throw new UnsupportedOptionsException("Maximum nice length of matches is 273: " + i);
      } else {
         this.niceLen = i;
      }
   }

   public int getNiceLen() {
      return this.niceLen;
   }

   public void setMatchFinder(int i) throws UnsupportedOptionsException {
      if (i != 4 && i != 20) {
         throw new UnsupportedOptionsException("Unsupported match finder: " + i);
      } else {
         this.mf = i;
      }
   }

   public int getMatchFinder() {
      return this.mf;
   }

   public void setDepthLimit(int i) throws UnsupportedOptionsException {
      if (i < 0) {
         throw new UnsupportedOptionsException("Depth limit cannot be negative: " + i);
      } else {
         this.depthLimit = i;
      }
   }

   public int getDepthLimit() {
      return this.depthLimit;
   }

   @Override
   public int getEncoderMemoryUsage() {
      return this.mode == 0 ? UncompressedLZMA2OutputStream.getMemoryUsage() : LZMA2OutputStream.getMemoryUsage(this);
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return (FinishableOutputStream)(this.mode == 0
         ? new UncompressedLZMA2OutputStream(finishableOutputStream, arrayCache)
         : new LZMA2OutputStream(finishableOutputStream, this, arrayCache));
   }

   @Override
   public int getDecoderMemoryUsage() {
      int var1 = this.dictSize - 1;
      var1 |= var1 >>> 2;
      var1 |= var1 >>> 3;
      var1 |= var1 >>> 4;
      var1 |= var1 >>> 8;
      var1 |= var1 >>> 16;
      return LZMA2InputStream.getMemoryUsage(var1 + 1);
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) throws IOException {
      return new LZMA2InputStream(inputStream, this.dictSize, this.presetDict, arrayCache);
   }

   @Override
   FilterEncoder getFilterEncoder() {
      return new LZMA2Encoder(this);
   }

   @Override
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
