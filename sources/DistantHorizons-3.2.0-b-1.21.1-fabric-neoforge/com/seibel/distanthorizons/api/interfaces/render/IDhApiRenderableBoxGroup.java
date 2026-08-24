package com.seibel.distanthorizons.api.interfaces.render;

import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;
import java.util.List;
import java.util.function.Consumer;

public interface IDhApiRenderableBoxGroup extends List<DhApiRenderableBox> {
   long getId();

   String getResourceLocationNamespace();

   String getResourceLocationPath();

   void setActive(boolean bl);

   boolean isActive();

   void setSsaoEnabled(boolean bl);

   boolean isSsaoEnabled();

   void setOriginBlockPos(DhApiVec3d dhApiVec3d);

   DhApiVec3d getOriginBlockPos();

   void setPreRenderFunc(Consumer<DhApiRenderParam> consumer);

   void setPostRenderFunc(Consumer<DhApiRenderParam> consumer);

   void triggerBoxChange();

   void setSkyLight(int i);

   int getSkyLight();

   void setBlockLight(int i);

   int getBlockLight();

   void setShading(DhApiRenderableBoxGroupShading dhApiRenderableBoxGroupShading);

   DhApiRenderableBoxGroupShading getShading();
}
