package cc.cosmetica.include.twelvemonkeys.imageio.plugins.webp.vp8;

import cc.cosmetica.include.twelvemonkeys.imageio.color.YCbCrConverter;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

public final class VP8Frame {
   private static final int BLOCK_TYPES = 4;
   private static final int COEF_BANDS = 8;
   private static final int MAX_ENTROPY_TOKENS = 12;
   private static final int MAX_MODE_LF_DELTAS = 4;
   private static final int MAX_REF_LF_DELTAS = 4;
   private static final int PREV_COEF_CONTEXTS = 3;
   private IIOReadProgressListener listener = null;
   private final int[][][][] coefProbs;
   private int filterLevel;
   private final ImageInputStream frame;
   private final boolean debug;
   private int frameType;
   private int height;
   private int macroBlockCols;
   private int macroBlockNoCoeffSkip;
   private int macroBlockRows;
   private MacroBlock[][] macroBlocks;
   private int macroBlockSegementAbsoluteDelta;
   private int[] macroBlockSegmentTreeProbs;
   private final int[] modeLoopFilterDeltas = new int[4];
   private int modeRefLoopFilterDeltaEnabled;
   private int modeRefLoopFilterDeltaUpdate;
   private int multiTokenPartition = 0;
   private long offset;
   private final int[] refLoopFilterDeltas = new int[4];
   private int refreshEntropyProbs;
   private int refreshLastFrame;
   private int segmentationIsEnabled;
   private SegmentQuants segmentQuants;
   private int sharpnessLevel;
   private boolean simpleFilter;
   private BoolDecoder tokenBoolDecoder;
   private final List<BoolDecoder> tokenBoolDecoders;
   private int updateMacroBlockSegmentationMap;
   private int updateMacroBlockSegmentatonData;
   private int width;
   private final byte[] yuv = new byte[3];
   private final byte[] rgb = new byte[4];

   public VP8Frame(ImageInputStream var1, boolean var2) throws IOException {
      this.frame = var1;
      this.debug = var2;
      this.offset = this.frame.getStreamPosition();
      this.coefProbs = Globals.getDefaultCoefProbs();
      this.tokenBoolDecoders = new ArrayList<>();
   }

   public void setProgressListener(IIOReadProgressListener var1) {
      this.listener = var1;
   }

   private void createMacroBlocks() {
      this.macroBlocks = new MacroBlock[this.macroBlockRows + 2][this.macroBlockCols + 2];

      for (int var1 = 0; var1 < this.macroBlockRows + 2; var1++) {
         for (int var2 = 0; var2 < this.macroBlockCols + 2; var2++) {
            this.macroBlocks[var1][var2] = new MacroBlock(var2, var1, this.debug);
         }
      }
   }

