package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class SlabBlockBuilder extends ShapedBlockBuilder {
   public static final ResourceLocation[] SLAB_TAGS = new ResourceLocation[]{BlockTags.SLABS.location()};
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/slab");
   private static final ResourceLocation TOP_MODEL = ResourceLocation.withDefaultNamespace("block/slab_top");

   public SlabBlockBuilder(ResourceLocation i) {
      super(i, "_slab");
      this.tagBoth(SLAB_TAGS);
   }

   public Block createObject() {
      return new SlabBlock(this.createProperties());
   }

   @Override
   protected void generateBlockState(VariantBlockStateGenerator bs) {
      bs.variant("type=bottom", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(this.id.withPath(ID.BLOCK))));
      bs.variant("type=top", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(this.newID("block/", "_top"))));
      bs.variant("type=double", (Consumer<VariantBlockStateGenerator.Variant>)(v -> v.model(this.newID("block/", "_double"))));
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.id, m -> {
         m.parent(MODEL);
         m.texture("bottom", this.baseTexture);
         m.texture("top", this.baseTexture);
         m.texture("side", this.baseTexture);
      });
      generator.blockModel(this.newID("", "_top"), m -> {
         m.parent(TOP_MODEL);
         m.texture("bottom", this.baseTexture);
         m.texture("top", this.baseTexture);
         m.texture("side", this.baseTexture);
      });
      generator.blockModel(this.newID("", "_double"), m -> {
         m.parent(KubeAssetGenerator.CUBE_ALL_BLOCK_MODEL);
         m.texture("all", this.baseTexture);
      });
   }
}
