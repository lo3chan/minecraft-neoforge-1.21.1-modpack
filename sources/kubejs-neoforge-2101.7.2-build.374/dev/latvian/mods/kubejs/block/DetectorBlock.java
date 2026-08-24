package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class DetectorBlock extends Block {
   private final DetectorBlock.Builder builder;

   public DetectorBlock(DetectorBlock.Builder b) {
      super(Properties.ofFullCopy(Blocks.BEDROCK));
      this.builder = b;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BlockStateProperties.POWERED, false));
   }

   @Deprecated
   public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
      boolean p = !(Boolean)blockState.getValue(BlockStateProperties.POWERED);
      if (p == level.hasNeighborSignal(blockPos)) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(BlockStateProperties.POWERED, p), 2);
         if (BlockEvents.DETECTOR_CHANGED.hasListeners(this.builder.detectorId)
            || (p ? BlockEvents.DETECTOR_POWERED : BlockEvents.DETECTOR_UNPOWERED).hasListeners(this.builder.detectorId)) {
            DetectorBlockKubeEvent e = new DetectorBlockKubeEvent(this.builder.detectorId, level, blockPos, p);
            BlockEvents.DETECTOR_CHANGED.post(level, this.builder.detectorId, e);
            if (p) {
               BlockEvents.DETECTOR_POWERED.post(level, this.builder.detectorId, e);
            } else {
               BlockEvents.DETECTOR_UNPOWERED.post(level, this.builder.detectorId, e);
            }
         }
      }
   }

   protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BlockStateProperties.POWERED});
   }

   @ReturnsSelf
   public static class Builder extends BlockBuilder {
      private static final ResourceLocation OFF_MODEL = KubeJS.id("block/detector");
      private static final ResourceLocation ON_MODEL = KubeJS.id("block/detector_on");
      public transient String detectorId = (this.id.getNamespace().equals("kubejs") ? "" : this.id.getNamespace() + ".") + this.id.getPath().replace('/', '.');

      public Builder(ResourceLocation i) {
         super(i);
         if (this.detectorId.endsWith("_detector")) {
            this.detectorId = this.detectorId.substring(0, this.detectorId.length() - 9);
         }

         if (this.detectorId.startsWith("detector_")) {
            this.detectorId = this.detectorId.substring(9);
         }

         this.displayName(Component.literal("KubeJS Detector [" + this.detectorId + "]"));
      }

      public DetectorBlock.Builder detectorId(String id) {
         this.detectorId = id;
         this.displayName(Component.literal("KubeJS Detector [" + this.detectorId + "]"));
         return this;
      }

      public Block createObject() {
         return new DetectorBlock(this);
      }

      @Override
      protected void generateBlockState(VariantBlockStateGenerator bs) {
         bs.simpleVariant("powered=false", OFF_MODEL);
         bs.simpleVariant("powered=true", ON_MODEL);
      }

      @Override
      protected void generateItemModel(ModelGenerator m) {
         m.parent(OFF_MODEL);
      }
   }
}