   public boolean decode(WritableRaster var1, ImageReadParam var2) throws IOException {
      this.segmentQuants = new SegmentQuants();
      int var3 = this.frame.readUnsignedByte();
      this.frameType = this.getBitAsInt(var3, 0);
      if (this.frameType != 0) {
         return false;
      } else {
         int var4 = this.getBitAsInt(var3, 1) << 1;
         var4 += this.getBitAsInt(var3, 2) << 1;
         var4 += this.getBitAsInt(var3, 3);
         int var5 = this.getBitAsInt(var3, 5);
         var5 += this.getBitAsInt(var3, 6) << 1;
         var5 += this.getBitAsInt(var3, 7) << 2;
         var3 = this.frame.readUnsignedByte();
         var5 += var3 << 3;
         var3 = this.frame.readUnsignedByte();
         var5 += var3 << 11;
         var3 = this.frame.readUnsignedByte();
         var3 = this.frame.readUnsignedByte();
         var3 = this.frame.readUnsignedByte();
         var3 = this.frame.readUnsignedByte();
         int var23 = this.frame.readUnsignedByte();
         int var32 = var3 + (var23 << 8);
         this.width = var32 & 16383;
         var3 = this.frame.readUnsignedByte();
         int var25 = this.frame.readUnsignedByte();
         int var33 = var3 + (var25 << 8);
         this.height = var33 & 16383;
         int var8 = this.width;
         int var9 = this.height;
         if ((var8 & 15) != 0) {
            var8 += 16 - (var8 & 15);
         }

         if ((var9 & 15) != 0) {
            var9 += 16 - (var9 & 15);
         }

         this.macroBlockRows = var9 >> 4;
         this.macroBlockCols = var8 >> 4;
         this.createMacroBlocks();
         this.offset = this.frame.getStreamPosition();
         BoolDecoder var10 = new BoolDecoder(this.frame, this.offset);
         if (this.frameType == 0) {
            int var11 = var10.readBit();
            int var12 = var10.readBit();
         }

         this.segmentationIsEnabled = var10.readBit();
         if (this.segmentationIsEnabled > 0) {
            this.updateMacroBlockSegmentationMap = var10.readBit();
            this.updateMacroBlockSegmentatonData = var10.readBit();
            if (this.updateMacroBlockSegmentatonData > 0) {
               this.macroBlockSegementAbsoluteDelta = var10.readBit();

               for (int var34 = 0; var34 < 4; var34++) {
                  int var41 = 0;
                  if (var10.readBit() > 0) {
                     var41 = var10.readLiteral(Globals.vp8MacroBlockFeatureDataBits[0]);
                     if (var10.readBit() > 0) {
                        var41 = -var41;
                     }
                  }

                  this.segmentQuants.getSegQuants()[var34].setQindex(var41);
               }

               for (int var35 = 0; var35 < 4; var35++) {
                  int var42 = 0;
                  if (var10.readBit() > 0) {
                     var42 = var10.readLiteral(Globals.vp8MacroBlockFeatureDataBits[1]);
                     if (var10.readBit() > 0) {
                        var42 = -var42;
                     }
                  }

                  this.segmentQuants.getSegQuants()[var35].setFilterStrength(var42);
               }

               if (this.updateMacroBlockSegmentationMap > 0) {
                  this.macroBlockSegmentTreeProbs = new int[3];

                  for (int var36 = 0; var36 < 3; var36++) {
                     int var43 = var10.readBit() > 0 ? var10.readLiteral(8) : 255;
                     this.macroBlockSegmentTreeProbs[var36] = var43;
                  }
               }
            }
         }

         this.simpleFilter = var10.readBit() != 0;
         this.filterLevel = var10.readLiteral(6);
         this.sharpnessLevel = var10.readLiteral(3);
         this.modeRefLoopFilterDeltaEnabled = var10.readBit();
         if (this.modeRefLoopFilterDeltaEnabled > 0) {
            this.modeRefLoopFilterDeltaUpdate = var10.readBit();
            if (this.modeRefLoopFilterDeltaUpdate > 0) {
               for (int var37 = 0; var37 < 4; var37++) {
                  if (var10.readBit() > 0) {
                     this.refLoopFilterDeltas[var37] = var10.readLiteral(6);
                     if (var10.readBit() > 0) {
                        this.refLoopFilterDeltas[var37] = this.refLoopFilterDeltas[var37] * -1;
                     }
                  }
               }

               for (int var38 = 0; var38 < 4; var38++) {
                  if (var10.readBit() > 0) {
                     this.modeLoopFilterDeltas[var38] = var10.readLiteral(6);
                     if (var10.readBit() > 0) {
                        this.modeLoopFilterDeltas[var38] = this.modeLoopFilterDeltas[var38] * -1;
                     }
                  }
               }
            }
         }

         this.setupTokenDecoder(var10, var5, this.offset);
         var10.seek();
         this.segmentQuants.parse(var10, this.segmentationIsEnabled == 1, this.macroBlockSegementAbsoluteDelta == 1);
         if (this.frameType != 0) {
            throw new IllegalArgumentException("Bad input: Not an Intra frame");
         } else {
            this.refreshEntropyProbs = var10.readBit();
            if (this.refreshEntropyProbs > 0) {
            }

            this.refreshLastFrame = 0;
            if (this.frameType == 0) {
               this.refreshLastFrame = 1;
            } else {
               this.refreshLastFrame = var10.readBit();
            }

            for (int var39 = 0; var39 < 4; var39++) {
               for (int var44 = 0; var44 < 8; var44++) {
                  for (int var13 = 0; var13 < 3; var13++) {
                     for (int var14 = 0; var14 < 11; var14++) {
                        if (var10.readBool(Globals.vp8CoefUpdateProbs[var39][var44][var13][var14]) > 0) {
                           int var15 = var10.readLiteral(8);
                           this.coefProbs[var39][var44][var13][var14] = var15;
                        }
                     }
                  }
               }
            }

            this.macroBlockNoCoeffSkip = var10.readBit();
            if (this.frameType != 0) {
               throw new IIOException("Bad input: Not an Intra frame");
            } else {
               this.readModes(var10);
               int var40 = 0;
               int var45 = 1 << this.multiTokenPartition;
               Rectangle var46 = var2 != null && var2.getSourceRegion() != null ? var2.getSourceRegion() : var1.getBounds();
               int var47 = var2 != null ? var2.getSourceXSubsampling() : 1;
               int var48 = var2 != null ? var2.getSourceYSubsampling() : 1;

               for (int var16 = 0; var16 < this.macroBlockRows; var16++) {
                  if (var45 > 1) {
                     this.tokenBoolDecoder = this.tokenBoolDecoders.get(var40);
                     this.tokenBoolDecoder.seek();
                     if (++var40 == var45) {
                        var40 = 0;
                     }
                  }

                  this.decodeMacroBlockRow(var16, var1, var46, var47, var48);
                  this.fireProgressUpdate(var16);
               }

               return true;
            }
         }
      }
   }

