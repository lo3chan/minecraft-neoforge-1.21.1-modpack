package io.wispforest.owo.mixin;

import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.SerializationAttribute.Instance;
import io.wispforest.endec.impl.KeyedEndec;
import io.wispforest.endec.util.MapCarrier;
import io.wispforest.owo.serialization.format.nbt.NbtDeserializer;
import io.wispforest.owo.serialization.format.nbt.NbtSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({CompoundTag.class})
public abstract class NbtCompoundMixin implements MapCarrier {
   @Shadow
   @Nullable
   public abstract Tag get(String var1);

   @Shadow
   @Nullable
   public abstract Tag put(String var1, Tag var2);

   @Shadow
   public abstract void remove(String var1);

   @Shadow
   public abstract boolean contains(String var1);

   public <T> T getWithErrors(SerializationContext ctx, @NotNull KeyedEndec<T> key) {
      return (T)(!this.has(key)
         ? key.defaultValue()
         : key.endec().decodeFully(ctx.withAttributes(new Instance[]{SerializationAttributes.HUMAN_READABLE}), NbtDeserializer::of, this.get(key.key())));
   }

   public <T> void put(SerializationContext ctx, @NotNull KeyedEndec<T> key, @NotNull T value) {
      this.put(key.key(), (Tag)key.endec().encodeFully(ctx.withAttributes(new Instance[]{SerializationAttributes.HUMAN_READABLE}), NbtSerializer::of, value));
   }

   public <T> void delete(@NotNull KeyedEndec<T> key) {
      this.remove(key.key());
   }

   public <T> boolean has(@NotNull KeyedEndec<T> key) {
      return this.contains(key.key());
   }
}
