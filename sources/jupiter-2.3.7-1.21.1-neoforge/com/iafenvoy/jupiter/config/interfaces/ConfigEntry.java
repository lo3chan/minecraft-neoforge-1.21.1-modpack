package com.iafenvoy.jupiter.config.interfaces;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigDataFixer;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.interfaces.IConfigEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface ConfigEntry<T> extends IConfigEntry<T> {
   @Override
   ConfigType<T> getType();

   @Nullable
   @Override
   String getKey();

   @Override
   Component getName();

   @Nullable
   @Override
   Component getTooltip();

   ConfigEntry<T> newInstance();

   @Override
   void registerCallback(ValueChangeCallback<T> var1);

   @Override
   T getValue();

   @Override
   T getDefaultValue();

   @Override
   void setValue(T var1);

   @Override
   Codec<T> getCodec();

   @Override
   default <R> DataResult<R> encode(ConfigDataFixer dataFixer, DynamicOps<R> ops) {
      return this.getCodec().encodeStart(ops, this.getValue());
   }

   @Override
   default <R> void decode(ConfigDataFixer dataFixer, DynamicOps<R> ops, R input) {
      this.getCodec().parse(ops, input).resultOrPartial(Jupiter.LOGGER::error).ifPresent(this::setValue);
   }

   @Override
   void reset();

   @Override
   default boolean canReset() {
      return !Objects.equals(this.getValue(), this.getDefaultValue());
   }
}
