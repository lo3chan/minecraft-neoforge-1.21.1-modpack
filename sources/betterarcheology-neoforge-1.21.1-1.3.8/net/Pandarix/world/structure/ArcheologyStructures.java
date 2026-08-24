package net.Pandarix.world.structure;

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
import org.jetbrains.annotations.NotNull;

public class ArcheologyStructures extends Structure {
   public static final MapCodec<ArcheologyStructures> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 50).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliasBindings),
            DimensionPadding.CODEC
               .optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING)
               .forGetter(structure -> structure.dimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
         )
         .apply(instance, ArcheologyStructures::new)
   );
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   private final LiquidSettings liquidSettings;
   private final DimensionPadding dimensionPadding;
   private final List<PoolAliasBinding> poolAliasBindings;

   public ArcheologyStructures(
      StructureSettings config,
      Holder<StructureTemplatePool> startPool,
      Optional<ResourceLocation> startJigsawName,
      int size,
      HeightProvider startHeight,
      Optional<Types> projectStartToHeightmap,
      int maxDistanceFromCenter,
      List<PoolAliasBinding> poolAliasBindings,
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
      this.poolAliasBindings = poolAliasBindings;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   private static boolean extraSpawningChecks(GenerationContext context) {
      ChunkPos chunkpos = context.chunkPos();
      return context.chunkGenerator()
            .getFirstOccupiedHeight(
               chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState()
            )
         < 150;
   }

   @NotNull
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
            PoolAliasLookup.create(this.poolAliasBindings, blockPos, context.seed()),
            this.dimensionPadding,
            this.liquidSettings
         );
      }
   }

   @NotNull
   public StructureType<?> type() {
      return (StructureType<?>)ModStructures.ARCHEOLOGY_STRUCTURES.get();
   }
}
