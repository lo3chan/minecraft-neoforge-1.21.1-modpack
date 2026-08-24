package org.jcodec.scale.highbd;

import org.jcodec.common.model.PictureHiBD;

public interface TransformHiBD {
   void transform(PictureHiBD var1, PictureHiBD var2);

   public static enum Levels {
      STUDIO,
      PC;
   }
}
