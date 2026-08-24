package net.joefoxe.hexerei.world.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.joefoxe.hexerei.world.structure.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class BabaYagaHutStructure extends Structure {
   public static final MapCodec<BabaYagaHutStructure> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
         )
         .apply(instance, BabaYagaHutStructure::new)
   );
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;

   public BabaYagaHutStructure(
      StructureSettings config,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawName,
      int size,
      HeightProvider startHeight,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter
   ) {
      super(config);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.size = size;
      this.startHeight = startHeight;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
   }

   private static boolean extraSpawningChecks(GenerationContext context) {
      ChunkPos chunkpos = context.chunkPos();
      return context.chunkGenerator()
            .getFirstOccupiedHeight(
               chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState()
            )
         < 150;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      if (!extraSpawningChecks(context)) {
         return Optional.empty();
      } else {
         int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
         ChunkPos chunkPos = context.chunkPos();
         BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());
         return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.size,
            blockPos,
            false,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter,
            PoolAliasLookup.EMPTY,
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
         );
      }
   }

   public StructureType<?> type() {
      return (StructureType<?>)ModStructures.BABA_YAGA_HUT.get();
   }
}
