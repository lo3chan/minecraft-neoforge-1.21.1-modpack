package io.wispforest.owo.serialization.format.edm;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.format.edm.EdmElement;
import io.wispforest.owo.serialization.format.ContextHolder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdmOps implements DynamicOps<EdmElement<?>>, ContextHolder {
   private static final EdmOps NO_CONTEXT = new EdmOps(SerializationContext.empty());
   private final SerializationContext capturedContext;

   private EdmOps(SerializationContext capturedContext) {
      this.capturedContext = capturedContext;
   }

   public static EdmOps withContext(SerializationContext context) {
      return new EdmOps(context);
   }

   public static EdmOps withoutContext() {
      return NO_CONTEXT;
   }

   @Override
   public SerializationContext capturedContext() {
      return this.capturedContext;
   }

   public EdmElement<?> empty() {
      return EdmElement.EMPTY;
   }

   public EdmElement<?> createNumeric(Number number) {
      return EdmElement.wrapDouble(number.doubleValue());
   }

   public EdmElement<?> createByte(byte b) {
      return EdmElement.wrapByte(b);
   }

   public EdmElement<?> createShort(short s) {
      return EdmElement.wrapShort(s);
   }

   public EdmElement<?> createInt(int i) {
      return EdmElement.wrapInt(i);
   }

   public EdmElement<?> createLong(long l) {
      return EdmElement.wrapLong(l);
   }

   public EdmElement<?> createFloat(float f) {
      return EdmElement.wrapFloat(f);
   }

   public EdmElement<?> createDouble(double d) {
      return EdmElement.wrapDouble(d);
   }

   public EdmElement<?> createBoolean(boolean bl) {
      return EdmElement.wrapBoolean(bl);
   }

   public EdmElement<?> createString(String value) {
      return EdmElement.wrapString(value);
   }

   public EdmElement<?> createByteList(ByteBuffer input) {
      return EdmElement.wrapBytes(DataFixUtils.toArray(input));
   }

   public EdmElement<?> createList(Stream<EdmElement<?>> input) {
      return EdmElement.wrapSequence(input.toList());
   }

   public DataResult<EdmElement<?>> mergeToList(EdmElement<?> list, EdmElement<?> value) {
      if (list == this.empty()) {
         return DataResult.success(EdmElement.wrapSequence(List.of(value)));
      } else if (list.value() instanceof List<?> properList) {
         ArrayList<EdmElement<?>> newList = new ArrayList<>((Collection<? extends EdmElement<?>>)properList);
         newList.add(value);
         return DataResult.success(EdmElement.wrapSequence(newList));
      } else {
         return DataResult.error(() -> "Not a sequence: " + list);
      }
   }

   public EdmElement<?> createMap(Stream<Pair<EdmElement<?>, EdmElement<?>>> map) {
      return EdmElement.wrapMap(map.collect(Collectors.toMap(pair -> (String)((EdmElement)pair.getFirst()).cast(), Pair::getSecond)));
   }

   public DataResult<EdmElement<?>> mergeToMap(EdmElement<?> map, EdmElement<?> key, EdmElement<?> value) {
      if (!(key.value() instanceof String)) {
         return DataResult.error(() -> "Key is not a string: " + key);
      } else if (map == this.empty()) {
         return DataResult.success(EdmElement.wrapMap(Map.of((String)key.cast(), value)));
      } else if (map.value() instanceof Map<?, ?> properMap) {
         HashMap<String, EdmElement<?>> newMap = new HashMap<>((Map<? extends String, ? extends EdmElement<?>>)properMap);
         newMap.put((String)key.cast(), value);
         return DataResult.success(EdmElement.wrapMap(newMap));
      } else {
         return DataResult.error(() -> "Not a map: " + map);
      }
   }

   public DataResult<Number> getNumberValue(EdmElement<?> input) {
      return input.value() instanceof Number number ? DataResult.success(number) : DataResult.error(() -> "Not a number: " + input);
   }

   public DataResult<Boolean> getBooleanValue(EdmElement<?> input) {
      if (input.value() instanceof Boolean bl) {
         return DataResult.success(bl);
      } else {
         return input.value() instanceof Byte b ? DataResult.success(b == 1) : DataResult.error(() -> "Not a boolean: " + input);
      }
   }

   public DataResult<String> getStringValue(EdmElement<?> input) {
      return input.value() instanceof String string ? DataResult.success(string) : DataResult.error(() -> "Not a string: " + input);
   }

   public DataResult<ByteBuffer> getByteBuffer(EdmElement<?> input) {
      return input.value() instanceof byte[] bytes ? DataResult.success(ByteBuffer.wrap(bytes)) : DataResult.error(() -> "Not bytes: " + input);
   }

   public DataResult<Stream<EdmElement<?>>> getStream(EdmElement<?> input) {
      if (input == this.empty()) {
         return DataResult.success(Stream.of());
      } else {
         return input.value() instanceof List<?> list
            ? DataResult.success(list.stream().map(o -> (EdmElement)o))
            : DataResult.error(() -> "Not a sequence: " + input);
      }
   }

   public DataResult<Stream<Pair<EdmElement<?>, EdmElement<?>>>> getMapValues(EdmElement<?> input) {
      if (input == this.empty()) {
         return DataResult.success(Stream.of());
      } else {
         return input.value() instanceof Map<?, ?> map
            ? DataResult.success(map.entrySet().stream().map(entry -> new Pair(EdmElement.wrapString((String)entry.getKey()), entry.getValue())))
            : DataResult.error(() -> "Not a map: " + input);
      }
   }

   public <U> U convertTo(DynamicOps<U> outOps, EdmElement<?> input) {
      if (input == this.empty()) {
         return (U)outOps.empty();
      } else {
         return (U)(switch (input.type()) {
            case BYTE -> (Object)outOps.createByte((Byte)input.cast());
            case SHORT -> (Object)outOps.createShort((Short)input.cast());
            case INT -> (Object)outOps.createInt((Integer)input.cast());
            case LONG -> (Object)outOps.createLong((Long)input.cast());
            case FLOAT -> (Object)outOps.createFloat((Float)input.cast());
            case DOUBLE -> (Object)outOps.createDouble((Double)input.cast());
            case BOOLEAN -> (Object)outOps.createBoolean((Boolean)input.cast());
            case STRING -> (Object)outOps.createString((String)input.cast());
            case BYTES -> (Object)outOps.createByteList(ByteBuffer.wrap((byte[])input.cast()));
            case OPTIONAL -> (Object)((Optional)input.cast()).<Object>map(element -> this.convertTo(outOps, (EdmElement<?>)element)).orElse(outOps.empty());
            case SEQUENCE -> (Object)outOps.createList(((List)input.cast()).stream().map(element -> this.convertTo(outOps, element)));
            case MAP -> (Object)outOps.createMap(
               ((Map)input.cast())
                  .entrySet()
                  .stream()
                  .map(entry -> new Pair(outOps.createString((String)entry.getKey()), this.convertTo(outOps, (EdmElement<?>)entry.getValue())))
            );
            default -> throw new MatchException(null, null);
         });
      }
   }

   public EdmElement<?> remove(EdmElement<?> input, String key) {
      if (input.value() instanceof Map<?, ?> map) {
         HashMap<String, EdmElement<?>> newMap = new HashMap<>((Map<? extends String, ? extends EdmElement<?>>)map);
         newMap.remove(key);
         return EdmElement.wrapMap(newMap);
      } else {
         return input;
      }
   }
}
