package com.aetherteam.aether.attachment;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.network.packet.AetherTimeSyncPacket;
import com.aetherteam.aether.world.AetherLevelData;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class AetherTimeAttachment implements INBTSynchable {
   private static int ticksPerDayMultiplier = -1;
   private long dayTime = -1L;
   private boolean isEternalDay = true;
   private boolean shouldWait = false;
   private final Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> synchableFunctions = Map.ofEntries(
      Map.entry("setEternalDay", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setEternalDay((Boolean)object), this::isEternalDay)),
      Map.entry("setShouldWait", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setShouldWait((Boolean)object), this::getShouldWait))
   );
   public static final Codec<AetherTimeAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.LONG.fieldOf("day_time").forGetter(AetherTimeAttachment::getDayTime),
            Codec.BOOL.fieldOf("eternal_day").forGetter(AetherTimeAttachment::isEternalDay),
            Codec.BOOL.fieldOf("should_wait").forGetter(AetherTimeAttachment::getShouldWait)
         )
         .apply(instance, AetherTimeAttachment::new)
   );

   protected AetherTimeAttachment() {
   }

   private AetherTimeAttachment(long time, boolean eternalDay, boolean shouldWait) {
      this.setDayTime(time);
      this.setEternalDay(eternalDay);
      this.setShouldWait(shouldWait);
   }

   public Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions() {
      return this.synchableFunctions;
   }

   public long tickTime(Level level) {
      long dayTime = level.getDayTime();
      if (this.getDayTime() == -1L && !(Boolean)AetherConfig.SERVER.disable_eternal_day.get()) {
         dayTime = getTicksPerDay() / 4;
      }

      dayTime = this.configsChanged(dayTime);
      if (!this.isTimeSynced()) {
         if (!this.isEternalDay() && !this.getShouldWait()) {
            dayTime++;
         } else {
            if (dayTime != getTicksPerDay() / 4) {
               long tempTime = dayTime % getTicksPerDay();
               if (tempTime > getTicksPerDay() * 0.75) {
                  tempTime -= getTicksPerDay();
               }

               long target = Mth.clamp(getTicksPerDay() / 4 - tempTime, -10L, 10L);
               dayTime += target;
            }

            if (!level.isClientSide() && level.getLevelData() instanceof AetherLevelData aetherLevelData) {
               if ((Boolean)AetherConfig.SERVER.sync_aether_time.get()) {
                  if (aetherLevelData.getOverworldDayTime() == aetherLevelData.getDayTime()) {
                     this.setSynched(-1, Direction.DIMENSION, "setShouldWait", false, level);
                  }
               } else if (this.getShouldWait()) {
                  this.setSynched(-1, Direction.DIMENSION, "setShouldWait", false, level);
               }
            }
         }
      }

      this.setDayTime(dayTime);
      return dayTime;
   }

   private long configsChanged(long time) {
      int multiplier = configMultiplier();
      if (ticksPerDayMultiplier != multiplier) {
         ticksPerDayMultiplier = multiplier;
      }

      return time;
   }

   public void updateEternalDay(Level level) {
      this.setSynched(-1, Direction.DIMENSION, "setEternalDay", this.isEternalDay, level);
   }

   public void updateEternalDay(ServerPlayer player) {
      this.setSynched(player.getId(), Direction.PLAYER, "setEternalDay", this.isEternalDay, player);
   }

   public void setDayTime(long time) {
      this.dayTime = time;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   public void setEternalDay(boolean isEternalDay) {
      this.isEternalDay = isEternalDay;
   }

   public boolean isEternalDay() {
      return this.isEternalDay;
   }

   public void setShouldWait(boolean shouldWait) {
      this.shouldWait = shouldWait;
   }

   public boolean getShouldWait() {
      return this.shouldWait;
   }

   public boolean isTimeSynced() {
      return (Boolean)AetherConfig.SERVER.sync_aether_time.get() && !this.isEternalDay() && !this.getShouldWait();
   }

   public SyncPacket getSyncPacket(int entityID, String key, Type type, Object value) {
      return new AetherTimeSyncPacket(key, type, value);
   }

   public static int getTicksPerDayMultiplier() {
      if (ticksPerDayMultiplier < 0) {
         ticksPerDayMultiplier = !AetherConfig.SERVER.normal_length_aether_time.get() && !AetherConfig.SERVER.sync_aether_time.get() ? 3 : 1;
      }

      return ticksPerDayMultiplier;
   }

   public static int getTicksPerDay() {
      return 24000 * getTicksPerDayMultiplier();
   }

   public static int configMultiplier() {
      return !AetherConfig.SERVER.normal_length_aether_time.get() && !AetherConfig.SERVER.sync_aether_time.get() ? 3 : 1;
   }
}
