package io.wispforest.owo.serialization.format.nbt;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationAttribute;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer.Struct;
import io.wispforest.endec.temp.OptionalFieldFlag;
import io.wispforest.endec.util.RecursiveSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import org.apache.commons.lang3.mutable.MutableObject;

public class NbtSerializer extends RecursiveSerializer<Tag> implements SelfDescribedSerializer<Tag> {
   protected Tag prefix;
   private final Set<IdentityHolder<Tag>> encodedOptionals = Collections.newSetFromMap(new WeakHashMap<>());

   protected NbtSerializer(Tag prefix) {
      super(EndTag.INSTANCE);
      this.prefix = prefix;
   }

   public static NbtSerializer of(Tag prefix) {
      return new NbtSerializer(prefix);
   }

   public static NbtSerializer of() {
      return of(null);
   }

   public void writeByte(SerializationContext ctx, byte value) {
      this.consume(ByteTag.valueOf(value));
   }

   public void writeShort(SerializationContext ctx, short value) {
      this.consume(ShortTag.valueOf(value));
   }

   public void writeInt(SerializationContext ctx, int value) {
      this.consume(IntTag.valueOf(value));
   }

   public void writeLong(SerializationContext ctx, long value) {
      this.consume(LongTag.valueOf(value));
   }

   public void writeFloat(SerializationContext ctx, float value) {
      this.consume(FloatTag.valueOf(value));
   }

   public void writeDouble(SerializationContext ctx, double value) {
      this.consume(DoubleTag.valueOf(value));
   }

   public void writeVarInt(SerializationContext ctx, int value) {
      this.consume(switch (VarInt.getByteSize(value)) {
         case 0, 1 -> ByteTag.valueOf((byte)value);
         case 2 -> ShortTag.valueOf((short)value);
         default -> IntTag.valueOf(value);
      });
   }

   public void writeVarLong(SerializationContext ctx, long value) {
      this.consume(switch (VarLong.getByteSize(value)) {
         case 0, 1 -> ByteTag.valueOf((byte)value);
         case 2 -> ShortTag.valueOf((short)value);
         case 3, 4 -> IntTag.valueOf((int)value);
         default -> LongTag.valueOf(value);
      });
   }

   public void writeBoolean(SerializationContext ctx, boolean value) {
      this.consume(ByteTag.valueOf(value));
   }

   public void writeString(SerializationContext ctx, String value) {
      this.consume(StringTag.valueOf(value));
   }

   public void writeBytes(SerializationContext ctx, byte[] bytes) {
      this.consume(new ByteArrayTag(bytes));
   }

