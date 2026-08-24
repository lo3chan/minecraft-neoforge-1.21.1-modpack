package org.tukaani.xz.lzma;

import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lz.Matches;
import org.tukaani.xz.rangecoder.RangeEncoder;

final class LZMAEncoderNormal extends LZMAEncoder {
   private static final int OPTS = 4096;
   private static final int EXTRA_SIZE_BEFORE = 4096;
   private static final int EXTRA_SIZE_AFTER = 4096;
   private final Optimum[] opts = new Optimum[4096];
   private int optCur = 0;
   private int optEnd = 0;
   private Matches matches;
   private final int[] repLens = new int[4];
   private final State nextState = new State();

   static int getMemoryUsage(int i, int j, int k) {
      return LZEncoder.getMemoryUsage(i, Math.max(j, 4096), 4096, 273, k) + 256;
   }

   LZMAEncoderNormal(RangeEncoder rangeEncoder, int i, int j, int k, int l, int m, int n, int o, int p, ArrayCache arrayCache) {
      super(rangeEncoder, LZEncoder.getInstance(l, Math.max(m, 4096), 4096, n, 273, o, p, arrayCache), i, j, k, l, n);

      for (int var11 = 0; var11 < 4096; var11++) {
         this.opts[var11] = new Optimum();
      }
   }

   @Override
   public void reset() {
      this.optCur = 0;
      this.optEnd = 0;
      super.reset();
   }

   private int convertOpts() {
      this.optEnd = this.optCur;
      int var1 = this.opts[this.optCur].optPrev;

      do {
         Optimum var2 = this.opts[this.optCur];
         if (var2.prev1IsLiteral) {
            this.opts[var1].optPrev = this.optCur;
            this.opts[var1].backPrev = -1;
            this.optCur = var1--;
            if (var2.hasPrev2) {
               this.opts[var1].optPrev = var1 + 1;
               this.opts[var1].backPrev = var2.backPrev2;
               this.optCur = var1;
               var1 = var2.optPrev2;
            }
         }

         int var3 = this.opts[var1].optPrev;
         this.opts[var1].optPrev = this.optCur;
         this.optCur = var1;
         var1 = var3;
      } while (this.optCur > 0);

      this.optCur = this.opts[0].optPrev;
      this.back = this.opts[this.optCur].backPrev;
      return this.optCur;
   }

