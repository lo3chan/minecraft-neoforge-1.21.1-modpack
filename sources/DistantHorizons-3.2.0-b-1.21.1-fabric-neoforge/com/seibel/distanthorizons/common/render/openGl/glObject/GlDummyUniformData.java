package com.seibel.distanthorizons.common.render.openGl.glObject;

import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.ILodContainerUniformBufferWrapper;

public class GlDummyUniformData implements ILodContainerUniformBufferWrapper {
   @Override
   public void tryUpload(LodBufferContainer bufferContainer) {
   }

   @Override
   public void close() {
   }
}
