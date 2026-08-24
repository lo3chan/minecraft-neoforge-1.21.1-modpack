package fuzs.eternalnether.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.eternalnether.init.ModStructures;
import fuzs.eternalnether.util.ModStructureUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public class CatacombStructure extends Structure {
   public static final MapCodec<CatacombStructure> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
         )
         .apply(instance, CatacombStructure::new)
   );
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;

   public CatacombStructure(
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

   private static boolean checkLocation(GenerationContext context) {
      BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);
      NoiseColumn blockReader = context.chunkGenerator().getBaseColumn(blockpos.getX(), blockpos.getZ(), context.heightAccessor(), context.randomState());
      return !ModStructureUtils.isBuried(blockReader, 48, ModStructureUtils.getScaledNetherHeight(context, 72)) && !ModStructureUtils.isLavaLake(blockReader);
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      if (checkLocation(context)) {
         BlockPos blockPos = ModStructureUtils.getElevation(context, 56, ModStructureUtils.getScaledNetherHeight(context, 84));
         return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.size,
            blockPos,
            false,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter,
            PoolAliasLookup.create(List.of(), blockPos, context.seed()),
            JigsawStructure.DEFAULT_DIMENSION_PADDING,
            JigsawStructure.DEFAULT_LIQUID_SETTINGS
         );
      } else {
         return Optional.empty();
      }
   }

   public StructureType<?> type() {
      return (StructureType<?>)ModStructures.CATACOMB_STRUCTURE_TYPE.value();
   }
}
