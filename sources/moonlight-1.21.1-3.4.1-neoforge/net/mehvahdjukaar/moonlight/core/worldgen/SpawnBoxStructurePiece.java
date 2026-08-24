package net.mehvahdjukaar.moonlight.core.worldgen;

import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class SpawnBoxStructurePiece extends PoolElementStructurePiece {
   private static final RandomSource RAND = RandomSource.createNewThreadLocalInstance();

   public SpawnBoxStructurePiece(
      StructureTemplateManager structureTemplateManager,
      SpawnBoxPoolElement poolElement,
      BlockPos blockPos,
      int groundLevelDelta,
      Rotation rotation,
      LiquidSettings liquidSettings
   ) {
      super(
         structureTemplateManager,
         poolElement,
         blockPos,
         groundLevelDelta,
         rotation,
         poolElement.getBoundingBox(structureTemplateManager, blockPos, rotation),
         liquidSettings
      );
   }

   public SpawnBoxStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
      super(context, tag);
   }

   public StructurePieceType getType() {
      return MoonlightRegistry.SPAWN_BOX_PIECE.get();
   }

   public void place(
      WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, BlockPos pos, boolean keepJigsaws
   ) {
   }

   @Nullable
   private String targetName() {
      return this.element instanceof SpawnBoxPoolElement sbe ? sbe.getTargetName() : null;
   }

   @Nullable
   public static String getNamedBoxesAt(StructureStart structureStart, BlockPos pos) {
      List<SpawnBoxStructurePiece> boxes = new ArrayList<>();

      for (StructurePiece structurePiece : structureStart.getPieces()) {
         if (structurePiece instanceof SpawnBoxStructurePiece sb) {
            boxes.add(sb);
         }
      }

      if (boxes.isEmpty()) {
         return null;
      } else {
         Util.shuffle(boxes, RAND);

         for (SpawnBoxStructurePiece box : boxes) {
            if (box.getBoundingBox().isInside(pos)) {
               return box.targetName();
            }
         }

         return null;
      }
   }

   private static List<StructureBlockInfo> findSpawnBoxesInStructure(
      StructurePoolElement poolElement, StructureTemplateManager manager, BlockPos pos, Rotation rotation
   ) {
      SinglePoolElement sp = null;
      if (poolElement instanceof SinglePoolElement spp) {
         sp = spp;
      } else if (poolElement instanceof ListPoolElement lp && lp.elements.getFirst() instanceof SinglePoolElement spp) {
         sp = spp;
      }

      if (sp != null) {
         StructureTemplate structureTemplate = sp.getTemplate(manager);
         return structureTemplate.filterBlocks(pos, new StructurePlaceSettings().setRotation(rotation), MoonlightRegistry.SPAWN_BOX_BLOCK.get(), true);
      } else {
         return List.of();
      }
   }

   public static List<SpawnBoxStructurePiece> getSpawnBoxPieces(
      PoolElementStructurePiece parentPiece, StructureTemplateManager structureTemplateManager, LiquidSettings liquidSettings
   ) {
      StructurePoolElement structurePoolElement = parentPiece.getElement();
      BlockPos parentPiecePos = parentPiece.getPosition();
      Rotation parentPieceRot = parentPiece.getRotation();
      List<StructureBlockInfo> list = findSpawnBoxesInStructure(structurePoolElement, structureTemplateManager, parentPiecePos, parentPieceRot);
      List<SpawnBoxStructurePiece> result = new ArrayList<>();

      for (StructureBlockInfo spawnBox : list) {
         if (spawnBox.nbt() != null) {
            BlockPos spawnBoxPos = spawnBox.pos();
            BlockPos offset = SpawnBoxBlockEntity.readOffsetPos(spawnBox.nbt());
            Vec3i size = SpawnBoxBlockEntity.readBoxSize(spawnBox.nbt());
            String name = SpawnBoxBlockEntity.readBoxName(spawnBox.nbt());
            SpawnBoxPoolElement element = new SpawnBoxPoolElement(size, offset, name);
            int groundLevelDelta = structurePoolElement.getGroundLevelDelta();
            SpawnBoxStructurePiece newPiece = new SpawnBoxStructurePiece(
               structureTemplateManager, element, spawnBoxPos, groundLevelDelta, parentPieceRot, liquidSettings
            );
            result.add(newPiece);
         }
      }

      return result;
   }
}