   @Override
   int getNextSymbol() {
      if (this.optCur < this.optEnd) {
         int var17 = this.opts[this.optCur].optPrev - this.optCur;
         this.optCur = this.opts[this.optCur].optPrev;
         this.back = this.opts[this.optCur].backPrev;
         return var17;
      } else {
         assert this.optCur == this.optEnd;

         this.optCur = 0;
         this.optEnd = 0;
         this.back = -1;
         if (this.readAhead == -1) {
            this.matches = this.getMatches();
         }

         int var1 = Math.min(this.lz.getAvail(), 273);
         if (var1 < 2) {
            return 1;
         } else {
            int var2 = 0;

            for (int var3 = 0; var3 < 4; var3++) {
               this.repLens[var3] = this.lz.getMatchLen(this.reps[var3], var1);
               if (this.repLens[var3] < 2) {
                  this.repLens[var3] = 0;
               } else if (this.repLens[var3] > this.repLens[var2]) {
                  var2 = var3;
               }
            }

            if (this.repLens[var2] >= this.niceLen) {
               this.back = var2;
               this.skip(this.repLens[var2] - 1);
               return this.repLens[var2];
            } else {
               int var18 = 0;
               int var4 = 0;
               if (this.matches.count > 0) {
                  var18 = this.matches.len[this.matches.count - 1];
                  var4 = this.matches.dist[this.matches.count - 1];
                  if (var18 >= this.niceLen) {
                     this.back = var4 + 4;
                     this.skip(var18 - 1);
                     return var18;
                  }
               }

               int var5 = this.lz.getByte(0);
               int var6 = this.lz.getByte(this.reps[0] + 1);
               if (var18 < 2 && var5 != var6 && this.repLens[var2] < 2) {
                  return 1;
               } else {
                  int var7 = this.lz.getPos();
                  int var8 = var7 & this.posMask;
                  int var9 = this.lz.getByte(1);
                  int var10 = this.literalEncoder.getPrice(var5, var6, var9, var7, this.state);
                  this.opts[1].set1(var10, 0, -1);
                  var9 = this.getAnyMatchPrice(this.state, var8);
                  var10 = this.getAnyRepPrice(var9, this.state);
                  if (var6 == var5) {
                     int var11 = this.getShortRepPrice(var10, this.state, var8);
                     if (var11 < this.opts[1].price) {
                        this.opts[1].set1(var11, 0, 0);
                     }
                  }

                  this.optEnd = Math.max(var18, this.repLens[var2]);
                  if (this.optEnd < 2) {
                     assert this.optEnd == 0 : this.optEnd;

                     this.back = this.opts[1].backPrev;
                     return 1;
                  } else {
                     this.updatePrices();
                     this.opts[0].state.set(this.state);
                     System.arraycopy(this.reps, 0, this.opts[0].reps, 0, 4);

                     for (int var25 = this.optEnd; var25 >= 2; var25--) {
                        this.opts[var25].reset();
                     }

                     for (int var26 = 0; var26 < 4; var26++) {
                        int var12 = this.repLens[var26];
                        if (var12 >= 2) {
                           int var13 = this.getLongRepPrice(var10, var26, this.state, var8);

                           while (true) {
                              int var14 = var13 + this.repLenEncoder.getPrice(var12, var8);
                              if (var14 < this.opts[var12].price) {
                                 this.opts[var12].set1(var14, 0, var26);
                              }

                              if (--var12 < 2) {
                                 break;
                              }
                           }
                        }
                     }

                     int var27 = Math.max(this.repLens[0] + 1, 2);
                     if (var27 <= var18) {
                        int var29 = this.getNormalMatchPrice(var9, this.state);
                        int var30 = 0;

                        while (var27 > this.matches.len[var30]) {
                           var30++;
                        }

                        while (true) {
                           int var31 = this.matches.dist[var30];
                           int var15 = this.getMatchAndLenPrice(var29, var31, var27, var8);
                           if (var15 < this.opts[var27].price) {
                              this.opts[var27].set1(var15, 0, var31 + 4);
                           }

                           if (var27 == this.matches.len[var30]) {
                              if (++var30 == this.matches.count) {
                                 break;
                              }
                           }

                           var27++;
                        }
                     }

                     var1 = Math.min(this.lz.getAvail(), 4095);

                     while (++this.optCur < this.optEnd) {
                        this.matches = this.getMatches();
                        if (this.matches.count > 0 && this.matches.len[this.matches.count - 1] >= this.niceLen) {
                           break;
                        }

                        var1--;
                        var8 = ++var7 & this.posMask;
                        this.updateOptStateAndReps();
                        var9 = this.opts[this.optCur].price + this.getAnyMatchPrice(this.opts[this.optCur].state, var8);
                        var10 = this.getAnyRepPrice(var9, this.opts[this.optCur].state);
                        this.calc1BytePrices(var7, var8, var1, var10);
                        if (var1 >= 2) {
                           var27 = this.calcLongRepPrices(var7, var8, var1, var10);
                           if (this.matches.count > 0) {
                              this.calcNormalMatchPrices(var7, var8, var1, var9, var27);
                           }
                        }
                     }

                     return this.convertOpts();
                  }
               }
            }
         }
      }
   }

