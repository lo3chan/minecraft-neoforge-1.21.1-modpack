package net.bettercombat.network;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.config.ServerConfig;
import net.bettercombat.logic.AnimatedHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class Packets {
   public record Ack(String code) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "ack");
      public static final Type<Packets.Ack> PACKET_ID = new Type(ID);
      public static final StreamCodec<FriendlyByteBuf, Packets.Ack> CODEC = StreamCodec.ofMember(Packets.Ack::write, Packets.Ack::read);

      public void write(FriendlyByteBuf buffer) {
         buffer.writeUtf(this.code);
      }

      public static Packets.Ack read(FriendlyByteBuf buffer) {
         String code = buffer.readUtf();
         return new Packets.Ack(code);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record AttackAnimation(
      int playerId,
      AnimatedHand animatedHand,
      String animationName,
      float length,
      float upswing,
      float weaponRange,
      int upswingTicks,
      Packets.SwingParticles particles
   ) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "attack_animation");
      public static final Type<Packets.AttackAnimation> PACKET_ID = new Type(ID);
      public static final StreamCodec<RegistryFriendlyByteBuf, Packets.AttackAnimation> CODEC = StreamCodec.ofMember(
         Packets.AttackAnimation::write, Packets.AttackAnimation::read
      );
      private static final Gson gson = new Gson();
      public static String StopSymbol = "!STOP!";

      public static Packets.AttackAnimation stop(int playerId, int length) {
         return new Packets.AttackAnimation(playerId, AnimatedHand.MAIN_HAND, StopSymbol, length, 0.0F, 0.0F, 0, Packets.SwingParticles.EMPTY);
      }

      public void write(FriendlyByteBuf buffer) {
         buffer.writeInt(this.playerId);
         buffer.writeInt(this.animatedHand.ordinal());
         buffer.writeUtf(this.animationName);
         buffer.writeFloat(this.length);
         buffer.writeFloat(this.upswing);
         buffer.writeFloat(this.weaponRange);
         buffer.writeInt(this.upswingTicks);
         buffer.writeUtf(gson.toJson(this.particles));
      }

      public static Packets.AttackAnimation read(FriendlyByteBuf buffer) {
         int playerId = buffer.readInt();
         AnimatedHand animatedHand = AnimatedHand.values()[buffer.readInt()];
         String animationName = buffer.readUtf();
         float length = buffer.readFloat();
         float upswing = buffer.readFloat();
         float weaponRange = buffer.readFloat();
         int upswingTicks = buffer.readInt();
         String json = buffer.readUtf();
         Packets.SwingParticles particles = (Packets.SwingParticles)gson.fromJson(json, Packets.SwingParticles.class);
         return new Packets.AttackAnimation(playerId, animatedHand, animationName, length, upswing, weaponRange, upswingTicks, particles);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record AttackSound(double x, double y, double z, String soundId, float volume, float pitch, long seed) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "attack_sound");
      public static final Type<Packets.AttackSound> PACKET_ID = new Type(ID);
      public static final StreamCodec<RegistryFriendlyByteBuf, Packets.AttackSound> CODEC = StreamCodec.ofMember(
         Packets.AttackSound::write, Packets.AttackSound::read
      );

      public void write(FriendlyByteBuf buffer) {
         buffer.writeDouble(this.x);
         buffer.writeDouble(this.y);
         buffer.writeDouble(this.z);
         buffer.writeUtf(this.soundId);
         buffer.writeFloat(this.volume);
         buffer.writeFloat(this.pitch);
         buffer.writeLong(this.seed);
      }

      public static Packets.AttackSound read(FriendlyByteBuf buffer) {
         double x = buffer.readDouble();
         double y = buffer.readDouble();
         double z = buffer.readDouble();
         String soundId = buffer.readUtf();
         float volume = buffer.readFloat();
         float pitch = buffer.readFloat();
         long seed = buffer.readLong();
         return new Packets.AttackSound(x, y, z, soundId, volume, pitch, seed);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, int cursorTarget, int[] entityIds) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "c2s_request_attack");
      public static final Type<Packets.C2S_AttackRequest> PACKET_ID = new Type(ID);
      public static final StreamCodec<RegistryFriendlyByteBuf, Packets.C2S_AttackRequest> CODEC = StreamCodec.ofMember(
         Packets.C2S_AttackRequest::write, Packets.C2S_AttackRequest::read
      );
      public static boolean UseVanillaPacket = true;

      public C2S_AttackRequest(int comboCount, boolean isSneaking, int selectedSlot, @Nullable Entity cursorTarget, List<Entity> entities) {
         this(comboCount, isSneaking, selectedSlot, convertEntity(cursorTarget), convertEntityList(entities));
      }

      private static int[] convertEntityList(List<Entity> entities) {
         int[] ids = new int[entities.size()];

         for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            ids[i] = entity.getId();
         }

         return ids;
      }

      private static int convertEntity(@Nullable Entity entity) {
         return entity == null ? -1 : entity.getId();
      }

      public void write(FriendlyByteBuf buffer) {
         buffer.writeInt(this.comboCount);
         buffer.writeBoolean(this.isSneaking);
         buffer.writeInt(this.selectedSlot);
         buffer.writeInt(this.cursorTarget);
         buffer.writeVarIntArray(this.entityIds);
      }

      public static Packets.C2S_AttackRequest read(FriendlyByteBuf buffer) {
         int comboCount = buffer.readInt();
         boolean isSneaking = buffer.readBoolean();
         int selectedSlot = buffer.readInt();
         int cursorTarget = buffer.readInt();
         int[] ids = buffer.readVarIntArray();
         return new Packets.C2S_AttackRequest(comboCount, isSneaking, selectedSlot, cursorTarget, ids);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record C2S_BlockHit(BlockPos pos) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "block_hit");
      public static final Type<Packets.C2S_BlockHit> PACKET_ID = new Type(ID);
      public static final StreamCodec<FriendlyByteBuf, Packets.C2S_BlockHit> CODEC = BlockPos.STREAM_CODEC
         .map(Packets.C2S_BlockHit::new, Packets.C2S_BlockHit::pos)
         .cast();

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record ConfigSync(String json) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "config_sync");
      public static final Type<Packets.ConfigSync> PACKET_ID = new Type(ID);
      public static final StreamCodec<FriendlyByteBuf, Packets.ConfigSync> CODEC = StreamCodec.ofMember(Packets.ConfigSync::write, Packets.ConfigSync::read);
      private static final Gson gson = new Gson();

      public static String serialize(ServerConfig config) {
         return gson.toJson(config);
      }

      public void write(FriendlyByteBuf buffer) {
         buffer.writeUtf(this.json);
      }

      public static Packets.ConfigSync read(FriendlyByteBuf buffer) {
         String json = buffer.readUtf();
         return new Packets.ConfigSync(json);
      }

      public ServerConfig deserialized() {
         return (ServerConfig)gson.fromJson(this.json, ServerConfig.class);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }

   public record SwingParticles(List<ParticlePlacement> particles, TrailAppearance appearance) {
      public static final Packets.SwingParticles EMPTY = new Packets.SwingParticles(List.of(), new TrailAppearance());
   }

   public record WeaponRegistrySync(boolean compressed, List<String> chunks) implements CustomPacketPayload {
      public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("bettercombat", "weapon_registry");
      public static final Type<Packets.WeaponRegistrySync> PACKET_ID = new Type(ID);
      public static final StreamCodec<FriendlyByteBuf, Packets.WeaponRegistrySync> CODEC = StreamCodec.ofMember(
         Packets.WeaponRegistrySync::write, Packets.WeaponRegistrySync::read
      );

      public void write(FriendlyByteBuf buffer) {
         buffer.writeBoolean(this.compressed);
         buffer.writeInt(this.chunks.size());

         for (String chunk : this.chunks) {
            buffer.writeUtf(chunk);
         }
      }

      public static Packets.WeaponRegistrySync read(FriendlyByteBuf buffer) {
         boolean compressed = buffer.readBoolean();
         int chunkCount = buffer.readInt();
         ArrayList<String> chunks = new ArrayList<>();

         for (int i = 0; i < chunkCount; i++) {
            chunks.add(buffer.readUtf());
         }

         return new Packets.WeaponRegistrySync(compressed, chunks);
      }

      public Type<? extends CustomPacketPayload> type() {
         return PACKET_ID;
      }
   }
}
