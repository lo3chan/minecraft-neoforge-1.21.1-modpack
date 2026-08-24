package com.seibel.distanthorizons.api.interfaces.override.rendering;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiRenderParam;
import com.seibel.distanthorizons.api.objects.math.DhApiVec3f;

public interface IDhApiShaderProgram extends IDhApiOverrideable {
   boolean overrideThisFrame();

   int getId();

   void free();

   void bind();

   void unbind();

   void fillUniformData(DhApiRenderParam dhApiRenderParam);

   void setModelOffsetPos(DhApiVec3f dhApiVec3f);

   void bindVertexBuffer(int i);
}
