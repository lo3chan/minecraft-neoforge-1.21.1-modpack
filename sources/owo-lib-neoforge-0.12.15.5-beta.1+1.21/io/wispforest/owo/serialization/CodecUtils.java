package io.wispforest.owo.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.wispforest.endec.Deserializer;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.Endec.Decoder;
import io.wispforest.endec.Endec.Encoder;
import io.wispforest.endec.SerializationAttribute.Instance;
import io.wispforest.endec.Serializer.Struct;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.endec.format.edm.EdmElement;
import io.wispforest.endec.format.edm.EdmEndec;
import io.wispforest.endec.format.edm.EdmMap;
import io.wispforest.endec.format.edm.EdmSerializer;
import io.wispforest.endec.format.edm.LenientEdmDeserializer;
import io.wispforest.endec.format.forwarding.ForwardingDeserializer;
import io.wispforest.endec.format.forwarding.ForwardingSerializer;
import io.wispforest.endec.format.gson.GsonDeserializer;
import io.wispforest.endec.format.gson.GsonEndec;
import io.wispforest.endec.format.gson.GsonSerializer;
import io.wispforest.owo.mixin.ForwardingDynamicOpsAccessor;
import io.wispforest.owo.mixin.RegistryOpsAccessor;
import io.wispforest.owo.serialization.endec.EitherEndec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.serialization.format.ContextHolder;
import io.wispforest.owo.serialization.format.DynamicOpsWithContext;
import io.wispforest.owo.serialization.format.edm.EdmOps;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import io.wispforest.owo.util.Scary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class CodecUtils {
   private static final Map<Class<? extends Serializer<?>>, CodecUtils.CodecAdapter<?, ?, ?>> serializerToAdapter = new HashMap<>();
   private static final Map<Class<? extends Deserializer<?>>, CodecUtils.CodecAdapter<?, ?, ?>> deserializerToAdapter = new HashMap<>();
   private static final Map<Class<? extends DynamicOps<?>>, CodecUtils.CodecAdapter<?, ?, ?>> opsToAdapter = new HashMap<>();

   public static <T> Endec<T> toEndec(Codec<T> codec) {
      return Endec.of(encoderOfCodec(codec), decoderOfCodec(codec));
   }

   private static <T> Encoder<T> encoderOfCodec(Codec<T> codec) {
      return (ctx, serializer, value) -> encodeWithCodecIntoSerializer(codec, value, serializer, ctx);
   }

   private static <T, S> void encodeWithCodecIntoSerializer(Codec<T> codec, T value, Serializer<S> serializer, SerializationContext ctx) {
      Serializer<S> unpackedSerializer = unpackSerializer((Serializer<T>)serializer);
      Pair<DynamicOps<S>, CodecUtils.CodecAdapter<S, SelfDescribedSerializer<S>, ?>> pair = getOpsAndAdapter(unpackedSerializer, ctx);
      if (pair != null && unpackedSerializer instanceof SelfDescribedSerializer<S> selfDescribedSerializer) {
         DynamicOps ops = (DynamicOps)pair.getFirst();
         CodecUtils.CodecAdapter<S, SelfDescribedSerializer<S>, ? extends SelfDescribedDeserializer<S>> adapter = (CodecUtils.CodecAdapter<S, SelfDescribedSerializer<S>, ? extends SelfDescribedDeserializer<S>>)pair.getSecond();
         encodeValue(adapter, selfDescribedSerializer, (S)codec.encodeStart(ops, value).getOrThrow());
      } else {
         EdmEndec.INSTANCE.encode(ctx, serializer, (EdmElement)codec.encodeStart(createEdmOps(ctx), value).getOrThrow());
      }
   }

   private static <T> Decoder<T> decoderOfCodec(Codec<T> codec) {
      return (ctx, deserializer) -> decodeWithCodecFromDeserializer(codec, deserializer, ctx);
   }

   private static <T, S> T decodeWithCodecFromDeserializer(Codec<T> codec, Deserializer<S> deserializer, SerializationContext ctx) {
      Deserializer<S> unpackedDeserializer = unpackDeserializer((Deserializer<T>)deserializer);
      Pair<DynamicOps<S>, CodecUtils.CodecAdapter<S, ?, SelfDescribedDeserializer<S>>> pair = getOpsAndAdapter(unpackedDeserializer, ctx);
      return (T)(pair != null && unpackedDeserializer instanceof SelfDescribedDeserializer<S> selfDescribedDeserializer
         ? codec.parse(
               (DynamicOps)pair.getFirst(),
               copyDecodedValue((CodecUtils.CodecAdapter<S, ?, SelfDescribedDeserializer<S>>)pair.getSecond(), selfDescribedDeserializer)
            )
            .getOrThrow()
         : codec.parse(createEdmOps(ctx), EdmEndec.INSTANCE.decode(ctx, deserializer)).getOrThrow());
   }

   public static <T> Endec<T> toEndec(Codec<T> codec, StreamCodec<ByteBuf, T> packetCodec) {
      Encoder<T> encoder = encoderOfCodec(codec);
      Decoder<T> decoder = decoderOfCodec(codec);
      return Endec.of(
         (ctx, serializer, value) -> {
            if (serializer instanceof ByteBufSerializer) {
               FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
               packetCodec.encode(buffer, value);
               MinecraftEndecs.PACKET_BYTE_BUF.encode(ctx, serializer, buffer);
            } else {
               encoder.encode(ctx, serializer, value);
            }
         },
         (ctx, deserializer) -> deserializer instanceof ByteBufDeserializer
            ? packetCodec.decode((ByteBuf)MinecraftEndecs.PACKET_BYTE_BUF.decode(ctx, deserializer))
            : decoder.decode(ctx, deserializer)
      );
   }

   public static <T> Endec<T> toEndecWithRegistries(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec) {
      Encoder<T> encoder = encoderOfCodec(codec);
      Decoder<T> decoder = decoderOfCodec(codec);
      return Endec.of(
         (ctx, serializer, value) -> {
            if (serializer instanceof ByteBufSerializer) {
               RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(
                  new FriendlyByteBuf(Unpooled.buffer()), ((RegistriesAttribute)ctx.requireAttributeValue(RegistriesAttribute.REGISTRIES)).registryManager()
               );
               packetCodec.encode(buffer, value);
               MinecraftEndecs.PACKET_BYTE_BUF.encode(ctx, serializer, buffer);
            } else {
               encoder.encode(ctx, serializer, value);
            }
         },
         (ctx, deserializer) -> deserializer instanceof ByteBufDeserializer
            ? packetCodec.decode(
               new RegistryFriendlyByteBuf(
                  (ByteBuf)MinecraftEndecs.PACKET_BYTE_BUF.decode(ctx, deserializer),
                  ((RegistriesAttribute)ctx.requireAttributeValue(RegistriesAttribute.REGISTRIES)).registryManager()
               )
            )
            : decoder.decode(ctx, deserializer)
      );
   }

   public static <F, S> Endec<Either<F, S>> eitherEndec(Endec<F> first, Endec<S> second) {
      return new EitherEndec(first, second, false);
   }

   public static <F, S> Endec<Either<F, S>> xorEndec(Endec<F> first, Endec<S> second) {
      return new EitherEndec(first, second, true);
   }

   public static <T> Codec<T> toCodec(Endec<T> endec, SerializationContext assumedContext) {
      return new Codec<T>() {
         public <D> DataResult<Pair<T, D>> decode(DynamicOps<D> ops, D input) {
            return CodecUtils.captureThrows(
               () -> {
                  Deserializer<D> deserializer = CodecUtils.deserializerForValue(ops, input);
                  SerializationContext context = CodecUtils.createContext(ops, assumedContext);
                  T decodedValue = (T)(deserializer != null
                     ? endec.decode(deserializer.setupContext(context), deserializer)
                     : endec.decode(context, LenientEdmDeserializer.of((EdmElement)ops.convertTo(EdmOps.withoutContext(), input))));
                  return new Pair(decodedValue, input);
               }
            );
         }

         public <D> DataResult<D> encode(T input, DynamicOps<D> ops, D prefix) {
            return CodecUtils.captureThrows(
               () -> {
                  Serializer<D> serializer = CodecUtils.serializerForOps((DynamicOps<T>)ops);
                  SerializationContext context = CodecUtils.createContext(ops, assumedContext);
                  return (T)(serializer != null
                     ? endec.encodeFully(context, () -> serializer, input)
                     : EdmOps.withoutContext().convertTo((DynamicOps<T>)ops, (EdmElement<?>)endec.encodeFully(context, EdmSerializer::of, input)));
               }
            );
         }
      };
   }

   @Deprecated
   public static <T> Codec<T> ofEndec(Endec<T> endec) {
      return toCodec(endec);
   }

   public static <T> Codec<T> toCodec(Endec<T> endec) {
      return toCodec(endec, SerializationContext.empty());
   }

   public static <T> MapCodec<T> toMapCodec(StructEndec<T> structEndec, SerializationContext assumedContext) {
      return new MapCodec<T>() {
         public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            throw new UnsupportedOperationException("MapCodec generated from StructEndec cannot report keys");
         }

         public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
            return CodecUtils.captureThrows(
               () -> {
                  Deserializer<T1> deserializer = CodecUtils.deserializerForMapLike((DynamicOps<T>)ops, (MapLike<T>)input);
                  SerializationContext context = CodecUtils.createContext(ops, assumedContext);
                  if (deserializer != null) {
                     return (T)structEndec.decode(deserializer.setupContext(context), deserializer);
                  } else {
                     HashMap<String, EdmElement<?>> map = new HashMap<>();
                     input.entries()
                        .forEach(
                           pair -> map.put(
                              (String)ops.getStringValue(pair.getFirst()).getOrThrow(s -> new IllegalStateException("Unable to parse key: " + s)),
                              (EdmElement)ops.convertTo(EdmOps.withoutContext(), pair.getSecond())
                           )
                        );
                     return (T)structEndec.decode(context, LenientEdmDeserializer.of(EdmElement.wrapMap(map)));
                  }
               }
            );
         }

         public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
            try {
               SerializationContext context = CodecUtils.createContext(ops, assumedContext);
               Pair<Serializer<T1>, Function<T1, RecordBuilder<T1>>> pair = CodecUtils.serializerForRecordBuilder((DynamicOps<T>)ops, (RecordBuilder<T>)prefix);
               if (pair != null) {
                  Serializer<T1> serializer = (Serializer<T1>)pair.getFirst();
                  return (RecordBuilder<T1>)((Function)pair.getSecond())
                     .apply(structEndec.encodeFully(serializer.setupContext(context), () -> serializer, input));
               } else {
                  Map<String, EdmElement<?>> element = (Map<String, EdmElement<?>>)((EdmElement)structEndec.encodeFully(context, EdmSerializer::of, input))
                     .cast();
                  RecordBuilder<T1> result = prefix;

                  for (Entry<String, EdmElement<?>> entry : element.entrySet()) {
                     result = result.add(entry.getKey(), EdmOps.withoutContext().convertTo(ops, entry.getValue()));
                  }

                  return result;
               }
            } catch (Exception var10) {
               return prefix.withErrorsFrom(DataResult.error(var10::getMessage, input));
            }
         }
      };
   }

   public static <T> MapCodec<T> toMapCodec(StructEndec<T> structEndec) {
      return toMapCodec(structEndec, SerializationContext.empty());
   }

   @Scary
   @Experimental
   public static <T> StructEndec<T> toStructEndec(MapCodec<T> mapCodec) {
      return new StructEndec<T>() {
         public void encodeStruct(SerializationContext ctx, Serializer<?> serializer, Struct struct, T value) {
            this.doStructEncode(ctx, serializer, struct, value);
         }

         private <S> void doStructEncode(SerializationContext ctx, Serializer<S> serializer, Struct struct, T value) {
            Serializer<S> unpackedSerializer = CodecUtils.unpackSerializer((Serializer<T>)serializer);
            Pair<DynamicOps<S>, CodecUtils.CodecAdapter<S, SelfDescribedSerializer<S>, ?>> pair = CodecUtils.getOpsAndAdapter(unpackedSerializer, ctx);
            if (pair != null && unpackedSerializer instanceof SelfDescribedSerializer<S> selfDescribedSerializer) {
               CodecUtils.encodeStruct(
                  (CodecUtils.CodecAdapter<S, SelfDescribedSerializer<S>, ?>)pair.getSecond(),
                  (DynamicOps<S>)pair.getFirst(),
                  selfDescribedSerializer,
                  struct,
                  mapCodec,
                  value
               );
            } else {
               DynamicOps<EdmElement<?>> edmOps = CodecUtils.createEdmOps(ctx);
               EdmMap edmMap = ((EdmElement)mapCodec.encode(value, edmOps, edmOps.mapBuilder()).build((EdmElement)edmOps.emptyMap()).getOrThrow()).asMap();
               if (serializer instanceof SelfDescribedSerializer) {
                  ((Map)edmMap.value()).forEach((s, element) -> struct.field(s, ctx, EdmEndec.INSTANCE, element));
               } else {
                  struct.field("element", ctx, EdmEndec.MAP, edmMap);
               }
            }
         }

         public T decodeStruct(SerializationContext ctx, Deserializer<?> deserializer, io.wispforest.endec.Deserializer.Struct struct) {
            return (T)this.doStructDecode(ctx, deserializer, struct);
         }

         private <S> T doStructDecode(SerializationContext ctx, Deserializer<S> deserializer, io.wispforest.endec.Deserializer.Struct struct) {
            Deserializer<S> unpackedDeserializer = CodecUtils.unpackDeserializer((Deserializer<T>)deserializer);
            Pair<DynamicOps<S>, CodecUtils.CodecAdapter<S, ?, SelfDescribedDeserializer<S>>> pair = CodecUtils.getOpsAndAdapter(unpackedDeserializer, ctx);
            if (pair != null && unpackedDeserializer instanceof SelfDescribedDeserializer<S> selfDescribedDeserializer) {
               return CodecUtils.decodeStruct(
                  (CodecUtils.CodecAdapter<S, ?, SelfDescribedDeserializer<S>>)pair.getSecond(),
                  (DynamicOps<S>)pair.getFirst(),
                  selfDescribedDeserializer,
                  struct,
                  mapCodec
               );
            } else {
               EdmMap edmMap = deserializer instanceof SelfDescribedDeserializer
                  ? (EdmMap)EdmEndec.MAP.decode(ctx, deserializer)
                  : (EdmMap)struct.field("element", ctx, EdmEndec.MAP);
               DynamicOps<EdmElement<?>> ops = CodecUtils.createEdmOps(ctx);
               return (T)mapCodec.decode(ops, (MapLike)ops.getMap(edmMap).getOrThrow()).getOrThrow();
            }
         }
      };
   }

   public static <B extends FriendlyByteBuf, T> StreamCodec<B, T> toPacketCodec(Endec<T> endec) {
      return new StreamCodec<B, T>() {
         public T decode(B buf) {
            SerializationContext ctx = buf instanceof RegistryFriendlyByteBuf registryByteBuf
               ? SerializationContext.attributes(new Instance[]{RegistriesAttribute.of(registryByteBuf.registryAccess())})
               : SerializationContext.empty();
            return (T)endec.decode(ctx, ByteBufDeserializer.of(buf));
         }

         public void encode(B buf, T value) {
            SerializationContext ctx = buf instanceof RegistryFriendlyByteBuf registryByteBuf
               ? SerializationContext.attributes(new Instance[]{RegistriesAttribute.of(registryByteBuf.registryAccess())})
               : SerializationContext.empty();
            endec.encode(ctx, ByteBufSerializer.of(buf), value);
         }
      };
   }

   private static SerializationContext createContext(DynamicOps<?> ops, SerializationContext assumedContext) {
      DynamicOps<?> rootOps = ops;
      SerializationContext context = ops instanceof ContextHolder holder ? holder.capturedContext().and(assumedContext) : null;

      while (rootOps instanceof DelegatingOps) {
         rootOps = ((ForwardingDynamicOpsAccessor)rootOps).owo$delegate();
         if (context == null && rootOps instanceof ContextHolder holderx) {
            context = holderx.capturedContext().and(assumedContext);
         }
      }

      if (context == null) {
         context = assumedContext;
      }

      if (ops instanceof RegistryOps<?> registryOps) {
         context = context.withAttributes(new Instance[]{RegistriesAttribute.tryFromCachedInfoGetter(((RegistryOpsAccessor)registryOps).owo$infoGetter())});
      }

      return context;
   }

   private static DynamicOps<EdmElement<?>> createEdmOps(SerializationContext ctx) {
      DynamicOps<EdmElement<?>> ops = EdmOps.withContext(ctx);
      if (ctx.hasAttribute(RegistriesAttribute.REGISTRIES)) {
         ops = RegistryOps.create(ops, ((RegistriesAttribute)ctx.getAttributeValue(RegistriesAttribute.REGISTRIES)).infoGetter());
      }

      return ops;
   }

   private static <T> DataResult<T> captureThrows(Supplier<T> action) {
      try {
         return DataResult.success(action.get());
      } catch (Exception var2) {
         return DataResult.error(var2::getMessage);
      }
   }

   @Experimental
   public static void registerCodecAdapter(CodecUtils.CodecAdapter<?, ?, ?> adapter) {
      if (serializerToAdapter.containsKey(adapter.serializerClass())) {
         throw new IllegalStateException("Serializer class " + adapter.serializerClass().getSimpleName() + " is already managed by a different codec adapter");
      } else if (deserializerToAdapter.containsKey(adapter.deserializerClass())) {
         throw new IllegalStateException(
            "Deserializer class " + adapter.deserializerClass().getSimpleName() + " is already managed by a different codec adapter"
         );
      } else if (opsToAdapter.containsKey(adapter.opsClass())) {
         throw new IllegalStateException("DynamicOps class " + adapter.opsClass().getSimpleName() + " is already managed by a different codec adapter");
      } else {
         serializerToAdapter.put(adapter.serializerClass(), adapter);
         deserializerToAdapter.put(adapter.deserializerClass(), adapter);
         opsToAdapter.put(adapter.opsClass(), adapter);
      }
   }

   private static <T> DynamicOps<T> unpackOps(DynamicOps<T> ops) {
      DynamicOps<T> rootOps = ops;

      while (rootOps instanceof DelegatingOps) {
         rootOps = ((ForwardingDynamicOpsAccessor)rootOps).owo$delegate();
      }

      return rootOps;
   }

   private static <T> Serializer<T> unpackSerializer(Serializer<T> serializer) {
      Serializer<T> rootSerializer = serializer;

      while (rootSerializer instanceof ForwardingSerializer) {
         ForwardingSerializer<T> forwardingSerializer = (ForwardingSerializer<T>)rootSerializer;
         rootSerializer = forwardingSerializer.delegate();
      }

      return rootSerializer;
   }

   private static <T> Deserializer<T> unpackDeserializer(Deserializer<T> deserializer) {
      Deserializer<T> rootDeserializer = deserializer;

      while (rootDeserializer instanceof ForwardingDeserializer) {
         ForwardingDeserializer<T> forwardingDeserializer = (ForwardingDeserializer<T>)rootDeserializer;
         rootDeserializer = forwardingDeserializer.delegate();
      }

      return rootDeserializer;
   }

   @Nullable
   private static <T, S extends SelfDescribedSerializer<T>> Pair<DynamicOps<T>, CodecUtils.CodecAdapter<T, S, ?>> getOpsAndAdapter(
      Serializer<T> serializer, SerializationContext ctx
   ) {
      CodecUtils.CodecAdapter<T, S, ? extends SelfDescribedDeserializer<T>> adapter = (CodecUtils.CodecAdapter<T, S, ? extends SelfDescribedDeserializer<T>>)serializerToAdapter.get(
         serializer.getClass()
      );
      if (adapter == null) {
         return null;
      } else {
         DynamicOps<T> ops = DynamicOpsWithContext.<T>of(ctx, adapter.getOps());
         if (ctx.hasAttribute(RegistriesAttribute.REGISTRIES)) {
            ops = RegistryOps.create(ops, ((RegistriesAttribute)ctx.getAttributeValue(RegistriesAttribute.REGISTRIES)).infoGetter());
         }

         return new Pair(ops, adapter);
      }
   }

   @Nullable
   private static <T, D extends SelfDescribedDeserializer<T>> Pair<DynamicOps<T>, CodecUtils.CodecAdapter<T, ?, D>> getOpsAndAdapter(
      Deserializer<T> deserializer, SerializationContext ctx
   ) {
      CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, D> adapter = (CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, D>)deserializerToAdapter.get(
         deserializer.getClass()
      );
      if (adapter == null) {
         return null;
      } else {
         DynamicOps<T> ops = DynamicOpsWithContext.<T>of(ctx, adapter.getOps());
         if (ctx.hasAttribute(RegistriesAttribute.REGISTRIES)) {
            ops = RegistryOps.create(ops, ((RegistriesAttribute)ctx.getAttributeValue(RegistriesAttribute.REGISTRIES)).infoGetter());
         }

         return new Pair(ops, adapter);
      }
   }

   @Nullable
   private static <T> Serializer<T> serializerForOps(DynamicOps<T> dynamicOps) {
      CodecUtils.CodecAdapter<T, SelfDescribedSerializer<T>, ? extends SelfDescribedDeserializer<T>> adapter = (CodecUtils.CodecAdapter<T, SelfDescribedSerializer<T>, ? extends SelfDescribedDeserializer<T>>)opsToAdapter.get(
         unpackOps(dynamicOps).getClass()
      );
      return adapter != null ? adapter.createSerializer() : null;
   }

   @Nullable
   private static <T> Deserializer<T> deserializerForValue(DynamicOps<T> dynamicOps, T value) {
      CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, SelfDescribedDeserializer<T>> adapter = (CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, SelfDescribedDeserializer<T>>)opsToAdapter.get(
         unpackOps(dynamicOps).getClass()
      );
      return adapter != null ? adapter.createDeserializer(value) : null;
   }

   @Nullable
   private static <T> Pair<Serializer<T>, Function<T, RecordBuilder<T>>> serializerForRecordBuilder(DynamicOps<T> dynamicOps, RecordBuilder<T> builder) {
      CodecUtils.CodecAdapter<T, SelfDescribedSerializer<T>, ? extends SelfDescribedDeserializer<T>> adapter = (CodecUtils.CodecAdapter<T, SelfDescribedSerializer<T>, ? extends SelfDescribedDeserializer<T>>)opsToAdapter.get(
         unpackOps(dynamicOps).getClass()
      );
      return adapter != null ? new Pair(adapter.createSerializer(), (Function<Object, RecordBuilder>)t -> adapter.addToBuilder((T)t, builder)) : null;
   }

   @Nullable
   private static <T> Deserializer<T> deserializerForMapLike(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
      CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, SelfDescribedDeserializer<T>> adapter = (CodecUtils.CodecAdapter<T, ? extends SelfDescribedSerializer<T>, SelfDescribedDeserializer<T>>)opsToAdapter.get(
         unpackOps(dynamicOps).getClass()
      );
      return adapter != null ? adapter.createDeserializer(adapter.unpackMapLike(mapLike)) : null;
   }

   private static <T, S extends SelfDescribedSerializer<T>> void encodeValue(CodecUtils.CodecAdapter<T, S, ?> adapter, S serializer, T value) {
      adapter.createDeserializer(value).readAny(SerializationContext.empty(), serializer);
   }

   private static <T, D extends SelfDescribedDeserializer<T>> T copyDecodedValue(CodecUtils.CodecAdapter<T, ?, D> adapter, D deserializer) {
      SelfDescribedSerializer<T> serializer = adapter.createSerializer();
      deserializer.readAny(SerializationContext.empty(), serializer);
      return (T)serializer.result();
   }

   private static <T, V, S extends SelfDescribedSerializer<T>> void encodeStruct(
      CodecUtils.CodecAdapter<T, S, ?> adapter, DynamicOps<T> ops, S serializer, Struct struct, MapCodec<V> mapCodec, V value
   ) {
      T formatValue = (T)mapCodec.encode(value, ops, ops.mapBuilder()).build(ops.emptyMap()).getOrThrow();
      adapter.encodeStruct(SerializationContext.empty(), serializer, struct, formatValue);
   }

   private static <T, V, D extends SelfDescribedDeserializer<T>> V decodeStruct(
      CodecUtils.CodecAdapter<T, ?, D> adapter, DynamicOps<T> ops, D deserializer, io.wispforest.endec.Deserializer.Struct struct, MapCodec<V> mapCodec
   ) {
      T formatValue = adapter.copyDecodedStruct(SerializationContext.empty(), deserializer, struct);
      return (V)mapCodec.decode(ops, (MapLike)ops.getMap(formatValue).getOrThrow()).getOrThrow();
   }

   static {
      registerCodecAdapter(new CodecUtils.CodecAdapter<Tag, NbtSerializer, NbtDeserializer>() {
         @Override
         public Class<? extends Serializer<Tag>> serializerClass() {
            return NbtSerializer.class;
         }

         @Override
         public Class<? extends Deserializer<Tag>> deserializerClass() {
            return NbtDeserializer.class;
         }

         @Override
         public Class<? extends DynamicOps<Tag>> opsClass() {
            return NbtOps.class;
         }

         public NbtSerializer createSerializer() {
            return NbtSerializer.of();
         }

         public NbtDeserializer createDeserializer(Tag value) {
            return NbtDeserializer.of(value);
         }

         @Override
         public DynamicOps<Tag> getOps() {
            return NbtOps.INSTANCE;
         }

         public Tag unpackMapLike(MapLike<Tag> mapLike) {
            CompoundTag compound = new CompoundTag();
            mapLike.entries().forEach(pairs -> {
               Tag key = (Tag)pairs.getFirst();
               Tag value = (Tag)pairs.getSecond();
               if (key instanceof StringTag primitive) {
                  compound.put(primitive.getAsString(), value);
               } else {
                  throw new IllegalStateException("Unable to parse key: " + key);
               }
            });
            return compound;
         }

         public RecordBuilder<Tag> addToBuilder(Tag value, RecordBuilder<Tag> builder) {
            if (!(value instanceof CompoundTag compoundTag)) {
               throw new IllegalStateException("Cannot add non-NbtCompound value into record builder: " + value);
            } else {
               RecordBuilder result = builder;

               for (String key : compoundTag.getAllKeys()) {
                  result = result.add(key, compoundTag.get(key));
               }

               return result;
            }
         }

         public void encodeStruct(SerializationContext ctx, NbtSerializer serializer, Struct struct, Tag value) {
            if (value instanceof CompoundTag compoundTag) {
               compoundTag.getAllKeys().forEach(key -> struct.field(key, ctx, NbtEndec.ELEMENT, compoundTag.get(key)));
            } else {
               throw new IllegalStateException("Cannot encode non-NbtCompound value as struct: " + value);
            }
         }

         public Tag copyDecodedStruct(SerializationContext ctx, NbtDeserializer deserializer, io.wispforest.endec.Deserializer.Struct struct) {
            return (Tag)NbtEndec.COMPOUND.decode(ctx, deserializer);
         }
      });
      registerCodecAdapter(new CodecUtils.CodecAdapter<JsonElement, GsonSerializer, GsonDeserializer>() {
         @Override
         public Class<? extends Serializer<JsonElement>> serializerClass() {
            return GsonSerializer.class;
         }

         @Override
         public Class<? extends Deserializer<JsonElement>> deserializerClass() {
            return GsonDeserializer.class;
         }

         @Override
         public Class<? extends DynamicOps<JsonElement>> opsClass() {
            return JsonOps.class;
         }

         public GsonSerializer createSerializer() {
            return GsonSerializer.of();
         }

         public GsonDeserializer createDeserializer(JsonElement value) {
            return GsonDeserializer.of(value);
         }

         @Override
         public DynamicOps<JsonElement> getOps() {
            return JsonOps.INSTANCE;
         }

         public JsonElement unpackMapLike(MapLike<JsonElement> mapLike) {
            JsonObject jsonObject = new JsonObject();
            mapLike.entries().forEach(pairs -> {
               JsonElement key = (JsonElement)pairs.getFirst();
               JsonElement value = (JsonElement)pairs.getSecond();
               if (key instanceof JsonPrimitive primitive && primitive.isString()) {
                  jsonObject.add(primitive.getAsString(), value);
               } else {
                  throw new IllegalStateException("Unable to parse key: " + key);
               }
            });
            return jsonObject;
         }

         public RecordBuilder<JsonElement> addToBuilder(JsonElement value, RecordBuilder<JsonElement> builder) {
            if (!(value instanceof JsonObject jsonObject)) {
               throw new IllegalStateException("Cannot add non-JsonObject value into record builder: " + value);
            } else {
               RecordBuilder result = builder;

               for (Entry<String, JsonElement> entry : jsonObject.asMap().entrySet()) {
                  result = result.add(entry.getKey(), entry.getValue());
               }

               return result;
            }
         }

         public void encodeStruct(SerializationContext ctx, GsonSerializer serializer, Struct struct, JsonElement value) {
            if (value instanceof JsonObject jsonObject) {
               jsonObject.asMap().forEach((key, element) -> struct.field(key, ctx, GsonEndec.INSTANCE, element));
            } else {
               throw new IllegalStateException("Cannot encode non-JsonObject value as struct: " + value);
            }
         }

         public JsonElement copyDecodedStruct(SerializationContext ctx, GsonDeserializer serializer, io.wispforest.endec.Deserializer.Struct struct) {
            return GsonEndec.INSTANCE.decode(ctx, serializer);
         }
      });
   }

   public interface CodecAdapter<T, S extends SelfDescribedSerializer<T>, D extends SelfDescribedDeserializer<T>> {
      Class<? extends Serializer<T>> serializerClass();

      Class<? extends Deserializer<T>> deserializerClass();

      Class<? extends DynamicOps<T>> opsClass();

      S createSerializer();

      D createDeserializer(T var1);

      DynamicOps<T> getOps();

      T unpackMapLike(MapLike<T> var1);

      RecordBuilder<T> addToBuilder(T var1, RecordBuilder<T> var2);

      void encodeStruct(SerializationContext var1, S var2, Struct var3, T var4);

      T copyDecodedStruct(SerializationContext var1, D var2, io.wispforest.endec.Deserializer.Struct var3);
   }
}