   public <V> void writeOptional(SerializationContext ctx, Endec<V> endec, Optional<V> optional) {
      MutableObject<Tag> frameData = new MutableObject();
      this.frame(encoded -> {
         Struct struct = this.struct();

         try {
            struct.field("present", ctx, Endec.BOOLEAN, optional.isPresent());
            optional.ifPresent(value -> struct.field("value", ctx.withoutAttributes(new SerializationAttribute[]{OptionalFieldFlag.INSTANCE}), endec, value));
         } catch (Throwable var10) {
            if (struct != null) {
               try {
                  struct.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (struct != null) {
            struct.close();
         }

         Tag compound = (Tag)encoded.require("optional representation");
         this.encodedOptionals.add(new IdentityHolder<>(compound));
         frameData.setValue(compound);
      }, false);
      this.consume((Tag)frameData.getValue());
   }

   public <E> io.wispforest.endec.Serializer.Sequence<E> sequence(SerializationContext ctx, Endec<E> elementEndec, int size) {
      return new NbtSerializer.Sequence<>(ctx, elementEndec);
   }

   public <V> io.wispforest.endec.Serializer.Map<V> map(SerializationContext ctx, Endec<V> valueEndec, int size) {
      return new NbtSerializer.Map<>(ctx, valueEndec);
   }

   public Struct struct() {
      return new NbtSerializer.Map(null, null);
   }

   private class Map<V> implements io.wispforest.endec.Serializer.Map<V>, Struct {
      private final SerializationContext ctx;
      private final Endec<V> valueEndec;
      private final CompoundTag result;

      private Map(SerializationContext ctx, Endec<V> valueEndec) {
         this.ctx = ctx;
         this.valueEndec = valueEndec;
         if (NbtSerializer.this.prefix != null) {
            if (!(NbtSerializer.this.prefix instanceof CompoundTag prefixMap)) {
               throw new IllegalStateException(
                  "Incompatible prefix of type " + NbtSerializer.this.prefix.getClass().getSimpleName() + " provided for NBT map/struct"
               );
            }

            this.result = prefixMap;
         } else {
            this.result = new CompoundTag();
         }
      }

      public void entry(String key, V value) {
         NbtSerializer.this.frame(encoded -> {
            this.valueEndec.encode(this.ctx, NbtSerializer.this, value);
            this.result.put(key, (Tag)encoded.require("map value"));
         }, false);
      }

      public <F> Struct field(String name, SerializationContext ctx, Endec<F> endec, F value) {
         boolean mayOmit = ctx.hasAttribute(OptionalFieldFlag.INSTANCE);
         NbtSerializer.this.frame(encoded -> {
            endec.encode(ctx, NbtSerializer.this, value);
            Tag element = (Tag)encoded.require("struct field");
            if (mayOmit && NbtSerializer.this.encodedOptionals.contains(new IdentityHolder<>(element))) {
               CompoundTag nbtCompound = (CompoundTag)element;
               if (!nbtCompound.getBoolean("present")) {
                  return;
               }

               element = nbtCompound.get("value");
            }

            this.result.put(name, element);
         }, false);
         return this;
      }

      public void end() {
         NbtSerializer.this.consume(this.result);
      }
   }

   private class Sequence<V> implements io.wispforest.endec.Serializer.Sequence<V> {
      private final SerializationContext ctx;
      private final Endec<V> valueEndec;
      private final ListTag result;

      private Sequence(SerializationContext ctx, Endec<V> valueEndec) {
         this.ctx = ctx;
         this.valueEndec = valueEndec;
         if (NbtSerializer.this.prefix != null) {
            if (!(NbtSerializer.this.prefix instanceof ListTag prefixList)) {
               throw new IllegalStateException(
                  "Incompatible prefix of type " + NbtSerializer.this.prefix.getClass().getSimpleName() + " provided for NBT sequence"
               );
            }

            this.result = prefixList;
         } else {
            this.result = new ListTag();
         }
      }

      public void element(V element) {
         NbtSerializer.this.frame(encoded -> {
            this.valueEndec.encode(this.ctx, NbtSerializer.this, element);
            this.result.add((Tag)encoded.require("sequence element"));
         }, false);
      }

      public void end() {
         CollectionTag<? extends Tag> convertedResult = (CollectionTag<? extends Tag>)(switch (this.result.getElementType()) {
            case 1 -> {
               ArrayList<Byte> list = new ArrayList<>();

               for (Tag nbtElement : this.result) {
                  list.add(((NumericTag)nbtElement).getAsByte());
               }

               yield new ByteArrayTag(list);
            }
            default -> this.result;
            case 3 -> {
               ArrayList<Integer> list = new ArrayList<>();

               for (Tag nbtElement : this.result) {
                  list.add(((NumericTag)nbtElement).getAsInt());
               }

               yield new IntArrayTag(list);
            }
            case 4 -> {
               ArrayList<Long> list = new ArrayList<>();

               for (Tag nbtElement : this.result) {
                  list.add(((NumericTag)nbtElement).getAsLong());
               }

               yield new LongArrayTag(list);
            }
         });
         NbtSerializer.this.consume(convertedResult);
      }
   }
}
