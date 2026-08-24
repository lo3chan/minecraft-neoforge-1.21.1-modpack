package com.yungnickyoung.minecraft.betterendisland.world.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.yungnickyoung.minecraft.betterendisland.BetterEndIslandCommon;
import com.yungnickyoung.minecraft.betterendisland.world.IBetterDragonFight;
import com.yungnickyoung.minecraft.betterendisland.world.IEndSpike;
import com.yungnickyoung.minecraft.betterendisland.world.SpikeCacheLoader;
import com.yungnickyoung.minecraft.betterendisland.world.processor.BlockReplaceProcessor;
import com.yungnickyoung.minecraft.betterendisland.world.processor.DragonEggProcessor;
import com.yungnickyoung.minecraft.betterendisland.world.processor.ObsidianProcessor;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BetterSpikeFeature {
   private static final LoadingCache<Long, List<EndSpike>> SPIKE_CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(5L, TimeUnit.MINUTES)
      .build(new SpikeCacheLoader());
   private static final List<StructureProcessor> PROCESSORS = List.of(
      new BlockReplaceProcessor(
         Blocks.ORANGE_TERRACOTTA.defaultBlockState(),
         new BlockStateRandomizer(Blocks.OBSIDIAN.defaultBlockState()).addBlock(Blocks.CRYING_OBSIDIAN.defaultBlockState(), 0.3F),
         false,
         false,
         false,
         false
      ),
      new BlockReplaceProcessor(
         Blocks.MAGENTA_TERRACOTTA.defaultBlockState(),
         new BlockStateRandomizer(Blocks.AIR.defaultBlockState())
            .addBlock(Blocks.CRYING_OBSIDIAN.defaultBlockState(), 0.1F)
            .addBlock(Blocks.OBSIDIAN.defaultBlockState(), 0.1F),
         false,
         false,
         false,
         false
      ),
      new BlockReplaceProcessor(
         Blocks.PURPLE_CONCRETE.defaultBlockState(), new BlockStateRandomizer(Blocks.OBSIDIAN.defaultBlockState()), false, false, false, false
      ),
      new DragonEggProcessor()
   );

   public static List<EndSpike> getSpikesForLevel(WorldGenLevel level) {
      RandomSource randomSource = RandomSource.create(level.getSeed());
      long seed = randomSource.nextLong() & 65535L;
      return (List<EndSpike>)SPIKE_CACHE.getUnchecked(seed);
   }

   public static void placeSpike(ServerLevelAccessor level, RandomSource randomSource, SpikeConfiguration config, EndSpike spike, boolean isInitialSpawn) {
      Pair<ResourceLocation, ResourceLocation> templates = chooseTemplates(spike, isInitialSpawn, randomSource.nextFloat() < 0.2F);
      long seed = 0L;
      if (level instanceof WorldGenLevel) {
         seed = ((WorldGenLevel)level).getSeed();
      }

      RandomSource rand = RandomSource.create(seed ^ spike.getCenterX() ^ spike.getCenterZ());
      Rotation rotation = Rotation.getRandom(rand);
      int numberTimesDragonKilled = 0;
      if (level instanceof ServerLevel serverLevel && serverLevel.getDragonFight() != null) {
         numberTimesDragonKilled = ((IBetterDragonFight)serverLevel.getDragonFight()).getNumTimesDragonKilled();
      }

      int topY = BetterEndIslandCommon.betterEnd ? 70 : 60;
      BlockPos centerPos = new BlockPos(spike.getCenterX(), topY, spike.getCenterZ());
      if (!placeTemplate(level, randomSource, centerPos, rotation, (ResourceLocation)templates.getFirst(), numberTimesDragonKilled)) {
         BetterEndIslandCommon.LOGGER.error("Unable to place top spike at {}. This shouldn't happen!", centerPos);
      } else {
         centerPos = centerPos.below(67);
         if (!placeTemplate(level, randomSource, centerPos, rotation, (ResourceLocation)templates.getSecond(), numberTimesDragonKilled)) {
            BetterEndIslandCommon.LOGGER.error("Unable to place bottom spike at {}. This shouldn't happen!", centerPos);
         } else {
            if (!isInitialSpawn) {
               EndCrystal endCrystal = (EndCrystal)EntityType.END_CRYSTAL.create(level.getLevel());
               endCrystal.setBeamTarget(config.getCrystalBeamTarget());
               endCrystal.setInvulnerable(config.isCrystalInvulnerable());
               int crystalY = topY + ((IEndSpike)spike).getCrystalYOffset();
               endCrystal.moveTo(spike.getCenterX() + 0.5, crystalY, spike.getCenterZ() + 0.5, randomSource.nextFloat() * 360.0F, 0.0F);
               level.addFreshEntity(endCrystal);
               level.setBlock(new BlockPos(spike.getCenterX(), crystalY - 1, spike.getCenterZ()), Blocks.BEDROCK.defaultBlockState(), 3);
            }
         }
      }
   }

   private static Pair<ResourceLocation, ResourceLocation> chooseTemplates(EndSpike spike, boolean isInitialSpawn, boolean isGuarded) {
      String pillarType = isInitialSpawn ? "initial" : (isGuarded ? "guarded" : "broken");
      int pillarHeight = (spike.getHeight() - 73) / 3;
      if (pillarHeight == 10) {
         pillarHeight = 9;
      }

      String topName = "pillar_" + pillarType + "_" + pillarHeight;
      String bottomName = "pillar_bottom_" + pillarHeight;
      ((IEndSpike)spike).setCrystalYOffsetFromPillarHeight(pillarHeight);
      return new Pair(ResourceLocation.fromNamespaceAndPath("betterendisland", topName), ResourceLocation.fromNamespaceAndPath("betterendisland", bottomName));
   }

   private static boolean placeTemplate(
      ServerLevelAccessor level, RandomSource randomSource, BlockPos centerPos, Rotation rotation, ResourceLocation id, int numberTimesDragonKilled
   ) {
      Optional<StructureTemplate> templateOptional = level.getLevel().getStructureManager().get(id);
      if (templateOptional.isEmpty()) {
         BetterEndIslandCommon.LOGGER.warn("Failed to create invalid feature {}", id);
         return false;
      } else {
         StructureTemplate template = templateOptional.get();
         BlockPos cornerPos = centerPos.offset(-template.getSize().getX() / 2, 0, -template.getSize().getZ() / 2);
         StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings();
         PROCESSORS.forEach(structurePlaceSettings::addProcessor);
         structurePlaceSettings.addProcessor(new ObsidianProcessor(numberTimesDragonKilled));
         structurePlaceSettings.setRotation(rotation);
         structurePlaceSettings.setRotationPivot(new BlockPos(9, 0, 9));
         structurePlaceSettings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
         template.placeInWorld(level, cornerPos, centerPos, structurePlaceSettings, randomSource, 2);
         return true;
      }
   }
}
