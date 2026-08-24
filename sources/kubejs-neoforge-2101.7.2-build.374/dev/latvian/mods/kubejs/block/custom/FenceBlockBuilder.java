package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.neoforged.neoforge.common.Tags.Blocks;

public class FenceBlockBuilder extends ShapedBlockBuilder {
   public static final ResourceLocation[] FENCE_TAGS = new ResourceLocation[]{BlockTags.FENCES.location(), Blocks.FENCES.location()};
   private static final ResourceLocation SIDE_MODEL = ResourceLocation.withDefaultNamespace("block/fence_side");
   private static final ResourceLocation POST_MODEL = ResourceLocation.withDefaultNamespace("block/fence_post");
   private static final ResourceLocation INVENTORY_MODEL = ResourceLocation.withDefaultNamespace("block/fence_inventory");

   public FenceBlockBuilder(ResourceLocation i) {
      super(i, "_fence");
      this.tagBoth(FENCE_TAGS);
   }

   public Block createObject() {
      return new FenceBlock(this.createProperties());
   }

   @Override
   protected boolean useMultipartBlockState() {
      return true;
   }

   @Override
   protected void generateMultipartBlockState(MultipartBlockStateGenerator bs) {
      ResourceLocation modPost = this.newID("block/", "_post");
      ResourceLocation modSide = this.newID("block/", "_side");
      bs.part("", modPost);
      bs.part("north=true", (Consumer<MultipartBlockStateGenerator.Part>)(p -> p.model(modSide).uvlock()));
      bs.part("east=true", (Consumer<MultipartBlockStateGenerator.Part>)(p -> p.model(modSide).uvlock().y(90)));
      bs.part("south=true", (Consumer<MultipartBlockStateGenerator.Part>)(p -> p.model(modSide).uvlock().y(180)));
      bs.part("west=true", (Consumer<MultipartBlockStateGenerator.Part>)(p -> p.model(modSide).uvlock().y(270)));
   }

   @Override
   protected void generateItemModel(ModelGenerator m) {
      m.parent(INVENTORY_MODEL);
      m.texture("texture", this.baseTexture);
   }

   @Override
   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.newID("", "_post"), m -> {
         m.parent(POST_MODEL);
         m.texture("texture", this.baseTexture);
      });
      generator.blockModel(this.newID("", "_side"), m -> {
         m.parent(SIDE_MODEL);
         m.texture("texture", this.baseTexture);
      });
   }
}
