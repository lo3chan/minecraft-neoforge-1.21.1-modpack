package me.lucko.spark.common.platform.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface WorldInfoProvider {
   WorldInfoProvider NO_OP = new WorldInfoProvider() {
      @Override
      public WorldInfoProvider.CountsResult pollCounts() {
         return null;
      }

      @Override
      public WorldInfoProvider.ChunksResult<? extends ChunkInfo<?>> pollChunks() {
         return null;
      }

      @Override
      public WorldInfoProvider.GameRulesResult pollGameRules() {
         return null;
      }

      @Override
      public Collection<WorldInfoProvider.DataPackInfo> pollDataPacks() {
         return null;
      }
   };

   WorldInfoProvider.CountsResult pollCounts();

   WorldInfoProvider.ChunksResult<? extends ChunkInfo<?>> pollChunks();

   WorldInfoProvider.GameRulesResult pollGameRules();

   Collection<WorldInfoProvider.DataPackInfo> pollDataPacks();

   default boolean mustCallSync() {
      return true;
   }

   public static final class ChunksResult<T extends ChunkInfo<?>> {
      private final Map<String, List<T>> worlds = new HashMap<>();

      public void put(String worldName, List<T> chunks) {
         this.worlds.put(worldName, chunks);
      }

      public Map<String, List<T>> getWorlds() {
         return this.worlds;
      }
   }

   public static final class CountsResult {
      private final int players;
      private final int entities;
      private final int tileEntities;
      private final int chunks;

      public CountsResult(int players, int entities, int tileEntities, int chunks) {
         this.players = players;
         this.entities = entities;
         this.tileEntities = tileEntities;
         this.chunks = chunks;
      }

      public int players() {
         return this.players;
      }

      public int entities() {
         return this.entities;
      }

      public int tileEntities() {
         return this.tileEntities;
      }

      public int chunks() {
         return this.chunks;
      }
   }

   public static final class DataPackInfo {
      private final String name;
      private final String description;
      private final String source;

      public DataPackInfo(String name, String description, String source) {
         this.name = name;
         this.description = description;
         this.source = source;
      }

      public String name() {
         return this.name;
      }

      public String description() {
         return this.description;
      }

      public String source() {
         return this.source;
      }
   }

   public static final class GameRulesResult {
      private final Map<String, WorldInfoProvider.GameRulesResult.GameRule> rules = new HashMap<>();

      private WorldInfoProvider.GameRulesResult.GameRule rule(String name) {
         return this.rules.computeIfAbsent(name, k -> new WorldInfoProvider.GameRulesResult.GameRule());
      }

      public void put(String gameRuleName, String worldName, String value) {
         this.rule(gameRuleName).worldValues.put(worldName, value);
      }

      public void putDefault(String gameRuleName, String value) {
         this.rule(gameRuleName).defaultValue = value;
      }

      public Map<String, WorldInfoProvider.GameRulesResult.GameRule> getRules() {
         return this.rules;
      }

      public static final class GameRule {
         Map<String, String> worldValues = new HashMap<>();
         String defaultValue = null;

         public String getDefaultValue() {
            return this.defaultValue;
         }

         public Map<String, String> getWorldValues() {
            return this.worldValues;
         }
      }
   }
}
