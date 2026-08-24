package net.astralya.hexalia.worldgen.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.block.custom.SilkwormCocoonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;

public class CocoonTreeDecorator extends TreeDecorator {
   public static final MapCodec<CocoonTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(d -> d.chance)).apply(instance, CocoonTreeDecorator::new)
   );
   private final float chance;

   public CocoonTreeDecorator(float chance) {
      this.chance = chance;
   }

   protected TreeDecoratorType<?> type() {
      return (TreeDecoratorType<?>)ModTreeDecorators.COCOON_TREE.get();
   }

   public void place(Context context) {
      RandomSource random = context.random();
      ObjectListIterator var3 = context.logs().iterator();

      while (var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         if (random.nextFloat() <= this.chance) {
            Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            Direction direction = directions[random.nextInt(directions.length)];
            BlockPos target = pos.relative(direction);
            if (context.isAir(target) && context.isAir(target.below())) {
               context.setBlock(
                  target, (BlockState)((Block)ModBlocks.SILKWORM_COCOON.get()).defaultBlockState().setValue(SilkwormCocoonBlock.FACING, direction)
               );
               break;
            }
         }
      }
   }
}
