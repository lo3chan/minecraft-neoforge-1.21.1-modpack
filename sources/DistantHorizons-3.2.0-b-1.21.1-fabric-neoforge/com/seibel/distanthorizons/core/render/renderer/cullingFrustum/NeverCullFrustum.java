package com.seibel.distanthorizons.core.render.renderer.cullingFrustum;

import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiCullingFrustum;
import com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiShadowCullingFrustum;
import com.seibel.distanthorizons.api.objects.math.DhApiMat4f;

public class NeverCullFrustum implements IDhApiCullingFrustum, IDhApiShadowCullingFrustum {
   @Override
   public void update(int worldMinBlockY, int worldMaxBlockY, DhApiMat4f dhWorldViewProjection) {
   }

   @Override
   public boolean intersects(int lodBlockPosMinX, int lodBlockPosMinZ, int lodBlockWidth, int lodDetailLevel) {
      return true;
   }

   @Override
   public int getPriority() {
      return -1;
   }
}
