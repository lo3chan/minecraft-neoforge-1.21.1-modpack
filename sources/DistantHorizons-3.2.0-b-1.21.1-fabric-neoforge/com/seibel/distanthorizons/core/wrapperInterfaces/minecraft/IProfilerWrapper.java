package com.seibel.distanthorizons.core.wrapperInterfaces.minecraft;

import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IProfilerWrapper extends IBindable {
   IProfilerWrapper.IProfileBlock push(String string);

   void popPush(String string);

   public interface IProfileBlock extends AutoCloseable {
      @Override
      void close();
   }
}
