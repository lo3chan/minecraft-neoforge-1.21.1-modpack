package com.seibel.distanthorizons.core.multiplayer.config;

import com.google.common.base.MoreObjects;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.ConfigChangeListener;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.network.INetworkObject;
import io.netty.buffer.ByteBuf;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SessionConfig implements INetworkObject {
   private static final LinkedHashMap<String, SessionConfig.Entry> CONFIG_ENTRIES = new LinkedHashMap<>();
   private final HashMap<String, Object> values = new HashMap<>();
   public SessionConfig constrainingConfig;

   public boolean isDistantGenerationEnabled() {
      return this.getValue(Config.Common.WorldGenerator.enableDistantGeneration);
   }

   public int getMaxGenerationRequestDistance() {
      return this.getValue(Config.Server.maxGenerationRequestDistance);
   }

   public Integer getGenerationCenterChunkX() {
      return this.getValue(Config.Common.WorldGenerator.generationCenterChunkX);
   }

   public Integer getGenerationCenterChunkZ() {
      return this.getValue(Config.Common.WorldGenerator.generationCenterChunkZ);
   }

   public Integer getGenerationMaxChunkRadius() {
      return this.getValue(Config.Common.WorldGenerator.generationMaxChunkRadius);
   }

   public int getGenerationRequestRateLimit() {
      return this.getValue(Config.Server.generationRequestRateLimit);
   }

   public boolean isRealTimeUpdatesEnabled() {
      return this.getValue(Config.Server.enableRealTimeUpdates);
   }

   public int getMaxUpdateDistanceRadius() {
      return this.getValue(Config.Server.realTimeUpdateDistanceRadiusInChunks);
   }

   public boolean getSynchronizeOnLoad() {
      return this.getValue(Config.Server.synchronizeOnLoad);
   }

   public int getMaxSyncOnLoadDistance() {
      return this.getValue(Config.Server.maxSyncOnLoadRequestDistance);
   }

   public int getSyncOnLoginRateLimit() {
      return this.getValue(Config.Server.syncOnLoadRateLimit);
   }

   public int getPlayerBandwidthLimit() {
      return this.getValue(Config.Server.playerBandwidthLimit);
   }

   private static <T> void registerConfigEntry(ConfigEntry<T> configEntry, BinaryOperator<T> valueConstrainer) {
      registerConfigEntry(
         Objects.requireNonNull(configEntry.getChatCommandName()),
         new SessionConfig.Entry(configEntry::get, runnable -> new ConfigChangeListener<>(configEntry, ignored -> runnable.run()), valueConstrainer)
      );
   }

   private static void registerConfigEntry(String key, SessionConfig.Entry entry) {
      if (CONFIG_ENTRIES.containsKey(key)) {
         throw new IllegalArgumentException("Attempted to register config entry with duplicate key: " + key);
      } else {
         CONFIG_ENTRIES.put(key, entry);
      }
   }

   private <T> T getValue(ConfigEntry<T> configEntry) {
      return this.getValue(configEntry.getChatCommandName());
   }

   private <T> T getValue(String name) {
      SessionConfig.Entry entry = CONFIG_ENTRIES.get(name);
      T value = (T)this.values.get(name);
      if (value == null) {
         value = (T)entry.valueSupplier.get();
      }

      return (T)(this.constrainingConfig != null ? entry.valueConstrainer.apply(this.constrainingConfig.getValue(name), value) : value);
   }

   public <T> void constrainValue(ConfigEntry<T> configEntry, T value) {
      this.constrainValue(configEntry.getChatCommandName(), value);
   }

   private void constrainValue(String name, Object value) {
      SessionConfig.Entry entry = CONFIG_ENTRIES.get(name);
      this.values.put(name, entry.valueConstrainer.apply(this.getValue(name), value));
   }

   private Map<String, ?> getValues() {
      return CONFIG_ENTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), this::getValue, (x, y) -> x, LinkedHashMap::new));
   }

   @Override
   public void encode(ByteBuf outBuffer) {
      this.writeFixedLengthCollection(outBuffer, this.getValues().values());
   }

   @Override
   public void decode(ByteBuf inBuffer) {
      for (String key : CONFIG_ENTRIES.keySet()) {
         Object currentValue = this.getValue(key);
         Object newValue = INetworkObject.Codec.getCodec(currentValue.getClass()).decode.apply(currentValue, inBuffer);
         this.values.put(key, newValue);
      }
   }

   public String getDifferencesAsString(SessionConfig that) {
      StringBuilder stringBuilder = new StringBuilder();

      for (String key : this.values.keySet()) {
         String thisFieldString = this.values.get(key) + "";
         String thatFieldString = that.values.get(key) + "";
         if (!thisFieldString.equals(thatFieldString)) {
            stringBuilder.append(key + ":[" + thisFieldString + "], ");
         }
      }

      return stringBuilder.toString();
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("values", this.getValues()).toString();
   }

   static {
      registerConfigEntry(
         Config.Common.WorldGenerator.enableDistantGeneration.getChatCommandName(),
         new SessionConfig.Entry(
            Config.Server.enableServerGeneration::get,
            runnable -> new Closeable() {
               private final ConfigChangeListener<Boolean> distantGenerationChanges = new ConfigChangeListener<>(
                  Config.Common.WorldGenerator.enableDistantGeneration, ignored -> runnable.run()
               );
               private final ConfigChangeListener<Boolean> serverGenerationChanges = new ConfigChangeListener<>(
                  Config.Server.enableServerGeneration, ignored -> runnable.run()
               );

               @Override
               public void close() {
                  this.serverGenerationChanges.close();
                  this.distantGenerationChanges.close();
               }
            },
            (client, server) -> client && Config.Common.WorldGenerator.enableDistantGeneration.get()
         )
      );
      registerConfigEntry(Config.Server.maxGenerationRequestDistance, Math::min);
      registerConfigEntry(Config.Common.WorldGenerator.generationCenterChunkX, (x, y) -> y);
      registerConfigEntry(Config.Common.WorldGenerator.generationCenterChunkZ, (x, y) -> y);
      registerConfigEntry(Config.Common.WorldGenerator.generationMaxChunkRadius, (x, y) -> y);
      registerConfigEntry(Config.Server.generationRequestRateLimit, Math::min);
      registerConfigEntry(Config.Server.enableRealTimeUpdates, Boolean::logicalAnd);
      registerConfigEntry(Config.Server.realTimeUpdateDistanceRadiusInChunks, Math::min);
      registerConfigEntry(Config.Server.synchronizeOnLoad, Boolean::logicalAnd);
      registerConfigEntry(Config.Server.maxSyncOnLoadRequestDistance, Math::min);
      registerConfigEntry(Config.Server.syncOnLoadRateLimit, Math::min);
      registerConfigEntry(Config.Server.playerBandwidthLimit, (x, y) -> x == 0 && y == 0 ? 0 : Math.min(x > 0 ? x : 2147483647, y > 0 ? y : 2147483647));
   }

   public static class AnyChangeListener implements Closeable {
      private final ArrayList<Closeable> changeListeners = new ArrayList<>(SessionConfig.CONFIG_ENTRIES.size());

      public AnyChangeListener(Runnable runnable) {
         for (SessionConfig.Entry entry : SessionConfig.CONFIG_ENTRIES.values()) {
            this.changeListeners.add(entry.changeListenerFactory.apply(runnable));
         }
      }

      @Override
      public void close() {
         for (Closeable changeListener : this.changeListeners) {
            try {
               changeListener.close();
            } catch (Exception var4) {
            }
         }

         this.changeListeners.clear();
      }
   }

   private static class Entry {
      public final Supplier<Object> valueSupplier;
      public final Function<Runnable, Closeable> changeListenerFactory;
      public final BinaryOperator<Object> valueConstrainer;

      private <T> Entry(Supplier<Object> valueSupplier, Function<Runnable, Closeable> changeListenerFactory, BinaryOperator<T> valueConstrainer) {
         this.valueSupplier = valueSupplier;
         this.changeListenerFactory = changeListenerFactory;
         this.valueConstrainer = valueConstrainer;
      }
   }
}
