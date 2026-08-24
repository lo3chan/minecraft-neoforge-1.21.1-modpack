package cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.vp8;

import java.io.IOException;

final class MacroBlock {
   private int filterLevel;
   private final boolean keepDebugInfo;
   private int segmentId;
   private int skipCoeff;
   private boolean skipInnerLoopFilter;
   final SubBlock[][] uSubBlocks;
   private int uVFilterLevel;
   private int uvMode;
   final SubBlock[][] vSubBlocks;
   private final int x;
   private final int y;
   final SubBlock y2SubBlock;
   private int yMode;
   final SubBlock[][] ySubBlocks;

   MacroBlock(int var1, int var2, boolean var3) {
      this.x = var1 - 1;
      this.y = var2 - 1;
      this.keepDebugInfo = var3;
      this.ySubBlocks = new SubBlock[4][4];
      this.uSubBlocks = new SubBlock[2][2];
      this.vSubBlocks = new SubBlock[2][2];

      for (int var6 = 0; var6 < 4; var6++) {
         for (int var7 = 0; var7 < 4; var7++) {
            SubBlock var5 = null;
            SubBlock var4 = null;
            if (var7 > 0) {
               var5 = this.ySubBlocks[var7 - 1][var6];
            }

            if (var6 > 0) {
               var4 = this.ySubBlocks[var7][var6 - 1];
            }

            this.ySubBlocks[var7][var6] = new SubBlock(this, var4, var5, SubBlock.Plane.Y1);
         }
      }

      for (int var12 = 0; var12 < 2; var12++) {
         for (int var14 = 0; var14 < 2; var14++) {
            SubBlock var10 = null;
            SubBlock var8 = null;
            if (var14 > 0) {
               var10 = this.uSubBlocks[var14 - 1][var12];
            }

            if (var12 > 0) {
               var8 = this.uSubBlocks[var14][var12 - 1];
            }

            this.uSubBlocks[var14][var12] = new SubBlock(this, var8, var10, SubBlock.Plane.U);
         }
      }

      for (int var13 = 0; var13 < 2; var13++) {
         for (int var15 = 0; var15 < 2; var15++) {
            SubBlock var11 = null;
            SubBlock var9 = null;
            if (var15 > 0) {
               var11 = this.vSubBlocks[var15 - 1][var13];
            }

            if (var13 > 0) {
               var9 = this.vSubBlocks[var15][var13 - 1];
            }

            this.vSubBlocks[var15][var13] = new SubBlock(this, var9, var11, SubBlock.Plane.V);
         }
      }

      this.y2SubBlock = new SubBlock(this, null, null, SubBlock.Plane.Y2);
   }

   public void decodeMacroBlock(VP8Frame var1) throws IOException {
      if (this.getSkipCoeff() > 0) {
         if (this.getYMode() != 4) {
            this.skipInnerLoopFilter = true;
         }
      } else {
         this.decodeMacroBlockTokens(var1, this.getYMode() != 4);
      }
   }

   private void decodeMacroBlockTokens(VP8Frame var1, boolean var2) throws IOException {
      if (var2) {
         this.skipInnerLoopFilter = this.decodePlaneTokens(var1, 1, SubBlock.Plane.Y2, false);
      }

      this.skipInnerLoopFilter = this.skipInnerLoopFilter | this.decodePlaneTokens(var1, 4, SubBlock.Plane.Y1, var2);
      this.skipInnerLoopFilter = this.skipInnerLoopFilter | this.decodePlaneTokens(var1, 2, SubBlock.Plane.U, false);
      this.skipInnerLoopFilter = this.skipInnerLoopFilter | this.decodePlaneTokens(var1, 2, SubBlock.Plane.V, false);
      this.skipInnerLoopFilter = !this.skipInnerLoopFilter;
   }

