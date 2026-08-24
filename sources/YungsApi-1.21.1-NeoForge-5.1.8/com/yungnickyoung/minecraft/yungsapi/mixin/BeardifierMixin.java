package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.DensityFunctions.BeardifierOrMarker;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Beardifier.class},
   priority = 1100
)
public abstract class BeardifierMixin implements EnhancedBeardifierData, BeardifierOrMarker {
   @Unique
   @Nullable
   private ObjectList<EnhancedJigsawJunction> enhancedJunctions;
   @Unique
   @Nullable
   private ObjectList<EnhancedBeardifierRigid> enhancedPieces;
   @Unique
   @Nullable
   private NoiseChunk noiseChunk;

   @Inject(
      method = {"forStructuresInChunk"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void yungsapi_supportCustomTerrainAdaptations(StructureManager structureManager, ChunkPos chunkPos, CallbackInfoReturnable<Beardifier> cir) {
      cir.setReturnValue(EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, (Beardifier)cir.getReturnValue()));
   }

   @Inject(
      method = {"compute"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void yungsapi_calculateDensity(FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
      double density = (Double)cir.getReturnValue();
      double newDensity = EnhancedBeardifierHelper.computeDensity(ctx, density, this);
      cir.setReturnValue(newDensity);
   }

   @Nullable
   @Override
   public ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator() {
      return this.enhancedPieces == null ? null : this.enhancedPieces.iterator();
   }

   @Override
   public void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> enhancedPieces) {
      this.enhancedPieces = enhancedPieces;
   }

   @Nullable
   @Override
   public ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator() {
      return this.enhancedJunctions == null ? null : this.enhancedJunctions.iterator();
   }

   @Override
   public void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> enhancedJunctions) {
      this.enhancedJunctions = enhancedJunctions;
   }

   @Nullable
   @Override
   public NoiseChunk yungsapi_getNoiseChunk() {
      return this.noiseChunk;
   }

   @Override
   public void yungsapi_setNoiseChunk(NoiseChunk noiseChunk) {
      this.noiseChunk = noiseChunk;
   }
}
