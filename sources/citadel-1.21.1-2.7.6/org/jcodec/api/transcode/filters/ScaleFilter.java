package org.jcodec.api.transcode.filters;

import org.jcodec.api.transcode.Filter;
import org.jcodec.api.transcode.PixelStore;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.scale.BaseResampler;
import org.jcodec.scale.LanczosResampler;

public class ScaleFilter implements Filter {
   private BaseResampler resampler;
   private ColorSpace currentColor;
   private Size currentSize;
   private Size targetSize;
   private int width;
   private int height;

   public ScaleFilter(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public Size getTarget() {
      return new Size(this.width, this.height);
   }

   @Override
   public PixelStore.LoanerPicture filter(Picture picture, PixelStore store) {
      Size pictureSize = picture.getSize();
      if (this.resampler == null || this.currentColor != picture.getColor() || !pictureSize.equals(this.currentSize)) {
         this.currentColor = picture.getColor();
         this.currentSize = picture.getSize();
         this.targetSize = new Size(this.width & this.currentColor.getWidthMask(), this.height & this.currentColor.getHeightMask());
         this.resampler = new LanczosResampler(this.currentSize, this.targetSize);
      }

      PixelStore.LoanerPicture dest = store.getPicture(this.targetSize.getWidth(), this.targetSize.getHeight(), this.currentColor);
      this.resampler.resample(picture, dest.getPicture());
      return dest;
   }

   @Override
   public ColorSpace getInputColor() {
      return ColorSpace.ANY_PLANAR;
   }

   @Override
   public ColorSpace getOutputColor() {
      return ColorSpace.SAME;
   }
}
