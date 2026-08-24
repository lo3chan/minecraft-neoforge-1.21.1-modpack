package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw;

import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.StructureTemplatePoolAccessor;
import com.yungnickyoung.minecraft.yungsapi.util.BoxOctree;
import com.yungnickyoung.minecraft.yungsapi.world.structure.context.StructureContext;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.assembler.JigsawStructureAssembler;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawPoolElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;

public class JigsawManager {
   public static Optional<GenerationStub> assembleJigsawStructure(
      GenerationContext generationContext,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawNameOptional,
      int maxDepth,
      BlockPos locatePos,
      boolean useExpansionHack,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter,
      Optional<Integer> maxY,
      Optional<Integer> minY,
      DimensionPadding dimensionPadding,
      LiquidSettings liquidSettings
   ) {
      RegistryAccess registryAccess = generationContext.registryAccess();
      Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
      ChunkGenerator chunkGenerator = generationContext.chunkGenerator();
      StructureTemplateManager structureManager = generationContext.structureTemplateManager();
      LevelHeightAccessor levelHeightAccessor = generationContext.heightAccessor();
      WorldgenRandom worldgenRandom = generationContext.random();
      Optional<PoolElementStructurePiece> startPieceOptional = getStartPiece(startPool, startJigsawNameOptional, locatePos, liquidSettings, generationContext);
      if (startPieceOptional.isEmpty()) {
         return Optional.empty();
      } else {
         PoolElementStructurePiece startPiece = startPieceOptional.get();
         Vec3i startingPosOffset = locatePos.subtract(startPiece.getPosition());
         BoundingBox pieceBoundingBox = startPiece.getBoundingBox();
         int bbCenterX = (pieceBoundingBox.maxX() + pieceBoundingBox.minX()) / 2;
         int bbCenterZ = (pieceBoundingBox.maxZ() + pieceBoundingBox.minZ()) / 2;
         int bbCenterY = projectStartToHeightmap.<Integer>map(
               types -> locatePos.getY() + chunkGenerator.getFirstFreeHeight(bbCenterX, bbCenterZ, types, levelHeightAccessor, generationContext.randomState())
            )
            .orElseGet(() -> startPiece.getPosition().getY());
         int adjustedPieceCenterY = bbCenterY + startingPosOffset.getY();
         int yAdjustment = pieceBoundingBox.minY() + startPiece.getGroundLevelDelta();
         startPiece.move(0, bbCenterY - yAdjustment, 0);
         AABB aABB = new AABB(
            bbCenterX - maxDistanceFromCenter,
            adjustedPieceCenterY - maxDistanceFromCenter,
            bbCenterZ - maxDistanceFromCenter,
            bbCenterX + maxDistanceFromCenter + 1,
            adjustedPieceCenterY + maxDistanceFromCenter + 1,
            bbCenterZ + maxDistanceFromCenter + 1
         );
         BoxOctree maxStructureBounds = new BoxOctree(aABB);
         maxStructureBounds.addBox(AABB.of(pieceBoundingBox));
         return Optional.of(
            new GenerationStub(
               new BlockPos(bbCenterX, adjustedPieceCenterY, bbCenterZ),
               structurePiecesBuilder -> {
                  if (maxDepth > 0) {
                     JigsawStructureAssembler assembler = new JigsawStructureAssembler(
                        new JigsawStructureAssembler.Settings()
                           .poolRegistry(registry)
                           .maxDepth(maxDepth)
                           .chunkGenerator(chunkGenerator)
                           .structureTemplateManager(structureManager)
                           .randomState(generationContext.randomState())
                           .biomeSource(generationContext.biomeSource())
                           .rand(worldgenRandom)
                           .maxY(maxY)
                           .minY(minY)
                           .useExpansionHack(useExpansionHack)
                           .levelHeightAccessor(levelHeightAccessor)
                           .dimensionPadding(dimensionPadding)
                           .liquidSettings(liquidSettings)
                     );
                     assembler.assembleStructure(startPiece, maxStructureBounds);
                     assembler.addAllPiecesToStructureBuilder(structurePiecesBuilder);
                  }
               }
            )
         );
      }
   }

