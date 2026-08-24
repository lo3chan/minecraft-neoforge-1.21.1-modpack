package org.dimdev.limlib.client.specialmodels;

import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;

public interface ShaderInstanceExt {
   void addUniformSetCallback(Consumer<ShaderInstance> var1);
}
