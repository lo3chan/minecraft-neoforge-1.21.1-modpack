package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockFaceTexture;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateFaceTextureProvider;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import org.jetbrains.annotations.Nullable;

public class BlockStateTextureProvider_fabric implements IBlockStateFaceTextureProvider {
   public static final BlockStateTextureProvider_fabric INSTANCE = new BlockStateTextureProvider_fabric();

   @Nullable
   @Override
   public BlockFaceTexture getFaceTexture(IBlockStateWrapper blockState, EDhDirection direction) {
      if (!(blockState instanceof BlockStateWrapper_fabric)) {
         throw new UnsupportedOperationException("blockState must be a [" + BlockStateWrapper_fabric.class.getSimpleName() + "]");
      } else {
         return ClientBlockStateTextureCache_fabric.getFaceTexture((BlockStateWrapper_fabric)blockState, direction);
      }
   }

   @Override
   public void clear() {
      ClientBlockStateTextureCache_fabric.clearCache();
      ClientBlockStateColorCache_fabric.clearCachedTints();
   }
}
