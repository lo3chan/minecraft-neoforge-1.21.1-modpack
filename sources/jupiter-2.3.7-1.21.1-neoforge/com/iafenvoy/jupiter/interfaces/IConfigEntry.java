package com.iafenvoy.jupiter.interfaces;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigDataFixer;
import com.iafenvoy.jupiter.config.interfaces.ValueChangeCallback;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Deprecated(
   forRemoval = true
)
@Comment("Use ConfigEntry<T>")
public interface IConfigEntry<T> {
   ConfigType<T> getType();

   @Nullable
   String getKey();

   Component getName();

   @Nullable
   Component getTooltip();

   IConfigEntry<T> newInstance();

   void registerCallback(ValueChangeCallback<T> var1);

   T getValue();

   T getDefaultValue();

   void setValue(T var1);

   Codec<T> getCodec();

   default <R> DataResult<R> encode(ConfigDataFixer dataFixer, DynamicOps<R> ops) {
      return this.getCodec().encodeStart(ops, this.getValue());
   }

   default <R> void decode(ConfigDataFixer dataFixer, DynamicOps<R> ops, R input) {
      this.getCodec().parse(ops, input).resultOrPartial(Jupiter.LOGGER::error).ifPresent(this::setValue);
   }

   void reset();

   default boolean canReset() {
      return !Objects.equals(this.getValue(), this.getDefaultValue());
   }
}
