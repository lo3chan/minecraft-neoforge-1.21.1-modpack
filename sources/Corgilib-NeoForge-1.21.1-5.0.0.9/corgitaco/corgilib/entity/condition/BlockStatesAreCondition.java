package corgitaco.corgilib.entity.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStatesAreCondition implements Condition {
   public static final Codec<BlockStatesAreCondition> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            BlockStatesAreCondition.BlockStateIs.CODEC
               .listOf()
               .fieldOf("has")
               .forGetter(blockStatesAreCondition -> new ArrayList<>(blockStatesAreCondition.blockStatesAre))
         )
         .apply(builder, BlockStatesAreCondition::new)
   );
   private final Set<BlockStatesAreCondition.BlockStateIs> blockStatesAre;

   public BlockStatesAreCondition(List<BlockStatesAreCondition.BlockStateIs> blockStatesAre) {
      this.blockStatesAre = new ObjectOpenHashSet(blockStatesAre);
   }

   @Override
   public boolean passes(ConditionContext conditionContext) {
      for (BlockStatesAreCondition.BlockStateIs blockStateIs : this.blockStatesAre) {
         BlockPos offsetPos = conditionContext.entity().blockPosition().offset(blockStateIs.offset);
         if (!blockStateIs.is.contains(conditionContext.world().getBlockState(offsetPos))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Codec<? extends Condition> codec() {
      return CODEC;
   }

   public static class BlockStateIs {
      public static final Codec<BlockStatesAreCondition.BlockStateIs> CODEC = RecordCodecBuilder.create(
         builder -> builder.group(
               BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(blockStateIs -> blockStateIs.offset),
               BlockState.CODEC.listOf().fieldOf("is").forGetter(blockStateIs -> new ArrayList<>(blockStateIs.is))
            )
            .apply(builder, BlockStatesAreCondition.BlockStateIs::new)
      );
      private final BlockPos offset;
      private final Set<BlockState> is;

      public BlockStateIs(BlockPos offset, Collection<BlockState> is) {
         this.offset = offset;
         this.is = new ObjectOpenHashSet(is);
      }
   }
}
