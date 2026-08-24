package fuzs.puzzleslib.impl.core;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import fuzs.puzzleslib.api.core.v1.utility.NbtSerializable;
import java.util.function.Supplier;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryOps.HolderLookupAdapter;

public record NbtSerializableCodec<T extends NbtSerializable>(Supplier<T> factory) implements Codec<T> {
   public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
      return ops instanceof RegistryOps<T1> registryOps && registryOps.lookupProvider instanceof HolderLookupAdapter adapter
         ? TagParser.AS_CODEC.decode(ops, input).map(pair -> pair.mapFirst(compoundTag -> {
            T nbtSerializable = this.factory.get();
            nbtSerializable.read(compoundTag, adapter.lookupProvider);
            return nbtSerializable;
         }))
         : DataResult.error(() -> "Can't decode element " + input + " without registry");
   }

   public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
      return ops instanceof RegistryOps<T1> registryOps && registryOps.lookupProvider instanceof HolderLookupAdapter adapter
         ? TagParser.AS_CODEC.encode(input.toCompoundTag(adapter.lookupProvider), ops, prefix)
         : DataResult.error(() -> "Can't encode element " + input + " without registry");
   }
}
