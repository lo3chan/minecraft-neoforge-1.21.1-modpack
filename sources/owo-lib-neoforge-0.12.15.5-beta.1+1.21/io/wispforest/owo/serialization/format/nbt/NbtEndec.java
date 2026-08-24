package io.wispforest.owo.serialization.format.nbt;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.wispforest.endec.Deserializer;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

public final class NbtEndec implements Endec<Tag> {
   public static final Endec<Tag> ELEMENT = new NbtEndec();
   public static final Endec<CompoundTag> COMPOUND = new NbtEndec().xmap(CompoundTag.class::cast, compound -> compound);

   private NbtEndec() {
   }

   public void encode(SerializationContext ctx, Serializer<?> serializer, Tag value) {
      if (serializer instanceof SelfDescribedSerializer) {
         NbtDeserializer.of(value).readAny(ctx, serializer);
      } else {
         try {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            NbtIo.writeAnyTag(value, output);
            serializer.writeBytes(ctx, output.toByteArray());
         } catch (IOException var5) {
            throw new RuntimeException("Failed to encode binary NBT in NbtEndec", var5);
         }
      }
   }

   public Tag decode(SerializationContext ctx, Deserializer<?> deserializer) {
      if (deserializer instanceof SelfDescribedDeserializer<?> selfDescribedDeserializer) {
         NbtSerializer nbt = NbtSerializer.of();
         selfDescribedDeserializer.readAny(ctx, nbt);
         return (Tag)nbt.result();
      } else {
         try {
            return NbtIo.readAnyTag(ByteStreams.newDataInput(deserializer.readBytes(ctx)), NbtAccounter.unlimitedHeap());
         } catch (IOException var5) {
            throw new RuntimeException("Failed to parse binary NBT in NbtEndec", var5);
         }
      }
   }
}
