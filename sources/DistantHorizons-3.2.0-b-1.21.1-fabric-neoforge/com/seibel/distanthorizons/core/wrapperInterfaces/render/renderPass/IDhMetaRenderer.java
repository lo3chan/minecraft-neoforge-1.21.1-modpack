package com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass;

import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhMetaRenderer extends IBindable {
   void runRenderPassSetup(RenderParams renderParams);

   void runRenderPassCleanup(RenderParams renderParams);

   void applyToMcTexture(RenderParams renderParams);

   void clearDhDepthAndColorTextures(RenderParams renderParams);
}