   private void updateOptStateAndReps() {
      int var1 = this.opts[this.optCur].optPrev;

      assert var1 < this.optCur;

      if (this.opts[this.optCur].prev1IsLiteral) {
         var1--;
         if (this.opts[this.optCur].hasPrev2) {
            this.opts[this.optCur].state.set(this.opts[this.opts[this.optCur].optPrev2].state);
            if (this.opts[this.optCur].backPrev2 < 4) {
               this.opts[this.optCur].state.updateLongRep();
            } else {
               this.opts[this.optCur].state.updateMatch();
            }
         } else {
            this.opts[this.optCur].state.set(this.opts[var1].state);
         }

         this.opts[this.optCur].state.updateLiteral();
      } else {
         this.opts[this.optCur].state.set(this.opts[var1].state);
      }

      if (var1 == this.optCur - 1) {
         assert this.opts[this.optCur].backPrev == 0 || this.opts[this.optCur].backPrev == -1;

         if (this.opts[this.optCur].backPrev == 0) {
            this.opts[this.optCur].state.updateShortRep();
         } else {
            this.opts[this.optCur].state.updateLiteral();
         }

         System.arraycopy(this.opts[var1].reps, 0, this.opts[this.optCur].reps, 0, 4);
      } else {
         int var2;
         if (this.opts[this.optCur].prev1IsLiteral && this.opts[this.optCur].hasPrev2) {
            var1 = this.opts[this.optCur].optPrev2;
            var2 = this.opts[this.optCur].backPrev2;
            this.opts[this.optCur].state.updateLongRep();
         } else {
            var2 = this.opts[this.optCur].backPrev;
            if (var2 < 4) {
               this.opts[this.optCur].state.updateLongRep();
            } else {
               this.opts[this.optCur].state.updateMatch();
            }
         }

         if (var2 < 4) {
            this.opts[this.optCur].reps[0] = this.opts[var1].reps[var2];

            int var3;
            for (var3 = 1; var3 <= var2; var3++) {
               this.opts[this.optCur].reps[var3] = this.opts[var1].reps[var3 - 1];
            }

            while (var3 < 4) {
               this.opts[this.optCur].reps[var3] = this.opts[var1].reps[var3];
               var3++;
            }
         } else {
            this.opts[this.optCur].reps[0] = var2 - 4;
            System.arraycopy(this.opts[var1].reps, 0, this.opts[this.optCur].reps, 1, 3);
         }
      }
   }

   private void calc1BytePrices(int i, int j, int k, int l) {
      boolean var5 = false;
      int var6 = this.lz.getByte(0);
      int var7 = this.lz.getByte(this.opts[this.optCur].reps[0] + 1);
      int var8 = this.opts[this.optCur].price + this.literalEncoder.getPrice(var6, var7, this.lz.getByte(1), i, this.opts[this.optCur].state);
      if (var8 < this.opts[this.optCur + 1].price) {
         this.opts[this.optCur + 1].set1(var8, this.optCur, -1);
         var5 = true;
      }

      if (var7 == var6 && (this.opts[this.optCur + 1].optPrev == this.optCur || this.opts[this.optCur + 1].backPrev != 0)) {
         int var9 = this.getShortRepPrice(l, this.opts[this.optCur].state, j);
         if (var9 <= this.opts[this.optCur + 1].price) {
            this.opts[this.optCur + 1].set1(var9, this.optCur, 0);
            var5 = true;
         }
      }

      if (!var5 && var7 != var6 && k > 2) {
         int var14 = Math.min(this.niceLen, k - 1);
         int var10 = this.lz.getMatchLen(1, this.opts[this.optCur].reps[0], var14);
         if (var10 >= 2) {
            this.nextState.set(this.opts[this.optCur].state);
            this.nextState.updateLiteral();
            int var11 = i + 1 & this.posMask;
            int var12 = var8 + this.getLongRepAndLenPrice(0, var10, this.nextState, var11);
            int var13 = this.optCur + 1 + var10;

            while (this.optEnd < var13) {
               this.opts[++this.optEnd].reset();
            }

            if (var12 < this.opts[var13].price) {
               this.opts[var13].set2(var12, this.optCur, 0);
            }
         }
      }
   }

