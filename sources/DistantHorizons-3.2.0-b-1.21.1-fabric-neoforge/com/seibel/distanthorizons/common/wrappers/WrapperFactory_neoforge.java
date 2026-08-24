package com.seibel.distanthorizons.common.wrappers;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_neoforge;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.level.IDhServerLevel;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.AbstractDhRenderApiDefinition;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IDhGenericObjectVertexBufferContainer;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.ILodContainerUniformBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.objects.IVertexBufferWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.render.renderPass.IDhGenericRenderer;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.worldGeneration.IBatchGeneratorEnvironmentWrapper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WrapperFactory_neoforge implements IWrapperFactory {
   public static final WrapperFactory_neoforge INSTANCE = new WrapperFactory_neoforge();
   private AbstractDhRenderApiDefinition renderDefinition;

   private AbstractDhRenderApiDefinition getRenderDefinition() {
      if (this.renderDefinition != null) {
         return this.renderDefinition;
      } else {
         this.renderDefinition = SingletonInjector.INSTANCE.get(AbstractDhRenderApiDefinition.class);
         return this.renderDefinition;
      }
   }

   @Override
   public IBatchGeneratorEnvironmentWrapper createBatchGenerator(IDhLevel targetLevel) {
      if (targetLevel instanceof IDhServerLevel) {
         return new BatchGenerationEnvironment_neoforge((IDhServerLevel)targetLevel);
      } else {
         throw new IllegalArgumentException("The target level must be a server-side level.");
      }
   }

   @Override
   public IDhApiBiomeWrapper getBiomeWrapper(String resourceLocationString, IDhApiLevelWrapper levelWrapper) throws IOException, ClassCastException {
      if (!(levelWrapper instanceof ILevelWrapper)) {
         throw new ClassCastException("levelWrapper must be returned by DH and of type [" + ILevelWrapper.class.getName() + "].");
      } else {
         return BiomeWrapper_neoforge.deserialize(resourceLocationString, (ILevelWrapper)levelWrapper);
      }
   }

   @Override
   public IDhApiBlockStateWrapper getDefaultBlockStateWrapper(String resourceLocationString, IDhApiLevelWrapper levelWrapper) throws IOException, ClassCastException {
      if (!(levelWrapper instanceof ILevelWrapper)) {
         throw new ClassCastException(
            "Invalid ["
               + IDhApiLevelWrapper.class.getSimpleName()
               + "] value given. Level wrapper object must be one given by the DH API (it can't be a custom implementation), specifically of type ["
               + ILevelWrapper.class.getName()
               + "]."
         );
      } else {
         return BlockStateWrapper_neoforge.deserialize(resourceLocationString, (ILevelWrapper)levelWrapper);
      }
   }

   @Override
   public IBiomeWrapper deserializeBiomeWrapper(String str, ILevelWrapper levelWrapper) throws IOException {
      return BiomeWrapper_neoforge.deserialize(str, levelWrapper);
   }

   @Override
   public IBiomeWrapper getPlainsBiomeWrapper(ILevelWrapper levelWrapper) {
      try {
         return BiomeWrapper_neoforge.deserialize("minecraft:plains", levelWrapper);
      } catch (IOException var3) {
         throw new LodUtil.AssertFailureException("Unable to parse plains resource string [minecraft:plains], error:\n " + var3.getMessage());
      }
   }

   @Override
   public IBlockStateWrapper deserializeBlockStateWrapper(String str, ILevelWrapper levelWrapper) throws IOException {
      return BlockStateWrapper_neoforge.deserialize(str, levelWrapper);
   }

   @Override
   public IBlockStateWrapper getAirBlockStateWrapper() {
      return BlockStateWrapper_neoforge.AIR;
   }

   @Override
   public IBlockStateWrapper getWaterBlockStateWrapper(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_neoforge.getWaterBlockStateWrapper(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_neoforge.getRendererIgnoredBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredCaveBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_neoforge.getRendererIgnoredCaveBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getWaterSubsurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_neoforge.getWaterSubsurfaceReplacementBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getWaterSurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_neoforge.getWaterSurfaceReplacementBlocks(levelWrapper);
   }

   @Override
   public void resetCachedIgnoredBlocksSets() {
      BlockStateWrapper_neoforge.clearCachedIgnoreBlocks();
   }

   @Override
   public IChunkWrapper createChunkWrapper(Object[] objectArray) throws ClassCastException {
      if (objectArray.length == 1 && objectArray[0] instanceof IChunkWrapper) {
         try {
            return (IChunkWrapper)objectArray[0];
         } catch (Exception var8) {
            throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
         }
      } else if (objectArray.length != 2) {
         throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
      } else {
         boolean chunkClassCorrect = objectArray[0] instanceof ChunkAccess;
         if (!chunkClassCorrect) {
            throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
         } else {
            ChunkAccess chunk = (ChunkAccess)objectArray[0];
            boolean levelClassCorrect = objectArray[1] instanceof Level;
            if (!levelClassCorrect) {
               throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
            } else {
               Level level = (Level)objectArray[1];
               boolean isClientSide = level.isClientSide();
               ILevelWrapper levelWrapper;
               if (isClientSide) {
                  levelWrapper = ClientLevelWrapper_neoforge.getWrapper((ClientLevel)level);
               } else {
                  levelWrapper = ServerLevelWrapper_neoforge.getWrapper((ServerLevel)level);
               }

               return new ChunkWrapper_neoforge(chunk, levelWrapper);
            }
         }
      }
   }

   private static String createChunkWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{ChunkAccess.class.getName(), "[ServerLevel] or [ClientLevel]"};
      return createWrapperErrorMessage("Chunk wrapper", expectedClassNames, objectArray);
   }

   @Override
   public IVertexBufferWrapper createVboWrapper(String name) {
      return this.getRenderDefinition().createVboWrapper(name);
   }

   @Override
   public ILodContainerUniformBufferWrapper createLodContainerUniformWrapper() {
      return this.getRenderDefinition().createLodContainerUniformWrapper();
   }

   @Override
   public IDhGenericObjectVertexBufferContainer createGenericObjectVboContainer() {
      return this.getRenderDefinition().createGenericVboContainer();
   }

   @Override
   public IDhGenericRenderer createGenericRenderer() {
      return this.getRenderDefinition().createGenericRenderer();
   }

   @Override
   public IDhApiBiomeWrapper getBiomeWrapper(Object[] objectArray, IDhApiLevelWrapper levelWrapper) {
      if (!(levelWrapper instanceof ILevelWrapper coreLevelWrapper)) {
         throw new ClassCastException(
            "Invalid ["
               + IDhApiLevelWrapper.class.getSimpleName()
               + "] value given. Level wrapper object must be one given by the DH API (it can't be a custom implementation), specifically of type ["
               + ILevelWrapper.class.getName()
               + "]."
         );
      } else if (objectArray[0] instanceof Holder && ((Holder)objectArray[0]).value() instanceof Biome) {
         Holder<Biome> biomeHolder = (Holder<Biome>)objectArray[0];
         return BiomeWrapper_neoforge.getBiomeWrapper(biomeHolder, coreLevelWrapper);
      } else {
         throw new ClassCastException(createBiomeWrapperErrorMessage(objectArray));
      }
   }

   private static String createBiomeWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{Holder.class.getName() + "<" + Biome.class.getName() + ">"};
      return createWrapperErrorMessage("Biome wrapper", expectedClassNames, objectArray);
   }

   @Override
   public IDhApiBlockStateWrapper getBlockStateWrapper(Object[] objectArray, IDhApiLevelWrapper levelWrapper) {
      if (!(levelWrapper instanceof ILevelWrapper coreLevelWrapper)) {
         throw new ClassCastException(
            "Invalid ["
               + IDhApiLevelWrapper.class.getSimpleName()
               + "] value given. Level wrapper object must be one given by the DH API (it can't be a custom implementation), specifically of type ["
               + ILevelWrapper.class.getName()
               + "]."
         );
      } else if (objectArray.length != 1) {
         throw new ClassCastException(createBlockStateWrapperErrorMessage(objectArray));
      } else {
         boolean blockClassCorrect = objectArray[0] instanceof BlockState;
         if (!blockClassCorrect) {
            throw new ClassCastException(createBlockStateWrapperErrorMessage(objectArray));
         } else {
            BlockState blockState = (BlockState)objectArray[0];
            return BlockStateWrapper_neoforge.fromBlockState(blockState, coreLevelWrapper);
         }
      }
   }

   private static String createBlockStateWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{Holder.class.getName() + "<" + Biome.class.getName() + ">"};
      return createWrapperErrorMessage("BlockState wrapper", expectedClassNames, objectArray);
   }

   private static String createWrapperErrorMessage(String wrapperName, String[] expectedClassNames, Object[] objectArray) {
      StringBuilder message = new StringBuilder(wrapperName + " creation failed. \nExpected object array parameters: \n");

      for (String expectedClassName : expectedClassNames) {
         message.append("[").append(expectedClassName).append("], \n");
      }

      if (objectArray.length != 0) {
         message.append("Given parameters: ");

         for (Object obj : objectArray) {
            String objClassName = obj != null ? obj.getClass().getName() : "NULL";
            message.append("[").append(objClassName).append("], ");
         }
      } else {
         message.append(" No parameters given.");
      }

      return message.toString();
   }
}
