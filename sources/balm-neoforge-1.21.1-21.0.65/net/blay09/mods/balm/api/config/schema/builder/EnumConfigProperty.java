package net.blay09.mods.balm.api.config.schema.builder;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.blay09.mods.balm.api.config.LenientEnumCodecs;
import net.blay09.mods.balm.api.config.schema.ConfiguredEnum;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public class EnumConfigProperty<T extends Enum<T>> extends AbstractConfigProperty<T> implements ConfiguredEnum<T> {
   private final T defaultValue;
   private final Codec<T> codec;
   private final StreamCodec<ByteBuf, T> streamCodec;

   public EnumConfigProperty(ConfigPropertyBuilder parent, T defaultValue) {
      super(parent);
      this.defaultValue = defaultValue;
      Class<T> enumClass = defaultValue.getDeclaringClass();
      IntFunction<T> byIdMapper = ByIdMap.continuous(Enum::ordinal, enumClass.getEnumConstants(), OutOfBoundsStrategy.ZERO);
      this.codec = LenientEnumCodecs.fromValues(enumClass::getEnumConstants);
      this.streamCodec = ByteBufCodecs.idMapper(byIdMapper, Enum::ordinal).cast();
   }

   @Override
   public Class<T> type() {
      return this.defaultValue.getDeclaringClass();
   }

   @Override
   public Codec<T> codec() {
      return this.codec;
   }

   @Override
   public StreamCodec<ByteBuf, T> streamCodec() {
      return this.streamCodec;
   }

   public T defaultValue() {
      return this.defaultValue;
   }
}
