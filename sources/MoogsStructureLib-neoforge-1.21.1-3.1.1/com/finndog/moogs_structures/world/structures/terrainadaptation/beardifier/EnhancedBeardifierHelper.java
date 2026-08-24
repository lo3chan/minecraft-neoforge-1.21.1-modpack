package com.finndog.moogs_structures.world.structures.terrainadaptation.beardifier;

import com.finndog.moogs_structures.mixins.terrainadaptation.BeardifierAccessor;
import com.finndog.moogs_structures.world.structures.terrainadaptation.EnhancedTerrainAdaptation;
import com.finndog.moogs_structures.world.structures.terrainadaptation.EnhancedTerrainAdaptationStructure;
import com.finndog.moogs_structures.world.structures.terrainadaptation.PoolElementAdaptationOverride;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;

public class EnhancedBeardifierHelper {
   public static Beardifier forStructuresInChunk(StructureManager structureManager, ChunkPos chunkPos, Beardifier original) {
      ObjectList<EnhancedBeardifierRigid> enhancedBeardifierRigidList = new ObjectArrayList(10);
      ObjectList<EnhancedJigsawJunction> enhancedJunctionList = new ObjectArrayList(10);
      int chunkMinBlockX = chunkPos.getMinBlockX();
      int chunkMinBlockZ = chunkPos.getMinBlockZ();

      for (StructureStart structureStart : structureManager.startsForStructure(chunkPos, structure -> structure instanceof EnhancedTerrainAdaptationStructure)) {
         EnhancedTerrainAdaptation structureAdaptation = ((EnhancedTerrainAdaptationStructure)structureStart.getStructure()).getEnhancedTerrainAdaptation();
         int kernelRadius = structureAdaptation.getKernelRadius();

         for (StructurePiece structurePiece : structureStart.getPieces()) {
            if (structurePiece instanceof PoolElementStructurePiece poolPiece
               && poolPiece.getElement() instanceof PoolElementAdaptationOverride override
               && override.moogs_structures_getAdaptationOverride().isPresent()) {
               kernelRadius = Math.max(kernelRadius, override.moogs_structures_getAdaptationOverride().get().getKernelRadius());
            }
         }

         int maxKernelRadius = kernelRadius;
         if (maxKernelRadius > 0) {
            for (StructurePiece nearbyPiece : structureStart.getPieces()
               .stream()
               .filter(structurePiecex -> structurePiecex.isCloseToChunk(chunkPos, maxKernelRadius))
               .toList()) {
               if (nearbyPiece instanceof PoolElementStructurePiece poolElementPiece) {
                  Projection projection = poolElementPiece.getElement().getProjection();
                  EnhancedTerrainAdaptation pieceAdaptation = structureAdaptation;
                  if (poolElementPiece.getElement() instanceof PoolElementAdaptationOverride override
                     && override.moogs_structures_getAdaptationOverride().isPresent()) {
                     pieceAdaptation = override.moogs_structures_getAdaptationOverride().get();
                  }

                  if (pieceAdaptation != EnhancedTerrainAdaptation.NONE) {
                     int pieceKernelRadius = pieceAdaptation.getKernelRadius();
                     if (projection == Projection.RIGID) {
                        enhancedBeardifierRigidList.add(
                           new EnhancedBeardifierRigid(
                              poolElementPiece.getBoundingBox(), pieceAdaptation, poolElementPiece.getGroundLevelDelta(), poolElementPiece.getRotation()
                           )
                        );
                     }

                     for (JigsawJunction jigsawJunction : poolElementPiece.getJunctions()) {
                        int sourceX = jigsawJunction.getSourceX();
                        int sourceZ = jigsawJunction.getSourceZ();
                        if (sourceX > chunkMinBlockX - pieceKernelRadius
                           && sourceZ > chunkMinBlockZ - pieceKernelRadius
                           && sourceX < chunkMinBlockX + 15 + pieceKernelRadius
                           && sourceZ < chunkMinBlockZ + 15 + pieceKernelRadius) {
                           enhancedJunctionList.add(new EnhancedJigsawJunction(jigsawJunction, pieceAdaptation));
                        }
                     }
                  }
               } else if (structureAdaptation != EnhancedTerrainAdaptation.NONE) {
                  enhancedBeardifierRigidList.add(new EnhancedBeardifierRigid(nearbyPiece.getBoundingBox(), structureAdaptation, 0, Rotation.NONE));
               }
            }
         }
      }

      Beardifier newBeardifier = new Beardifier(((BeardifierAccessor)original).getPieceIterator(), ((BeardifierAccessor)original).getJunctionIterator());
      EnhancedBeardifierData enhancedBeardifier = (EnhancedBeardifierData)newBeardifier;
      enhancedBeardifier.moogs_structures_setEnhancedPieceIterator(enhancedBeardifierRigidList.iterator());
      enhancedBeardifier.moogs_structures_setEnhancedJunctionIterator(enhancedJunctionList.iterator());
      return newBeardifier;
   }

