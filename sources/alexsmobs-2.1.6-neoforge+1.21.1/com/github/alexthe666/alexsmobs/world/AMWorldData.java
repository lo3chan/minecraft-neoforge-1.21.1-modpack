package com.github.alexthe666.alexsmobs.world;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class AMWorldData extends SavedData {
   private static final String IDENTIFIER = "alexsmobs_world_data";
   private ServerLevel level;
   private int tickCounter;
   private int beachedCachalotSpawnDelay;
   private int beachedCachalotSpawnChance;
   private UUID beachedCachalotID;
   private ChunkPos pupfishChunk;
   private int pupfishChunkTime = 0;
   private int pupfishSeedAddition = 0;
   private long startPupfishSearchTimestamp = -1L;
   private boolean noPupfishChunk;
   private static final Map<Level, AMWorldData> dataMap = new HashMap<>();
   private static final Predicate<BlockState> IS_WATER = state -> state.is(Blocks.WATER);

   private static String savedDataId() {
      return "alexsmobs_world_data";
   }

   public static AMWorldData get(Level world) {
      if (world instanceof ServerLevel) {
         ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
         AMWorldData fromMap = dataMap.get(overworld);
         if (fromMap == null) {
            DimensionDataStorage storage = overworld.getDataStorage();
            AMWorldData data = (AMWorldData)storage.computeIfAbsent(
               new Factory(AMWorldData::new, AMWorldData::load, DataFixTypes.LEVEL), "alexsmobs_world_data"
            );
            if (data != null) {
               data.level = overworld;
               data.setDirty();
            }

            dataMap.put(world, data);
            return data;
         } else {
            return fromMap;
         }
      } else {
         return null;
      }
   }

   public static AMWorldData load(CompoundTag nbt) {
      AMWorldData data = new AMWorldData();
      if (AMCompat.contains(nbt, "BeachedCachalotSpawnDelay", 99)) {
         data.beachedCachalotSpawnDelay = AMCompat.getInt(nbt, "BeachedCachalotSpawnDelay");
      }

      if (AMCompat.contains(nbt, "BeachedCachalotSpawnChance", 99)) {
         data.beachedCachalotSpawnChance = AMCompat.getInt(nbt, "BeachedCachalotSpawnChance");
      }

      if (AMCompat.contains(nbt, "BeachedCachalotId", 8)) {
         data.beachedCachalotID = UUID.fromString(AMCompat.getString(nbt, "BeachedCachalotId"));
      }

      if (AMCompat.contains(nbt, "PupfishChunkX") && AMCompat.contains(nbt, "PupfishChunkZ")) {
         data.pupfishChunk = new ChunkPos(AMCompat.getInt(nbt, "PupfishChunkX"), AMCompat.getInt(nbt, "PupfishChunkZ"));
      }

      if (AMCompat.contains(nbt, "NoPupfishChunk")) {
         data.noPupfishChunk = AMCompat.getBoolean(nbt, "NoPupfishChunk");
      }

      return data;
   }

   public int getBeachedCachalotSpawnDelay() {
      return this.beachedCachalotSpawnDelay;
   }

   public void setBeachedCachalotSpawnDelay(int delay) {
      this.beachedCachalotSpawnDelay = delay;
   }

   public int getBeachedCachalotSpawnChance() {
      return this.beachedCachalotSpawnChance;
   }

   public void setBeachedCachalotSpawnChance(int chance) {
      this.beachedCachalotSpawnChance = chance;
   }

   public void setBeachedCachalotID(UUID id) {
      this.beachedCachalotID = id;
   }

   public void debug() {
   }

   public void tick() {
      this.tickCounter++;
   }

   public CompoundTag save(CompoundTag compound, Provider provider) {
      return this.saveTo(compound);
   }

   public static AMWorldData load(CompoundTag nbt, Provider provider) {
      return load(nbt);
   }

   private CompoundTag saveTo(CompoundTag compound) {
      compound.putInt("beachedCachalotSpawnDelay", this.beachedCachalotSpawnDelay);
      compound.putInt("beachedCachalotSpawnChance", this.beachedCachalotSpawnChance);
      if (this.beachedCachalotID != null) {
         compound.putString("beachedCachalotId", this.beachedCachalotID.toString());
      }

      if (this.pupfishChunk != null) {
         compound.putInt("PupfishChunkX", this.pupfishChunk.x);
         compound.putInt("PupfishChunkZ", this.pupfishChunk.z);
      }

      if (this.noPupfishChunk) {
         compound.putBoolean("NoPupfishChunk", this.noPupfishChunk);
      }

      return compound;
   }

   @Nullable
   public ChunkPos getPupfishChunk() {
      return this.pupfishChunk;
   }

   public boolean isInPupfishChunk(BlockPos pos) {
      return this.pupfishChunk == null
         ? false
         : pos.getX() >= this.pupfishChunk.getMinBlockX()
            && pos.getX() <= this.pupfishChunk.getMaxBlockX()
            && pos.getZ() >= this.pupfishChunk.getMinBlockZ()
            && pos.getZ() <= this.pupfishChunk.getMaxBlockZ();
   }

   public void tickPupfish() {
      if (AMConfig.restrictPupfishSpawns && !this.noPupfishChunk) {
         if (this.pupfishChunk == null && this.startPupfishSearchTimestamp == -1L) {
            this.startPupfishSearchTimestamp = System.currentTimeMillis();
         }

         if (this.pupfishChunk == null && this.pupfishChunkTime % 10 == 0) {
            long seconds = (System.currentTimeMillis() - this.startPupfishSearchTimestamp) / 1000L;
            if (seconds / 60L > 5L) {
               AlexsMobs.LOGGER.info("Giving up search for pupfish chunk after " + seconds / 60L + " minutes. no pupfish will spawn in this world :( ");
               this.noPupfishChunk = true;
            } else {
               this.searchForPupfishChunk();
            }
         }

         this.pupfishChunkTime++;
      }
   }

   private void searchForPupfishChunk() {
      if (this.level != null && this.level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator chunkGenerator) {
         Random random = new Random(this.level.getSeed() + this.pupfishSeedAddition);
         int randomXCoord = random.nextInt(AMConfig.pupfishChunkSpawnDistance * 2) - AMConfig.pupfishChunkSpawnDistance;
         int randomZCoord = random.nextInt(AMConfig.pupfishChunkSpawnDistance * 2) - AMConfig.pupfishChunkSpawnDistance;
         ChunkPos checkPos = new ChunkPos(randomXCoord >> 4, randomZCoord >> 4);
         BlockPos center = new BlockPos(checkPos.getMiddleBlockX(), chunkGenerator.getSeaLevel(), checkPos.getMiddleBlockZ());
         int maxWater = this.getWaterHeight(chunkGenerator, this.level.getChunkSource().randomState(), center.getX(), center.getZ(), this.level);
         if (maxWater > 31 && maxWater < 63) {
            this.pupfishChunk = checkPos;
            AlexsMobs.LOGGER
               .info(
                  "Found Pupfish chunk at "
                     + this.pupfishChunk.getMaxBlockX()
                     + " ~ "
                     + this.pupfishChunk.getMinBlockZ()
                     + " after "
                     + this.pupfishSeedAddition
                     + " tries"
               );
         }
      }

      this.pupfishSeedAddition++;
   }

   public int getWaterHeight(NoiseBasedChunkGenerator generator, RandomState rand, int x, int z, LevelHeightAccessor level) {
      NoiseSettings noisesettings = ((NoiseGeneratorSettings)generator.settings.value()).noiseSettings();
      int i = Math.max(noisesettings.minY(), AMCompat.minBuildHeight(level));
      int j = Math.min(noisesettings.minY() + noisesettings.height(), AMCompat.maxBuildHeight(level));
      int k = Mth.floorDiv(i, noisesettings.getCellHeight());
      int l = Mth.floorDiv(j - i, noisesettings.getCellHeight());
      return generator.iterateNoiseColumn(level, rand, x, z, null, IS_WATER).orElse(AMCompat.minBuildHeight(level));
   }
}
