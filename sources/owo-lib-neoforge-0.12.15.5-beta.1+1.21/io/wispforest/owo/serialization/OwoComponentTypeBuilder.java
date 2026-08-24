package io.wispforest.owo.serialization;

import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import net.minecraft.core.component.DataComponentType.Builder;

public interface OwoComponentTypeBuilder<T> {
   default Builder<T> endec(Endec<T> endec) {
      return this.endec(endec, SerializationContext.empty());
   }

   default Builder<T> endec(Endec<T> endec, SerializationContext assumedContext) {
      return ((Builder)this).persistent(CodecUtils.toCodec(endec, assumedContext)).networkSynchronized(CodecUtils.toPacketCodec(endec));
   }
}
