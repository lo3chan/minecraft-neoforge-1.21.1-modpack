package net.irisshaders.iris.api.v0;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class IrisApiInternal {
   static final IrisApi INSTANCE;

   static {
      try {
         INSTANCE = (IrisApi)Class.forName("net.irisshaders.iris.apiimpl.IrisApiV0Impl").getField("INSTANCE").get(null);
      } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException var1) {
         throw new RuntimeException(var1);
      }
   }
}