   private boolean decodePlaneTokens(VP8Frame var1, int var2, SubBlock.Plane var3, boolean var4) throws IOException {
      MacroBlock var5 = this;
      boolean var6 = false;

      for (int var7 = 0; var7 < var2; var7++) {
         for (int var8 = 0; var8 < var2; var8++) {
            byte var9 = 0;
            byte var10 = 0;
            int var11 = 0;
            SubBlock var12 = var5.getSubBlock(var3, var8, var7);
            SubBlock var13 = var1.getLeftSubBlock(var12, var3);
            SubBlock var14 = var1.getAboveSubBlock(var12, var3);
            if (var13.hasNoZeroToken()) {
               var9 = 1;
            }

            var11 += var9;
            if (var14.hasNoZeroToken()) {
               var10 = 1;
            }

            var11 += var10;
            var12.decodeSubBlock(var1.getTokenBoolDecoder(), var1.getCoefProbs(), var11, SubBlock.planeToType(var3, var4), var4);
            var6 |= var12.hasNoZeroToken();
         }
      }

      return var6;
   }

   public void dequantMacroBlock(VP8Frame var1) {
      MacroBlock var2 = this;
      if (this.getYMode() != 4) {
         SubBlock var3 = this.getY2SubBlock();
         int var4 = var1.getSegmentQuants().getSegQuants()[this.getSegmentId()].getY2ac_delta_q();
         int var5 = var1.getSegmentQuants().getSegQuants()[this.getSegmentId()].getY2dc();
         int[] var6 = new int[16];
         var6[0] = var3.getTokens()[0] * var5;

         for (int var7 = 1; var7 < 16; var7++) {
            var6[var7] = var3.getTokens()[var7] * var4;
         }

         var3.setDiff(IDCT.iwalsh4x4(var6));

         for (int var19 = 0; var19 < 4; var19++) {
            for (int var8 = 0; var8 < 4; var8++) {
               SubBlock var9 = var2.getYSubBlock(var8, var19);
               var9.dequantSubBlock(var1, var3.getDiff()[var8][var19]);
            }
         }

         var2.predictY(var1);
         var2.predictUV(var1);

         for (int var20 = 0; var20 < 2; var20++) {
            for (int var21 = 0; var21 < 2; var21++) {
               SubBlock var22 = var2.getUSubBlock(var21, var20);
               var22.dequantSubBlock(var1, null);
               var22 = var2.getVSubBlock(var20, var21);
               var22.dequantSubBlock(var1, null);
            }
         }

         var2.recon_mb();
      } else {
         for (int var10 = 0; var10 < 4; var10++) {
            for (int var13 = 0; var13 < 4; var13++) {
               SubBlock var16 = var2.getYSubBlock(var13, var10);
               var16.dequantSubBlock(var1, null);
               var16.predict(var1);
               var16.reconstruct();
            }
         }

         var2.predictUV(var1);

         for (int var11 = 0; var11 < 2; var11++) {
            for (int var14 = 0; var14 < 2; var14++) {
               SubBlock var17 = var2.getUSubBlock(var14, var11);
               var17.dequantSubBlock(var1, null);
               var17.reconstruct();
            }
         }

         for (int var12 = 0; var12 < 2; var12++) {
            for (int var15 = 0; var15 < 2; var15++) {
               SubBlock var18 = var2.getVSubBlock(var15, var12);
               var18.dequantSubBlock(var1, null);
               var18.reconstruct();
            }
         }
      }
   }

   public void drawDebug() {
      for (int var1 = 0; var1 < 4; var1++) {
         for (int var2 = 0; var2 < 4; var2++) {
            SubBlock var3 = this.ySubBlocks[var2][0];
            var3.drawDebugH();
            var3 = this.ySubBlocks[0][var1];
            var3.drawDebugV();
         }
      }
   }

   public String getDebugString() {
      String var1 = " YMode: " + Globals.getModeAsString(this.yMode);
      var1 = var1 + "\n UVMode: " + Globals.getModeAsString(this.uvMode);
      var1 = var1 + "\n SegmentID: " + this.segmentId;
      var1 = var1 + "\n Filter Level: " + this.filterLevel;
      var1 = var1 + "\n UV Filter Level: " + this.uVFilterLevel;
      return var1 + "\n Skip Coeff: " + this.skipCoeff;
   }

   public int getFilterLevel() {
      return this.filterLevel;
   }

