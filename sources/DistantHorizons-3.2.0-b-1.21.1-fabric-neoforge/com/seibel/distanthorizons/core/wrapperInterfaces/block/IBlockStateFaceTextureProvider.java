package com.seibel.distanthorizons.core.wrapperInterfaces.block;

import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockFaceTexture;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import org.jetbrains.annotations.Nullable;

public interface IBlockStateFaceTextureProvider extends IBindable {
   @Nullable
   BlockFaceTexture getFaceTexture(IBlockStateWrapper iBlockStateWrapper, EDhDirection eDhDirection);

   void clear();
}
