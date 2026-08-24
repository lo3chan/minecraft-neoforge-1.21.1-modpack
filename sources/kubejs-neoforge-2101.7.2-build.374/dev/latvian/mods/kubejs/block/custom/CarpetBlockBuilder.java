package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;

@ReturnsSelf
public class CarpetBlockBuilder extends ShapedBlockBuilder {
   public static final ResourceLocation[] CARPET_TAGS = new ResourceLocation[]{BlockTags.WOOL_CARPETS.location()};
   private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/carpet");

   public CarpetBlockBuilder(ResourceLocation i) {
      super(i, "_carpet");
      this.tagBoth(CARPET_TAGS);
   }

   public Block createObject() {
      return new CarpetBlock(this.createProperties());
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.id, m -> {
         m.parent(MODEL);
         m.texture("wool", this.baseTexture);
      });
   }
}
