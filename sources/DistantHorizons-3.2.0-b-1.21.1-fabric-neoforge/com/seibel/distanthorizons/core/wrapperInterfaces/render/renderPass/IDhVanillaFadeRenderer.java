package com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass;

import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhVanillaFadeRenderer extends IBindable {
   void render(RenderParams renderParams);
}
