package com.seibel.distanthorizons.common.wrappers;

import com.seibel.distanthorizons.api.interfaces.block.IDhApiBiomeWrapper;
import com.seibel.distanthorizons.api.interfaces.block.IDhApiBlockStateWrapper;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.chunk.ChunkWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.common.wrappers.worldGeneration.BatchGenerationEnvironment_fabric;
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
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_3218;
import net.minecraft.class_638;
import net.minecraft.class_6880;

public class WrapperFactory_fabric implements IWrapperFactory {
   public static final WrapperFactory_fabric INSTANCE = new WrapperFactory_fabric();
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
         return new BatchGenerationEnvironment_fabric((IDhServerLevel)targetLevel);
      } else {
         throw new IllegalArgumentException("The target level must be a server-side level.");
      }
   }

   @Override
   public IDhApiBiomeWrapper getBiomeWrapper(String resourceLocationString, IDhApiLevelWrapper levelWrapper) throws IOException, ClassCastException {
      if (!(levelWrapper instanceof ILevelWrapper)) {
         throw new ClassCastException("levelWrapper must be returned by DH and of type [" + ILevelWrapper.class.getName() + "].");
      } else {
         return BiomeWrapper_fabric.deserialize(resourceLocationString, (ILevelWrapper)levelWrapper);
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
         return BlockStateWrapper_fabric.deserialize(resourceLocationString, (ILevelWrapper)levelWrapper);
      }
   }

   @Override
   public IBiomeWrapper deserializeBiomeWrapper(String str, ILevelWrapper levelWrapper) throws IOException {
      return BiomeWrapper_fabric.deserialize(str, levelWrapper);
   }

   @Override
   public IBiomeWrapper getPlainsBiomeWrapper(ILevelWrapper levelWrapper) {
      try {
         return BiomeWrapper_fabric.deserialize("minecraft:plains", levelWrapper);
      } catch (IOException var3) {
         throw new LodUtil.AssertFailureException("Unable to parse plains resource string [minecraft:plains], error:\n " + var3.getMessage());
      }
   }

   @Override
   public IBlockStateWrapper deserializeBlockStateWrapper(String str, ILevelWrapper levelWrapper) throws IOException {
      return BlockStateWrapper_fabric.deserialize(str, levelWrapper);
   }

   @Override
   public IBlockStateWrapper getAirBlockStateWrapper() {
      return BlockStateWrapper_fabric.AIR;
   }

   @Override
   public IBlockStateWrapper getWaterBlockStateWrapper(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_fabric.getWaterBlockStateWrapper(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_fabric.getRendererIgnoredBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getRendererIgnoredCaveBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_fabric.getRendererIgnoredCaveBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getWaterSubsurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_fabric.getWaterSubsurfaceReplacementBlocks(levelWrapper);
   }

   @Override
   public ObjectOpenHashSet<IBlockStateWrapper> getWaterSurfaceReplacementBlocks(ILevelWrapper levelWrapper) {
      return BlockStateWrapper_fabric.getWaterSurfaceReplacementBlocks(levelWrapper);
   }

   @Override
   public void resetCachedIgnoredBlocksSets() {
      BlockStateWrapper_fabric.clearCachedIgnoreBlocks();
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
         boolean chunkClassCorrect = objectArray[0] instanceof class_2791;
         if (!chunkClassCorrect) {
            throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
         } else {
            class_2791 chunk = (class_2791)objectArray[0];
            boolean levelClassCorrect = objectArray[1] instanceof class_1937;
            if (!levelClassCorrect) {
               throw new ClassCastException(createChunkWrapperErrorMessage(objectArray));
            } else {
               class_1937 level = (class_1937)objectArray[1];
               boolean isClientSide = level.method_8608();
               ILevelWrapper levelWrapper;
               if (isClientSide) {
                  levelWrapper = ClientLevelWrapper_fabric.getWrapper((class_638)level);
               } else {
                  levelWrapper = ServerLevelWrapper_fabric.getWrapper((class_3218)level);
               }

               return new ChunkWrapper_fabric(chunk, levelWrapper);
            }
         }
      }
   }

   private static String createChunkWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{class_2791.class.getName(), "[ServerLevel] or [ClientLevel]"};
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
      } else if (objectArray[0] instanceof class_6880 && ((class_6880)objectArray[0]).comp_349() instanceof class_1959) {
         class_6880<class_1959> biomeHolder = (class_6880<class_1959>)objectArray[0];
         return BiomeWrapper_fabric.getBiomeWrapper(biomeHolder, coreLevelWrapper);
      } else {
         throw new ClassCastException(createBiomeWrapperErrorMessage(objectArray));
      }
   }

   private static String createBiomeWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{class_6880.class.getName() + "<" + class_1959.class.getName() + ">"};
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
         boolean blockClassCorrect = objectArray[0] instanceof class_2680;
         if (!blockClassCorrect) {
            throw new ClassCastException(createBlockStateWrapperErrorMessage(objectArray));
         } else {
            class_2680 blockState = (class_2680)objectArray[0];
            return BlockStateWrapper_fabric.fromBlockState(blockState, coreLevelWrapper);
         }
      }
   }

   private static String createBlockStateWrapperErrorMessage(Object[] objectArray) {
      String[] expectedClassNames = new String[]{class_6880.class.getName() + "<" + class_1959.class.getName() + ">"};
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
