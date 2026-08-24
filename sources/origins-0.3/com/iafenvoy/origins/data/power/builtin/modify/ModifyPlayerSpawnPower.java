package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.accessor.EndRespawningEntity;
import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ModifyPlayerSpawnPower extends Power {
   public static final MapCodec<ModifyPlayerSpawnPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(ModifyPlayerSpawnPower::getDimension),
            Codec.FLOAT.optionalFieldOf("dimension_distance_multiplier", 1.0F).forGetter(ModifyPlayerSpawnPower::getDistanceMultiplier),
            ModifyPlayerSpawnPower.SpawnStrategy.CODEC
               .optionalFieldOf("spawn_strategy", ModifyPlayerSpawnPower.SpawnStrategy.DEFAULT)
               .forGetter(ModifyPlayerSpawnPower::getSpawnStrategy),
            Codec.either(ResourceKey.codec(Registries.BIOME), TagKey.hashedCodec(Registries.BIOME))
               .optionalFieldOf("biome")
               .forGetter(ModifyPlayerSpawnPower::getBiome),
            Codec.either(ResourceKey.codec(Registries.STRUCTURE), TagKey.hashedCodec(Registries.STRUCTURE))
               .optionalFieldOf("structure")
               .forGetter(ModifyPlayerSpawnPower::getStructure)
         )
         .apply(i, ModifyPlayerSpawnPower::new)
   );
   private final ResourceKey<Level> dimension;
   private final float distanceMultiplier;
   private final ModifyPlayerSpawnPower.SpawnStrategy spawnStrategy;
   private final Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome;
   private final Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> structure;

   public ModifyPlayerSpawnPower(
      Power.BaseSettings settings,
      ResourceKey<Level> dimension,
      float distanceMultiplier,
      ModifyPlayerSpawnPower.SpawnStrategy spawnStrategy,
      Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome,
      Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> structure
   ) {
      super(settings);
      this.dimension = dimension;
      this.distanceMultiplier = distanceMultiplier;
      this.spawnStrategy = spawnStrategy;
      this.biome = biome;
      this.structure = structure;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   public float getDistanceMultiplier() {
      return this.distanceMultiplier;
   }

   public ModifyPlayerSpawnPower.SpawnStrategy getSpawnStrategy() {
      return this.spawnStrategy;
   }

   public Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> getBiome() {
      return this.biome;
   }

   public Optional<Either<ResourceKey<Structure>, TagKey<Structure>>> getStructure() {
      return this.structure;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void inactive(@NotNull OriginDataHolder holder) {
      if (holder.getEntity() instanceof ServerPlayer serverPlayer) {
         if (!serverPlayer.hasDisconnected() && serverPlayer.getRespawnPosition() != null && !serverPlayer.isRespawnForced()) {
            serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);
         }
      }
   }

   @SubscribeEvent
   public static void preventEndExitSpawnPointResetting(PlayerRespawnPositionEvent event) {
      event.setCopyOriginalSpawnPosition(((EndRespawningEntity)event.getEntity()).origins$hasRealRespawnPoint());
   }

   public Optional<Tuple<ServerLevel, BlockPos>> getSpawn(Entity entity) {
      if (entity instanceof ServerPlayer serverPlayer) {
         MinecraftServer server = serverPlayer.server;
         ServerLevel targetDimension = server.getLevel(this.dimension);
         if (targetDimension == null) {
            return Optional.empty();
         } else {
            int center = targetDimension.getLogicalHeight() / 2;
            int range = 64;
            AtomicReference<Vec3> newSpawnPointVec = new AtomicReference<>();
            BlockPos dimensionSpawnPos = serverPlayer.serverLevel().getSharedSpawnPos();
            MutableBlockPos newSpawnPointPos = new MutableBlockPos();
            MutableBlockPos mutableDimensionSpawnPos = this.spawnStrategy.apply(dimensionSpawnPos, center, this.distanceMultiplier).mutable();
            this.getBiomePos(entity, targetDimension, mutableDimensionSpawnPos).ifPresent(mutableDimensionSpawnPos::set);
            this.getSpawnPos(entity, targetDimension, mutableDimensionSpawnPos, range).ifPresent(newSpawnPointVec::set);
            if (newSpawnPointVec.get() == null) {
               return Optional.empty();
            } else {
               Vec3 msp = newSpawnPointVec.get();
               newSpawnPointPos.set(msp.x, msp.y, msp.z);
               targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(newSpawnPointPos), 11, Unit.INSTANCE);
               return Optional.of(new Tuple(targetDimension, newSpawnPointPos));
            }
         }
      } else {
         return Optional.empty();
      }
   }

   private Optional<BlockPos> getBiomePos(Entity entity, ServerLevel targetDimension, BlockPos originPos) {
      if (this.biome.isEmpty()) {
         return Optional.empty();
      } else {
         int radius = (Integer)OriginsConfig.INSTANCE.modifyPlayerSpawnPower.radius.getValue();
         int horizontalBlockCheckInterval = (Integer)OriginsConfig.INSTANCE.modifyPlayerSpawnPower.horizontalBlockCheckInterval.getValue();
         int verticalBlockCheckInterval = (Integer)OriginsConfig.INSTANCE.modifyPlayerSpawnPower.verticalBlockCheckInterval.getValue();
         if (radius < 0) {
            radius = 6400;
         }

         if (horizontalBlockCheckInterval <= 0) {
            horizontalBlockCheckInterval = 64;
         }

         if (verticalBlockCheckInterval <= 0) {
            verticalBlockCheckInterval = 64;
         }

         Pair<BlockPos, Holder<Biome>> targetBiomePos = targetDimension.findClosestBiome3d(
            biome -> this.biome.<Boolean>map(x -> (Boolean)x.map(biome::is, biome::is)).orElse(false),
            originPos,
            radius,
            horizontalBlockCheckInterval,
            verticalBlockCheckInterval
         );
         if (targetBiomePos != null) {
            return Optional.of((BlockPos)targetBiomePos.getFirst());
         } else {
            StringBuilder name = new StringBuilder();
            this.biome
               .ifPresent(
                  x -> x.map(
                     key -> name.append("biome \"").append(key.location()).append("\""),
                     tag -> name.append(!name.isEmpty() ? " or " : "").append("any biomes from tag \"").append(tag.location()).append("\"")
                  )
               );
            RegistryAccess access = targetDimension.registryAccess();
            Origins.LOGGER
               .warn(
                  "Power \"{}\" could not set player {}'s spawn point at {} as no matched biome can be found nearby in dimension \"{}\".",
                  new Object[]{this.getId(access), entity.getName().getString(), name, this.dimension.location()}
               );
            entity.sendSystemMessage(
               Component.literal(
                     "Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!"
                        .formatted(this.getId(access), name, this.dimension.location())
                  )
                  .withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY})
            );
            return Optional.empty();
         }
      }
   }

   private Optional<Tuple<BlockPos, Structure>> getStructurePos(Entity entity, ServerLevel dimension) {
      if (this.structure.isEmpty()) {
         return Optional.empty();
      } else {
         Registry<Structure> structureRegistry = dimension.registryAccess().registryOrThrow(Registries.STRUCTURE);
         List<Holder<Structure>> structureEntries = new ArrayList<>();
         this.structure
            .ifPresent(
               x -> x.ifLeft(key -> structureEntries.add(structureRegistry.getHolderOrThrow(key)))
                  .ifRight(tag -> structureRegistry.getTag(tag).ifPresent(h -> h.stream().forEach(structureEntries::add)))
            );
         BlockPos center = new BlockPos(0, 70, 0);
         int radius = (Integer)OriginsConfig.INSTANCE.modifyPlayerSpawnPower.radius.getValue();
         if (radius < 0) {
            radius = 6400;
         }

         Optional<Tuple<BlockPos, Structure>> result = Optional.ofNullable(
               dimension.getChunkSource().getGenerator().findNearestMapStructure(dimension, HolderSet.direct(structureEntries), center, radius, false)
            )
            .map(pair -> pair.mapSecond(Holder::value))
            .map(pair -> new Tuple((BlockPos)pair.getFirst(), (Structure)pair.getSecond()));
         if (result.isEmpty()) {
            StringBuilder name = new StringBuilder();
            this.structure
               .ifPresent(
                  x -> x.ifLeft(key -> name.append("structure \"").append(key.location()).append("\""))
                     .ifRight(tag -> name.append(!name.isEmpty() ? " or " : "").append("any structures from tag \"").append(tag.location()).append("\""))
               );
            RegistryAccess access = dimension.registryAccess();
            Origins.LOGGER
               .warn(
                  "Power \"{}\" could not set player {}'s spawn point at {} as no matched structure can be found nearby in dimension \"{}\".",
                  new Object[]{this.getId(access), entity.getName().getString(), name, this.dimension.location()}
               );
            entity.sendSystemMessage(
               Component.literal(
                     "Power \"%s\" couldn't set spawn point at %s as none can be found nearby in dimension \"%s\"!"
                        .formatted(this.getId(access), name, this.dimension.location())
                  )
                  .withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY})
            );
            return Optional.empty();
         } else {
            return result;
         }
      }
   }

   private Optional<Vec3> getSpawnPos(Entity entity, ServerLevel targetDimension, BlockPos originPos, int range) {
      if (this.structure.isEmpty()) {
         return this.getValidSpawn(entity, targetDimension, originPos, range);
      } else {
         Optional<Tuple<BlockPos, Structure>> targetStructure = this.getStructurePos(entity, targetDimension);
         if (targetStructure.isEmpty()) {
            return Optional.empty();
         } else {
            BlockPos structurePos = (BlockPos)targetStructure.get().getA();
            Structure structure = (Structure)targetStructure.get().getB();
            ChunkPos chunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
            SectionPos chunkSectionPos = SectionPos.of(chunkPos, 0);
            return Optional.ofNullable(
                  targetDimension.structureManager().getStartForStructure(chunkSectionPos, structure, targetDimension.getChunk(structurePos))
               )
               .map(structureStart -> structureStart.getBoundingBox().getCenter())
               .flatMap(pos -> this.getValidSpawn(entity, targetDimension, pos, range));
         }
      }
   }

   private Optional<Vec3> getValidSpawn(Entity entity, ServerLevel targetDimension, BlockPos startPos, int range) {
      int dx = 1;
      int dz = 0;
      int segmentLength = 1;
      int center = startPos.getY();
      MutableBlockPos mutableStartPos = startPos.mutable();
      int x = startPos.getX();
      int z = startPos.getZ();
      int segmentPassed = 0;
      int upOffset = 0;
      int downOffset = 0;
      int maxY = targetDimension.getLogicalHeight();
      int minY = ((DimensionType)targetDimension.dimensionTypeRegistration().value()).minY();

      while (upOffset < maxY || downOffset > minY) {
         for (int steps = 0; steps < range; steps++) {
            x += dx;
            z += dz;
            mutableStartPos.setX(x);
            mutableStartPos.setZ(z);
            segmentPassed++;
            mutableStartPos.setY(center + upOffset);
            Vec3 spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
            if (spawnPos != null) {
               return Optional.of(spawnPos);
            }

            mutableStartPos.setY(center + downOffset);
            spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
            if (spawnPos != null) {
               return Optional.of(spawnPos);
            }

            if (segmentPassed == segmentLength) {
               segmentPassed = 0;
               int bdx = dx;
               dx = -dz;
               dz = bdx;
               if (bdx == 0) {
                  segmentLength++;
               }
            }
         }

         if (upOffset < maxY) {
            upOffset++;
         }

         if (downOffset > minY) {
            downOffset--;
         }
      }

      return Optional.empty();
   }

   public static enum SpawnStrategy implements StringRepresentable {
      CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
      DEFAULT((blockPos, center, multiplier) -> {
         MutableBlockPos mut = new MutableBlockPos();
         if (multiplier != 0.0F) {
            mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
         } else {
            mut.set(blockPos);
         }

         return mut;
      });

      public static final Codec<ModifyPlayerSpawnPower.SpawnStrategy> CODEC = StringRepresentable.fromValues(ModifyPlayerSpawnPower.SpawnStrategy::values);
      private final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;

      private SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
         this.strategyApplier = strategyApplier;
      }

      public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
         return (BlockPos)this.strategyApplier.apply(blockPos, center, multiplier);
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
