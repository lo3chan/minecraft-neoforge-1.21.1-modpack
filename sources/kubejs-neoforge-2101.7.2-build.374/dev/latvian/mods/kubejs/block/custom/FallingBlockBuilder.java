package dev.latvian.mods.kubejs.block.custom;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

@ReturnsSelf
public class FallingBlockBuilder extends BlockBuilder {
   private KubeColor dustColor = new SimpleColor(8420475);

   public FallingBlockBuilder(ResourceLocation i) {
      super(i);
   }

   public Block createObject() {
      return new FallingBlockBuilder.KubeJSFallingBlock(this.createProperties());
   }

   public FallingBlockBuilder dustColor(KubeColor color) {
      this.dustColor = color;
      return this;
   }

   static class KubeJSFallingBlock extends FallingBlock {
      private static final MapCodec<FallingBlockBuilder.KubeJSFallingBlock> CODEC = simpleCodec(FallingBlockBuilder.KubeJSFallingBlock::new);

      public KubeJSFallingBlock(Properties p) {
         super(p);
      }

      protected MapCodec<FallingBlockBuilder.KubeJSFallingBlock> codec() {
         return CODEC;
      }

      public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
         return ((FallingBlockBuilder)this.kjs$getBlockBuilder()).dustColor.kjs$getARGB();
      }
   }
}
