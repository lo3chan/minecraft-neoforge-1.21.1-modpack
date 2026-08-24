package fuzs.puzzleslib.api.core.v1.utility;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.impl.core.NbtSerializableCodec;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public interface NbtSerializable {
   static <T extends NbtSerializable> Codec<T> codec(Supplier<T> factory) {
      return new NbtSerializableCodec<>(factory);
   }

   void write(CompoundTag var1, Provider var2);

   void read(CompoundTag var1, Provider var2);

   default CompoundTag toCompoundTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.write(tag, registries);
      return tag;
   }
}
