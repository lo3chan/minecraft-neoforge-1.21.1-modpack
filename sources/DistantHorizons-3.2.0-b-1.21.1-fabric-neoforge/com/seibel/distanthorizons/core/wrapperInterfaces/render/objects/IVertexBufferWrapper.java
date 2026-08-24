package com.seibel.distanthorizons.core.wrapperInterfaces.render.objects;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.nio.ByteBuffer;

public interface IVertexBufferWrapper extends IBindable, AutoCloseable {
   void uploadVertexBuffer(ByteBuffer byteBuffer, int i);

   void uploadIndexBuffer(ByteBuffer byteBuffer, int i);

   @Override
   void close();
}
