package org.jcodec.api.transcode.filters;

import org.jcodec.api.transcode.Filter;
import org.jcodec.api.transcode.PixelStore;
import org.jcodec.common.logging.Logger;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

public class ColorTransformFilter implements Filter {
   private Transform transform;
   private ColorSpace outputColor;

   public ColorTransformFilter(ColorSpace outputColor) {
      this.outputColor = outputColor;
   }

   @Override
   public PixelStore.LoanerPicture filter(Picture picture, PixelStore store) {
      if (this.transform == null) {
         this.transform = ColorUtil.getTransform(picture.getColor(), this.outputColor);
         Logger.debug("Creating transform: " + this.transform);
      }

      PixelStore.LoanerPicture outFrame = store.getPicture(picture.getWidth(), picture.getHeight(), this.outputColor);
      outFrame.getPicture().setCrop(picture.getCrop());
      this.transform.transform(picture, outFrame.getPicture());
      return outFrame;
   }

   @Override
   public ColorSpace getInputColor() {
      return ColorSpace.ANY_PLANAR;
   }

   @Override
   public ColorSpace getOutputColor() {
      return this.outputColor;
   }
}
