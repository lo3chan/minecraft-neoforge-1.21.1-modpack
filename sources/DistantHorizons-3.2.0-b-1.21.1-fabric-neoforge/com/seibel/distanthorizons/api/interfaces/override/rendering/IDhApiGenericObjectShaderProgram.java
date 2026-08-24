package com.seibel.distanthorizons.api.interfaces.override.rendering;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.interfaces.render.IDhApiRenderableBoxGroup;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3d;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBox;
import com.seibel.distanthorizons.api.objects.render.DhApiRenderableBoxGroupShading;

public interface IDhApiGenericObjectShaderProgram extends IDhApiOverrideable {
   boolean overrideThisFrame();

   int getId();

   void free();

   void bind(DhApiRenderParam dhApiRenderParam);

   void unbind();

   void bindVertexBuffer(int i);

   void fillIndirectUniformData(
      DhApiRenderParam dhApiRenderParam,
      DhApiRenderableBoxGroupShading dhApiRenderableBoxGroupShading,
      IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup,
      DhApiVec3d dhApiVec3d
   );

   void fillSharedDirectUniformData(
      DhApiRenderParam dhApiRenderParam,
      DhApiRenderableBoxGroupShading dhApiRenderableBoxGroupShading,
      IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup,
      DhApiVec3d dhApiVec3d
   );

   void fillDirectUniformData(
      DhApiRenderParam dhApiRenderParam, IDhApiRenderableBoxGroup iDhApiRenderableBoxGroup, DhApiRenderableBox dhApiRenderableBox, DhApiVec3d dhApiVec3d
   );
}