   private static Optional<PoolElementStructurePiece> getStartPiece(
      Holder<StructureTemplatePool> startPoolHolder,
      Optional<ResourceLocation> startJigsawNameOptional,
      BlockPos locatePos,
      LiquidSettings liquidSettings,
      GenerationContext generationContext
   ) {
      StructureTemplateManager structureTemplateManager = generationContext.structureTemplateManager();
      RandomSource rand = generationContext.random();
      StructureTemplatePool startPool = (StructureTemplatePool)startPoolHolder.value();
      ObjectArrayList<Pair<StructurePoolElement, Integer>> candidatePoolElements = new ObjectArrayList(
         ((StructureTemplatePoolAccessor)startPool).getRawTemplates()
      );
      Util.shuffle(candidatePoolElements, rand);
      Rotation rotation = Rotation.getRandom(rand);
      int totalWeightSum = candidatePoolElements.stream().mapToInt(Pair::getSecond).reduce(0, Integer::sum);

      while (candidatePoolElements.size() > 0 && totalWeightSum > 0) {
         Pair<StructurePoolElement, Integer> chosenPoolElementPair = null;
         ObjectListIterator chosenPoolElement = candidatePoolElements.iterator();

         while (chosenPoolElement.hasNext()) {
            Pair<StructurePoolElement, Integer> candidatePiecePair = (Pair<StructurePoolElement, Integer>)chosenPoolElement.next();
            StructurePoolElement candidatePiece = (StructurePoolElement)candidatePiecePair.getFirst();
            if (candidatePiece instanceof YungJigsawPoolElement yungElement && yungElement.isPriorityPiece()) {
               chosenPoolElementPair = candidatePiecePair;
               break;
            }
         }

         if (chosenPoolElementPair == null) {
            int chosenWeight = rand.nextInt(totalWeightSum) + 1;
            ObjectListIterator var21 = candidatePoolElements.iterator();

            while (var21.hasNext()) {
               Pair<StructurePoolElement, Integer> candidate = (Pair<StructurePoolElement, Integer>)var21.next();
               chosenWeight -= candidate.getSecond();
               if (chosenWeight <= 0) {
                  chosenPoolElementPair = candidate;
                  break;
               }
            }
         }

         StructurePoolElement chosenPoolElementx = (StructurePoolElement)chosenPoolElementPair.getFirst();
         int chosenPieceWeight = (Integer)chosenPoolElementPair.getSecond();
         if (chosenPoolElementx == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
         }

         BlockPos anchorPos;
         if (startJigsawNameOptional.isPresent()) {
            ResourceLocation name = startJigsawNameOptional.get();
            Optional<BlockPos> optional = getPosOfJigsawBlockWithName(chosenPoolElementx, name, locatePos, rotation, structureTemplateManager, rand);
            if (optional.isEmpty()) {
               YungsApiCommon.LOGGER
                  .error(
                     "No starting jigsaw with Name {} found in start pool {}",
                     name,
                     startPoolHolder.unwrapKey().map(pool -> pool.location().toString()).orElse("<unregistered>")
                  );
               return Optional.empty();
            }

            anchorPos = optional.get();
         } else {
            anchorPos = locatePos;
         }

         Vec3i startingPosOffset = anchorPos.subtract(locatePos);
         BlockPos adjustedStartPos = locatePos.subtract(startingPosOffset);
         if (chosenPoolElementx instanceof YungJigsawPoolElement yungElement) {
            StructureContext ctx = new StructureContext.Builder()
               .structureTemplateManager(structureTemplateManager)
               .pos(adjustedStartPos)
               .rotation(rotation)
               .depth(0)
               .random(rand)
               .randomState(generationContext.randomState())
               .biomeSource(generationContext.biomeSource())
               .build();
            if (!yungElement.passesConditions(ctx)) {
               totalWeightSum -= chosenPieceWeight;
               candidatePoolElements.remove(chosenPoolElementPair);
               continue;
            }
         }

         return Optional.of(
            new PoolElementStructurePiece(
               structureTemplateManager,
               chosenPoolElementx,
               adjustedStartPos,
               chosenPoolElementx.getGroundLevelDelta(),
               rotation,
               chosenPoolElementx.getBoundingBox(structureTemplateManager, adjustedStartPos, rotation),
               liquidSettings
            )
         );
      }

      return Optional.empty();
   }

   private static Optional<BlockPos> getPosOfJigsawBlockWithName(
      StructurePoolElement structurePoolElement,
      ResourceLocation name,
      BlockPos startPos,
      Rotation rotation,
      StructureTemplateManager structureTemplateManager,
      RandomSource rand
   ) {
      try {
         for (StructureBlockInfo jigsawBlockInfo : structurePoolElement.getShuffledJigsawBlocks(structureTemplateManager, startPos, rotation, rand)) {
            ResourceLocation jigsawBlockName = ResourceLocation.tryParse(jigsawBlockInfo.nbt().getString("name"));
            if (name.equals(jigsawBlockName)) {
               return Optional.of(jigsawBlockInfo.pos());
            }
         }
      } catch (ConcurrentModificationException var10) {
         YungsApiCommon.LOGGER
            .error(
               "Encountered unexpected ConcurrentModException while trying to get jigsaw block with name {} from structure pool element {}",
               name,
               structurePoolElement
            );
         YungsApiCommon.LOGGER.error("Ignoring - the structure will still generate, but /locate will not point to the structure's anchor block.");
         return Optional.empty();
      }

      return Optional.empty();
   }
}
