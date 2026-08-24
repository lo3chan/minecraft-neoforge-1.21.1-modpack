package com.seibel.distanthorizons.core.network;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public interface INetworkObject {
   void encode(ByteBuf byteBuf);

   void decode(ByteBuf byteBuf);

   static <T extends INetworkObject> T decodeToInstance(T obj, ByteBuf inputByteBuf) {
      obj.decode(inputByteBuf);
      return obj;
   }

   @Contract("_, null -> false; _, !null -> true")
   default boolean writeOptional(ByteBuf outputByteBuf, Object value) {
      boolean isNull = value != null;
      outputByteBuf.writeBoolean(isNull);
      return isNull;
   }

   @Nullable
   default <T> T readOptional(ByteBuf inputByteBuf, Supplier<T> decoder) {
      return inputByteBuf.readBoolean() ? decoder.get() : null;
   }

   default void readOptional(ByteBuf inputByteBuf, Runnable decoder) {
      if (inputByteBuf.readBoolean()) {
         decoder.run();
      }
   }

   default void writeString(String inputString, ByteBuf outputByteBuf) {
      writeStringStatic(inputString, outputByteBuf);
   }

   static void writeStringStatic(String inputString, ByteBuf outputByteBuf) {
      byte[] bytes = inputString.getBytes(StandardCharsets.UTF_8);
      outputByteBuf.writeShort(bytes.length);
      outputByteBuf.writeBytes(bytes);
   }

   default String readString(ByteBuf inputByteBuf) {
      return readStringStatic(inputByteBuf);
   }

   static String readStringStatic(ByteBuf inputByteBuf) {
      int length = inputByteBuf.readUnsignedShort();
      return inputByteBuf.readSlice(length).toString(StandardCharsets.UTF_8);
   }

   default void writeCollection(ByteBuf outputByteBuf, Collection<?> collection) {
      outputByteBuf.writeInt(collection.size());
      this.writeFixedLengthCollection(outputByteBuf, collection);
   }

   default void writeFixedLengthCollection(ByteBuf outputByteBuf, Collection<?> collection) {
      for (Object item : collection) {
         INetworkObject.Codec codec = INetworkObject.Codec.getCodec(item.getClass());
         codec.encode.accept(item, outputByteBuf);
      }
   }

   default <TCollection extends Collection<T>, T> TCollection readCollection(ByteBuf inputByteBuf, TCollection collection, Supplier<T> innerValueConstructor) {
      int size = inputByteBuf.readInt();
      INetworkObject.Codec codec = null;

      for (int i = 0; i < size; i++) {
         T item = innerValueConstructor.get();
         if (codec == null) {
            codec = INetworkObject.Codec.getCodec(item.getClass());
         }

         item = (T)codec.decode.apply(item, inputByteBuf);
         collection.add(item);
      }

      return collection;
   }

   default <TMap extends Map<K, V>, K, V> TMap readMap(ByteBuf inputByteBuf, TMap map, Supplier<K> keySupplier, Supplier<V> valueSupplier) {
      ArrayList<Entry<K, V>> entryList = new ArrayList<>();
      this.readCollection(inputByteBuf, entryList, () -> new SimpleEntry<>(keySupplier.get(), valueSupplier.get()));

      for (Entry<K, V> entry : entryList) {
         map.put(entry.getKey(), entry.getValue());
      }

      return map;
   }

   public static class Codec {
      private static final ConcurrentMap<Class<?>, INetworkObject.Codec> CODEC_MAP = new ConcurrentHashMap<Class<?>, INetworkObject.Codec>() {
         {
            this.put(
               Integer.class, new INetworkObject.Codec((obj, outByteBuff) -> outByteBuff.writeInt((Integer)obj), (obj, inByteBuff) -> inByteBuff.readInt())
            );
            this.put(
               Boolean.class,
               new INetworkObject.Codec((obj, outByteBuff) -> outByteBuff.writeBoolean((Boolean)obj), (obj, inByteBuff) -> inByteBuff.readBoolean())
            );
            this.put(
               String.class,
               new INetworkObject.Codec(
                  (obj, outByteBuff) -> INetworkObject.writeStringStatic((String)obj, outByteBuff),
                  (obj, inByteBuff) -> INetworkObject.readStringStatic(inByteBuff)
               )
            );
            this.put(INetworkObject.class, new INetworkObject.Codec(INetworkObject::encode, INetworkObject::decodeToInstance));
            this.put(
               Entry.class,
               new INetworkObject.Codec(
                  (obj, outByteBuff) -> {
                     Entry<?, ?> entry = (Entry<?, ?>)obj;
                     INetworkObject.Codec.getCodec(entry.getKey().getClass()).encode.accept(entry.getKey(), outByteBuff);
                     INetworkObject.Codec.getCodec(entry.getValue().getClass()).encode.accept(entry.getValue(), outByteBuff);
                  },
                  (obj, inByteBuff) -> {
                     Entry<?, ?> entry = (Entry<?, ?>)obj;
                     return new SimpleEntry<>(
                        INetworkObject.Codec.getCodec(entry.getKey().getClass()).decode.apply(entry.getKey(), inByteBuff),
                        INetworkObject.Codec.getCodec(entry.getValue().getClass()).decode.apply(entry.getValue(), inByteBuff)
                     );
                  }
               )
            );
         }
      };
      public final BiConsumer<Object, ByteBuf> encode;
      public final BiFunction<Object, ByteBuf, Object> decode;

      public <T> Codec(BiConsumer<T, ByteBuf> encode, BiFunction<T, ByteBuf, T> decode) {
         this.encode = encode;
         this.decode = decode;
      }

      public static <T> INetworkObject.Codec getCodec(Class<T> clazz) {
         return CODEC_MAP.computeIfAbsent(clazz, classToAdd -> {
            for (Entry<Class<?>, INetworkObject.Codec> entry : CODEC_MAP.entrySet()) {
               if (entry.getKey().isAssignableFrom((Class<?>)classToAdd)) {
                  return entry.getValue();
               }
            }

            throw new AssertionError("Class has no compatible codec: " + classToAdd.getSimpleName());
         });
      }
   }
}
