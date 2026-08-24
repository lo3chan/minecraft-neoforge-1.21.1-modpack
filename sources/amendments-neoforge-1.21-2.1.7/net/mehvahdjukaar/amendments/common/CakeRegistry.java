package net.mehvahdjukaar.amendments.common;

import java.util.Optional;
import net.mehvahdjukaar.amendments.common.block.DirectionalCakeBlock;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.DummyBlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CakeRegistry extends BlockTypeRegistry<CakeRegistry.CakeType> {
   public static final CakeRegistry INSTANCE = new CakeRegistry();
   public static final CakeRegistry.CakeType VANILLA = new CakeRegistry.CakeType(ResourceLocation.withDefaultNamespace("cake"), Blocks.CAKE);

   private CakeRegistry() {
      super(CakeRegistry.CakeType.class, "cake");
   }

   public CakeRegistry.CakeType getDefaultType() {
      return VANILLA;
   }

   public Optional<CakeRegistry.CakeType> detectTypeFromBlock(Block block, ResourceLocation blockId) {
      if ((block instanceof CakeBlock || blockId.getPath().contains("cake") && block.defaultBlockState().hasProperty(CakeBlock.BITES))
         && !(block instanceof DirectionalCakeBlock)
         && !(block instanceof EntityBlock)) {
         BlockState def = block.defaultBlockState();
         if (def.getShape(DummyBlockGetter.INSTANCE, BlockPos.ZERO)
            .bounds()
            .equals(Blocks.CAKE.defaultBlockState().getShape(DummyBlockGetter.INSTANCE, BlockPos.ZERO).bounds())) {
            String namespace = blockId.getNamespace();
            if (!namespace.equals("tconstruct") && !namespace.equals("enigmaticlegacy")) {
               return Optional.of(new CakeRegistry.CakeType(blockId, block));
            }
         }
      }

      return Optional.empty();
   }

   public static class CakeType extends BlockType {
      public final Block cake;

      public CakeType(ResourceLocation name, Block cake) {
         super(name);
         this.cake = cake;
      }

      public String getTranslationKey() {
         return "cake";
      }

      public ItemLike mainChild() {
         return this.cake;
      }

      protected void initializeChildrenBlocks() {
         this.addChild("cake", this.cake);
      }

      protected void initializeChildrenItems() {
      }
   }
}