   public SubBlock getBottomSubBlock(int var1, SubBlock.Plane var2) {
      switch (var2) {
         case Y1:
            return this.ySubBlocks[var1][3];
         case U:
            return this.uSubBlocks[var1][1];
         case V:
            return this.vSubBlocks[var1][1];
         case Y2:
            return this.y2SubBlock;
         default:
            throw new IllegalArgumentException("Bad plane: " + var2);
      }
   }

   public SubBlock getLeftSubBlock(int var1, SubBlock.Plane var2) {
      switch (var2) {
         case Y1:
            return this.ySubBlocks[0][var1];
         case U:
            return this.uSubBlocks[0][var1];
         case V:
            return this.vSubBlocks[0][var1];
         case Y2:
            return this.y2SubBlock;
         default:
            throw new IllegalArgumentException("Bad plane: " + var2);
      }
   }

   public SubBlock getRightSubBlock(int var1, SubBlock.Plane var2) {
      switch (var2) {
         case Y1:
            return this.ySubBlocks[3][var1];
         case U:
            return this.uSubBlocks[1][var1];
         case V:
            return this.vSubBlocks[1][var1];
         case Y2:
            return this.y2SubBlock;
         default:
            throw new IllegalArgumentException("Bad plane: " + var2);
      }
   }

   public int getSkipCoeff() {
      return this.skipCoeff;
   }

   public SubBlock getSubBlock(SubBlock.Plane var1, int var2, int var3) {
      switch (var1) {
         case Y1:
            return this.getYSubBlock(var2, var3);
         case U:
            return this.getUSubBlock(var2, var3);
         case V:
            return this.getVSubBlock(var2, var3);
         case Y2:
            return this.getY2SubBlock();
         default:
            throw new IllegalArgumentException("Bad plane: " + var1);
      }
   }

