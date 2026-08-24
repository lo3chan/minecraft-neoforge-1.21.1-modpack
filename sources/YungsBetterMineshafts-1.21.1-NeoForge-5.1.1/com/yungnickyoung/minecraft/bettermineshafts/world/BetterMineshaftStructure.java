package com.yungnickyoung.minecraft.bettermineshafts.world;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshaftsCommon;
import com.yungnickyoung.minecraft.bettermineshafts.module.StructureTypeModule;
import com.yungnickyoung.minecraft.bettermineshafts.world.config.BetterMineshaftConfiguration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.BetterMineshaftPiece;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.VerticalEntrance;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BetterMineshaftStructure extends Structure {
   public static final MapCodec<BetterMineshaftStructure> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(settingsCodec(builder), BetterMineshaftConfiguration.CODEC.fieldOf("config").forGetter(structure -> structure.config))
         .apply(builder, BetterMineshaftStructure::new)
   );
   private final BetterMineshaftConfiguration config;

   public BetterMineshaftStructure(StructureSettings settings, BetterMineshaftConfiguration config) {
      super(settings);
      this.config = config;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      context.random().nextDouble();
      ChunkPos chunkPos = context.chunkPos();
      int y = context.random().nextInt(BetterMineshaftsCommon.CONFIG.maxY - BetterMineshaftsCommon.CONFIG.minY + 1) + BetterMineshaftsCommon.CONFIG.minY;
      MutableBlockPos startingPos = new MutableBlockPos(chunkPos.getBlockX(3), y, chunkPos.getBlockZ(3));
      StructurePiecesBuilder structurePiecesBuilder = new StructurePiecesBuilder();
      this.generatePieces(structurePiecesBuilder, context);
      return Optional.of(new GenerationStub(startingPos, Either.right(structurePiecesBuilder)));
   }

   public StructureType<?> type() {
      return StructureTypeModule.BETTER_MINESHAFT;
   }

   private void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context) {
      WorldgenRandom rand = new WorldgenRandom(new LegacyRandomSource(0L));
      rand.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
      Direction direction = Plane.HORIZONTAL.getRandomDirection(rand);
      int y = context.random().nextInt(BetterMineshaftsCommon.CONFIG.maxY - BetterMineshaftsCommon.CONFIG.minY + 1) + BetterMineshaftsCommon.CONFIG.minY;
      MutableBlockPos startingPos = new MutableBlockPos(context.chunkPos().getBlockX(3), y, context.chunkPos().getBlockZ(3));
      BetterMineshaftPiece entryPoint = new VerticalEntrance(-1, startingPos, direction, this.config, context.heightAccessor().getMaxBuildHeight());
      structurePiecesBuilder.addPiece(entryPoint);
      entryPoint.addChildren(entryPoint, structurePiecesBuilder, context.random());
   }
}
