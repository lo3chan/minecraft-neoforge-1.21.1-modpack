package com.seibel.distanthorizons.api.interfaces.render;

import com.seibel.distanthorizons.api.enums.config.EDhApiRenderingApi;
import com.seibel.distanthorizons.api.objects.DhApiResult;

public interface IDhApiRenderProxy {
   DhApiResult<Boolean> clearRenderDataCache();

   EDhApiRenderingApi getRenderingApi() throws IllegalStateException;

   boolean isNativeRenderer() throws IllegalStateException;

   DhApiResult<Integer> getDhDepthTextureId();

   DhApiResult<Integer> getDhColorTextureId();

   void setDeferTransparentRendering(boolean bl);

   boolean getDeferTransparentRendering();

   float getNearClipPlaneDistanceInBlocks(float f);
}
