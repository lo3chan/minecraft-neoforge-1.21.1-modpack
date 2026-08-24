package com.yungnickyoung.minecraft.betterdungeons.world.structure.spider_dungeon;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureTypeModule;
import com.yungnickyoung.minecraft.betterdungeons.world.structure.spider_dungeon.piece.SpiderDungeonBigTunnelPiece;
import com.yungnickyoung.minecraft.betterdungeons.world.structure.spider_dungeon.piece.SpiderDungeonPiece;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class SpiderDungeonStructure extends Structure {
   public static final MapCodec<SpiderDungeonStructure> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(settingsCodec(builder), HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight))
         .apply(builder, SpiderDungeonStructure::new)
   );
   private final HeightProvider startHeight;

   public SpiderDungeonStructure(StructureSettings structureSettings, HeightProvider startHeight) {
      super(structureSettings);
      this.startHeight = startHeight;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      int startX = context.chunkPos().getMiddleBlockX();
      int startZ = context.chunkPos().getMiddleBlockZ();
      int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
      BlockPos startPos = new BlockPos(startX, startY, startZ);
      SpiderDungeonPiece startPiece = new SpiderDungeonBigTunnelPiece(startPos);
      StructurePiecesBuilder structurePiecesBuilder = new StructurePiecesBuilder();
      structurePiecesBuilder.addPiece(startPiece);
      startPiece.addChildren(startPiece, structurePiecesBuilder, context.random());
      return Optional.of(new GenerationStub(startPos, Either.right(structurePiecesBuilder)));
   }

   public StructureType<?> type() {
      return StructureTypeModule.SPIDER_DUNGEON;
   }
}