   public static double computeDensity(FunctionContext ctx, double density, EnhancedBeardifierData data) {
      int x = ctx.blockX();
      int y = ctx.blockY();
      int z = ctx.blockZ();

      while (data.moogs_structures_getEnhancedPieceIterator() != null && data.moogs_structures_getEnhancedPieceIterator().hasNext()) {
         EnhancedBeardifierRigid rigid = (EnhancedBeardifierRigid)data.moogs_structures_getEnhancedPieceIterator().next();
         if (rigid != null) {
            BoundingBox originalBox = rigid.pieceBoundingBox();
            EnhancedTerrainAdaptation adaptation = rigid.pieceTerrainAdaptation();
            Rotation pieceRotation = rigid.rotation();
            Optional<EnhancedTerrainAdaptation.Band> bandOpt = adaptation.getBand();
            if (!bandOpt.isPresent() || !bandOpt.get().pieceHeights().isPresent() || bandOpt.get().pieceHeights().get().contains(originalBox.getYSpan())) {
               BoundingBox adaptationx = originalBox.moved(0, (int)adaptation.getBottomOffset(), 0);
               Axis xPaddingDirection = pieceRotation.rotate(Direction.EAST).getAxis();
               int xPadding = xPaddingDirection == Axis.X ? adaptation.getPadding().x() : adaptation.getPadding().z();
               int zPadding = xPaddingDirection == Axis.X ? adaptation.getPadding().z() : adaptation.getPadding().x();
               adaptationx = adaptationx.inflatedBy(xPadding, 0, zPadding);
               if (bandOpt.isPresent()) {
                  EnhancedTerrainAdaptation.Band band = bandOpt.get();
                  int floor = originalBox.minY() + (int)adaptation.getBottomOffset();
                  adaptationx = new BoundingBox(
                     adaptationx.minX(), floor + band.bottom(), adaptationx.minZ(), adaptationx.maxX(), floor + band.top(), adaptationx.maxZ()
                  );
               } else {
                  if (adaptation.getPadding().top() != 0) {
                     adaptationx = new BoundingBox(
                        adaptationx.minX(),
                        adaptationx.minY(),
                        adaptationx.minZ(),
                        adaptationx.maxX(),
                        adaptationx.maxY() + adaptation.getPadding().top(),
                        adaptationx.maxZ()
                     );
                  }

                  if (adaptation.getPadding().bottom() != 0) {
                     adaptationx = new BoundingBox(
                        adaptationx.minX(),
                        adaptationx.minY() - adaptation.getPadding().bottom(),
                        adaptationx.minZ(),
                        adaptationx.maxX(),
                        adaptationx.maxY(),
                        adaptationx.maxZ()
                     );
                  }
               }

               int xDistanceToBoundingBox = Math.max(0, Math.max(adaptationx.minX() - x, x - adaptationx.maxX()));
               int yDistanceToBoundingBox = Math.max(0, Math.max(adaptationx.minY() - y, y - adaptationx.maxY()));
               int zDistanceToBoundingBox = Math.max(0, Math.max(adaptationx.minZ() - z, z - adaptationx.maxZ()));
               int yDistanceToPieceBottom = y - adaptationx.minY();
               double densityFactor = adaptation.computeDensityFactor(
                     xDistanceToBoundingBox, yDistanceToBoundingBox, zDistanceToBoundingBox, yDistanceToPieceBottom
                  )
                  * 0.8;
               density += densityFactor;
            }
         }
      }

      data.moogs_structures_getEnhancedPieceIterator().back(2147483647);

      while (data.moogs_structures_getEnhancedJunctionIterator() != null && data.moogs_structures_getEnhancedJunctionIterator().hasNext()) {
         EnhancedJigsawJunction enhancedJigsawJunction = (EnhancedJigsawJunction)data.moogs_structures_getEnhancedJunctionIterator().next();
         if (enhancedJigsawJunction != null) {
            JigsawJunction jigsawJunction = enhancedJigsawJunction.jigsawJunction();
            EnhancedTerrainAdaptation adaptation = enhancedJigsawJunction.pieceTerrainAdaptation();
            if (!adaptation.getBand().isPresent()) {
               int groundY = jigsawJunction.getSourceGroundY() + (int)adaptation.getBottomOffset();
               int xDistanceToJunction = x - jigsawJunction.getSourceX();
               int yDistanceToJunction = y - groundY;
               int zDistanceToJunction = z - jigsawJunction.getSourceZ();
               double densityFactor = adaptation.computeDensityFactor(xDistanceToJunction, yDistanceToJunction, zDistanceToJunction, yDistanceToJunction) * 0.4;
               density += densityFactor;
            }
         }
      }

      data.moogs_structures_getEnhancedJunctionIterator().back(2147483647);
      return density;
   }
}
