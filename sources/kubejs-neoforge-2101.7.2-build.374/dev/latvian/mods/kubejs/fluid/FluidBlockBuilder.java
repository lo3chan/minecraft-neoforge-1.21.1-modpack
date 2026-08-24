package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class FluidBlockBuilder extends BlockBuilder {
   public final FluidBuilder fluidBuilder;

   public FluidBlockBuilder(FluidBuilder b) {
      super(b.id);
      this.fluidBuilder = b;
      this.defaultTranslucent();
      this.noItem();
      this.noDrops();
      this.renderType(BlockRenderType.SOLID);
   }

   public Block createObject() {
      return new LiquidBlock(this.fluidBuilder.get(), Properties.ofFullCopy(Blocks.WATER).noCollission().strength(100.0F).noLootTable());
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.id, mg -> {
         mg.parent(null);
         mg.texture("particle", this.fluidBuilder.fluidType.stillTexture.toString());
      });
   }

   @Override
   public BlockBuilder item(@Nullable Consumer<ItemBuilder> i) {
      if (i != null) {
         throw new IllegalStateException("Fluid blocks cannot have items!");
      } else {
         return super.item(null);
      }
   }
}
