package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.packet.PhoenixArrowSyncPacket;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Type;
import com.aetherteam.nitrogen.network.packet.SyncPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Triple;

public class PhoenixArrowAttachment implements INBTSynchable {
   private boolean isPhoenixArrow;
   private int fireTime;
   private final Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> synchableFunctions = Map.ofEntries(
      Map.entry("setPhoenixArrow", Triple.of(Type.BOOLEAN, (Consumer<Object>)object -> this.setPhoenixArrow((Boolean)object), this::isPhoenixArrow))
   );
   public static final Codec<PhoenixArrowAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.BOOL.fieldOf("is_phoenix_arrow").forGetter(PhoenixArrowAttachment::isPhoenixArrow),
            Codec.INT.fieldOf("fire_time").forGetter(PhoenixArrowAttachment::getFireTime)
         )
         .apply(instance, PhoenixArrowAttachment::new)
   );

   public PhoenixArrowAttachment() {
      this(false, 0);
   }

   private PhoenixArrowAttachment(boolean isPhoenixArrow, int fireTime) {
      this.setPhoenixArrow(isPhoenixArrow);
      this.setFireTime(fireTime);
   }

   public Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions() {
      return this.synchableFunctions;
   }

   public void setPhoenixArrow(boolean isPhoenixArrow) {
      this.isPhoenixArrow = isPhoenixArrow;
   }

   public boolean isPhoenixArrow() {
      return this.isPhoenixArrow;
   }

   public void setFireTime(int time) {
      this.fireTime = time;
   }

   public int getFireTime() {
      return this.fireTime;
   }

   public SyncPacket getSyncPacket(int entityID, String key, Type type, Object value) {
      return new PhoenixArrowSyncPacket(entityID, key, type, value);
   }
}
