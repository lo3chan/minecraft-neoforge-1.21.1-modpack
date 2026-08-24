package com.iafenvoy.origins.data.condition.builtin.entity;

import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.util.math.Comparison;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record InBlockAnywhereCondition(BlockCondition blockCondition, Comparison comparison) implements EntityCondition {
   public static final MapCodec<InBlockAnywhereCondition> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            BlockCondition.CODEC.fieldOf("block_condition").forGetter(InBlockAnywhereCondition::blockCondition),
            Comparison.optionalCodec(Comparison.CompareOperation.GREATER_THAN_OR_EQUAL, 1.0).forGetter(InBlockAnywhereCondition::comparison)
         )
         .apply(i, InBlockAnywhereCondition::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends EntityCondition> codec() {
      return CODEC;
   }

   @Override
   public boolean test(@NotNull Entity entity) {
      AABB boundingBox = entity.getBoundingBox();
      MutableBlockPos mutablePos = new MutableBlockPos();
      BlockPos minPos = BlockPos.containing(boundingBox.minX + 0.001, boundingBox.minY + 0.001, boundingBox.minZ + 0.001);
      BlockPos maxPos = BlockPos.containing(boundingBox.maxX - 0.001, boundingBox.maxY - 0.001, boundingBox.maxZ - 0.001);
      int matches = 0;

      for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
         for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
               mutablePos.set(x, y, z);
               if (this.blockCondition.test(entity.level(), mutablePos)) {
                  matches++;
               }
            }
         }
      }

      return this.comparison.compare(matches);
   }
}
