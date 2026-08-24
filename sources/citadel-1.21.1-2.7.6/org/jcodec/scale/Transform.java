package org.jcodec.scale;

import org.jcodec.common.model.Picture;

public interface Transform {
   void transform(Picture var1, Picture var2);

   public static enum Levels {
      STUDIO,
      PC;
   }
}
