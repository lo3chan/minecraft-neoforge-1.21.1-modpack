package net.conczin.immersive_gateways.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.conczin.immersive_gateways.Blocks;
import net.conczin.immersive_gateways.Common;
import net.conczin.immersive_gateways.Utils;
import net.conczin.immersive_gateways.compat.StructurifyCompat;
import net.conczin.immersive_gateways.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class PortalDataManager {
   private static final long MAX_INHABITED_TIME = 1200L;
   private static final long SEARCH_ATTEMPTS = 16L;
   private static final long SEARCH_FALLBACK_ATTEMPTS = 5L;
   private static final int TOO_CLOSE_CHUNKS = 2;
   private static final RandomSource random = RandomSource.createThreadSafe();

   public static long toLong(int x, int z) {
      return (long)x << 32 | z & 4294967295L;
   }

   public static PortalDataManager.PortalDataLookup getState(ServerLevel level) {
      return (PortalDataManager.PortalDataLookup)level.getDataStorage().computeIfAbsent(PortalDataManager.PortalDataLookup.factory(), "immersive_gateways");
   }

   public static PortalDataManager.PortalPair search(ServerLevel level, BlockPos pos, boolean generate) {
      PortalDataManager.PortalDataLookup state = getState(level);
      PortalDataManager.PortalPair portal = state.search(pos);
      if (portal == null && generate) {
         long t = System.currentTimeMillis();
         BlockPos target = null;

         int attempt;
         for (attempt = 0; attempt < 16L; attempt++) {
            Config c = Config.getInstance();
            float distance = random.nextFloat() * (c.maxDistance - c.minDistance) + c.minDistance;
            double angle = random.nextFloat() * 3.141592653589793;
            target = new BlockPos((int)(pos.getX() + Math.cos(angle) * distance), pos.getY(), (int)(pos.getZ() + Math.sin(angle) * distance));
            BlockPos realTarget = placeStructure(level, target, attempt >= 11L, attempt != 15L, attempt != 15L);
            if (realTarget != null) {
               target = realTarget;
               break;
            }
         }

         portal = new PortalDataManager.PortalPair(
            new PortalDataManager.Portal(estimateBoundingBox(level, pos), getColor(level, pos)),
            new PortalDataManager.Portal(estimateBoundingBox(level, target), getColor(level, target))
         );
         state.add(portal);
         long delta = System.currentTimeMillis() - t;
         Common.LOGGER.info("Portal created in {} ms using {} attempts", delta, attempt);
      }

      return portal;
   }

   public static void addManualConnection(ServerLevel level, BlockPos first, BlockPos second) {
      PortalDataManager.PortalDataLookup state = getState(level);
      state.remove(first);
      state.remove(second);
      PortalDataManager.PortalPair pair = new PortalDataManager.PortalPair(
         new PortalDataManager.Portal(estimateBoundingBox(level, first), getColor(level, first)),
         new PortalDataManager.Portal(estimateBoundingBox(level, second), getColor(level, second))
      );
      state.add(pair);
   }

   public static BlockPos placeStructure(ServerLevel level, BlockPos pos, boolean useFallback, boolean checkInhabitedTime, boolean checkWorldBorder) {
      Registry<Structure> registry = (Registry<Structure>)level.registryAccess().registry(Registries.STRUCTURE).orElse(null);
      if (registry == null) {
         return null;
      } else if (StructurifyCompat.areAllStructuresDisabled()) {
         return null;
      } else if (checkWorldBorder && !level.getWorldBorder().isWithinBounds(pos)) {
         return null;
      } else {
         ChunkAccess chunk = level.getChunk(pos);
         if (checkInhabitedTime && chunk.getInhabitedTime() > 1200L) {
            return null;
         } else {
            PortalDataManager.PortalDataLookup state = getState(level);

            for (int x = -2; x <= 2; x++) {
               for (int z = -2; z <= 2; z++) {
                  PortalDataManager.PortalPair pair = state.search(pos.offset(x * 16, 0, z * 16));
                  if (pair != null) {
                     return null;
                  }
               }
            }

            Holder<Biome> biome = level.getBiome(pos);
            List<Structure> structures = registry.stream().filter(s -> {
               ResourceLocation key = registry.getKey(s);
               return s.biomes().contains(biome) && key != null && key.getNamespace().equals("immersive_gateways");
            }).toList();
            if (structures.isEmpty() && useFallback) {
               TagKey<Structure> structureTagKey = TagKey.create(Registries.STRUCTURE, Common.locate("plains"));
               structures = registry.getTag(structureTagKey).map(t -> t.stream().<Structure>map(Holder::value).toList()).orElse(List.of());
               ResourceLocation biomeName = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(ResourceLocation.parse("minecraft:unknown"));
               Common.LOGGER.info("No structure found for biome {}, using default plains structures.", biomeName);
            }

            structures = structures.stream().filter(s -> !StructurifyCompat.isStructureDisabled(registry.getKey(s))).toList();
            if (structures.isEmpty()) {
               return null;
            } else {
               Structure structure = structures.get(random.nextInt(structures.size()));
               ChunkGenerator chunkgenerator = level.getChunkSource().getGenerator();
               StructureStart structureStart = structure.generate(
                  level.registryAccess(),
                  chunkgenerator,
                  chunkgenerator.getBiomeSource(),
                  level.getChunkSource().randomState(),
                  level.getStructureManager(),
                  level.getSeed(),
                  new ChunkPos(pos),
                  0,
                  level,
                  holder -> true
               );
               if (structureStart.isValid()) {
                  BoundingBox boundingbox = structureStart.getBoundingBox();
                  Utils.getChunksInBoundingBox(boundingbox)
                     .forEach(
                        chunkPos -> level.getServer()
                           .executeBlocking(
                              () -> structureStart.placeInChunk(
                                 level,
                                 level.structureManager(),
                                 chunkgenerator,
                                 random,
                                 new BoundingBox(
                                    chunkPos.getMinBlockX(),
                                    level.getMinBuildHeight(),
                                    chunkPos.getMinBlockZ(),
                                    chunkPos.getMaxBlockX(),
                                    level.getMaxBuildHeight(),
                                    chunkPos.getMaxBlockZ()
                                 ),
                                 chunkPos
                              )
                           )
                     );
                  return findBlockInArea(level, boundingbox);
               } else {
                  return null;
               }
            }
         }
      }
   }

   private static BoundingBox estimateBoundingBox(ServerLevel level, BlockPos pos) {
      int minX = pos.getX();
      int minY = pos.getY();
      int minZ = pos.getZ();
      int maxX = pos.getX();
      int maxY = pos.getY();
      int maxZ = pos.getZ();
      Set<BlockPos> open = new HashSet<>();
      Set<BlockPos> closed = new HashSet<>();
      open.add(pos);
      closed.add(pos);

      while (!open.isEmpty()) {
         BlockPos current = open.iterator().next();
         open.remove(current);
         if (level.getBlockState(current).is(Blocks.GATEWAY)) {
            minX = Math.min(minX, current.getX());
            minY = Math.min(minY, current.getY());
            minZ = Math.min(minZ, current.getZ());
            maxX = Math.max(maxX, current.getX());
            maxY = Math.max(maxY, current.getY());
            maxZ = Math.max(maxZ, current.getZ());

            for (int x = -1; x <= 1; x++) {
               for (int y = -1; y <= 1; y++) {
                  for (int z = -1; z <= 1; z++) {
                     BlockPos neighbor = current.offset(x, y, z);
                     if (!closed.contains(neighbor)) {
                        open.add(neighbor);
                        closed.add(neighbor);
                     }
                  }
               }
            }
         }
      }

      return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
   }

   private static BlockPos findBlockInArea(ServerLevel level, BoundingBox box) {
      MutableBlockPos gatewayPos = new MutableBlockPos();

      for (ChunkPos chunkPos : Utils.getChunksInBoundingBox(box).toList()) {
         LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

         for (int cy = 0; cy < chunk.getSectionsCount(); cy++) {
            LevelChunkSection section = chunk.getSection(cy);
            if (!section.hasOnlyAir() && section.maybeHas(s -> s.is(Blocks.GATEWAY))) {
               for (int x = 0; x < 16; x++) {
                  for (int y = 0; y < 16; y++) {
                     for (int z = 0; z < 16; z++) {
                        gatewayPos.set(
                           SectionPos.sectionToBlockCoord(chunkPos.x, x),
                           SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(cy), y),
                           SectionPos.sectionToBlockCoord(chunkPos.z, z)
                        );
                        if (chunk.getBlockState(gatewayPos).is(Blocks.GATEWAY)) {
                           return gatewayPos;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private static int getColor(ServerLevel level, BlockPos pos) {
      Holder<Biome> biome = level.getBiome(pos);
      ResourceLocation resourceLocation = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(ResourceLocation.parse("minecraft:plains"));
      if (!Config.getInstance().colors.containsKey(resourceLocation.toString())) {
         Common.LOGGER.info("Biome {} not found in color config, using default foliage color.", resourceLocation);
      }

      return Config.getInstance().colors.getOrDefault(resourceLocation.toString(), ((Biome)biome.value()).getFoliageColor());
   }

   public record Portal(BoundingBox boundingBox, int color) {
      public static final Codec<PortalDataManager.Portal> CODEC = RecordCodecBuilder.create(
         portal -> portal.group(
               BoundingBox.CODEC.fieldOf("boundingBox").forGetter(PortalDataManager.Portal::boundingBox),
               Codec.INT.fieldOf("color").forGetter(PortalDataManager.Portal::color)
            )
            .apply(portal, PortalDataManager.Portal::new)
      );

      public BlockPos getSafePosition(ServerLevel level) {
         List<BlockPos> candidates = new LinkedList<>();
         if (this.boundingBox.getZSpan() > 1) {
            int z = (this.boundingBox.minZ() + this.boundingBox.maxZ()) / 2;
            candidates.add(new BlockPos(this.boundingBox.minX() - 1, this.boundingBox.minY(), z));
            candidates.add(new BlockPos(this.boundingBox.maxX() + 1, this.boundingBox.minY(), z));
         }

         if (this.boundingBox.getXSpan() > 1) {
            int x = (this.boundingBox.minX() + this.boundingBox.maxX()) / 2;
            candidates.add(new BlockPos(x, this.boundingBox.minY(), this.boundingBox.minZ() - 1));
            candidates.add(new BlockPos(x, this.boundingBox.minY(), this.boundingBox.maxZ() + 1));
         }

         BlockPos center = this.boundingBox.getCenter();
         level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(center), 3, center);

         for (int y = this.boundingBox.minY(); y <= level.getMaxBuildHeight(); y++) {
            for (BlockPos candidate : candidates) {
               BlockPos pos = new BlockPos(candidate.getX(), y, candidate.getZ());
               if (level.getBlockState(pos).isAir() && level.getBlockState(pos.offset(0, 1, 0)).isAir()) {
                  return pos;
               }
            }
         }

         return center;
      }
   }

   public static class PortalDataLookup extends SavedData {
      final Set<PortalDataManager.PortalPair> portals = new HashSet<>();
      final Map<Long, Set<PortalDataManager.PortalPair>> lookup = new HashMap<>();

      public static Factory<PortalDataManager.PortalDataLookup> factory() {
         return new Factory(PortalDataManager.PortalDataLookup::new, PortalDataManager.PortalDataLookup::load, DataFixTypes.SAVED_DATA_MAP_DATA);
      }

      public static PortalDataManager.PortalDataLookup load(CompoundTag nbt, Provider registries) {
         PortalDataManager.PortalDataLookup c = new PortalDataManager.PortalDataLookup();

         for (String key : nbt.getAllKeys()) {
            PortalDataManager.PortalPair pair = PortalDataManager.PortalPair.load(nbt.get(key));
            c.portals.add(pair);
            c.populateLookup(pair);
         }

         return c;
      }

      public CompoundTag save(CompoundTag nbt, Provider registries) {
         int index = 0;

         for (PortalDataManager.PortalPair pair : this.portals) {
            nbt.put(String.valueOf(index), pair.save());
            index++;
         }

         return nbt;
      }

      public synchronized void add(PortalDataManager.PortalPair data) {
         this.portals.add(data);
         this.populateLookup(data);
         this.setDirty();
      }

      public synchronized void remove(BlockPos pos) {
         PortalDataManager.PortalPair pair = this.search(pos);
         if (pair != null) {
            this.portals.remove(pair);
            this.rebuildLookup();
            this.setDirty();
         }
      }

      private void rebuildLookup() {
         this.lookup.clear();

         for (PortalDataManager.PortalPair pair : this.portals) {
            this.populateLookup(pair);
         }
      }

      private void populateLookup(PortalDataManager.PortalPair data) {
         this.populateLookup(data, data.first);
         this.populateLookup(data, data.second);
      }

      private void populateLookup(PortalDataManager.PortalPair data, PortalDataManager.Portal portal) {
         int minX = portal.boundingBox().minX() >> 4;
         int minZ = portal.boundingBox().minZ() >> 4;
         int maxX = portal.boundingBox().maxX() >> 4;
         int maxZ = portal.boundingBox().maxZ() >> 4;

         for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
               long cellId = PortalDataManager.toLong(x, z);
               this.lookup.computeIfAbsent(cellId, k -> new HashSet<>()).add(data);
            }
         }
      }

      public synchronized PortalDataManager.PortalPair search(BlockPos pos) {
         int cx = pos.getX() >> 4;
         int cz = pos.getZ() >> 4;
         long cellId = PortalDataManager.toLong(cx, cz);
         Set<PortalDataManager.PortalPair> candidates = this.lookup.get(cellId);
         if (candidates != null) {
            for (PortalDataManager.PortalPair candidate : candidates) {
               if (candidate.first.boundingBox().isInside(pos) || candidate.second.boundingBox().isInside(pos)) {
                  return candidate;
               }
            }
         }

         return null;
      }
   }

   public record PortalPair(PortalDataManager.Portal first, PortalDataManager.Portal second) {
      public static final Codec<PortalDataManager.PortalPair> CODEC = RecordCodecBuilder.create(
         pair -> pair.group(
               PortalDataManager.Portal.CODEC.fieldOf("first").forGetter(PortalDataManager.PortalPair::first),
               PortalDataManager.Portal.CODEC.fieldOf("second").forGetter(PortalDataManager.PortalPair::second)
            )
            .apply(pair, PortalDataManager.PortalPair::new)
      );

      public static PortalDataManager.PortalPair load(Tag nbt) {
         return (PortalDataManager.PortalPair)CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(Common.LOGGER::error).orElseThrow();
      }

      public Tag save() {
         return (Tag)CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(Common.LOGGER::error).orElseThrow();
      }

      public PortalDataManager.Portal getTarget(BlockPos pos) {
         double dist1 = this.first.boundingBox.getCenter().distSqr(pos);
         double dist2 = this.second.boundingBox.getCenter().distSqr(pos);
         return dist1 < dist2 ? this.second : this.first;
      }
   }
}
