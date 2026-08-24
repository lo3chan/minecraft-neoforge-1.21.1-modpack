package io.wispforest.owo.serialization.format.nbt;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import io.wispforest.endec.temp.OptionalFieldFlag;
import io.wispforest.endec.util.RecursiveDeserializer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class NbtDeserializer extends RecursiveDeserializer<Tag> implements SelfDescribedDeserializer<Tag> {
   private final Set<IdentityHolder<Tag>> encodedOptionals = Collections.newSetFromMap(new WeakHashMap<>());

   protected NbtDeserializer(Tag element) {
      super(element);
   }

   public static NbtDeserializer of(Tag element) {
      return new NbtDeserializer(element);
   }

   private <N extends Tag> N getAs(Tag element, Class<N> clazz) {
      if (clazz.isInstance(element)) {
         return clazz.cast(element);
      } else {
         throw new IllegalStateException("Expected a " + clazz.getSimpleName() + ", found a " + element.getClass().getSimpleName());
      }
   }

   public byte readByte(SerializationContext ctx) {
      return ((ByteTag)this.getAs((Tag)this.getValue(), ByteTag.class)).getAsByte();
   }

   public short readShort(SerializationContext ctx) {
      return ((ShortTag)this.getAs((Tag)this.getValue(), ShortTag.class)).getAsShort();
   }

   public int readInt(SerializationContext ctx) {
      return ((IntTag)this.getAs((Tag)this.getValue(), IntTag.class)).getAsInt();
   }

   public long readLong(SerializationContext ctx) {
      return ((LongTag)this.getAs((Tag)this.getValue(), LongTag.class)).getAsLong();
   }

   public float readFloat(SerializationContext ctx) {
      return ((FloatTag)this.getAs((Tag)this.getValue(), FloatTag.class)).getAsFloat();
   }

   public double readDouble(SerializationContext ctx) {
      return ((DoubleTag)this.getAs((Tag)this.getValue(), DoubleTag.class)).getAsDouble();
   }

   public int readVarInt(SerializationContext ctx) {
      return ((NumericTag)this.getAs((Tag)this.getValue(), NumericTag.class)).getAsInt();
   }

   public long readVarLong(SerializationContext ctx) {
      return ((NumericTag)this.getAs((Tag)this.getValue(), NumericTag.class)).getAsLong();
   }

   public boolean readBoolean(SerializationContext ctx) {
      return ((ByteTag)this.getAs((Tag)this.getValue(), ByteTag.class)).getAsByte() != 0;
   }

   public String readString(SerializationContext ctx) {
      return ((StringTag)this.getAs((Tag)this.getValue(), StringTag.class)).getAsString();
   }

   public byte[] readBytes(SerializationContext ctx) {
      return ((ByteArrayTag)this.getAs((Tag)this.getValue(), ByteArrayTag.class)).getAsByteArray();
   }

   public <V> Optional<V> readOptional(SerializationContext ctx, Endec<V> endec) {
      Tag value = (Tag)this.getValue();
      if (this.encodedOptionals.contains(new IdentityHolder<>(value))) {
         return Optional.of((V)endec.decode(ctx, this));
      } else {
         io.wispforest.endec.Deserializer.Struct struct = this.struct();
         return struct.field("present", ctx, Endec.BOOLEAN) ? Optional.of((V)struct.field("value", ctx, endec)) : Optional.empty();
      }
   }

   public <E> io.wispforest.endec.Deserializer.Sequence<E> sequence(SerializationContext ctx, Endec<E> elementEndec) {
      return new NbtDeserializer.Sequence<>(ctx, elementEndec, this.getAs((Tag)this.getValue(), CollectionTag.class));
   }

   public <V> io.wispforest.endec.Deserializer.Map<V> map(SerializationContext ctx, Endec<V> valueEndec) {
      return new NbtDeserializer.Map<>(ctx, valueEndec, this.getAs((Tag)this.getValue(), CompoundTag.class));
   }

   public io.wispforest.endec.Deserializer.Struct struct() {
      return new NbtDeserializer.Struct(this.getAs((Tag)this.getValue(), CompoundTag.class));
   }

   public <S> void readAny(SerializationContext ctx, Serializer<S> visitor) {
      this.decodeValue(ctx, visitor, (Tag)this.getValue());
   }

   private <S> void decodeValue(SerializationContext ctx, Serializer<S> visitor, Tag value) {
      switch (value.getId()) {
         case 1:
            visitor.writeByte(ctx, ((ByteTag)value).getAsByte());
            break;
         case 2:
            visitor.writeShort(ctx, ((ShortTag)value).getAsShort());
            break;
         case 3:
            visitor.writeInt(ctx, ((IntTag)value).getAsInt());
            break;
         case 4:
            visitor.writeLong(ctx, ((LongTag)value).getAsLong());
            break;
         case 5:
            visitor.writeFloat(ctx, ((FloatTag)value).getAsFloat());
            break;
         case 6:
            visitor.writeDouble(ctx, ((DoubleTag)value).getAsDouble());
            break;
         case 7:
            visitor.writeBytes(ctx, ((ByteArrayTag)value).getAsByteArray());
            break;
         case 8:
            visitor.writeString(ctx, value.getAsString());
            break;
         case 9:
         case 11:
         case 12:
            CollectionTag<?> list = (CollectionTag<?>)value;
            io.wispforest.endec.Serializer.Sequence<Tag> sequence = visitor.sequence(
               ctx, Endec.of(this::decodeValue, (ctx1, deserializer) -> null), list.size()
            );

            try {
               list.forEach(sequence::element);
            } catch (Throwable var10) {
               if (sequence != null) {
                  try {
                     sequence.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (sequence != null) {
               sequence.close();
            }
            break;
         case 10:
            CompoundTag compound = (CompoundTag)value;
            io.wispforest.endec.Serializer.Map<Tag> map = visitor.map(ctx, Endec.of(this::decodeValue, (ctx1, deserializer) -> null), compound.size());

            try {
               for (String key : compound.getAllKeys()) {
                  map.entry(key, compound.get(key));
               }
            } catch (Throwable var11) {
               if (map != null) {
                  try {
                     map.close();
                  } catch (Throwable var8) {
                     var11.addSuppressed(var8);
                  }
               }

               throw var11;
            }

            if (map != null) {
               map.close();
            }
            break;
         default:
            throw new IllegalArgumentException("Non-standard, unrecognized NbtElement implementation cannot be decoded");
      }
   }

   private class Map<V> implements io.wispforest.endec.Deserializer.Map<V> {
      private final SerializationContext ctx;
      private final Endec<V> valueEndec;
      private final CompoundTag compound;
      private final Iterator<String> keys;
      private final int size;

      private Map(SerializationContext ctx, Endec<V> valueEndec, CompoundTag compound) {
         this.ctx = ctx;
         this.valueEndec = valueEndec;
         this.compound = compound;
         this.keys = compound.getAllKeys().iterator();
         this.size = compound.size();
      }

      public int estimatedSize() {
         return this.size;
      }

      public boolean hasNext() {
         return this.keys.hasNext();
      }

      public Entry<String, V> next() {
         String key = this.keys.next();
         return (Entry<String, V>)NbtDeserializer.this.frame(
            () -> this.compound.get(key), () -> java.util.Map.entry(key, this.valueEndec.decode(this.ctx, NbtDeserializer.this)), false
         );
      }
   }

   private class Sequence<V> implements io.wispforest.endec.Deserializer.Sequence<V> {
      private final SerializationContext ctx;
      private final Endec<V> valueEndec;
      private final Iterator<Tag> elements;
      private final int size;

      private Sequence(SerializationContext ctx, Endec<V> valueEndec, List<Tag> elements) {
         this.ctx = ctx;
         this.valueEndec = valueEndec;
         this.elements = elements.iterator();
         this.size = elements.size();
      }

      public int estimatedSize() {
         return this.size;
      }

      public boolean hasNext() {
         return this.elements.hasNext();
      }

      public V next() {
         Tag element = this.elements.next();
         return (V)NbtDeserializer.this.frame(() -> element, () -> this.valueEndec.decode(this.ctx, NbtDeserializer.this), false);
      }
   }

   public class Struct implements io.wispforest.endec.Deserializer.Struct {
      private final CompoundTag compound;

      public Struct(CompoundTag compound) {
         this.compound = compound;
      }

      @Nullable
      public <F> F field(String name, SerializationContext ctx, Endec<F> endec) {
         if (!this.compound.contains(name)) {
            throw new IllegalStateException("Field '" + name + "' was missing from serialized data, but no default value was provided");
         } else {
            return (F)NbtDeserializer.this.frame(() -> this.compound.get(name), () -> endec.decode(ctx, NbtDeserializer.this), true);
         }
      }

      @Nullable
      public <F> F field(String name, SerializationContext ctx, Endec<F> endec, @Nullable F defaultValue) {
         boolean mayOmit = ctx.hasAttribute(OptionalFieldFlag.INSTANCE);
         if (!this.compound.contains(name)) {
            if (!mayOmit) {
               throw new IllegalStateException("Field '" + name + "' was missing from serialized data, but no default value was provided");
            } else {
               return defaultValue;
            }
         } else {
            Tag element = this.compound.get(name);
            if (mayOmit) {
               NbtDeserializer.this.encodedOptionals.add(new IdentityHolder<>(element));
            }

            return (F)NbtDeserializer.this.frame(() -> element, () -> endec.decode(ctx, NbtDeserializer.this), false);
         }
      }
   }
}
