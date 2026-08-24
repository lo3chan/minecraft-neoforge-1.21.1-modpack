package fuzs.puzzleslib.impl.network.codec;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.network.v3.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v3.codec.StreamCodecRegistry;
import fuzs.puzzleslib.impl.PuzzlesLib;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class StreamCodecRegistryImpl implements StreamCodecRegistry<StreamCodecRegistryImpl> {
   public static final StreamCodecRegistry<?> INSTANCE = new StreamCodecRegistryImpl();
   private static final Map<Class<?>, StreamCodec<?, ?>> SERIALIZERS = Collections.synchronizedMap(Maps.newIdentityHashMap());
   private static final Map<Class<?>, Function<Type[], StreamCodec<?, ?>>> CONTAINER_PROVIDERS = Collections.synchronizedMap(Maps.newLinkedHashMap());

   public <B extends ByteBuf, V> StreamCodecRegistryImpl registerSerializer(Class<V> type, StreamCodec<? super B, V> streamCodec) {
      if (SERIALIZERS.put(type, streamCodec) != null) {
         PuzzlesLib.LOGGER.warn("Overriding serializer registered for type {}", type);
      }

      return this;
   }

   public <B extends ByteBuf, V> StreamCodecRegistryImpl registerContainerProvider(Class<V> type, Function<Type[], StreamCodec<? super B, ? extends V>> factory) {
      if (CONTAINER_PROVIDERS.put(type, factory) != null) {
         PuzzlesLib.LOGGER.warn("Overriding collection provider registered for type {}", type);
      }

      return this;
   }

   public static <B extends ByteBuf, V> StreamCodec<B, V> fromType(Class<V> type) {
      StreamCodec<B, V> streamCodec = (StreamCodec<B, V>)SERIALIZERS.get(type);
      if (streamCodec == null) {
         streamCodec = computeIfAbsent(type);
         SERIALIZERS.put(type, streamCodec);
      }

      return streamCodec;
   }

   private static <B extends ByteBuf, V> StreamCodec<B, V> computeIfAbsent(Class<V> clazz) {
      if (Record.class.isAssignableFrom(clazz)) {
         return RecordStreamCodec.createRecordSerializer(clazz);
      } else if (clazz.isArray()) {
         return createArraySerializer(clazz.getComponentType());
      } else if (clazz.isEnum()) {
         return createEnumSerializer(clazz);
      } else {
         throw new RuntimeException("Missing serializer for type " + clazz);
      }
   }

   public static <B extends ByteBuf, V> StreamCodec<B, V> fromGenericType(Type type) {
      if (type instanceof Class) {
         return fromType((Class<V>)type);
      } else {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         Class<?> clazz = (Class<?>)parameterizedType.getRawType();
         Type[] typeArguments = parameterizedType.getActualTypeArguments();

         for (Entry<Class<?>, Function<Type[], StreamCodec<?, ?>>> entry : CONTAINER_PROVIDERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
               return (StreamCodec<B, V>)entry.getValue().apply(typeArguments);
            }
         }

         return Collection.class.isAssignableFrom(clazz)
            ? createCollectionSerializer(typeArguments, Sets::newLinkedHashSetWithExpectedSize)
            : fromType((Class<V>)clazz);
      }
   }

   private static <K, V> StreamCodec<FriendlyByteBuf, Map<K, V>> createMapSerializer(Type[] typeArguments) {
      StreamCodec<ByteBuf, K> keyStreamCodec = (StreamCodec<ByteBuf, K>)fromType((Class<V>)typeArguments[0]);
      StreamCodec<ByteBuf, V> valueStreamCodec = fromType((Class<V>)typeArguments[1]);
      return StreamCodec.of(
         (friendlyByteBuf, map) -> friendlyByteBuf.writeMap(map, keyStreamCodec, valueStreamCodec), buf -> buf.readMap(keyStreamCodec, valueStreamCodec)
      );
   }

   private static <V, C extends Collection<V>> StreamCodec<FriendlyByteBuf, C> createCollectionSerializer(Type[] typeArguments, IntFunction<C> factory) {
      StreamCodec<ByteBuf, V> streamCodec = fromType((Class<V>)typeArguments[0]);
      return StreamCodec.of((buf, collection) -> buf.writeCollection(collection, streamCodec), buf -> buf.readCollection(factory, streamCodec));
   }

   private static <V> StreamCodec<FriendlyByteBuf, Optional<V>> createOptionalSerializer(Type[] typeArguments) {
      StreamCodec<ByteBuf, V> streamCodec = fromType((Class<V>)typeArguments[0]);
      return StreamCodec.of((buf, optional) -> buf.writeOptional(optional, streamCodec), buf -> buf.readOptional(streamCodec));
   }

   private static <V> StreamCodec<RegistryFriendlyByteBuf, Holder<V>> createHolderSerializer(Type[] typeArguments) {
      return StreamCodec.of((buf, holder) -> {
         ResourceKey<V> resourceKey = (ResourceKey<V>)holder.unwrapKey().orElseThrow();
         ExtraStreamCodecs.DIRECT_RESOURCE_KEY.encode(buf, resourceKey);
         ByteBufCodecs.holderRegistry(resourceKey.registryKey()).encode(buf, holder);
      }, buf -> {
         ResourceKey<V> resourceKey = (ResourceKey<V>)ExtraStreamCodecs.DIRECT_RESOURCE_KEY.decode(buf);
         return (Holder)ByteBufCodecs.holderRegistry(resourceKey.registryKey()).decode(buf);
      });
   }

   private static <V> StreamCodec<FriendlyByteBuf, V[]> createArraySerializer(Class<?> clazz) {
      StreamCodec<ByteBuf, V> streamCodec = fromType((Class<V>)clazz);
      return StreamCodec.of((buf, array) -> {
         int length = Array.getLength(array);
         buf.writeVarInt(length);

         for (int i = 0; i < length; i++) {
            streamCodec.encode(buf, Array.get(array, i));
         }
      }, buf -> {
         int length = buf.readVarInt();
         Object array = Array.newInstance(clazz, length);

         for (int i = 0; i < length; i++) {
            Array.set(array, i, streamCodec.decode(buf));
         }

         return (Object[])array;
      });
   }

   private static <E extends Enum<E>> StreamCodec<FriendlyByteBuf, E> createEnumSerializer(Class<Enum<?>> clazz) {
      return StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(clazz));
   }

   static {
      INSTANCE.registerSerializer(boolean.class, ByteBufCodecs.BOOL);
      INSTANCE.registerSerializer(Boolean.class, ByteBufCodecs.BOOL);
      INSTANCE.registerSerializer(int.class, ByteBufCodecs.VAR_INT);
      INSTANCE.registerSerializer(Integer.class, ByteBufCodecs.VAR_INT);
      INSTANCE.registerSerializer(long.class, ByteBufCodecs.VAR_LONG);
      INSTANCE.registerSerializer(Long.class, ByteBufCodecs.VAR_LONG);
      INSTANCE.registerSerializer(float.class, ByteBufCodecs.FLOAT);
      INSTANCE.registerSerializer(Float.class, ByteBufCodecs.FLOAT);
      INSTANCE.registerSerializer(double.class, ByteBufCodecs.DOUBLE);
      INSTANCE.registerSerializer(Double.class, ByteBufCodecs.DOUBLE);
      INSTANCE.registerSerializer(byte.class, ByteBufCodecs.BYTE);
      INSTANCE.registerSerializer(Byte.class, ByteBufCodecs.BYTE);
      INSTANCE.registerSerializer(short.class, ByteBufCodecs.SHORT);
      INSTANCE.registerSerializer(Short.class, ByteBufCodecs.SHORT);
      INSTANCE.registerSerializer(char.class, ExtraStreamCodecs.CHAR);
      INSTANCE.registerSerializer(Character.class, ExtraStreamCodecs.CHAR);
      INSTANCE.registerSerializer(String.class, ByteBufCodecs.STRING_UTF8);
      INSTANCE.registerSerializer(Date.class, ExtraStreamCodecs.DATE);
      INSTANCE.registerSerializer(Instant.class, ExtraStreamCodecs.INSTANT);
      INSTANCE.registerSerializer(UUID.class, UUIDUtil.STREAM_CODEC);
      INSTANCE.registerSerializer(Component.class, ComponentSerialization.TRUSTED_STREAM_CODEC);
      INSTANCE.registerSerializer(ItemStack.class, ItemStack.OPTIONAL_STREAM_CODEC);
      INSTANCE.registerSerializer(Ingredient.class, Ingredient.CONTENTS_STREAM_CODEC);
      INSTANCE.registerSerializer(Rotations.class, Rotations.STREAM_CODEC);
      INSTANCE.registerSerializer(BlockPos.class, BlockPos.STREAM_CODEC);
      INSTANCE.registerSerializer(Direction.class, Direction.STREAM_CODEC);
      INSTANCE.registerSerializer(CompoundTag.class, ByteBufCodecs.TRUSTED_COMPOUND_TAG);
      INSTANCE.registerSerializer(ParticleOptions.class, ParticleTypes.STREAM_CODEC);
      INSTANCE.registerSerializer(VillagerData.class, VillagerData.STREAM_CODEC);
      INSTANCE.registerSerializer(Pose.class, Pose.STREAM_CODEC);
      INSTANCE.registerSerializer(ChunkPos.class, ExtraStreamCodecs.CHUNK_POS);
      INSTANCE.registerSerializer(ResourceLocation.class, ResourceLocation.STREAM_CODEC);
      INSTANCE.registerSerializer(ResourceKey.class, ExtraStreamCodecs.DIRECT_RESOURCE_KEY);
      INSTANCE.registerSerializer(TypedDataComponent.class, TypedDataComponent.STREAM_CODEC);
      INSTANCE.registerSerializer(BlockHitResult.class, ExtraStreamCodecs.BLOCK_HIT_RESULT);
      INSTANCE.registerSerializer(BitSet.class, ExtraStreamCodecs.BIT_SET);
      INSTANCE.registerSerializer(GameProfile.class, ByteBufCodecs.GAME_PROFILE);
      INSTANCE.registerSerializer(Vec3.class, ExtraStreamCodecs.VEC3);
      INSTANCE.registerSerializer(Vector3f.class, ExtraStreamCodecs.VECTOR3F);
      INSTANCE.registerSerializer(FriendlyByteBuf.class, ExtraStreamCodecs.FRIENDLY_BYTE_BUF);
      INSTANCE.registerSerializer(RegistryFriendlyByteBuf.class, ExtraStreamCodecs.REGISTRY_FRIENDLY_BYTE_BUF);
      INSTANCE.registerSerializer(SoundEvent.class, Registries.SOUND_EVENT);
      INSTANCE.registerSerializer(Fluid.class, Registries.FLUID);
      INSTANCE.registerSerializer(MobEffect.class, Registries.MOB_EFFECT);
      INSTANCE.registerSerializer(Block.class, Registries.BLOCK);
      INSTANCE.registerSerializer(EntityType.class, Registries.ENTITY_TYPE);
      INSTANCE.registerSerializer(Item.class, Registries.ITEM);
      INSTANCE.registerSerializer(Potion.class, Registries.POTION);
      INSTANCE.registerSerializer(ParticleType.class, Registries.PARTICLE_TYPE);
      INSTANCE.registerSerializer(BlockEntityType.class, Registries.BLOCK_ENTITY_TYPE);
      INSTANCE.registerSerializer(MenuType.class, Registries.MENU);
      INSTANCE.registerSerializer(Attribute.class, Registries.ATTRIBUTE);
      INSTANCE.registerSerializer(GameEvent.class, Registries.GAME_EVENT);
      INSTANCE.registerSerializer(VillagerType.class, Registries.VILLAGER_TYPE);
      INSTANCE.registerSerializer(VillagerProfession.class, Registries.VILLAGER_PROFESSION);
      INSTANCE.registerSerializer(PoiType.class, Registries.POINT_OF_INTEREST_TYPE);
      INSTANCE.registerContainerProvider(Map.class, StreamCodecRegistryImpl::createMapSerializer);
      INSTANCE.registerContainerProvider(List.class, typeArguments -> createCollectionSerializer(typeArguments, ArrayList::new));
      INSTANCE.registerContainerProvider(Optional.class, StreamCodecRegistryImpl::createOptionalSerializer);
      INSTANCE.registerContainerProvider(Holder.class, StreamCodecRegistryImpl::createHolderSerializer);
   }
}
