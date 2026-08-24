package cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.lossless;

import cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.LSBBitReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOException;

final class HuffmanTable {
   private static final int LEVEL1_BITS = 8;
   private static final int[] L_CODE_ORDER = new int[]{17, 18, 0, 1, 2, 3, 4, 5, 16, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
   private final int[] level1 = new int[256];
   private final List<int[]> level2 = new ArrayList<>();

   public HuffmanTable(LSBBitReader var1, int var2) throws IOException {
      boolean var3 = var1.readBit() == 1;
      if (var3) {
         int var4 = var1.readBit() + 1;
         boolean var5 = var1.readBit() == 1;
         short var6 = (short)var1.readBits(var5 ? 8 : 1);
         if (var4 == 2) {
            short var7 = (short)var1.readBits(8);

            for (byte var8 = 0; var8 < 256; var8 += 2) {
               this.level1[var8] = 65536 | var6;
               this.level1[var8 + 1] = 65536 | var7;
            }
         } else {
            Arrays.fill(this.level1, var6);
         }
      } else {
         int var9 = (int)(var1.readBits(4) + 4L);
         short[] var10 = new short[L_CODE_ORDER.length];
         int var11 = 0;

         for (int var12 = 0; var12 < var9; var12++) {
            short var14 = (short)var1.readBits(3);
            var10[L_CODE_ORDER[var12]] = var14;
            if (var14 > 0) {
               var11++;
            }
         }

         short[] var13 = readCodeLengths(var1, var10, var2, var11);
         this.buildFromLengths(var13);
      }
   }

   private HuffmanTable(short[] var1, int var2) {
      this.buildFromLengths(var1, var2);
   }

   private void buildFromLengths(short[] var1) {
      int var2 = 0;

      for (short var6 : var1) {
         if (var6 != 0) {
            var2++;
         }
      }

      this.buildFromLengths(var1, var2);
   }

   private void buildFromLengths(short[] var1, int var2) {
      int[] var3 = new int[var2];
      int var4 = 0;

      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var1[var5] != 0) {
            var3[var4++] = var1[var5] << 16 | var5;
         }
      }

      if (var2 == 1) {
         Arrays.fill(this.level1, var3[0] & 65535);
      }

      Arrays.sort(var3);
      int var15 = 0;
      int var6 = -1;
      int[] var7 = null;

      for (int var8 = 0; var8 < var3.length; var8++) {
         int var9 = var3[var8];
         int var10 = var9 >>> 16;
         if (var10 <= 8) {
            for (int var17 = var15; var17 < this.level1.length; var17 += 1 << var10) {
               this.level1[var17] = var9;
            }
         } else {
            if ((var15 & 0xFF) != var6) {
               int var11 = var10;
               int var12 = var8;

               for (int var13 = 1 << var10 - 8; var12 < var3.length && var13 > 0; var13--) {
                  for (int var14 = var3[var12] >>> 16; var14 != var11; var13 <<= 1) {
                     var11++;
                  }

                  var12++;
               }

               var12 = var11 - 8;
               var7 = new int[1 << var12];
               var6 = var15 & 0xFF;
               this.level2.add(var7);
               this.level1[var6] = 8 + var12 << 16 | this.level2.size() - 1;
            }

            for (int var16 = var15 >>> 8; var16 < var7.length; var16 += 1 << var10 - 8) {
               var7[var16] = var10 - 8 << 16 | var9 & 65535;
            }
         }

         var15 = this.nextCode(var15, var10);
      }
   }

   private int nextCode(int var1, int var2) {
      int var3 = ~var1 & (1 << var2) - 1;
      int var4 = Integer.highestOneBit(var3);
      return var1 & var4 - 1 | var4;
   }

   private static short[] readCodeLengths(LSBBitReader var0, short[] var1, int var2, int var3) throws IOException {
      HuffmanTable var4 = new HuffmanTable(var1, var3);
      int var5;
      if (var0.readBit() == 1) {
         int var6 = (int)(2L + 2L * var0.readBits(3));
         var5 = (int)(2L + var0.readBits(var6));
      } else {
         var5 = var2;
      }

      short[] var14 = new short[var2];
      short var7 = 8;

      for (int var8 = 0; var8 < var2 && var5 > 0; var5--) {
         short var9 = var4.readSymbol(var0);
         if (var9 < 16) {
            var14[var8] = var9;
            if (var9 != 0) {
               var7 = var9;
            }
         } else {
            short var10 = 0;
            byte var11;
            byte var12;
            switch (var9) {
               case 16:
                  var10 = var7;
                  var11 = 2;
                  var12 = 3;
                  break;
               case 17:
                  var11 = 3;
                  var12 = 3;
                  break;
               case 18:
                  var11 = 7;
                  var12 = 11;
                  break;
               default:
                  throw new IIOException("Huffman: Unreachable: Decoded Code Length > 18.");
            }

            int var13 = (int)(var0.readBits(var11) + var12);
            if (var8 + var13 > var2) {
               throw new IIOException(
                  String.format("Huffman: Code length repeat count overflows alphabet: Start index: %d, count: %d, alphabet size: %d", var8, var13, var2)
               );
            }

            Arrays.fill(var14, var8, var8 + var13, var10);
            var8 += var13 - 1;
         }

         var8++;
      }

      return var14;
   }

   public short readSymbol(LSBBitReader var1) throws IOException {
      int var2 = (int)var1.peekBits(8);
      int var3 = this.level1[var2];
      int var4 = var3 >>> 16;
      if (var4 > 8) {
         var1.readBits(8);
         int var5 = (int)var1.peekBits(var4 - 8);
         var3 = this.level2.get(var3 & 65535)[var5];
         var4 = var3 >>> 16;
      }

      var1.readBits(var4);
      return (short)(var3 & 65535);
   }
}
