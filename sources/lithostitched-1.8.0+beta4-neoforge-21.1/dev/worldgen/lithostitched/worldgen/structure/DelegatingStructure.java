package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class DelegatingStructure extends Structure {
   public static final MapCodec<DelegatingStructure> CODEC = DelegatingConfig.CODEC.xmap(DelegatingStructure::new, DelegatingStructure::config);
   public static final StructureType<DelegatingStructure> TYPE = () -> CODEC;
   private final DelegatingConfig config;

   public DelegatingStructure(DelegatingConfig config) {
      super(createSettings(config));
      this.config = config;
   }

   public DelegatingConfig config() {
      return this.config;
   }

   public Structure delegate() {
      return (Structure)this.config.delegate().value();
   }

   public Optional<GenerationStub> findValidGenerationPoint(GenerationContext context) {
      return this.findGenerationPoint(context).filter(generationPoint -> this.isValid(generationPoint, context));
   }

   private boolean isValid(GenerationStub stub, GenerationContext context) {
      BlockPos pos = stub.position();
      return !context.validBiome()
            .test(
               context.chunkGenerator()
                  .getBiomeSource()
                  .getNoiseBiome(
                     QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), context.randomState().sampler()
                  )
            )
         ? false
         : this.config.spawnCondition().map(condition -> condition.test(context, pos)).orElse(true);
   }

   protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      return this.delegate().findValidGenerationPoint(context);
   }

   public void afterPlace(
      WorldGenLevel level,
      StructureManager structureManager,
      ChunkGenerator generator,
      RandomSource random,
      BoundingBox box,
      ChunkPos chunkPos,
      PiecesContainer container
   ) {
      this.delegate().afterPlace(level, structureManager, generator, random, box, chunkPos, container);
   }

   public StructureType<?> type() {
      return TYPE;
   }

   private static StructureSettings createSettings(DelegatingConfig config) {
      Structure delegate = (Structure)config.delegate().value();
      return new StructureSettings(delegate.biomes(), delegate.spawnOverrides(), delegate.step(), delegate.terrainAdaptation());
   }
}
