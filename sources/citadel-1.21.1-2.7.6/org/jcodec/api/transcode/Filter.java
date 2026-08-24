package org.jcodec.api.transcode;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

public interface Filter {
   PixelStore.LoanerPicture filter(Picture var1, PixelStore var2);

   ColorSpace getInputColor();

   ColorSpace getOutputColor();
}
