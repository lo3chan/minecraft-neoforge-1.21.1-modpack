package com.seibel.distanthorizons.core.wrapperInterfaces;

import com.seibel.distanthorizons.api.interfaces.factories.IDhApiWrapperFactory;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.ILodContainerUniformBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.worldGeneration.IBatchGeneratorEnvironmentWrapper;
import com.seibel.distanthorizons.coreapi.interfaces.dependencyInjection.IBindable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;

public interface IWrapperFactory extends IDhApiWrapperFactory, IBindable {
   IBatchGeneratorEnvironmentWrapper createBatchGenerator(IDhLevel iDhLevel);

   IBiomeWrapper deserializeBiomeWrapper(String string, ILevelWrapper iLevelWrapper) throws IOException;

   IBiomeWrapper getPlainsBiomeWrapper(ILevelWrapper iLevelWrapper);

   default IBiomeWrapper deserializeBiomeWrapperOrGetDefault(String str, ILevelWrapper levelWrapper) {
      IBiomeWrapper biome;
      try {
         biome = this.deserializeBiomeWrapper(str, levelWrapper);
      } catch (IOException var5) {
         biome = this.getPlainsBiomeWrapper(levelWrapper);
      }

      return biome;
   }

   IBlockStateWrapper deserializeBlockStateWrapper(String string, ILevelWrapper iLevelWrapper) throws IOException;

   IBlockStateWrapper getAirBlockStateWrapper();

   IBlockStateWrapper getWaterBlockStateWrapper(ILevelWrapper iLevelWrapper);

   default IBlockStateWrapper deserializeBlockStateWrapperOrGetDefault(String str, ILevelWrapper levelWrapper) {
      IBlockStateWrapper blockState;
      try {
         blockState = this.deserializeBlockStateWrapper(str, levelWrapper);
      } catch (IOException var5) {
         blockState = this.getAirBlockStateWrapper();
      }

      return blockState;
   }

   ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredBlocks(ILevelWrapper iLevelWrapper);

   ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredCaveBlocks(ILevelWrapper iLevelWrapper);

   ObjectOpenHashSet<IBlockStateWrapper> getWaterSubsurfaceReplacementBlocks(ILevelWrapper iLevelWrapper);

   ObjectOpenHashSet<IBlockStateWrapper> getWaterSurfaceReplacementBlocks(ILevelWrapper iLevelWrapper);

   void resetCachedIgnoredBlocksSets();

   IChunkWrapper createChunkWrapper(Object[] objects) throws ClassCastException;

   IVertexBufferWrapper createVboWrapper(String string);

   ILodContainerUniformBufferWrapper createLodContainerUniformWrapper();

   IDhGenericObjectVertexBufferContainer createGenericObjectVboContainer();

   IDhGenericRenderer createGenericRenderer();
}
