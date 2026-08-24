package net.blay09.mods.balm.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SegmentedProgressRenderer implements ProgressRenderer {
   private final List<ProgressRenderer> segments = new ArrayList<>();
   private final List<Float> segmentWeights = new ArrayList<>();
   private final ResourceLocation texture;
   private final int textureWidth;
   private final int textureHeight;
   private int totalLength;

   public SegmentedProgressRenderer(ResourceLocation texture, int textureWidth, int textureHeight) {
      this.texture = texture;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
   }

   public SegmentedProgressRenderer addHorizontalSegment(int x, int y, int width, int height, int textureU, int textureV) {
      this.addSegment(
         SimpleProgressRenderer.horizontal(this.texture, this.textureWidth, this.textureHeight).pos(x, y).size(width, height).uv(textureU, textureV)
      );
      return this;
   }

   public SegmentedProgressRenderer addVerticalSegment(int x, int y, int width, int height, int textureU, int textureV) {
      this.addSegment(SimpleProgressRenderer.vertical(this.texture, this.textureWidth, this.textureHeight).pos(x, y).size(width, height).uv(textureU, textureV));
      return this;
   }

   public SegmentedProgressRenderer addReverseHorizontalSegment(int x, int y, int width, int height, int textureU, int textureV) {
      this.addSegment(
         SimpleProgressRenderer.reverseHorizontal(this.texture, this.textureWidth, this.textureHeight).pos(x, y).size(width, height).uv(textureU, textureV)
      );
      return this;
   }

   public SegmentedProgressRenderer addReverseVerticalSegment(int x, int y, int width, int height, int textureU, int textureV) {
      this.addSegment(
         SimpleProgressRenderer.reverseVertical(this.texture, this.textureWidth, this.textureHeight).pos(x, y).size(width, height).uv(textureU, textureV)
      );
      return this;
   }

   public SegmentedProgressRenderer addInvisibleSegment(int size) {
      this.addSegment(SimpleProgressRenderer.invisible().size(size, size));
      return this;
   }

   public SegmentedProgressRenderer addSegment(ProgressRenderer segment) {
      this.segments.add(segment);
      this.totalLength = this.totalLength + segment.getLength();
      this.segmentWeights.clear();

      for (ProgressRenderer seg : this.segments) {
         this.segmentWeights.add((float)seg.getLength() / this.totalLength);
      }

      return this;
   }

   public SegmentedProgressRenderer addParallelSegments(ProgressRenderer... segments) {
      return this.addSegment(new ParallelProgressRenderer(segments));
   }

   @Override
   public int getLength() {
      return this.totalLength;
   }

   @Override
   public void render(GuiGraphics guiGraphics, int screenX, int screenY, float progress) {
      if (!(progress <= 0.0F) && !this.segments.isEmpty()) {
         float currentProgress = 0.0F;

         for (int i = 0; i < this.segments.size(); i++) {
            ProgressRenderer segment = this.segments.get(i);
            float segmentWeight = this.segmentWeights.get(i);
            if (progress > currentProgress) {
               float segmentProgress = Math.min((progress - currentProgress) / segmentWeight, 1.0F);
               segment.render(guiGraphics, screenX, screenY, segmentProgress);
            }

            currentProgress += segmentWeight;
         }
      }
   }
}
