package com.yungnickyoung.minecraft.yungsapi.world.structure.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.PieceEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

public class BiomeCondition extends StructureCondition {
   public static final MapCodec<BiomeCondition> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            TagKey.codec(Registries.BIOME).fieldOf("biome_tag").forGetter(condition -> condition.biomeTag),
            BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(condition -> condition.offset)
         )
         .apply(builder, BiomeCondition::new)
   );
   public final TagKey<Biome> biomeTag;
   public final BlockPos offset;

   public BiomeCondition(TagKey<Biome> biome, BlockPos offset) {
      this.biomeTag = biome;
      this.offset = offset;
   }

   @Override
   public StructureConditionType<?> type() {
      return StructureConditionType.BIOME;
   }

   @Override
   public boolean passes(StructureContext ctx) {
      BiomeSource biomeSource = ctx.biomeSource();
      PieceEntry pieceEntry = ctx.pieceEntry();
      RandomState randomState = ctx.randomState();
      if (biomeSource == null) {
         YungsApiCommon.LOGGER.error("Missing required field 'biomeSource' for biome condition!");
      }

      if (pieceEntry == null) {
         YungsApiCommon.LOGGER.error("Missing required field 'pieceEntry' for biome condition!");
      }

      if (randomState == null) {
         YungsApiCommon.LOGGER.error("Missing required field 'randomState' for biome condition!");
      }

      if (biomeSource != null && pieceEntry != null && randomState != null) {
         BlockPos checkPos = pieceEntry.getPiece().getPosition().offset(this.offset);
         Holder<Biome> biome = biomeSource.getNoiseBiome(
            QuartPos.fromBlock(checkPos.getX()), QuartPos.fromBlock(checkPos.getY()), QuartPos.fromBlock(checkPos.getZ()), randomState.sampler()
         );
         return biome.is(this.biomeTag);
      } else {
         return false;
      }
   }
}
