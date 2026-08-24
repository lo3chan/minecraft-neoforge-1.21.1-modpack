package io.github.razordevs.deep_aether.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class DAJigsawStructure extends Structure {
   public static final MapCodec<DAJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            Codec.intRange(-4096, 4096).fieldOf("discard_below_y").forGetter(structure -> structure.discardBelowY),
            Codec.intRange(-4096, 4096).fieldOf("discard_above_y").forGetter(structure -> structure.discardAboveY),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(structure -> structure.dimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
         )
         .apply(instance, DAJigsawStructure::new)
   );
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   private final int discardBelowY;
   private final int discardAboveY;
   private final List<PoolAliasBinding> poolAliases;
   private final DimensionPadding dimensionPadding;
   private final LiquidSettings liquidSettings;

   protected DAJigsawStructure(
      StructureSettings config,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawName,
      int size,
      HeightProvider startHeight,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter,
      int discardBelowY,
      int discardAboveY,
      List<PoolAliasBinding> poolAliases,
      DimensionPadding dimensionPadding,
      LiquidSettings liquidSettings
   ) {
      super(config);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.size = size;
      this.startHeight = startHeight;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
      this.discardBelowY = discardBelowY;
      this.discardAboveY = discardAboveY;
      this.poolAliases = poolAliases;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      if (!new HeightSpawningChecks().checkHeight(context, this.discardBelowY, this.discardAboveY)) {
         return Optional.empty();
      } else {
         int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
         ChunkPos chunkPos = context.chunkPos();
         BlockPos pos = new BlockPos(chunkPos.getMiddleBlockX(), startY, chunkPos.getMiddleBlockZ());
         return JigsawPlacement.addPieces(
            context,
            this.startPool,
            this.startJigsawName,
            this.size,
            pos,
            false,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter,
            PoolAliasLookup.create(this.poolAliases, pos, context.seed()),
            this.dimensionPadding,
            this.liquidSettings
         );
      }
   }

   public StructureType<?> type() {
      return (StructureType<?>)DAStructureTypes.DA_JIGSAW.get();
   }
}
