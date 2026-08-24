package com.aetherteam.aether.world;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;

public class AetherLevelData extends DerivedLevelData {
   private final ServerLevel level;
   private final ServerLevelData wrapped;
   private final WrappedGameRules gameRules;
   private long dayTime;

   public AetherLevelData(ServerLevel level, WorldData worldData, ServerLevelData overworldData, long dayTime) {
      super(worldData, overworldData);
      this.level = level;
      this.wrapped = overworldData;
      this.gameRules = new WrappedGameRules(worldData.getGameRules(), ImmutableSet.of(GameRules.RULE_WEATHER_CYCLE, GameRules.RULE_DOFIRETICK));
      this.dayTime = dayTime;
   }

   public long getOverworldDayTime() {
      return this.wrapped.getDayTime();
   }

   public long getDayTime() {
      return ((AetherTimeAttachment)this.level.getData(AetherDataAttachments.AETHER_TIME)).isTimeSynced() ? this.wrapped.getDayTime() : this.dayTime;
   }

   public void setDayTime(long time) {
      if (((AetherTimeAttachment)this.level.getData(AetherDataAttachments.AETHER_TIME)).isTimeSynced()) {
         this.wrapped.setDayTime(time);
      }

      this.dayTime = time;
   }

   public void setClearWeatherTime(int time) {
      this.wrapped.setClearWeatherTime(time);
   }

   public void setRaining(boolean raining) {
      this.wrapped.setRaining(raining);
   }

   public void setRainTime(int time) {
      this.wrapped.setRainTime(time);
   }

   public void setThundering(boolean thundering) {
      this.wrapped.setThundering(thundering);
   }

   public void setThunderTime(int time) {
      this.wrapped.setThunderTime(time);
   }

   public WrappedGameRules getGameRules() {
      return this.gameRules;
   }
}