   private void decodeMacroBlockRow(int var1, WritableRaster var2, Rectangle var3, int var4, int var5) throws IOException {
      boolean var6 = this.filterLevel != 0;
      MacroBlock var7 = null;
      MacroBlock[] var8 = this.macroBlocks[var1];
      MacroBlock[] var9 = this.macroBlocks[var1 + 1];

      for (int var10 = 0; var10 < this.macroBlockCols; var10++) {
         MacroBlock var11 = var9[var10 + 1];
         var11.decodeMacroBlock(this);
         var11.dequantMacroBlock(this);
         if (var6) {
            MacroBlock var12 = var1 > 0 ? var8[var10 + 1] : null;
            LoopFilter.loopFilterBlock(var11, var7, var12, this.frameType, this.simpleFilter, this.sharpnessLevel);
         }

         this.copyBlock(var11, var2, var3, var4, var5);
         var7 = var11;
      }
   }

   private void fireProgressUpdate(int var1) {
      if (this.listener != null) {
         float var2 = 100.0F * ((float)(var1 + 1) / this.getMacroBlockRows());
         this.listener.imageProgress(null, var2);
      }
   }

   public SubBlock getAboveRightSubBlock(SubBlock var1, SubBlock.Plane var2) {
      MacroBlock var4 = var1.getMacroBlock();
      int var5 = var4.getSubblockX(var1);
      int var6 = var4.getSubblockY(var1);
      if (var2 == SubBlock.Plane.Y1) {
         if (var6 == 0 && var5 < 3) {
            MacroBlock var12 = this.getMacroBlock(var4.getX(), var4.getY() - 1);
            return var12.getSubBlock(var2, var5 + 1, 3);
         } else if (var6 == 0 && var5 == 3) {
            MacroBlock var11 = this.getMacroBlock(var4.getX() + 1, var4.getY() - 1);
            SubBlock var3 = var11.getSubBlock(var2, 0, 3);
            if (var11.getX() == this.getMacroBlockCols()) {
               int[][] var8 = new int[4][4];

               for (int var9 = 0; var9 < 4; var9++) {
                  for (int var10 = 0; var10 < 4; var10++) {
                     if (var11.getY() < 0) {
                        var8[var10][var9] = 127;
                     } else {
                        var8[var10][var9] = this.getMacroBlock(var4.getX(), var4.getY() - 1).getSubBlock(SubBlock.Plane.Y1, 3, 3).getDest()[3][3];
                     }
                  }
               }

               var3 = new SubBlock(var11, null, null, SubBlock.Plane.Y1);
               var3.setDest(var8);
            }

            return var3;
         } else if (var6 > 0 && var5 < 3) {
            return var4.getSubBlock(var2, var5 + 1, var6 - 1);
         } else {
            SubBlock var7 = var4.getSubBlock(var1.getPlane(), 3, 0);
            return this.getAboveRightSubBlock(var7, var2);
         }
      } else {
         throw new IllegalArgumentException("bad input: getAboveRightSubBlock()");
      }
   }

   public SubBlock getAboveSubBlock(SubBlock var1, SubBlock.Plane var2) {
      SubBlock var3 = var1.getAbove();
      if (var3 == null) {
         MacroBlock var4 = var1.getMacroBlock();
         int var5 = var4.getSubblockX(var1);
         MacroBlock var6 = this.getMacroBlock(var4.getX(), var4.getY() - 1);

         while (var2 == SubBlock.Plane.Y2 && var6.getYMode() == 4) {
            var6 = this.getMacroBlock(var6.getX(), var6.getY() - 1);
         }

         var3 = var6.getBottomSubBlock(var5, var1.getPlane());
      }

      return var3;
   }

