package net.mehvahdjukaar.moonlight.api.resources.textures;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.util.math.Rect2D;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

public class TextureCollager {
   protected final int originFrameW;
   protected final int originFrameH;
   protected final int targetFrameW;
   protected final int targetFrameH;
   private final List<TextureCollager.Operation> operations;

   private TextureCollager(int originalW, int originalH, int targetW, int targetH, List<TextureCollager.Operation> list) {
      this.originFrameW = originalW;
      this.originFrameH = originalH;
      this.targetFrameW = targetW;
      this.targetFrameH = targetH;
      this.operations = list;
   }

   public void apply(TextureImage source, TextureImage destination) {
      String debugInfo = "Source='" + source.debugPath + "', Dest='" + destination.debugPath + "'";
      if (source.frameCount() <= 0) {
         throw new IllegalStateException("Source TextureImage has no frames: " + debugInfo);
      } else if (destination.frameCount() <= 0) {
         throw new IllegalStateException("Destination TextureImage has no frames: " + debugInfo);
      } else if (this.originFrameW > 0 && this.originFrameH > 0) {
         if (this.targetFrameW > 0 && this.targetFrameH > 0) {
            float scaleSourceX = (float)source.frameWidth() / this.originFrameW;
            float scaleSourceY = (float)source.frameHeight() / this.originFrameH;
            float scaleTargetX = (float)destination.frameWidth() / this.targetFrameW;
            float scaleTargetY = (float)destination.frameHeight() / this.targetFrameH;
            int sourceFrames = source.frameCount();
            int targetFrames = destination.frameCount();
            int maxFrames = Math.max(sourceFrames, targetFrames);

            for (int i = 0; i < maxFrames; i++) {
               int cappedSourceFrame = Math.min(i, sourceFrames - 1);
               int cappedTargetFrame = Math.min(i, targetFrames - 1);
               Sampler2D sourceFrameSampler = source.frameSampler(cappedSourceFrame);

               for (TextureCollager.Operation op : this.operations) {
                  int scaledSourceX = Math.round(op.sourceX * scaleSourceX);
                  int scaledSourceY = Math.round(op.sourceY * scaleSourceY);
                  int scaledSourceW = Math.round(op.sourceW * scaleSourceX);
                  int scaledSourceH = Math.round(op.sourceH * scaleSourceY);
                  int scaledTargetX = Math.round(op.targetX * scaleTargetX);
                  int scaledTargetY = Math.round(op.targetY * scaleTargetY);
                  int scaledTargetW = Math.round(op.targetW * scaleTargetX);
                  int scaledTargetH = Math.round(op.targetH * scaleTargetY);
                  if (scaledSourceW > 0 && scaledSourceH > 0) {
                     if (scaledTargetW > 0 && scaledTargetH > 0) {
                        if (scaledSourceX >= 0
                           && scaledSourceY >= 0
                           && scaledSourceX + scaledSourceW <= source.frameWidth()
                           && scaledSourceY + scaledSourceH <= source.frameHeight()) {
                           if (scaledTargetX >= 0
                              && scaledTargetY >= 0
                              && scaledTargetX + scaledTargetW <= destination.frameWidth()
                              && scaledTargetY + scaledTargetH <= destination.frameHeight()) {
                              Sampler2D sampler = Sampler2D.offset(sourceFrameSampler, scaledSourceX, scaledSourceY);
                              sampler = Sampler2D.clamp(sampler, scaledSourceW, scaledSourceH);
                              if (op.bilinear) {
                                 sampler = Sampler2D.bilinear(sampler);
                              }

                              int flipW = scaledSourceW;
                              int flipH = scaledSourceH;
                              if (op.rotation == Rotation.CLOCKWISE_90 || op.rotation == Rotation.COUNTERCLOCKWISE_90) {
                                 flipW = scaledSourceH;
                                 flipH = scaledSourceW;
                              }

                              if (op.flipX) {
                                 sampler = Sampler2D.flippedX(sampler, flipW);
                              }

                              if (op.flipY) {
                                 sampler = Sampler2D.flippedY(sampler, flipH);
                              }

                              if (op.rotation != Rotation.NONE) {
                                 sampler = Sampler2D.rotate(sampler, op.rotation, scaledSourceW, scaledSourceH);
                              }

                              sampler = Sampler2D.offset(sampler, -0.5F, -0.5F);
                              float opScaleX = (float)scaledSourceW / scaledTargetW;
                              float opScaleY = (float)scaledSourceH / scaledTargetH;
                              if (opScaleX != 1.0F || opScaleY != 1.0F) {
                                 sampler = Sampler2D.scale(sampler, opScaleX, opScaleY);
                              }

                              sampler = Sampler2D.offset(sampler, 0.5F, 0.5F);
                              int actualW = Math.min(scaledTargetW, destination.frameWidth() - scaledTargetX);
                              int actualH = Math.min(scaledTargetH, destination.frameHeight() - scaledTargetY);
                              if (actualW > 0 && actualH > 0) {
                                 for (int ty = 0; ty < actualH; ty++) {
                                    for (int tx = 0; tx < actualW; tx++) {
                                       float srcX = tx + 0.5F;
                                       float srcY = ty + 0.5F;
                                       int color = sampler.sample(srcX, srcY);
                                       if (op.palettes != null) {
                                          int maxPaletteIndex = Math.min(source.frameCount(), op.palettes.size() - 1);
                                          color = op.palettes.get(maxPaletteIndex).getColorClosestTo(new PaletteColor(color)).value();
                                       }

                                       if (op.blended) {
                                          destination.blendFramePixel(cappedTargetFrame, scaledTargetX + tx, scaledTargetY + ty, color);
                                       } else {
                                          destination.setFramePixel(cappedTargetFrame, scaledTargetX + tx, scaledTargetY + ty, color);
                                       }
                                    }
                                 }
                                 continue;
                              }

                              throw new IllegalStateException(
                                 "Operation would write zero or negative pixel area: actualW="
                                    + actualW
                                    + ", actualH="
                                    + actualH
                                    + " - "
                                    + debugInfo
                                    + ", op="
                                    + op
                              );
                           }

                           throw new IllegalStateException(
                              "Target operation rectangle out of bounds: "
                                 + scaledTargetX
                                 + ","
                                 + scaledTargetY
                                 + ","
                                 + scaledTargetW
                                 + ","
                                 + scaledTargetH
                                 + " - "
                                 + debugInfo
                                 + ", op="
                                 + op
                           );
                        }

                        throw new IllegalStateException(
                           "Source operation rectangle out of bounds: "
                              + scaledSourceX
                              + ","
                              + scaledSourceY
                              + ","
                              + scaledSourceW
                              + ","
                              + scaledSourceH
                              + " - "
                              + debugInfo
                              + ", op="
                              + op
                        );
                     }

                     throw new IllegalStateException(
                        "Scaled target rectangle invalid: " + scaledTargetW + "x" + scaledTargetH + " - " + debugInfo + ", op=" + op
                     );
                  }

                  throw new IllegalStateException("Scaled source rectangle invalid: " + scaledSourceW + "x" + scaledSourceH + " - " + debugInfo + ", op=" + op);
               }
            }
         } else {
            throw new IllegalStateException(
               "TextureCollager target frame dimensions invalid: " + this.targetFrameW + "x" + this.targetFrameH + " - " + debugInfo
            );
         }
      } else {
         throw new IllegalStateException("TextureCollager origin frame dimensions invalid: " + this.originFrameW + "x" + this.originFrameH + " - " + debugInfo);
      }
   }

