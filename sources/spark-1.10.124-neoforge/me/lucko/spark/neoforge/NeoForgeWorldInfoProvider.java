package me.lucko.spark.neoforge;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import me.lucko.spark.common.platform.world.AbstractChunkInfo;
import me.lucko.spark.common.platform.world.CountMap;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.GameRuleTypeVisitor;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.neoforged.fml.ModList;

public abstract class NeoForgeWorldInfoProvider implements WorldInfoProvider {
   protected abstract PackRepository getPackRepository();

   @Override
   public Collection<WorldInfoProvider.DataPackInfo> pollDataPacks() {
      return this.getPackRepository()
         .getSelectedPacks()
         .stream()
         .map(pack -> new WorldInfoProvider.DataPackInfo(pack.getId(), pack.getDescription().getString(), resourcePackSource(pack.getPackSource())))
         .collect(Collectors.toList());
   }

   private static String resourcePackSource(PackSource source) {
      if (source == PackSource.DEFAULT) {
         return "none";
      } else if (source == PackSource.BUILT_IN) {
         return "builtin";
      } else if (source == PackSource.WORLD) {
         return "world";
      } else {
         return source == PackSource.SERVER ? "server" : "unknown";
      }
   }

   public static final class Client extends NeoForgeWorldInfoProvider {
      private final Minecraft client;

      public Client(Minecraft client) {
         this.client = client;
      }

      @Override
      public WorldInfoProvider.CountsResult pollCounts() {
         ClientLevel level = this.client.level;
         if (level == null) {
            return null;
         } else {
            int entities;
            if (ModList.get().isLoaded("moonrise")) {
               entities = NeoForgeWorldInfoProvider.MoonriseMethods.getEntityCount(level.getEntities());
            } else {
               TransientEntitySectionManager<Entity> entityManager = level.entityStorage;
               EntityLookup<Entity> entityIndex = entityManager.entityStorage;
               entities = entityIndex.count();
            }

            int chunks = level.getChunkSource().getLoadedChunksCount();
            return new WorldInfoProvider.CountsResult(-1, entities, -1, chunks);
         }
      }

      @Override
      public WorldInfoProvider.ChunksResult<NeoForgeWorldInfoProvider.ForgeChunkInfo> pollChunks() {
         ClientLevel level = this.client.level;
         if (level == null) {
            return null;
         } else {
            WorldInfoProvider.ChunksResult<NeoForgeWorldInfoProvider.ForgeChunkInfo> data = new WorldInfoProvider.ChunksResult<>();
            Long2ObjectOpenHashMap<NeoForgeWorldInfoProvider.ForgeChunkInfo> levelInfos = new Long2ObjectOpenHashMap();

            for (Entity entity : level.getEntities().getAll()) {
               NeoForgeWorldInfoProvider.ForgeChunkInfo info = (NeoForgeWorldInfoProvider.ForgeChunkInfo)levelInfos.computeIfAbsent(
                  entity.chunkPosition().toLong(), NeoForgeWorldInfoProvider.ForgeChunkInfo::new
               );
               info.entityCounts.increment(entity.getType());
            }

            data.put(level.dimension().location().getPath(), List.copyOf(levelInfos.values()));
            return data;
         }
      }

      @Override
      public WorldInfoProvider.GameRulesResult pollGameRules() {
         return null;
      }

      @Override
      protected PackRepository getPackRepository() {
         return this.client.getResourcePackRepository();
      }
   }

   public static final class ForgeChunkInfo extends AbstractChunkInfo<EntityType<?>> {
      private final CountMap<EntityType<?>> entityCounts = new CountMap.Simple<>(new HashMap<>());

      ForgeChunkInfo(long chunkPos) {
         super(ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos));
      }

      @Override
      public CountMap<EntityType<?>> getEntityCounts() {
         return this.entityCounts;
      }

      public String entityTypeName(EntityType<?> type) {
         return EntityType.getKey(type).toString();
      }
   }

   private static final class MoonriseMethods {
      private static Method getEntityCount;

      private static Method getEntityCountMethod(LevelEntityGetter<Entity> getter) {
         if (getEntityCount == null) {
            try {
               getEntityCount = getter.getClass().getMethod("getEntityCount");
            } catch (ReflectiveOperationException var2) {
               throw new RuntimeException("Cannot find Moonrise getEntityCount method", var2);
            }
         }

         return getEntityCount;
      }

      private static int getEntityCount(LevelEntityGetter<Entity> getter) {
         try {
            return (Integer)getEntityCountMethod(getter).invoke(getter);
         } catch (ReflectiveOperationException var2) {
            throw new RuntimeException("Failed to invoke Moonrise getEntityCount method", var2);
         }
      }
   }

   public static final class Server extends NeoForgeWorldInfoProvider {
      private final MinecraftServer server;

      public Server(MinecraftServer server) {
         this.server = server;
      }

      @Override
      public WorldInfoProvider.CountsResult pollCounts() {
         int players = this.server.getPlayerCount();
         int entities = 0;
         int chunks = 0;

         for (ServerLevel level : this.server.getAllLevels()) {
            if (ModList.get().isLoaded("moonrise")) {
               entities += NeoForgeWorldInfoProvider.MoonriseMethods.getEntityCount(level.getEntities());
            } else {
               PersistentEntitySectionManager<Entity> entityManager = level.entityManager;
               EntityLookup<Entity> entityIndex = entityManager.visibleEntityStorage;
               entities += entityIndex.count();
            }

            chunks += level.getChunkSource().getLoadedChunksCount();
         }

         return new WorldInfoProvider.CountsResult(players, entities, -1, chunks);
      }

      @Override
      public WorldInfoProvider.ChunksResult<NeoForgeWorldInfoProvider.ForgeChunkInfo> pollChunks() {
         WorldInfoProvider.ChunksResult<NeoForgeWorldInfoProvider.ForgeChunkInfo> data = new WorldInfoProvider.ChunksResult<>();

         for (ServerLevel level : this.server.getAllLevels()) {
            Long2ObjectOpenHashMap<NeoForgeWorldInfoProvider.ForgeChunkInfo> levelInfos = new Long2ObjectOpenHashMap();

            for (Entity entity : level.getEntities().getAll()) {
               NeoForgeWorldInfoProvider.ForgeChunkInfo info = (NeoForgeWorldInfoProvider.ForgeChunkInfo)levelInfos.computeIfAbsent(
                  entity.chunkPosition().toLong(), NeoForgeWorldInfoProvider.ForgeChunkInfo::new
               );
               info.entityCounts.increment(entity.getType());
            }

            data.put(level.dimension().location().getPath(), List.copyOf(levelInfos.values()));
         }

         return data;
      }

      @Override
      public WorldInfoProvider.GameRulesResult pollGameRules() {
         final WorldInfoProvider.GameRulesResult data = new WorldInfoProvider.GameRulesResult();

         for (final ServerLevel level : this.server.getAllLevels()) {
            final String levelName = level.dimension().location().getPath();
            level.getGameRules();
            GameRules.visitGameRuleTypes(new GameRuleTypeVisitor() {
               public <T extends Value<T>> void visit(Key<T> key, Type<T> type) {
                  String defaultValue = type.createRule().serialize();
                  data.putDefault(key.getId(), defaultValue);
                  String value = level.getGameRules().getRule(key).serialize();
                  data.put(key.getId(), levelName, value);
               }
            });
         }

         return data;
      }

      @Override
      protected PackRepository getPackRepository() {
         return this.server.getPackRepository();
      }
   }
}
