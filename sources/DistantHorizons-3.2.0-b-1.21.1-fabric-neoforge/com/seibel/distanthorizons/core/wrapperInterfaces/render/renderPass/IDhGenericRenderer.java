package com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass;

import com.seibel.distanthorizons.api.interfaces.render.IDhApiCustomRenderRegister;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;

public interface IDhGenericRenderer extends IDhApiCustomRenderRegister, AutoCloseable {
   void render(RenderParams renderParams, IProfilerWrapper iProfilerWrapper, boolean bl);

   String getVboRenderDebugMenuString();

   @Override
   void close();
}