   public static TextureCollager.Builder builder(int originFrameW, int originFrameH, int targetFrameW, int targetFrameH) {
      return new TextureCollager.Builder(originFrameW, originFrameH, targetFrameW, targetFrameH);
   }

   public static class Builder {
      private final int originalFrameW;
      private final int originalFrameH;
      private final int targetFrameW;
      private final int targetFrameH;
      private final List<TextureCollager.Operation> operations = new ArrayList<>();
      private Integer fromX;
      private Integer fromY;
      private Integer fromW;
      private Integer fromH;
      private Integer targetX;
      private Integer targetY;
      private Integer targetW;
      private Integer targetH;
      private boolean flipX = false;
      private boolean flipY = false;
      private Rotation rotation = Rotation.NONE;
      private boolean bilinear = false;
      private boolean blended = false;
      @Nullable
      private List<Palette> palettes = null;

      public Builder(int originalW, int originalH, int targetW, int targetH) {
         this.originalFrameW = originalW;
         this.originalFrameH = originalH;
         this.targetFrameW = targetW;
         this.targetFrameH = targetH;
      }

      public TextureCollager build() {
         this.addLast();
         return new TextureCollager(this.originalFrameW, this.originalFrameH, this.targetFrameW, this.targetFrameH, List.copyOf(this.operations));
      }

      public TextureCollager.Builder copyFrom(Rect2D rect) {
         return this.copyFrom(rect.x(), rect.y(), rect.width(), rect.height());
      }