   private int getBitAsInt(int var1, int var2) {
      int var3 = var1 & 1 << var2;
      return var3 != 0 ? 1 : 0;
   }

   int[][][][] getCoefProbs() {
      return this.coefProbs;
   }

   public BufferedImage getDebugImageDiff() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.Y1, var3 % 16 / 4, var4 % 16 / 4).getDiff()[var3 % 4][var4 % 4];
            int var7 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.U, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDiff()[var3 / 2 % 4][var4
                  / 2
                  % 4];
            int var8 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.V, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDiff()[var3 / 2 % 4][var4
                  / 2
                  % 4];
            var5[0] = (int)(1.164 * (var6 - 16) + 1.596 * (var8 - 128));
            var5[1] = (int)(1.164 * (var6 - 16) - 0.813 * (var8 - 128) - 0.391 * (var7 - 128));
            var5[2] = (int)(1.164 * (var6 - 16) + 2.018 * (var7 - 128));

            for (int var9 = 0; var9 < 3; var9++) {
               if (var5[var9] < 0) {
                  var5[var9] = 0;
               }

               if (var5[var9] > 255) {
                  var5[var9] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImagePredict() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.Y1, var3 % 16 / 4, var4 % 16 / 4).getPredict()[var3 % 4][var4 % 4];
            int var7 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.U, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getPredict()[var3 / 2 % 4][var4
               / 2
               % 4];
            int var8 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.V, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getPredict()[var3 / 2 % 4][var4
               / 2
               % 4];
            var5[0] = (int)(1.164 * (var6 - 16) + 1.596 * (var8 - 128));
            var5[1] = (int)(1.164 * (var6 - 16) - 0.813 * (var8 - 128) - 0.391 * (var7 - 128));
            var5[2] = (int)(1.164 * (var6 - 16) + 2.018 * (var7 - 128));

            for (int var9 = 0; var9 < 3; var9++) {
               if (var5[var9] < 0) {
                  var5[var9] = 0;
               }

               if (var5[var9] > 255) {
                  var5[var9] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageUBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.U, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDest()[var3 / 2 % 4][var4
               / 2
               % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageUDiffBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.U, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDiff()[var3 / 2 % 4][var4
                  / 2
                  % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageUPredBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.U, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getPredict()[var3 / 2 % 4][var4
               / 2
               % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageVBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.V, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDest()[var3 / 2 % 4][var4
               / 2
               % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageVDiffBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.V, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getDiff()[var3 / 2 % 4][var4
                  / 2
                  % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageVPredBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.V, var3 / 2 % 8 / 4, var4 / 2 % 8 / 4).getPredict()[var3 / 2 % 4][var4
               / 2
               % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageYBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.Y1, var3 % 16 / 4, var4 % 16 / 4).getDest()[var3 % 4][var4 % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageYDiffBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = 127
               + this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.Y1, var3 % 16 / 4, var4 % 16 / 4).getDiff()[var3 % 4][var4 % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public BufferedImage getDebugImageYPredBuffer() {
      BufferedImage var1 = new BufferedImage(this.getWidth(), this.getHeight(), 1);
      WritableRaster var2 = var1.getWritableTile(0, 0);

      for (int var3 = 0; var3 < this.getWidth(); var3++) {
         for (int var4 = 0; var4 < this.getHeight(); var4++) {
            int[] var5 = new int[3];
            int var6 = this.getMacroBlock(var3 / 16, var4 / 16).getSubBlock(SubBlock.Plane.Y1, var3 % 16 / 4, var4 % 16 / 4).getPredict()[var3 % 4][var4 % 4];
            var5[0] = var6;
            var5[1] = var6;
            var5[2] = var6;

            for (int var7 = 0; var7 < 3; var7++) {
               if (var5[var7] < 0) {
                  var5[var7] = 0;
               }

               if (var5[var7] > 255) {
                  var5[var7] = 255;
               }
            }

            var2.setPixel(var3, var4, var5);
         }
      }

      return var1;
   }

   public int getFrameType() {
      return this.frameType;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public SubBlock getLeftSubBlock(SubBlock var1, SubBlock.Plane var2) {
      SubBlock var3 = var1.getLeft();
      if (var3 == null) {
         MacroBlock var4 = var1.getMacroBlock();
         int var5 = var4.getSubblockY(var1);
         MacroBlock var6 = this.getMacroBlock(var4.getX() - 1, var4.getY());

         while (var2 == SubBlock.Plane.Y2 && var6.getYMode() == 4) {
            var6 = this.getMacroBlock(var6.getX() - 1, var6.getY());
         }

         var3 = var6.getRightSubBlock(var5, var1.getPlane());
      }

      return var3;
   }

   public MacroBlock getMacroBlock(int var1, int var2) {
      return this.macroBlocks[var2 + 1][var1 + 1];
   }

   public int getMacroBlockCols() {
      return this.macroBlockCols;
   }

   public String getMacroBlockDebugString(int var1, int var2, int var3, int var4) {
      String var5 = "";
      if (var1 < this.macroBlockCols && var2 < this.getMacroBlockRows()) {
         MacroBlock var6 = this.getMacroBlock(var1, var2);
         var5 = var5 + var6.getDebugString();
         if (var3 < 4 && var4 < 4) {
            SubBlock var7 = var6.getSubBlock(SubBlock.Plane.Y1, var3, var4);
            var5 = var5 + "\n SubBlock " + var3 + ", " + var4 + "\n  " + var7.getDebugString();
            var7 = var6.getSubBlock(SubBlock.Plane.Y2, var3, var4);
            var5 = var5 + "\n SubBlock " + var3 + ", " + var4 + "\n  " + var7.getDebugString();
            var7 = var6.getSubBlock(SubBlock.Plane.U, var3 / 2, var4 / 2);
            var5 = var5 + "\n SubBlock " + var3 / 2 + ", " + var4 / 2 + "\n  " + var7.getDebugString();
            var7 = var6.getSubBlock(SubBlock.Plane.V, var3 / 2, var4 / 2);
            var5 = var5 + "\n SubBlock " + var3 / 2 + ", " + var4 / 2 + "\n  " + var7.getDebugString();
         }
      }

      return var5;
   }

   public int getMacroBlockRows() {
      return this.macroBlockRows;
   }

   public int getQIndex() {
      return this.segmentQuants.getqIndex();
   }

   public SegmentQuants getSegmentQuants() {
      return this.segmentQuants;
   }

   public int getSharpnessLevel() {
      return this.sharpnessLevel;
   }

   public BoolDecoder getTokenBoolDecoder() throws IOException {
      this.tokenBoolDecoder.seek();
      return this.tokenBoolDecoder;
   }

   private void readModes(BoolDecoder var1) throws IOException {
      int var2 = -1;
      int var3 = 0;
      if (this.macroBlockNoCoeffSkip > 0) {
         var3 = var1.readLiteral(8);
      }

      while (++var2 < this.macroBlockRows) {
         int var4 = -1;

         while (++var4 < this.macroBlockCols) {
            MacroBlock var5 = this.getMacroBlock(var4, var2);
            if (this.segmentationIsEnabled > 0 && this.updateMacroBlockSegmentationMap > 0) {
               int var6 = var1.readTree(Globals.macroBlockSegmentTree, this.macroBlockSegmentTreeProbs, 0);
               var5.setSegmentId(var6);
            }

            if (this.modeRefLoopFilterDeltaEnabled > 0) {
               int var14 = this.filterLevel;
               var14 += this.refLoopFilterDeltas[0];
               var14 = var14 < 0 ? 0 : Math.min(var14, 63);
               var5.setFilterLevel(var14);
            } else {
               var5.setFilterLevel(this.segmentQuants.getSegQuants()[var5.getSegmentId()].getFilterStrength());
            }

            int var17 = this.macroBlockNoCoeffSkip > 0 ? var1.readBool(var3) : 0;
            var5.setSkipCoeff(var17);
            int var7 = this.readYMode(var1);
            var5.setYMode(var7);
            if (var7 == 4) {
               for (int var18 = 0; var18 < 4; var18++) {
                  for (int var23 = 0; var23 < 4; var23++) {
                     SubBlock var24 = var5.getYSubBlock(var23, var18);
                     SubBlock var25 = this.getAboveSubBlock(var24, SubBlock.Plane.Y1);
                     SubBlock var12 = this.getLeftSubBlock(var24, SubBlock.Plane.Y1);
                     int var13 = this.readSubBlockMode(var1, var25.getMode(), var12.getMode());
                     var24.setMode(var13);
                  }
               }

               if (this.modeRefLoopFilterDeltaEnabled > 0) {
                  int var19 = var5.getFilterLevel();
                  var19 += this.modeLoopFilterDeltas[0];
                  var19 = var19 < 0 ? 0 : Math.min(var19, 63);
                  var5.setFilterLevel(var19);
               }
            } else {
               byte var8;
               switch (var7) {
                  case 0:
                  default:
                     var8 = 0;
                     break;
                  case 1:
                     var8 = 2;
                     break;
                  case 2:
                     var8 = 3;
                     break;
                  case 3:
                     var8 = 1;
               }

               for (int var9 = 0; var9 < 4; var9++) {
                  for (int var10 = 0; var10 < 4; var10++) {
                     SubBlock var11 = var5.getYSubBlock(var9, var10);
                     var11.setMode(var8);
                  }
               }
            }

            int var22 = this.readUvMode(var1);
            var5.setUvMode(var22);
         }
      }
   }

   private int readPartitionSize(long var1) throws IOException {
      this.frame.seek(var1);
      return this.frame.readUnsignedByte() + (this.frame.readUnsignedByte() << 8) + (this.frame.readUnsignedByte() << 16);
   }

   private int readSubBlockMode(BoolDecoder var1, int var2, int var3) throws IOException {
      return var1.readTree(Globals.vp8SubBlockModeTree, Globals.vp8KeyFrameSubBlockModeProb[var2][var3], 0);
   }

   private int readUvMode(BoolDecoder var1) throws IOException {
      return var1.readTree(Globals.vp8UVModeTree, Globals.vp8KeyFrameUVModeProb, 0);
   }

   private int readYMode(BoolDecoder var1) throws IOException {
      return var1.readTree(Globals.vp8KeyFrameYModeTree, Globals.vp8KeyFrameYModeProb, 0);
   }

   private void setupTokenDecoder(BoolDecoder var1, int var2, long var3) throws IOException {
      long var7 = var3 + var2;
      long var9 = var7;
      this.multiTokenPartition = var1.readLiteral(2);
      int var11 = 1 << this.multiTokenPartition;
      if (var11 > 1) {
         var9 = var7 + 3L * (var11 - 1);
      }

      for (int var12 = 0; var12 < var11; var12++) {
         long var5;
         if (var12 < var11 - 1) {
            var5 = this.readPartitionSize(var7 + var12 * 3L);
            var1.seek();
         } else {
            var5 = this.frame.length() - var9;
         }

         this.tokenBoolDecoders.add(new BoolDecoder(this.frame, var9));
         var9 += var5;
      }

      this.tokenBoolDecoder = this.tokenBoolDecoders.get(0);
   }

   private void copyBlock(MacroBlock var1, WritableRaster var2, Rectangle var3, int var4, int var5) {
      int var6 = var1.getY() * 16 - var3.y;
      int var7 = Math.min(16, var2.getHeight() * var5 - var6);
      int var8 = var1.getX() * 16 - var3.x;
      int var9 = Math.min(16, var2.getWidth() * var4 - var8);

      for (int var10 = 0; var10 < var7; var10 += var5) {
         int var11 = (var6 + var10) / var5;
         if (var11 >= 0) {
            for (int var12 = 0; var12 < var9; var12 += var4) {
               int var13 = (var8 + var12) / var4;
               if (var13 >= 0) {
                  this.yuv[0] = (byte)var1.getSubBlock(SubBlock.Plane.Y1, var12 / 4, var10 / 4).getDest()[var12 % 4][var10 % 4];
                  this.yuv[1] = (byte)var1.getSubBlock(SubBlock.Plane.U, var12 / 2 / 4, var10 / 2 / 4).getDest()[var12 / 2 % 4][var10 / 2 % 4];
                  this.yuv[2] = (byte)var1.getSubBlock(SubBlock.Plane.V, var12 / 2 / 4, var10 / 2 / 4).getDest()[var12 / 2 % 4][var10 / 2 % 4];
                  YCbCrConverter.convertRec601YCbCr2RGB(this.yuv, this.rgb, 0);
                  var2.setDataElements(var13, var11, this.rgb);
               }
            }
         }
      }
   }
}
