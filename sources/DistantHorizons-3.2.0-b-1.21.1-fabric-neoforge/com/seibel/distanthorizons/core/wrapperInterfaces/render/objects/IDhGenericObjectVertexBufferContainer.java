package com.seibel.distanthorizons.core.wrapperInterfaces.render.objects;

import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import java.util.List;

public interface IDhGenericObjectVertexBufferContainer extends AutoCloseable {
   void uploadDataToGpu();

   void updateVertexData(List<DhApiRenderableBox> list);

   IDhGenericObjectVertexBufferContainer.EState getState();

   void setState(IDhGenericObjectVertexBufferContainer.EState eState);

   @Override
   void close();

   public static enum EState {
      NEW,
      UPDATING_DATA,
      READY_TO_UPLOAD,
      RENDER,
      ERROR;
   }
}
