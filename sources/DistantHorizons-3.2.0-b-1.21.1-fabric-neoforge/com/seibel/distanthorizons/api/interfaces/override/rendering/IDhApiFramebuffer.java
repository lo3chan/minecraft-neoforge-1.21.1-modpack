package com.seibel.distanthorizons.api.interfaces.override.rendering;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;

public interface IDhApiFramebuffer extends IDhApiOverrideable {
   boolean overrideThisFrame();

   void bind();

   void addDepthAttachment(int i, boolean bl);

   int getId();

   int getStatus();

   void addColorAttachment(int i, int j);

   void destroy();
}
