package com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiFogRenderParam;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhFogRenderer extends IBindable {
   void render(RenderParams renderParams, DhApiFogRenderParam dhApiFogRenderParam);
}
