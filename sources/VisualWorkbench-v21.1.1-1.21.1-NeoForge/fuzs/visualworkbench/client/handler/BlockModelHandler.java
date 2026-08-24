package fuzs.visualworkbench.client.handler;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockModelHandler {
   private static final Supplier<Map<ModelResourceLocation, ModelResourceLocation>> MODEL_LOCATIONS = Suppliers.memoize(
      () -> BlockConversionHandler.getBlockConversions()
         .entrySet()
         .stream()
         .flatMap(entry -> convertAllBlockStates((Block)entry.getValue(), (Block)entry.getKey()).entrySet().stream())
         .collect(Util.toMap())
   );

   public static void onLoadComplete() {
      for (Entry<Block, Block> entry : BlockConversionHandler.getBlockConversions().entrySet()) {
         RenderType renderType = ClientAbstractions.INSTANCE.getRenderType(entry.getKey());
         ClientAbstractions.INSTANCE.registerRenderType(entry.getValue(), renderType);
      }
   }

   public static EventResultHolder<UnbakedModel> onModifyUnbakedModel(
      ModelResourceLocation modelLocation,
      Supplier<UnbakedModel> unbakedModel,
      Function<ModelResourceLocation, UnbakedModel> modelGetter,
      BiConsumer<ResourceLocation, UnbakedModel> modelAdder
   ) {
      return MODEL_LOCATIONS.get().containsKey(modelLocation)
         ? EventResultHolder.interrupt(modelGetter.apply(MODEL_LOCATIONS.get().get(modelLocation)))
         : EventResultHolder.pass();
   }

   private static Map<ModelResourceLocation, ModelResourceLocation> convertAllBlockStates(Block oldBlock, Block newBlock) {
      Map<ModelResourceLocation, ModelResourceLocation> modelLocations = Maps.newHashMap();
      UnmodifiableIterator var3 = oldBlock.getStateDefinition().getPossibleStates().iterator();

      while (var3.hasNext()) {
         BlockState oldBlockState = (BlockState)var3.next();
         BlockState newBlockState = convertBlockState(newBlock.getStateDefinition(), oldBlockState);
         modelLocations.put(BlockModelShaper.stateToModelLocation(oldBlockState), BlockModelShaper.stateToModelLocation(newBlockState));
      }

      return modelLocations;
   }

   private static BlockState convertBlockState(StateDefinition<Block, BlockState> newStateDefinition, BlockState oldBlockState) {
      BlockState newBlockState = (BlockState)newStateDefinition.any();

      for (Entry<Property<?>, Comparable<?>> entry : oldBlockState.getValues().entrySet()) {
         newBlockState = setBlockStateValue(entry.getKey(), entry.getValue(), newStateDefinition::getProperty, newBlockState);
      }

      return newBlockState;
   }

   private static <T extends Comparable<T>, V extends T> BlockState setBlockStateValue(
      Property<?> oldProperty, Comparable<?> oldValue, Function<String, Property<?>> propertyGetter, BlockState blockState
   ) {
      Property<?> newProperty = propertyGetter.apply(oldProperty.getName());
      return newProperty != null ? (BlockState)blockState.setValue(newProperty, oldValue) : blockState;
   }
}
