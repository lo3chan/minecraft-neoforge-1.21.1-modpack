package net.astralya.hexalia.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record SpiritrootTetherData(String boundDimension, int boundX, int boundY, int boundZ, String mobType, String mobNbt, String mobUuid, String mobNameJson) {
   public static final SpiritrootTetherData EMPTY = new SpiritrootTetherData("", 0, 0, 0, "", "", "", "");
   public static final Codec<SpiritrootTetherData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.optionalFieldOf("Dimension", "").forGetter(SpiritrootTetherData::boundDimension),
            Codec.INT.optionalFieldOf("X", 0).forGetter(SpiritrootTetherData::boundX),
            Codec.INT.optionalFieldOf("Y", 0).forGetter(SpiritrootTetherData::boundY),
            Codec.INT.optionalFieldOf("Z", 0).forGetter(SpiritrootTetherData::boundZ),
            Codec.STRING.optionalFieldOf("MobType", "").forGetter(SpiritrootTetherData::mobType),
            Codec.STRING.optionalFieldOf("MobNbt", "").forGetter(SpiritrootTetherData::mobNbt),
            Codec.STRING.optionalFieldOf("MobUuid", "").forGetter(SpiritrootTetherData::mobUuid),
            Codec.STRING.optionalFieldOf("MobName", "").forGetter(SpiritrootTetherData::mobNameJson)
         )
         .apply(instance, SpiritrootTetherData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, SpiritrootTetherData> STREAM_CODEC = StreamCodec.of(
      (buffer, value) -> {
         ByteBufCodecs.STRING_UTF8.encode(buffer, value.boundDimension());
         ByteBufCodecs.VAR_INT.encode(buffer, value.boundX());
         ByteBufCodecs.VAR_INT.encode(buffer, value.boundY());
         ByteBufCodecs.VAR_INT.encode(buffer, value.boundZ());
         ByteBufCodecs.STRING_UTF8.encode(buffer, value.mobType());
         ByteBufCodecs.STRING_UTF8.encode(buffer, value.mobNbt());
         ByteBufCodecs.STRING_UTF8.encode(buffer, value.mobUuid());
         ByteBufCodecs.STRING_UTF8.encode(buffer, value.mobNameJson());
      },
      buffer -> new SpiritrootTetherData(
         (String)ByteBufCodecs.STRING_UTF8.decode(buffer),
         (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
         (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
         (Integer)ByteBufCodecs.VAR_INT.decode(buffer),
         (String)ByteBufCodecs.STRING_UTF8.decode(buffer),
         (String)ByteBufCodecs.STRING_UTF8.decode(buffer),
         (String)ByteBufCodecs.STRING_UTF8.decode(buffer),
         (String)ByteBufCodecs.STRING_UTF8.decode(buffer)
      )
   );

   public boolean hasMob() {
      return !this.mobType.isEmpty();
   }

   public Optional<SpiritrootTetherData.BoundLocation> bound() {
      return this.boundDimension.isEmpty()
         ? Optional.empty()
         : Optional.of(new SpiritrootTetherData.BoundLocation(this.boundDimension, new BlockPos(this.boundX, this.boundY, this.boundZ)));
   }

   public Optional<SpiritrootTetherData.CapturedMob> mob() {
      return this.mobType.isEmpty()
         ? Optional.empty()
         : Optional.of(
            new SpiritrootTetherData.CapturedMob(ResourceLocation.parse(this.mobType), this.mobNbt, parseUuid(this.mobUuid).orElse(null), this.mobNameJson)
         );
   }

   public SpiritrootTetherData withBound(ResourceKey<Level> dimension, BlockPos pos) {
      return new SpiritrootTetherData(
         dimension.location().toString(), pos.getX(), pos.getY(), pos.getZ(), this.mobType, this.mobNbt, this.mobUuid, this.mobNameJson
      );
   }

   public SpiritrootTetherData withCapturedMob(ResourceLocation typeId, CompoundTag tag, UUID uuid, String nameJson) {
      return new SpiritrootTetherData(
         this.boundDimension,
         this.boundX,
         this.boundY,
         this.boundZ,
         typeId.toString(),
         tag.toString(),
         uuid != null ? uuid.toString() : "",
         nameJson != null ? nameJson : ""
      );
   }

   public SpiritrootTetherData clearedMob() {
      return new SpiritrootTetherData(this.boundDimension, this.boundX, this.boundY, this.boundZ, "", "", "", "");
   }

   private static Optional<UUID> parseUuid(String value) {
      if (value != null && !value.isEmpty()) {
         try {
            return Optional.of(UUID.fromString(value));
         } catch (IllegalArgumentException var2) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public record BoundLocation(String dimensionId, BlockPos pos) {
      public ResourceKey<Level> dimension() {
         return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(this.dimensionId));
      }
   }

   public record CapturedMob(ResourceLocation typeId, String snbt, UUID uuid, String nameJson) {
   }
}
