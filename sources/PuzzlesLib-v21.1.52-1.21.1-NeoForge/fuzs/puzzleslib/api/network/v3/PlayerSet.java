package fuzs.puzzleslib.api.network.v3;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlayerSet {
   void apply(Consumer<ServerPlayer> var1);

   static PlayerSet ofNone() {
      return Function.identity()::apply;
   }

   static PlayerSet ofEntity(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return entity instanceof ServerPlayer serverPlayer ? ofPlayer(serverPlayer) : ofNone();
   }

   static PlayerSet ofPlayer(ServerPlayer serverPlayer) {
      Objects.requireNonNull(serverPlayer, "server player is null");
      return serverPlayerConsumer -> serverPlayerConsumer.accept(serverPlayer);
   }

   static PlayerSet ofOthers(ServerPlayer serverPlayer) {
      Objects.requireNonNull(serverPlayer, "server player is null");
      return serverPlayerConsumer -> serverPlayer.level().getServer().getPlayerList().getPlayers().forEach(currentServerPlayer -> {
         if (currentServerPlayer != serverPlayer) {
            ofPlayer(currentServerPlayer).apply(serverPlayerConsumer);
         }
      });
   }

   static PlayerSet ofAll(MinecraftServer minecraftServer) {
      return serverPlayerConsumer -> minecraftServer.getPlayerList().getPlayers().forEach(serverPlayer -> ofPlayer(serverPlayer).apply(serverPlayerConsumer));
   }

   static PlayerSet inLevel(Level level) {
      Objects.requireNonNull(level, "level is null");
      return level instanceof ServerLevel serverLevel ? inLevel(serverLevel) : ofNone();
   }

   static PlayerSet inLevel(ServerLevel serverLevel) {
      Objects.requireNonNull(serverLevel, "server level is null");
      return serverPlayerConsumer -> {
         for (ServerPlayer serverPlayer : serverLevel.players()) {
            ofPlayer(serverPlayer).apply(serverPlayerConsumer);
         }
      };
   }

   static PlayerSet nearPosition(Vec3i position, ServerLevel serverLevel) {
      Objects.requireNonNull(position, "position is null");
      return nearPosition(position.getX(), position.getY(), position.getZ(), serverLevel);
   }

   static PlayerSet nearPosition(double posX, double posY, double posZ, ServerLevel serverLevel) {
      return nearPosition(null, posX, posY, posZ, 64.0, serverLevel);
   }

   static PlayerSet nearPosition(@Nullable ServerPlayer excludePlayer, double posX, double posY, double posZ, double distance, ServerLevel serverLevel) {
      Objects.requireNonNull(serverLevel, "server level is null");
      return serverPlayerConsumer -> {
         for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != excludePlayer && serverPlayer.level().dimension() == serverLevel.dimension()) {
               double deltaX = posX - serverPlayer.getX();
               double deltaY = posY - serverPlayer.getY();
               double deltaZ = posZ - serverPlayer.getZ();
               if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ < distance * distance) {
                  ofPlayer(serverPlayer).apply(serverPlayerConsumer);
               }
            }
         }
      };
   }

   static PlayerSet nearBlockEntity(BlockEntity blockEntity) {
      Objects.requireNonNull(blockEntity, "block entity is null");
      Level level = blockEntity.getLevel();
      Objects.requireNonNull(level, "block entity level is null");
      return level.isClientSide() ? ofNone() : nearPosition(blockEntity.getBlockPos(), (ServerLevel)level);
   }

   static PlayerSet nearChunk(LevelChunk levelChunk) {
      Objects.requireNonNull(levelChunk, "chunk is null");
      return levelChunk.getLevel().isClientSide() ? ofNone() : nearChunk((ServerLevel)levelChunk.getLevel(), levelChunk.getPos());
   }

   static PlayerSet nearChunk(ServerLevel serverLevel, ChunkPos chunkPos) {
      Objects.requireNonNull(serverLevel, "server level is null");
      Objects.requireNonNull(chunkPos, "chunk pos is null");
      return serverPlayerConsumer -> serverLevel.getChunkSource()
         .chunkMap
         .getPlayers(chunkPos, false)
         .forEach(serverPlayer -> ofPlayer(serverPlayer).apply(serverPlayerConsumer));
   }

   static PlayerSet nearEntity(Entity entity) {
      Objects.requireNonNull(entity, "entity is null");
      return entity.level().isClientSide() ? ofNone() : serverPlayerConsumer -> {
         ChunkMap chunkMap = ((ServerLevel)entity.level()).getChunkSource().chunkMap;
         TrackedEntity trackedEntity = (TrackedEntity)chunkMap.entityMap.get(entity.getId());
         if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
               ofPlayer(serverPlayerConnection.getPlayer()).apply(serverPlayerConsumer);
            }

            if (entity instanceof ServerPlayer serverPlayer) {
               ofPlayer(serverPlayer).apply(serverPlayerConsumer);
            }
         }
      };
   }

   static PlayerSet nearPlayer(ServerPlayer serverPlayer) {
      Objects.requireNonNull(serverPlayer, "server player is null");
      return serverPlayerConsumer -> {
         ChunkMap chunkMap = serverPlayer.serverLevel().getChunkSource().chunkMap;
         TrackedEntity trackedEntity = (TrackedEntity)chunkMap.entityMap.get(serverPlayer.getId());
         if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
               ofPlayer(serverPlayerConnection.getPlayer()).apply(serverPlayerConsumer);
            }
         }
      };
   }
}
