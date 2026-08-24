package io.github.steveplays28.noisium.mixin.compat.lithium;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({NoiseBasedChunkGenerator.class})
public abstract class LithiumNoiseChunkGeneratorMixin extends ChunkGenerator {
   @Shadow
   protected abstract ChunkAccess doFill(Blender var1, StructureManager var2, RandomState var3, ChunkAccess var4, int var5, int var6);

   public LithiumNoiseChunkGeneratorMixin(BiomeSource biomeSource) {
      super(biomeSource);
   }

   @Redirect(
      method = {"doFill(Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;II)Lnet/minecraft/world/level/chunk/ChunkAccess;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private BlockState noisium$populateNoiseWrapSetBlockStateOperation(
      @NotNull LevelChunkSection chunkSection,
      int chunkSectionBlockPosX,
      int chunkSectionBlockPosY,
      int chunkSectionBlockPosZ,
      @NotNull BlockState blockState,
      boolean lock
   ) {
      int blockStateId = chunkSection.states.data.palette.idFor(blockState);
      chunkSection.states
         .data
         .storage()
         .set(chunkSection.states.strategy.getIndex(chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ), blockStateId);
      return blockState;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Overwrite
   @Nullable
   public ChunkAccess lambda$fillFromNoise$11(
      @NotNull ChunkAccess chunk,
      int generationShapeHeightFloorDiv,
      @NotNull NoiseSettings generationShapeConfig,
      int minimumY,
      @NotNull Blender blender,
      @NotNull StructureManager structureAccessor,
      @NotNull RandomState noiseConfig,
      int minimumYFloorDiv
   ) {
      int startingChunkSectionIndex = chunk.getSectionIndex(generationShapeHeightFloorDiv * generationShapeConfig.getCellHeight() - 1 + minimumY);
      int minimumYChunkSectionIndex = chunk.getSectionIndex(minimumY);
      LevelChunkSection[] chunkSections = chunk.getSections();

      for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; chunkSectionIndex--) {
         chunkSections[chunkSectionIndex].acquire();
      }

      boolean var19 = false /* VF: Semaphore variable */;

      ChunkAccess var21;
      try {
         var19 = true;
         var21 = this.doFill(blender, structureAccessor, noiseConfig, chunk, minimumYFloorDiv, generationShapeHeightFloorDiv);
         var19 = false;
      } finally {
         if (var19) {
            for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; chunkSectionIndex--) {
               LevelChunkSection chunkSection = chunkSections[chunkSectionIndex];
               chunkSection.recalcBlockCounts();
               chunkSection.release();
            }
         }
      }

      for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; chunkSectionIndex--) {
         LevelChunkSection chunkSection = chunkSections[chunkSectionIndex];
         chunkSection.recalcBlockCounts();
         chunkSection.release();
      }

      return var21;
   }
}
