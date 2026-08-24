package dev.worldgen.lithostitched.api.worldgen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

public enum SurfaceSnap implements StringRepresentable {
   CEILING("ceiling", 1),
   FLOOR("floor", -1);

   public static final Codec<SurfaceSnap> CODEC = StringRepresentable.fromEnum(SurfaceSnap::values);
   private final String name;
   private final int offset;

   private SurfaceSnap(String name, int offset) {
      this.name = name;
      this.offset = offset;
   }

   public Optional<Integer> findY(BlockPos pos, GenerationContext context, LevelHeightAccessor heightAccessor, RandomState randomState) {
      NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), heightAccessor, randomState);
      int y = pos.getY();
      boolean lastCheckSolid = true;

      while (!heightAccessor.isOutsideBuildHeight(y)) {
         y += this.offset;
         boolean thisCheckSolid = column.getBlock(y).isSolid();
         if (!lastCheckSolid && thisCheckSolid) {
            return Optional.of(y);
         }

         lastCheckSolid = thisCheckSolid;
      }

      return Optional.empty();
   }

   public String getSerializedName() {
      return this.name;
   }
}
