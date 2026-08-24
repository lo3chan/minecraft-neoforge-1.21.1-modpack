package fuzs.puzzleslib.api.network.v3.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.time.Instant;
import java.util.BitSet;
import java.util.Date;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ExtraStreamCodecs {
   public static final StreamCodec<ByteBuf, Character> CHAR = StreamCodec.of((buf, character) -> buf.writeChar(character), ByteBuf::readChar);
   public static final StreamCodec<FriendlyByteBuf, Date> DATE = StreamCodec.of(FriendlyByteBuf::writeDate, FriendlyByteBuf::readDate);
   public static final StreamCodec<FriendlyByteBuf, Instant> INSTANT = StreamCodec.of(FriendlyByteBuf::writeInstant, FriendlyByteBuf::readInstant);
   public static final StreamCodec<FriendlyByteBuf, ChunkPos> CHUNK_POS = StreamCodec.of(FriendlyByteBuf::writeChunkPos, FriendlyByteBuf::readChunkPos);
   public static final StreamCodec<FriendlyByteBuf, BlockHitResult> BLOCK_HIT_RESULT = StreamCodec.of(
      FriendlyByteBuf::writeBlockHitResult, FriendlyByteBuf::readBlockHitResult
   );
   public static final StreamCodec<FriendlyByteBuf, BitSet> BIT_SET = StreamCodec.of(FriendlyByteBuf::writeBitSet, FriendlyByteBuf::readBitSet);
   public static final StreamCodec<ByteBuf, ResourceKey<?>> DIRECT_RESOURCE_KEY = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC,
      ResourceKey::registry,
      ResourceLocation.STREAM_CODEC,
      ResourceKey::location,
      (registry, location) -> ResourceKey.create(ResourceKey.createRegistryKey(registry), location)
   );
   public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(
      ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new
   );
   public static final StreamCodec<ByteBuf, Vector3f> VECTOR3F = StreamCodec.composite(
      ByteBufCodecs.FLOAT, Vector3f::x, ByteBufCodecs.FLOAT, Vector3f::y, ByteBufCodecs.FLOAT, Vector3f::z, Vector3f::new
   );
   public static final StreamCodec<FriendlyByteBuf, FriendlyByteBuf> FRIENDLY_BYTE_BUF = new StreamCodec<FriendlyByteBuf, FriendlyByteBuf>() {
      public FriendlyByteBuf decode(FriendlyByteBuf buf) {
         FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
         newBuf.writeBytes(buf.copy());
         buf.skipBytes(buf.readableBytes());
         return newBuf;
      }

      public void encode(FriendlyByteBuf buf, FriendlyByteBuf toEncode) {
         buf.writeBytes(toEncode.copy());
         toEncode.release();
      }
   };
   public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> REGISTRY_FRIENDLY_BYTE_BUF = new StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf>() {
      public RegistryFriendlyByteBuf decode(RegistryFriendlyByteBuf buf) {
         RegistryFriendlyByteBuf newBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), buf.registryAccess());
         newBuf.writeBytes(buf.copy());
         buf.skipBytes(buf.readableBytes());
         return newBuf;
      }

      public void encode(RegistryFriendlyByteBuf buf, RegistryFriendlyByteBuf toEncode) {
         buf.writeBytes(toEncode.copy());
         toEncode.release();
      }
   };

   private ExtraStreamCodecs() {
   }

   public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Class<E> clazz) {
      return fromEnum(clazz, Enum::ordinal);
   }

   public static <E extends Enum<E>> StreamCodec<ByteBuf, E> fromEnum(Class<E> clazz, ToIntFunction<E> keyExtractor) {
      IntFunction<E> idMapper = ByIdMap.continuous(keyExtractor, clazz.getEnumConstants(), OutOfBoundsStrategy.ZERO);
      return ByteBufCodecs.idMapper(idMapper, keyExtractor);
   }

   public static Component readComponent(FriendlyByteBuf buf) {
      return (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf);
   }

   public static void writeComponent(FriendlyByteBuf buf, Component component) {
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf, component);
   }

   public static ItemStack readItem(FriendlyByteBuf buf) {
      return (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf);
   }

   public static void writeItem(FriendlyByteBuf buf, ItemStack itemStack) {
      ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf, itemStack);
   }
}