      public TextureCollager.Builder copyFrom(int x, int y, int w, int h) {
         this.addLast();
         this.fromX = x;
         this.fromY = y;
         this.fromW = w;
         this.fromH = h;
         this.targetH = this.fromH;
         this.targetW = this.fromW;
         return this;
      }

      public TextureCollager.Builder to(Rect2D rect) {
         return this.to(rect.x(), rect.y(), rect.width(), rect.height());
      }

      public TextureCollager.Builder to(int x, int y, int w, int h) {
         this.to(x, y);
         this.targetW = w;
         this.targetH = h;
         return this;
      }

      public TextureCollager.Builder to(int x, int y) {
         this.targetX = x;
         this.targetY = y;
         return this;
      }

      public TextureCollager.Builder flippedX() {
         this.flipX = true;
         return this;
      }

      public TextureCollager.Builder flippedY() {
         this.flipY = true;
         return this;
      }

      public TextureCollager.Builder rotated(Rotation r) {
         this.rotation = r == null ? Rotation.NONE : r;
         return this;
      }

      public TextureCollager.Builder blended() {
         this.blended = true;
         return this;
      }

      public TextureCollager.Builder paletted(List<Palette> palettes) {
         this.palettes = palettes;
         return this;
      }

      public TextureCollager.Builder bilinearScaling() {
         this.bilinear = true;
         return this;
      }

      private void addLast() {
         if (this.targetX != null) {
            this.validate();
            if (this.targetW == null || this.targetH == null) {
               boolean dimensionsSwapped = this.rotation == Rotation.CLOCKWISE_90 || this.rotation == Rotation.COUNTERCLOCKWISE_90;
               if (this.targetW == null) {
                  this.targetW = dimensionsSwapped ? this.fromH : this.fromW;
               }

               if (this.targetH == null) {
                  this.targetH = dimensionsSwapped ? this.fromW : this.fromH;
               }
            }

            this.operations
               .add(
                  new TextureCollager.Operation(
                     this.fromX,
                     this.fromY,
                     this.fromW,
                     this.fromH,
                     this.targetX,
                     this.targetY,
                     this.targetW,
                     this.targetH,
                     this.flipX,
                     this.flipY,
                     this.rotation,
                     this.bilinear,
                     this.blended,
                     this.palettes
                  )
               );
            this.fromX = this.fromY = this.fromW = this.fromH = null;
            this.targetX = this.targetY = null;
            this.targetW = this.targetH = null;
            this.flipX = this.flipY = false;
            this.rotation = Rotation.NONE;
            this.bilinear = false;
            this.blended = false;
            this.palettes = null;
         }
      }

      private void validate() {
         if (this.fromX == null) {
            throw new IllegalStateException("sourceX must be set");
         } else if (this.fromY == null) {
            throw new IllegalStateException("sourceY must be set");
         } else if (this.fromW == null) {
            throw new IllegalStateException("sourceW must be set");
         } else if (this.fromH == null) {
            throw new IllegalStateException("sourceH must be set");
         } else if (this.targetX == null) {
            throw new IllegalStateException("targetX must be set");
         } else if (this.targetY == null) {
            throw new IllegalStateException("targetY must be set");
         } else if (this.fromX < 0 || this.fromX + this.fromW > this.originalFrameW) {
            throw new IllegalArgumentException("Source rectangle out of bounds: fromX");
         } else if (this.fromY < 0 || this.fromY + this.fromH > this.originalFrameH) {
            throw new IllegalArgumentException("Source rectangle out of bounds: fromY");
         } else if (this.targetX < 0 || this.targetX + this.targetW > this.targetFrameW) {
            throw new IllegalArgumentException("Target rectangle out of bounds: targetX");
         } else if (this.targetY < 0 || this.targetY + this.targetH > this.targetFrameH) {
            throw new IllegalArgumentException("Target rectangle out of bounds: targetY");
         } else if (this.fromW <= 0 || this.fromH <= 0) {
            throw new IllegalArgumentException("Source width/height must be > 0");
         } else if (this.targetW <= 0 || this.targetH <= 0) {
            throw new IllegalArgumentException("Target width/height must be > 0");
         }
      }
   }

   private record Operation(
      int sourceX,
      int sourceY,
      int sourceW,
      int sourceH,
      int targetX,
      int targetY,
      int targetW,
      int targetH,
      boolean flipX,
      boolean flipY,
      Rotation rotation,
      boolean bilinear,
      boolean blended,
      @Nullable List<Palette> palettes
   ) {
   }
}
