package com.seibel.distanthorizons.api.interfaces.render;

import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import java.util.List;

public interface IDhApiCustomRenderObjectFactory extends IBindable {
   IDhApiRenderableBoxGroup createForSingleBox(String string, DhApiRenderableBox dhApiRenderableBox) throws IllegalArgumentException;

   IDhApiRenderableBoxGroup createRelativePositionedGroup(String string, DhApiVec3d dhApiVec3d, List<DhApiRenderableBox> list);

   IDhApiRenderableBoxGroup createAbsolutePositionedGroup(String string, List<DhApiRenderableBox> list);
}
