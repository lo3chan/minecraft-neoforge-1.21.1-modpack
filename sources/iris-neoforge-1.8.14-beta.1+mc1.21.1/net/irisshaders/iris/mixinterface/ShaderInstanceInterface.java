package net.irisshaders.iris.mixinterface;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import net.minecraft.server.packs.resources.ResourceProvider;

public interface ShaderInstanceInterface {
   void iris$createExtraShaders(ResourceProvider var1, String var2) throws IOException;

   void setShouldSkip(MethodHandle var1);
}
