package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult.Error;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.util.Optional;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public class CodecConfigEntryImpl<T> extends AbstractConfigEntry<T> {
   private final MapCodec<T> mapCodec;

   public CodecConfigEntryImpl(String fieldName, T defaultValue, Codec<T> codec) {
      super(fieldName, defaultValue);
      this.mapCodec = codec.fieldOf(this.fieldName());
   }

   @Override
   public <R> RecordBuilder<R> encode(DynamicOps<R> ops, RecordBuilder<R> recordBuilder) {
      return this.mapCodec.encode(this.get(), ops, recordBuilder);
   }

   @Override
   public <R> boolean decode(R encoded, DynamicOps<R> ops) {
      DataResult<T> result = this.mapCodec.decoder().parse(ops, encoded);
      Optional<Error<T>> error = result.error();
      if (error.isPresent()) {
         YACLConstants.LOGGER.error("Failed to decode entry {}: {}", this.fieldName(), error.get().message());
         return false;
      } else {
         T value = (T)result.result().orElseThrow();
         this.set(value);
         return true;
      }
   }
}