   private int calcLongRepPrices(int i, int j, int k, int l) {
      int var5 = 2;
      int var6 = Math.min(k, this.niceLen);

      for (int var7 = 0; var7 < 4; var7++) {
         int var8 = this.lz.getMatchLen(this.opts[this.optCur].reps[var7], var6);
         if (var8 >= 2) {
            while (this.optEnd < this.optCur + var8) {
               this.opts[++this.optEnd].reset();
            }

            int var9 = this.getLongRepPrice(l, var7, this.opts[this.optCur].state, j);

            for (int var10 = var8; var10 >= 2; var10--) {
               int var11 = var9 + this.repLenEncoder.getPrice(var10, j);
               if (var11 < this.opts[this.optCur + var10].price) {
                  this.opts[this.optCur + var10].set1(var11, this.optCur, var7);
               }
            }

            if (var7 == 0) {
               var5 = var8 + 1;
            }

            int var18 = Math.min(this.niceLen, k - var8 - 1);
            int var19 = this.lz.getMatchLen(var8 + 1, this.opts[this.optCur].reps[var7], var18);
            if (var19 >= 2) {
               int var12 = var9 + this.repLenEncoder.getPrice(var8, j);
               this.nextState.set(this.opts[this.optCur].state);
               this.nextState.updateLongRep();
               int var13 = this.lz.getByte(var8, 0);
               int var14 = this.lz.getByte(0);
               int var15 = this.lz.getByte(var8, 1);
               var12 += this.literalEncoder.getPrice(var13, var14, var15, i + var8, this.nextState);
               this.nextState.updateLiteral();
               int var16 = i + var8 + 1 & this.posMask;
               var12 += this.getLongRepAndLenPrice(0, var19, this.nextState, var16);
               int var17 = this.optCur + var8 + 1 + var19;

               while (this.optEnd < var17) {
                  this.opts[++this.optEnd].reset();
               }

               if (var12 < this.opts[var17].price) {
                  this.opts[var17].set3(var12, this.optCur, var7, var8, 0);
               }
            }
         }
      }

      return var5;
   }

   private void calcNormalMatchPrices(int i, int j, int k, int l, int m) {
      if (this.matches.len[this.matches.count - 1] > k) {
         this.matches.count = 0;

         while (this.matches.len[this.matches.count] < k) {
            this.matches.count++;
         }

         this.matches.len[this.matches.count++] = k;
      }

      if (this.matches.len[this.matches.count - 1] >= m) {
         while (this.optEnd < this.optCur + this.matches.len[this.matches.count - 1]) {
            this.opts[++this.optEnd].reset();
         }

         int var6 = this.getNormalMatchPrice(l, this.opts[this.optCur].state);
         int var7 = 0;

         while (m > this.matches.len[var7]) {
            var7++;
         }

         int var8 = m;

         while (true) {
            int var9 = this.matches.dist[var7];
            int var10 = this.getMatchAndLenPrice(var6, var9, var8, j);
            if (var10 < this.opts[this.optCur + var8].price) {
               this.opts[this.optCur + var8].set1(var10, this.optCur, var9 + 4);
            }

            if (var8 == this.matches.len[var7]) {
               int var11 = Math.min(this.niceLen, k - var8 - 1);
               int var12 = this.lz.getMatchLen(var8 + 1, var9, var11);
               if (var12 >= 2) {
                  this.nextState.set(this.opts[this.optCur].state);
                  this.nextState.updateMatch();
                  int var13 = this.lz.getByte(var8, 0);
                  int var14 = this.lz.getByte(0);
                  int var15 = this.lz.getByte(var8, 1);
                  int var16 = var10 + this.literalEncoder.getPrice(var13, var14, var15, i + var8, this.nextState);
                  this.nextState.updateLiteral();
                  int var17 = i + var8 + 1 & this.posMask;
                  var16 += this.getLongRepAndLenPrice(0, var12, this.nextState, var17);
                  int var18 = this.optCur + var8 + 1 + var12;

                  while (this.optEnd < var18) {
                     this.opts[++this.optEnd].reset();
                  }

                  if (var16 < this.opts[var18].price) {
                     this.opts[var18].set3(var16, this.optCur, var9 + 4, var8, 0);
                  }
               }

               if (++var7 == this.matches.count) {
                  return;
               }
            }

            var8++;
         }
      }
   }
}
