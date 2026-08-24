package org.jcodec.api.transcode.filters;

import org.jcodec.api.transcode.Filter;
import org.jcodec.api.transcode.PixelStore;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

public class CropFilter implements Filter {
   @Override
   public PixelStore.LoanerPicture filter(Picture picture, PixelStore store) {
      return null;
   }

   @Override
   public ColorSpace getInputColor() {
      return null;
   }

   @Override
   public ColorSpace getOutputColor() {
      return null;
   }
}
