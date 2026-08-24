package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.dataObjects.render.textures.BlockFaceTexture;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateFaceTextureProvider;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import org.jetbrains.annotations.Nullable;

public class BlockStateTextureProvider_neoforge implements IBlockStateFaceTextureProvider {
   public static final BlockStateTextureProvider_neoforge INSTANCE = new BlockStateTextureProvider_neoforge();

   @Nullable
   @Override
   public BlockFaceTexture getFaceTexture(IBlockStateWrapper blockState, EDhDirection direction) {
      if (!(blockState instanceof BlockStateWrapper_neoforge)) {
         throw new UnsupportedOperationException("blockState must be a [" + BlockStateWrapper_neoforge.class.getSimpleName() + "]");
      } else {
         return ClientBlockStateTextureCache_neoforge.getFaceTexture((BlockStateWrapper_neoforge)blockState, direction);
      }
   }

   @Override
   public void clear() {
      ClientBlockStateTextureCache_neoforge.clearCache();
      ClientBlockStateColorCache_neoforge.clearCachedTints();
   }
}
