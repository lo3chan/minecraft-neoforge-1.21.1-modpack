package com.seibel.distanthorizons.api.interfaces.override.rendering;

import com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;

public interface IDhApiCullingFrustum extends IDhApiOverrideable {
   void update(int i, int j, DhApiMat4f dhApiMat4f);

   boolean intersects(int i, int j, int k, int l);
}
