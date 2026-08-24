package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier;

import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.NoiseChunkAccessor;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element.YungJigsawPoolElement;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverride;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMask;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverrideMaskSupplier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.NoiseChunk;
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

      for (StructureStart structureStart : structureManager.startsForStructure(chunkPos, structure -> structure instanceof YungJigsawStructure)) {
         EnhancedTerrainAdaptation structureTerrainAdaptation = ((YungJigsawStructure)structureStart.getStructure()).enhancedTerrainAdaptation;
         int kernelRadius = structureTerrainAdaptation.getKernelRadius();

         for (StructurePiece structurePiece : structureStart.getPieces()) {
            if (structurePiece instanceof PoolElementStructurePiece poolPiece
               && poolPiece.getElement() instanceof YungJigsawPoolElement yungElement
               && yungElement.getEnhancedTerrainAdaptation().isPresent()) {
               kernelRadius = Math.max(kernelRadius, yungElement.getEnhancedTerrainAdaptation().get().getKernelRadius());
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
                  EnhancedTerrainAdaptation pieceTerrainAdaptation = structureTerrainAdaptation;
                  if (poolElementPiece.getElement() instanceof YungJigsawPoolElement yungElement && yungElement.getEnhancedTerrainAdaptation().isPresent()) {
                     pieceTerrainAdaptation = yungElement.getEnhancedTerrainAdaptation().get();
                  }

                  if (pieceTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                     int pieceKernelRadius = pieceTerrainAdaptation.getKernelRadius();
                     if (projection == Projection.RIGID) {
                        enhancedBeardifierRigidList.add(
                           new EnhancedBeardifierRigid(
                              poolElementPiece.getBoundingBox(), pieceTerrainAdaptation, poolElementPiece.getGroundLevelDelta(), poolElementPiece.getRotation()
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
                           enhancedJunctionList.add(new EnhancedJigsawJunction(jigsawJunction, pieceTerrainAdaptation));
                        }
                     }
                  }
               } else if (structureTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
                  enhancedBeardifierRigidList.add(new EnhancedBeardifierRigid(nearbyPiece.getBoundingBox(), structureTerrainAdaptation, 0, Rotation.NONE));
               }
            }
         }
      }

      EnhancedBeardifierData enhancedBeardifier = (EnhancedBeardifierData)original;
      enhancedBeardifier.yungsapi_setEnhancedPieces(enhancedBeardifierRigidList);
      enhancedBeardifier.yungsapi_setEnhancedJunctions(enhancedJunctionList);
      return original;
   }

   public static double computeDensity(FunctionContext ctx, double density, EnhancedBeardifierData data) {
      int x = ctx.blockX();
      int y = ctx.blockY();
      int z = ctx.blockZ();
      AquiferOverride aquiferOverride = AquiferOverride.NONE;
      ObjectListIterator<EnhancedBeardifierRigid> pieceIterator = data.yungsapi_getEnhancedPieceIterator();

      while (pieceIterator != null && pieceIterator.hasNext()) {
         EnhancedBeardifierRigid rigid = (EnhancedBeardifierRigid)pieceIterator.next();
         BoundingBox pieceBoundingBox = rigid.pieceBoundingBox();
         EnhancedTerrainAdaptation pieceTerrainAdaptation = rigid.pieceTerrainAdaptation();
         Rotation pieceRotation = rigid.rotation();
         pieceBoundingBox = pieceBoundingBox.moved(0, (int)pieceTerrainAdaptation.getBottomOffset(), 0);
         Axis xPaddingDirection = pieceRotation.rotate(Direction.EAST).getAxis();
         int xPadding = xPaddingDirection == Axis.X ? pieceTerrainAdaptation.getPadding().x() : pieceTerrainAdaptation.getPadding().z();
         int zPadding = xPaddingDirection == Axis.X ? pieceTerrainAdaptation.getPadding().z() : pieceTerrainAdaptation.getPadding().x();
         pieceBoundingBox = pieceBoundingBox.inflatedBy(xPadding, 0, zPadding);
         if (pieceTerrainAdaptation.getPadding().top() != 0) {
            pieceBoundingBox = new BoundingBox(
               pieceBoundingBox.minX(),
               pieceBoundingBox.minY(),
               pieceBoundingBox.minZ(),
               pieceBoundingBox.maxX(),
               pieceBoundingBox.maxY() + pieceTerrainAdaptation.getPadding().top(),
               pieceBoundingBox.maxZ()
            );
         }

         if (pieceTerrainAdaptation.getPadding().bottom() != 0) {
            pieceBoundingBox = new BoundingBox(
               pieceBoundingBox.minX(),
               pieceBoundingBox.minY() - pieceTerrainAdaptation.getPadding().bottom(),
               pieceBoundingBox.minZ(),
               pieceBoundingBox.maxX(),
               pieceBoundingBox.maxY(),
               pieceBoundingBox.maxZ()
            );
         }

         int xDistanceToBoundingBox = Math.max(0, Math.max(pieceBoundingBox.minX() - x, x - pieceBoundingBox.maxX()));
         int yDistanceToBoundingBox = Math.max(0, Math.max(pieceBoundingBox.minY() - y, y - pieceBoundingBox.maxY()));
         int zDistanceToBoundingBox = Math.max(0, Math.max(pieceBoundingBox.minZ() - z, z - pieceBoundingBox.maxZ()));
         int yDistanceToPieceBottom = y - pieceBoundingBox.minY();
         double densityFactor = 0.0;
         if (pieceTerrainAdaptation != EnhancedTerrainAdaptation.NONE) {
            densityFactor = pieceTerrainAdaptation.computeDensityFactor(
                  xDistanceToBoundingBox, yDistanceToBoundingBox, zDistanceToBoundingBox, yDistanceToPieceBottom
               )
               * 0.8;
         }

         density += densityFactor;
         if (densityFactor != 0.0 && pieceTerrainAdaptation.getAquiferOverride() != AquiferOverride.NONE) {
            aquiferOverride = pieceTerrainAdaptation.getAquiferOverride();
         }
      }

      ObjectListIterator<EnhancedJigsawJunction> junctionIterator = data.yungsapi_getEnhancedJunctionIterator();

      while (junctionIterator != null && junctionIterator.hasNext()) {
         EnhancedJigsawJunction enhancedJigsawJunction = (EnhancedJigsawJunction)junctionIterator.next();
         JigsawJunction jigsawJunction = enhancedJigsawJunction.jigsawJunction();
         EnhancedTerrainAdaptation pieceTerrainAdaptationx = enhancedJigsawJunction.pieceTerrainAdaptation();
         int groundY = jigsawJunction.getSourceGroundY() + (int)pieceTerrainAdaptationx.getBottomOffset();
         int xDistanceToJunction = x - jigsawJunction.getSourceX();
         int yDistanceToJunction = y - groundY;
         int zDistanceToJunction = z - jigsawJunction.getSourceZ();
         double densityFactorx = pieceTerrainAdaptationx.computeDensityFactor(
               xDistanceToJunction, yDistanceToJunction, zDistanceToJunction, yDistanceToJunction
            )
            * 0.4;
         density += densityFactorx;
         if (densityFactorx != 0.0 && pieceTerrainAdaptationx.getAquiferOverride() != AquiferOverride.NONE) {
            aquiferOverride = pieceTerrainAdaptationx.getAquiferOverride();
         }
      }

      if (aquiferOverride != AquiferOverride.NONE) {
         updateAquiferOverrideMask(data, aquiferOverride, x, y, z);
      }

      return density;
   }

   private static void updateAquiferOverrideMask(EnhancedBeardifierData data, AquiferOverride aquiferOverride, int x, int y, int z) {
      NoiseChunk noiseChunk = data.yungsapi_getNoiseChunk();
      NoiseChunkAccessor noiseChunkAccessor = (NoiseChunkAccessor)noiseChunk;
      AquiferOverrideMaskSupplier aquiferOverrideMaskSupplier = (AquiferOverrideMaskSupplier)noiseChunk;
      int chunkHeight = noiseChunkAccessor.getNoiseSettings().height();
      int minY = noiseChunkAccessor.getNoiseSettings().minY();
      AquiferOverrideMask aquiferOverrideMask = aquiferOverrideMaskSupplier.getOrCreateAquiferOverrideMask(() -> new AquiferOverrideMask(chunkHeight, minY));
      aquiferOverrideMask.set(x, y, z);
      aquiferOverrideMask.setAquiferOverride(aquiferOverride);
   }
}