   public int getSubblockX(SubBlock var1) {
      if (var1.getPlane() == SubBlock.Plane.Y1) {
         for (int var2 = 0; var2 < 4; var2++) {
            for (int var3 = 0; var3 < 4; var3++) {
               if (this.ySubBlocks[var3][var2] == var1) {
                  return var3;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.U) {
         for (int var4 = 0; var4 < 2; var4++) {
            for (int var6 = 0; var6 < 2; var6++) {
               if (this.uSubBlocks[var6][var4] == var1) {
                  return var6;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.V) {
         for (int var5 = 0; var5 < 2; var5++) {
            for (int var7 = 0; var7 < 2; var7++) {
               if (this.vSubBlocks[var7][var5] == var1) {
                  return var7;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.Y2) {
         return 0;
      }

      return -100;
   }

   public int getSubblockY(SubBlock var1) {
      if (var1.getPlane() == SubBlock.Plane.Y1) {
         for (int var2 = 0; var2 < 4; var2++) {
            for (int var3 = 0; var3 < 4; var3++) {
               if (this.ySubBlocks[var3][var2] == var1) {
                  return var2;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.U) {
         for (int var4 = 0; var4 < 2; var4++) {
            for (int var6 = 0; var6 < 2; var6++) {
               if (this.uSubBlocks[var6][var4] == var1) {
                  return var4;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.V) {
         for (int var5 = 0; var5 < 2; var5++) {
            for (int var7 = 0; var7 < 2; var7++) {
               if (this.vSubBlocks[var7][var5] == var1) {
                  return var5;
               }
            }
         }
      } else if (var1.getPlane() == SubBlock.Plane.Y2) {
         return 0;
      }

      return -100;
   }

   public SubBlock getUSubBlock(int var1, int var2) {
      return this.uSubBlocks[var1][var2];
   }

   public int getUVFilterLevel() {
      return this.uVFilterLevel;
   }

   public int getUvMode() {
      return this.uvMode;
   }

   public SubBlock getVSubBlock(int var1, int var2) {
      return this.vSubBlocks[var1][var2];
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public SubBlock getY2SubBlock() {
      return this.y2SubBlock;
   }

   public int getYMode() {
      return this.yMode;
   }

   public SubBlock getYSubBlock(int var1, int var2) {
      return this.ySubBlocks[var1][var2];
   }

   public boolean isKeepDebugInfo() {
      return this.keepDebugInfo;
   }

   public boolean isSkip_inner_lf() {
      return this.skipInnerLoopFilter;
   }

   public void predictUV(VP8Frame var1) {
      MacroBlock var2 = var1.getMacroBlock(this.x, this.y - 1);
      MacroBlock var3 = var1.getMacroBlock(this.x - 1, this.y);
      switch (this.uvMode) {
         case 0:
            boolean var4 = false;
            boolean var5 = false;
            int var6 = 0;
            int var7 = 0;
            if (this.x > 0) {
               var5 = true;
            }

            if (this.y > 0) {
               var4 = true;
            }

            int var8;
            int var9;
            if (!var4 && !var5) {
               var8 = 128;
               var9 = 128;
            } else {
               if (var4) {
                  for (int var10 = 0; var10 < 2; var10++) {
                     SubBlock var11 = var2.getUSubBlock(var10, 1);
                     SubBlock var34 = var2.getVSubBlock(var10, 1);

                     for (int var40 = 0; var40 < 4; var40++) {
                        var6 += var11.getDest()[var40][3];
                        var7 += var34.getDest()[var40][3];
                     }
                  }
               }

               if (var5) {
                  for (int var27 = 0; var27 < 2; var27++) {
                     SubBlock var30 = var3.getUSubBlock(1, var27);
                     SubBlock var35 = var3.getVSubBlock(1, var27);

                     for (int var41 = 0; var41 < 4; var41++) {
                        var6 += var30.getDest()[3][var41];
                        var7 += var35.getDest()[3][var41];
                     }
                  }
               }

               int var28 = 2;
               if (var4) {
                  var28++;
               }

               if (var5) {
                  var28++;
               }

               var8 = var6 + (1 << var28 - 1) >> var28;
               var9 = var7 + (1 << var28 - 1) >> var28;
            }

            int[][] var29 = new int[4][4];

            for (int var31 = 0; var31 < 4; var31++) {
               for (int var36 = 0; var36 < 4; var36++) {
                  var29[var36][var31] = var8;
               }
            }

            int[][] var32 = new int[4][4];

            for (int var37 = 0; var37 < 4; var37++) {
               for (int var42 = 0; var42 < 4; var42++) {
                  var32[var42][var37] = var9;
               }
            }

            for (int var38 = 0; var38 < 2; var38++) {
               for (int var43 = 0; var43 < 2; var43++) {
                  SubBlock var47 = this.uSubBlocks[var43][var38];
                  SubBlock var50 = this.vSubBlocks[var43][var38];
                  var47.setPredict(var29);
                  var50.setPredict(var32);
               }
            }
            break;
         case 1:
            SubBlock[] var33 = new SubBlock[2];
            SubBlock[] var39 = new SubBlock[2];

            for (int var45 = 0; var45 < 2; var45++) {
               var33[var45] = var2.getUSubBlock(var45, 1);
               var39[var45] = var2.getVSubBlock(var45, 1);
            }

            for (int var46 = 0; var46 < 2; var46++) {
               for (int var49 = 0; var49 < 2; var49++) {
                  SubBlock var53 = this.uSubBlocks[var46][var49];
                  SubBlock var55 = this.vSubBlocks[var46][var49];
                  int[][] var57 = new int[4][4];
                  int[][] var59 = new int[4][4];

                  for (int var61 = 0; var61 < 4; var61++) {
                     for (int var64 = 0; var64 < 4; var64++) {
                        var57[var61][var64] = var33[var46].getMacroBlockPredict(1)[var61][3];
                        var59[var61][var64] = var39[var46].getMacroBlockPredict(1)[var61][3];
                     }
                  }

                  var53.setPredict(var57);
                  var55.setPredict(var59);
               }
            }
            break;
         case 2:
            SubBlock[] var44 = new SubBlock[2];
            SubBlock[] var48 = new SubBlock[2];

            for (int var51 = 0; var51 < 2; var51++) {
               var44[var51] = var3.getUSubBlock(1, var51);
               var48[var51] = var3.getVSubBlock(1, var51);
            }

            for (int var52 = 0; var52 < 2; var52++) {
               for (int var54 = 0; var54 < 2; var54++) {
                  SubBlock var56 = this.uSubBlocks[var54][var52];
                  SubBlock var58 = this.vSubBlocks[var54][var52];
                  int[][] var60 = new int[4][4];
                  int[][] var63 = new int[4][4];

                  for (int var65 = 0; var65 < 4; var65++) {
                     for (int var66 = 0; var66 < 4; var66++) {
                        var60[var66][var65] = var44[var52].getMacroBlockPredict(2)[3][var65];
                        var63[var66][var65] = var48[var52].getMacroBlockPredict(2)[3][var65];
                     }
                  }

                  var56.setPredict(var60);
                  var58.setPredict(var63);
               }
            }
            break;
         case 3:
            MacroBlock var16 = var1.getMacroBlock(this.x - 1, this.y - 1);
            SubBlock var17 = var16.getUSubBlock(1, 1);
            int var18 = var17.getDest()[3][3];
            SubBlock var19 = var16.getVSubBlock(1, 1);
            int var20 = var19.getDest()[3][3];
            SubBlock[] var12 = new SubBlock[2];
            SubBlock[] var14 = new SubBlock[2];
            SubBlock[] var13 = new SubBlock[2];
            SubBlock[] var15 = new SubBlock[2];

            for (int var21 = 0; var21 < 2; var21++) {
               var12[var21] = var2.getUSubBlock(var21, 1);
               var14[var21] = var3.getUSubBlock(1, var21);
               var13[var21] = var2.getVSubBlock(var21, 1);
               var15[var21] = var3.getVSubBlock(1, var21);
            }

            for (int var62 = 0; var62 < 2; var62++) {
               for (int var22 = 0; var22 < 4; var22++) {
                  for (int var23 = 0; var23 < 2; var23++) {
                     for (int var24 = 0; var24 < 4; var24++) {
                        int var25 = var14[var62].getDest()[3][var22] + var12[var23].getDest()[var24][3] - var18;
                        var25 = Globals.clamp(var25, 255);
                        this.uSubBlocks[var23][var62].setPixel(var24, var22, var25);
                        int var26 = var15[var62].getDest()[3][var22] + var13[var23].getDest()[var24][3] - var20;
                        var26 = Globals.clamp(var26, 255);
                        this.vSubBlocks[var23][var62].setPixel(var24, var22, var26);
                     }
                  }
               }
            }
            break;
         default:
            throw new AssertionError("TODO predict_mb_uv: " + this.yMode);
      }
   }

   public void predictY(VP8Frame var1) {
      MacroBlock var2 = var1.getMacroBlock(this.x, this.y - 1);
      MacroBlock var3 = var1.getMacroBlock(this.x - 1, this.y);
      switch (this.yMode) {
         case 0:
            boolean var4 = false;
            boolean var5 = false;
            int var6 = 0;
            if (this.x > 0) {
               var5 = true;
            }

            if (this.y > 0) {
               var4 = true;
            }

            int var7;
            if (!var4 && !var5) {
               var7 = 128;
            } else {
               if (var4) {
                  for (int var8 = 0; var8 < 4; var8++) {
                     SubBlock var23 = var2.getYSubBlock(var8, 3);

                     for (int var30 = 0; var30 < 4; var30++) {
                        var6 += var23.getDest()[var30][3];
                     }
                  }
               }

               if (var5) {
                  for (int var19 = 0; var19 < 4; var19++) {
                     SubBlock var24 = var3.getYSubBlock(3, var19);

                     for (int var31 = 0; var31 < 4; var31++) {
                        var6 += var24.getDest()[3][var31];
                     }
                  }
               }

               int var20 = 3;
               if (var4) {
                  var20++;
               }

               if (var5) {
                  var20++;
               }

               var7 = var6 + (1 << var20 - 1) >> var20;
            }

            int[][] var21 = new int[4][4];

            for (int var25 = 0; var25 < 4; var25++) {
               for (int var32 = 0; var32 < 4; var32++) {
                  var21[var32][var25] = var7;
               }
            }

            for (int var26 = 0; var26 < 4; var26++) {
               for (int var33 = 0; var33 < 4; var33++) {
                  SubBlock var37 = this.ySubBlocks[var33][var26];
                  var37.setPredict(var21);
               }
            }
            break;
         case 1:
            SubBlock[] var22 = new SubBlock[4];

            for (int var28 = 0; var28 < 4; var28++) {
               var22[var28] = var2.getYSubBlock(var28, 3);
            }

            for (int var29 = 0; var29 < 4; var29++) {
               for (int var36 = 0; var36 < 4; var36++) {
                  SubBlock var39 = this.ySubBlocks[var36][var29];
                  int[][] var41 = new int[4][4];

                  for (int var45 = 0; var45 < 4; var45++) {
                     for (int var47 = 0; var47 < 4; var47++) {
                        var41[var47][var45] = var22[var36].getPredict(2, false)[var47][3];
                     }
                  }

                  var39.setPredict(var41);
               }
            }
            break;
         case 2:
            SubBlock[] var27 = new SubBlock[4];

            for (int var34 = 0; var34 < 4; var34++) {
               var27[var34] = var3.getYSubBlock(3, var34);
            }

            for (int var35 = 0; var35 < 4; var35++) {
               for (int var38 = 0; var38 < 4; var38++) {
                  SubBlock var40 = this.ySubBlocks[var38][var35];
                  int[][] var44 = new int[4][4];

                  for (int var46 = 0; var46 < 4; var46++) {
                     for (int var48 = 0; var48 < 4; var48++) {
                        var44[var48][var46] = var27[var35].getPredict(0, true)[3][var46];
                     }
                  }

                  var40.setPredict(var44);
               }
            }
            break;
         case 3:
            MacroBlock var11 = var1.getMacroBlock(this.x - 1, this.y - 1);
            SubBlock var12 = var11.getYSubBlock(3, 3);
            int var13 = var12.getDest()[3][3];
            SubBlock[] var9 = new SubBlock[4];
            SubBlock[] var10 = new SubBlock[4];

            for (int var14 = 0; var14 < 4; var14++) {
               var9[var14] = var2.getYSubBlock(var14, 3);
            }

            for (int var42 = 0; var42 < 4; var42++) {
               var10[var42] = var3.getYSubBlock(3, var42);
            }

            for (int var43 = 0; var43 < 4; var43++) {
               for (int var15 = 0; var15 < 4; var15++) {
                  for (int var16 = 0; var16 < 4; var16++) {
                     for (int var17 = 0; var17 < 4; var17++) {
                        int var18 = var10[var43].getDest()[3][var15] + var9[var16].getDest()[var17][3] - var13;
                        this.ySubBlocks[var16][var43].setPixel(var17, var15, Globals.clamp(var18, 255));
                     }
                  }
               }
            }
            break;
         default:
            System.out.println("TODO predict_mb_y: " + this.yMode);
            System.exit(0);
      }
   }

   public void recon_mb() {
      for (int var1 = 0; var1 < 4; var1++) {
         for (int var2 = 0; var2 < 4; var2++) {
            SubBlock var3 = this.ySubBlocks[var2][var1];
            var3.reconstruct();
         }
      }

      for (int var4 = 0; var4 < 2; var4++) {
         for (int var6 = 0; var6 < 2; var6++) {
            SubBlock var8 = this.uSubBlocks[var6][var4];
            var8.reconstruct();
         }
      }

      for (int var5 = 0; var5 < 2; var5++) {
         for (int var7 = 0; var7 < 2; var7++) {
            SubBlock var9 = this.vSubBlocks[var7][var5];
            var9.reconstruct();
         }
      }
   }

   public void setFilterLevel(int var1) {
      this.filterLevel = var1;
   }

   public void setSegmentId(int var1) {
      this.segmentId = var1;
   }

   public void setSkipCoeff(int var1) {
      this.skipCoeff = var1;
   }

   public void setUVFilterLevel(int var1) {
      this.uVFilterLevel = var1;
   }

   public void setUvMode(int var1) {
      this.uvMode = var1;
   }

   public void setYMode(int var1) {
      this.yMode = var1;
   }

   @Override
   public String toString() {
      return "x: " + this.x + "y: " + this.y;
   }

   public int getSegmentId() {
      return this.segmentId;
   }
}
