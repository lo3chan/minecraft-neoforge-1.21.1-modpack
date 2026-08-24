package fuzs.puzzleslib.impl.network.codec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

public final class RecordStreamCodec<R extends Record> implements StreamCodec<FriendlyByteBuf, R> {
   private final Class<R> recordType;
   private final List<RecordStreamCodec.RecordAccess<FriendlyByteBuf, ?, R>> recordAccess;
   private final Function<Object[], R> instanceFactory;

   private RecordStreamCodec(
      Class<R> recordType, List<RecordStreamCodec.RecordAccess<FriendlyByteBuf, ?, R>> recordAccess, Function<Object[], R> instanceFactory
   ) {
      this.recordType = recordType;
      this.instanceFactory = instanceFactory;
      this.recordAccess = recordAccess;
   }

   public static <R extends Record> StreamCodec<FriendlyByteBuf, R> createRecordSerializer(Class<R> clazz) {
      if (!clazz.isRecord()) {
         throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
      } else {
         Builder<RecordStreamCodec.RecordAccess<FriendlyByteBuf, ?, R>> builder = ImmutableList.builder();

         for (RecordComponent component : clazz.getRecordComponents()) {
            builder.add(RecordStreamCodec.RecordAccess.fromRecordComponent(component));
         }

         List<RecordStreamCodec.RecordAccess<FriendlyByteBuf, ?, R>> recordAccess = builder.build();
         Class<?>[] constructorArguments = recordAccess.stream().map(RecordStreamCodec.RecordAccess::type).toArray(Class[]::new);

         try {
            Constructor<R> constructor = clazz.getConstructor(constructorArguments);
            return new RecordStreamCodec<>(clazz, recordAccess, args -> {
               try {
                  return constructor.newInstance(args);
               } catch (ReflectiveOperationException var4) {
                  throw new RuntimeException("Unable to create new record instance of type %s".formatted(clazz), var4);
               }
            });
         } catch (NoSuchMethodException var6) {
            throw new RuntimeException(
               "Unable to find constructor with arguments %s for record type %s".formatted(Arrays.toString((Object[])constructorArguments), clazz), var6
            );
         }
      }
   }

   public Class<R> getRecordType() {
      return this.recordType;
   }

   public void encode(FriendlyByteBuf buf, R instance) {
      for (RecordStreamCodec.RecordAccess<FriendlyByteBuf, ?, R> access : this.recordAccess) {
         access.encode(buf, instance);
      }
   }

   public R decode(FriendlyByteBuf buf) {
      Object[] values = this.recordAccess.stream().map(recordAccess -> (R)recordAccess.decode((ByteBuf)buf)).toArray();
      return this.instanceFactory.apply(values);
   }

   private record RecordAccess<B extends ByteBuf, V, R extends Record>(Class<? extends V> type, Function<R, V> fieldAccess, StreamCodec<B, V> streamCodec)
      implements StreamEncoder<B, R>,
      StreamDecoder<B, V> {
      static <B extends ByteBuf, V, R extends Record> RecordStreamCodec.RecordAccess<B, V, R> fromRecordComponent(RecordComponent component) {
         Lookup lookup = MethodHandles.publicLookup();
         Class<V> type = (Class<V>)component.getType();
         Function<R, V> fieldAccess = instance -> {
            try {
               return (V)(Object)lookup.unreflect(component.getAccessor()).invoke((Record)instance);
            } catch (Throwable var5) {
               throw new RuntimeException(
                  "Unable to get record value of type %s from record component from record type %s".formatted(type, component.getDeclaringRecord()), var5
               );
            }
         };
         StreamCodec<B, V> streamCodec = StreamCodecRegistryImpl.fromGenericType(component.getGenericType());
         return new RecordStreamCodec.RecordAccess<>(type, fieldAccess, streamCodec);
      }

      public void encode(B buf, R instance) {
         this.streamCodec.encode(buf, this.fieldAccess.apply(instance));
      }

      public V decode(B buf) {
         return (V)this.streamCodec.decode(buf);
      }
   }
}
