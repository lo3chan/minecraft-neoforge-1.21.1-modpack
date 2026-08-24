package com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass;

import com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding.LodBufferContainer;
import com.seibel.distanthorizons.core.render.RenderParams;
import com.seibel.distanthorizons.core.util.objects.SortedArraySet;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IProfilerWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;

public interface IDhTerrainRenderer extends IBindable {
   void render(RenderParams renderParams, boolean bl, SortedArraySet<LodBufferContainer> sortedArraySet, IProfilerWrapper iProfilerWrapper);
}
