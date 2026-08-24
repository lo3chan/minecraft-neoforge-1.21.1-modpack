package com.seibel.distanthorizons.core.wrapperInterfaces.render.objects;

import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;

public interface ILodContainerUniformBufferWrapper extends AutoCloseable {
   void tryUpload(LodBufferContainer lodBufferContainer);

   @Override
   void close();
}
