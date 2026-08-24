package com.seibel.distanthorizons.api.methods.events.sharedParameterObjects;

import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;

public class DhApiTextureCreatedParam implements IDhApiEventParam {
   public final int previousWidth;
   public final int previousHeight;
   public final int newWidth;
   public final int newHeight;

   public DhApiTextureCreatedParam(int previousWidth, int previousHeight, int newWidth, int newHeight) {
      this.previousWidth = previousWidth;
      this.previousHeight = previousHeight;
      this.newWidth = newWidth;
      this.newHeight = newHeight;
   }

   public DhApiTextureCreatedParam copy() {
      return new DhApiTextureCreatedParam(this.previousWidth, this.previousHeight, this.newWidth, this.newHeight);
   }
}
