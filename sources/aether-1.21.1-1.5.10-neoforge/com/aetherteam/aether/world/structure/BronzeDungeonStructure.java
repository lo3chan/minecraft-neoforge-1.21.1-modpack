package com.aetherteam.aether.world.structure;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeDungeonBuilder;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeProcessorSettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableInt;

public class BronzeDungeonStructure extends Structure {
   public static final MapCodec<BronzeDungeonStructure> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            settingsCodec(builder),
            Codec.INT.fieldOf("maxrooms").forGetter(o -> o.maxRooms),
            Codec.INT.fieldOf("aboveBottom").forGetter(o -> o.aboveBottom),
            Codec.INT.fieldOf("belowTop").forGetter(o -> o.belowTop),
            BronzeProcessorSettings.CODEC.fieldOf("processor_settings").forGetter(o -> o.processors)
         )
         .apply(builder, BronzeDungeonStructure::new)
   );
   private final int maxRooms;
   private final int aboveBottom;
   private final int belowTop;
   private final BronzeProcessorSettings processors;

   public BronzeDungeonStructure(StructureSettings settings, int maxRooms, int aboveBottom, int belowTop, BronzeProcessorSettings processors) {
      super(settings);
      this.maxRooms = maxRooms;
      this.aboveBottom = aboveBottom;
      this.belowTop = belowTop;
      this.processors = processors;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      ChunkPos chunkPos = context.chunkPos();
      ChunkGenerator chunkGenerator = context.chunkGenerator();
      LevelHeightAccessor heightAccessor = context.heightAccessor();
      RandomState randomState = context.randomState();
      StructureTemplateManager templateManager = context.structureTemplateManager();
      int height = findStartingHeight(chunkGenerator, heightAccessor, chunkPos, randomState, templateManager, this.aboveBottom, this.belowTop);
      if (height <= heightAccessor.getMinBuildHeight()) {
         MutableInt y = new MutableInt(height);
         chunkPos = searchNearbyChunks(chunkPos, y, chunkGenerator, heightAccessor, randomState, templateManager, this.aboveBottom, this.belowTop);
         height = y.getValue();
         if (height <= heightAccessor.getMinBuildHeight()) {
            return Optional.empty();
         }
      }

      BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), height, chunkPos.getMinBlockZ());
      return Optional.of(new GenerationStub(blockPos, builder -> this.generatePieces(builder, context, blockPos)));
   }

   private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos startPos) {
      BronzeDungeonBuilder graph = new BronzeDungeonBuilder(context, this.maxRooms, this.processors);
      graph.initializeDungeon(startPos, context, builder);
   }

   private static ChunkPos searchNearbyChunks(
      ChunkPos chunkPos,
      MutableInt height,
      ChunkGenerator generator,
      LevelHeightAccessor heightAccessor,
      RandomState randomState,
      StructureTemplateManager templateManager,
      int aboveBottom,
      int belowTop
   ) {
      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            if (x != 0 || z != 0) {
               ChunkPos offset = new ChunkPos(chunkPos.x + x, chunkPos.z + z);
               int y = findStartingHeight(generator, heightAccessor, offset, randomState, templateManager, aboveBottom, belowTop);
               if (y > heightAccessor.getMinBuildHeight()) {
                  height.setValue(y);
                  return offset;
               }
            }
         }
      }

      return chunkPos;
   }

   private static int findStartingHeight(
      ChunkGenerator generator,
      LevelHeightAccessor heightAccessor,
      ChunkPos chunkPos,
      RandomState random,
      StructureTemplateManager templateManager,
      int aboveBottom,
      int belowTop
   ) {
      int minX = chunkPos.getMinBlockX() - 1;
      int minZ = chunkPos.getMinBlockZ() - 1;
      int maxX = chunkPos.getMaxBlockX() + 1;
      int maxZ = chunkPos.getMaxBlockZ() + 1;
      NoiseColumn[] columns = new NoiseColumn[]{
         generator.getBaseColumn(minX, minZ, heightAccessor, random),
         generator.getBaseColumn(minX, maxZ, heightAccessor, random),
         generator.getBaseColumn(maxX, minZ, heightAccessor, random),
         generator.getBaseColumn(maxX, maxZ, heightAccessor, random)
      };
      int roomHeight = checkRoomHeight(templateManager, ResourceLocation.fromNamespaceAndPath("aether", "bronze_dungeon/boss_room"));
      int height = heightAccessor.getMinBuildHeight();
      int maxHeight = heightAccessor.getMaxBuildHeight() - belowTop;
      int thickness = roomHeight + 2;
      int currentThickness = 0;

      for (int y = height + aboveBottom; y <= maxHeight; y++) {
         if (checkEachCornerAtY(columns, y)) {
            currentThickness++;
         } else {
            if (currentThickness > thickness) {
               thickness = currentThickness;
               height = y;
            }

            currentThickness = 0;
         }
      }

      int offset = (thickness + roomHeight) / 2;
      return height - offset;
   }

   private static int checkRoomHeight(StructureTemplateManager manager, ResourceLocation roomName) {
      StructureTemplate template = manager.getOrCreate(roomName);
      return template.getSize().getY();
   }

   private static boolean checkEachCornerAtY(NoiseColumn[] columns, int y) {
      for (NoiseColumn column : columns) {
         if (column.getBlock(y).isAir() || column.getBlock(y).is(AetherTags.Blocks.NON_BRONZE_DUNGEON_SPAWNABLE)) {
            return false;
         }
      }

      return true;
   }

   public BoundingBox adjustBoundingBox(BoundingBox box) {
      return box;
   }

   public StructureType<?> type() {
      return (StructureType<?>)AetherStructureTypes.BRONZE_DUNGEON.get();
   }
}
