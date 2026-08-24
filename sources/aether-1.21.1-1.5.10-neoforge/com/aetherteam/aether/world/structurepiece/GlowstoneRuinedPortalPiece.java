package com.aetherteam.aether.world.structurepiece;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.mixin.mixins.common.accessor.ChunkAccessAccessor;
import com.aetherteam.aether.mixin.mixins.common.accessor.SpreadingSnowyDirtBlockAccessor;
import com.aetherteam.aether.world.BlockLogicUtil;
import com.aetherteam.aether.world.processor.DoubleDropsProcessor;
import com.aetherteam.aether.world.processor.GlowstonePortalAgeProcessor;
import com.aetherteam.aether.world.processor.HolystoneReplaceProcessor;
import com.aetherteam.aether.world.processor.SurfaceRuleProcessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class GlowstoneRuinedPortalPiece extends TemplateStructurePiece {
   private final GlowstoneRuinedPortalPiece.VerticalPlacement verticalPlacement;
   private final GlowstoneRuinedPortalPiece.Properties properties;

   public GlowstoneRuinedPortalPiece(
      StructureTemplateManager structureTemplateManager,
      BlockPos templatePosition,
      GlowstoneRuinedPortalPiece.VerticalPlacement verticalPlacement,
      GlowstoneRuinedPortalPiece.Properties properties,
      ResourceLocation location,
      Rotation rotation,
      Mirror mirror,
      BlockPos pivotPos
   ) {
      super(
         (StructurePieceType)AetherStructurePieceTypes.RUINED_PORTAL.get(),
         0,
         structureTemplateManager,
         location,
         location.toString(),
         makeSettings(mirror, rotation, pivotPos, properties),
         templatePosition
      );
      this.verticalPlacement = verticalPlacement;
      this.properties = properties;
   }

   public GlowstoneRuinedPortalPiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
      super(
         (StructurePieceType)AetherStructurePieceTypes.RUINED_PORTAL.get(),
         tag,
         structureTemplateManager,
         location -> makeSettings(structureTemplateManager, tag, location)
      );
      this.verticalPlacement = GlowstoneRuinedPortalPiece.VerticalPlacement.byName(tag.getString("VerticalPlacement"));
      this.properties = (GlowstoneRuinedPortalPiece.Properties)GlowstoneRuinedPortalPiece.Properties.CODEC
         .codec()
         .parse(new Dynamic(NbtOps.INSTANCE, tag.get("Properties")))
         .getPartialOrThrow();
   }

   public GlowstoneRuinedPortalPiece(StructurePieceSerializationContext context, CompoundTag tag) {
      this(context.structureTemplateManager(), tag);
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
      super.addAdditionalSaveData(context, tag);
      tag.putString("Rotation", this.placeSettings.getRotation().name());
      tag.putString("Mirror", this.placeSettings.getMirror().name());
      tag.putString("VerticalPlacement", this.verticalPlacement.getName());
      GlowstoneRuinedPortalPiece.Properties.CODEC
         .codec()
         .encodeStart(NbtOps.INSTANCE, this.properties)
         .resultOrPartial(Aether.LOGGER::error)
         .ifPresent(propertiesTag -> tag.put("Properties", propertiesTag));
   }

   private static StructurePlaceSettings makeSettings(StructureTemplateManager structureTemplateManager, CompoundTag tag, ResourceLocation location) {
      StructureTemplate structuretemplate = structureTemplateManager.getOrCreate(location);
      BlockPos blockpos = new BlockPos(structuretemplate.getSize().getX() / 2, 0, structuretemplate.getSize().getZ() / 2);
      return makeSettings(
         Mirror.valueOf(tag.getString("Mirror")),
         Rotation.valueOf(tag.getString("Rotation")),
         blockpos,
         (GlowstoneRuinedPortalPiece.Properties)GlowstoneRuinedPortalPiece.Properties.CODEC
            .codec()
            .parse(new Dynamic(NbtOps.INSTANCE, tag.get("Properties")))
            .getPartialOrThrow()
      );
   }

   private static StructurePlaceSettings makeSettings(Mirror mirror, Rotation rotation, BlockPos pos, GlowstoneRuinedPortalPiece.Properties properties) {
      BlockIgnoreProcessor blockIgnoreProcessor = properties.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
      StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings()
         .setRotation(rotation)
         .setMirror(mirror)
         .setRotationPivot(pos)
         .addProcessor(blockIgnoreProcessor)
         .addProcessor(new SurfaceRuleProcessor())
         .addProcessor(new GlowstonePortalAgeProcessor(properties.mossiness))
         .addProcessor(new DoubleDropsProcessor())
         .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE));
      if (properties.replaceWithHolystone) {
         structurePlaceSettings.addProcessor(HolystoneReplaceProcessor.INSTANCE);
      }

      return structurePlaceSettings;
   }

   public void postProcess(
      WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos
   ) {
      BoundingBox boundingbox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (box.isInside(boundingbox.getCenter())) {
         box.encapsulate(boundingbox);
         super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);
         this.spreadAetherGrass(random, level);
         this.addDirtBuryingBelowPortal(random, level);
         if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach(p_229127_ -> {
               if (this.properties.vines) {
                  this.maybeAddVines(random, level, p_229127_);
               }

               if (this.properties.overgrown) {
                  this.maybeAddLeavesAbove(random, level, p_229127_);
               }
            });
         }
      }
   }

   protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
   }

   private void maybeAddVines(RandomSource random, LevelAccessor level, BlockPos pos) {
      BlockState blockState = level.getBlockState(pos);
      if (!blockState.isAir() && !blockState.is(Blocks.VINE)) {
         Direction direction = getRandomHorizontalDirection(random);
         BlockPos blockPos = pos.relative(direction);
         BlockState relativeState = level.getBlockState(blockPos);
         if (relativeState.isAir() && Block.isFaceFull(blockState.getCollisionShape(level, pos), direction)) {
            BooleanProperty property = VineBlock.getPropertyForFace(direction.getOpposite());
            level.setBlock(blockPos, (BlockState)Blocks.VINE.defaultBlockState().setValue(property, true), 3);
         }
      }
   }

   private void maybeAddLeavesAbove(RandomSource random, LevelAccessor level, BlockPos pos) {
      if (random.nextFloat() < 0.5F && level.getBlockState(pos).is((Block)AetherBlocks.AETHER_GRASS_BLOCK.get()) && level.getBlockState(pos.above()).isAir()) {
         level.setBlock(pos.above(), (BlockState)Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
      }
   }

   private void addDirtBuryingBelowPortal(RandomSource random, LevelAccessor level) {
      for (int i = this.boundingBox.minX() + 1; i < this.boundingBox.maxX(); i++) {
         for (int j = this.boundingBox.minZ() + 1; j < this.boundingBox.maxZ(); j++) {
            BlockPos blockPos = new BlockPos(i, this.boundingBox.minY(), j);
            if (level.getBlockState(blockPos).is((Block)AetherBlocks.AETHER_GRASS_BLOCK.get())) {
               this.addDirtBuryingColumn(random, level, blockPos.below());
            }
         }
      }
   }

   private void addDirtBuryingColumn(RandomSource random, LevelAccessor level, BlockPos pos) {
      MutableBlockPos mutableBlockPos = pos.mutable();
      this.placeAetherDirtOrGrass(random, level, mutableBlockPos);
      int i = 8;

      while (i > 0 && random.nextFloat() < 0.5F) {
         mutableBlockPos.move(Direction.DOWN);
         i--;
         this.placeAetherDirtOrGrass(random, level, mutableBlockPos);
      }
   }

   private void spreadAetherGrass(RandomSource random, LevelAccessor level) {
      boolean flag = this.verticalPlacement == GlowstoneRuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE
         || this.verticalPlacement == GlowstoneRuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
      BlockPos blockPos = this.boundingBox.getCenter();
      int i = blockPos.getX();
      int j = blockPos.getZ();
      float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
      int k = afloat.length;
      int l = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
      int i1 = random.nextInt(Math.max(1, 8 - l / 2));
      MutableBlockPos mutablePos = BlockPos.ZERO.mutable();

      for (int k1 = i - k; k1 <= i + k; k1++) {
         for (int l1 = j - k; l1 <= j + k; l1++) {
            int i2 = Math.abs(k1 - i) + Math.abs(l1 - j);
            int j2 = Math.max(0, i2 + i1);
            if (j2 < k) {
               float f = afloat[j2];
               if (random.nextDouble() < f) {
                  int k2 = getSurfaceY(level, k1, l1, this.verticalPlacement);
                  int l2 = flag ? k2 : Math.min(this.boundingBox.minY(), k2);
                  mutablePos.set(k1, l2, l1);
                  if (Math.abs(l2 - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByAetherGrass(level, mutablePos)) {
                     this.placeAetherDirtOrGrass(random, level, mutablePos);
                     if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(random, level, mutablePos);
                     }

                     this.addDirtBuryingColumn(random, level, mutablePos.below());
                  }
               }
            }
         }
      }
   }

   private boolean canBlockBeReplacedByAetherGrass(LevelAccessor level, BlockPos pos) {
      BlockState blockstate = level.getBlockState(pos);
      return blockstate.is(AetherTags.Blocks.RUINED_PORTAL_GROUND_REPLACEABLE);
   }

   private void placeAetherDirtOrGrass(RandomSource random, LevelAccessor level, BlockPos pos) {
      BlockState grass = getSurfaceBlockForPlacement(level, pos, level.getBlockState(pos));
      if (SpreadingSnowyDirtBlockAccessor.callCanBeGrass(grass, level, pos)) {
         level.setBlock(pos, grass, 3);
         this.growGrassAndFlowers(random, level, pos.above());
      } else {
         level.setBlock(pos, (BlockState)((Block)AetherBlocks.AETHER_DIRT.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true), 3);
      }
   }

   private void growGrassAndFlowers(RandomSource random, LevelAccessor level, BlockPos pos) {
      int featureType = random.nextInt(50);
      if (random.nextInt(100) < 20 && level.isEmptyBlock(pos)) {
         if (featureType < 5 && level.getBlockState(pos.below()).is(AetherTags.Blocks.AETHER_DIRT)) {
            Block flower = random.nextBoolean() ? (Block)AetherBlocks.PURPLE_FLOWER.get() : (Block)AetherBlocks.WHITE_FLOWER.get();
            level.setBlock(pos, flower.defaultBlockState(), 2);
         } else if (random.nextInt(50) > 5) {
            level.setBlock(pos, Blocks.SHORT_GRASS.defaultBlockState(), 2);
         } else {
            DoublePlantBlock.placeAt(level, Blocks.TALL_GRASS.defaultBlockState(), pos, 2);
         }
      }
   }

   private static int getSurfaceY(LevelAccessor level, int x, int z, GlowstoneRuinedPortalPiece.VerticalPlacement verticalPlacement) {
      return level.getHeight(getHeightMapType(verticalPlacement), x, z) - 1;
   }

   public static Types getHeightMapType(GlowstoneRuinedPortalPiece.VerticalPlacement verticalPlacement) {
      return verticalPlacement == GlowstoneRuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Types.OCEAN_FLOOR : Types.WORLD_SURFACE;
   }

   public static BlockState getSurfaceBlockForPlacement(LevelAccessor level, BlockPos pos, BlockState originalState) {
      if (level instanceof WorldGenLevel worldGenLevel
         && !(worldGenLevel instanceof WorldGenRegion region && BlockLogicUtil.isOutOfBounds(pos, region.getCenter()))
         && worldGenLevel.getBiome(pos).is(AetherTags.Biomes.HAS_RUINED_PORTAL_AETHER)
         && worldGenLevel.getChunkSource() instanceof ServerChunkCache serverChunkCache
         && serverChunkCache.getGenerator() instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator) {
         NoiseGeneratorSettings settingsHolder = (NoiseGeneratorSettings)noiseBasedChunkGenerator.generatorSettings().value();
         RuleSource surfaceRule = settingsHolder.surfaceRule();
         ChunkAccess chunkAccess = worldGenLevel.getChunk(pos);
         NoiseChunk noisechunk = ((ChunkAccessAccessor)chunkAccess).aether$getNoiseChunk();
         if (noisechunk != null) {
            CarvingContext carvingcontext = new CarvingContext(
               noiseBasedChunkGenerator,
               worldGenLevel.registryAccess(),
               chunkAccess.getHeightAccessorForGeneration(),
               noisechunk,
               serverChunkCache.randomState(),
               surfaceRule
            );
            Optional<BlockState> state = carvingcontext.topMaterial(worldGenLevel.getBiomeManager()::getBiome, chunkAccess, pos, false);
            if (state.isPresent()
               && originalState.is(AetherTags.Blocks.AETHER_DIRT)
               && !originalState.is((Block)AetherBlocks.AETHER_DIRT.get())
               && state.get().is(AetherTags.Blocks.AETHER_DIRT)) {
               return state.get();
            }
         }
      }

      return (BlockState)((Block)AetherBlocks.AETHER_GRASS_BLOCK.get()).defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, true);
   }

   public static class Properties {
      public static final MapCodec<GlowstoneRuinedPortalPiece.Properties> CODEC = RecordCodecBuilder.mapCodec(
         codec -> codec.group(
               Codec.FLOAT.fieldOf("mossiness").forGetter(properties -> properties.mossiness),
               Codec.BOOL.fieldOf("air_pocket").forGetter(properties -> properties.airPocket),
               Codec.BOOL.fieldOf("overgrown").forGetter(properties -> properties.overgrown),
               Codec.BOOL.fieldOf("vines").forGetter(properties -> properties.vines),
               Codec.BOOL.fieldOf("replace_with_holystone").forGetter(properties -> properties.replaceWithHolystone)
            )
            .apply(codec, GlowstoneRuinedPortalPiece.Properties::new)
      );
      public float mossiness;
      public boolean airPocket;
      public boolean overgrown;
      public boolean vines;
      public boolean replaceWithHolystone;

      public Properties() {
      }

      public Properties(float mossiness, boolean airPocket, boolean overgrown, boolean vines, boolean replaceWithHolystone) {
         this.mossiness = mossiness;
         this.airPocket = airPocket;
         this.overgrown = overgrown;
         this.vines = vines;
         this.replaceWithHolystone = replaceWithHolystone;
      }
   }

   public static enum VerticalPlacement implements StringRepresentable {
      ON_LAND_SURFACE("on_land_surface"),
      PARTLY_BURIED("partly_buried"),
      ON_OCEAN_FLOOR("on_ocean_floor");

      public static final EnumCodec<GlowstoneRuinedPortalPiece.VerticalPlacement> CODEC = StringRepresentable.fromEnum(
         GlowstoneRuinedPortalPiece.VerticalPlacement::values
      );
      private final String name;

      private VerticalPlacement(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static GlowstoneRuinedPortalPiece.VerticalPlacement byName(String name) {
         return (GlowstoneRuinedPortalPiece.VerticalPlacement)CODEC.byName(name);
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
