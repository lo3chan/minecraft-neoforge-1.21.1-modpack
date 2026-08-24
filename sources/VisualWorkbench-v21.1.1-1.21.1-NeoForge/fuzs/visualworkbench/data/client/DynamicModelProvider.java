package fuzs.visualworkbench.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class DynamicModelProvider extends AbstractModelProvider {
   public DynamicModelProvider(DataProviderContext context) {
      super(context);
   }

   public void addBlockModels(BlockModelGenerators builder) {
      ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(Blocks.STONE);
      BlockConversionHandler.getBlockConversions().values().forEach(block -> {
         builder.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation)));
         builder.skipAutoItemBlock(block);
      });
   }
}
