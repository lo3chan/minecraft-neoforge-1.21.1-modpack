package net.blay09.mods.balm.api.config.schema.builder;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.blay09.mods.balm.api.config.PrimitiveConfigCodecs;
import net.blay09.mods.balm.api.config.schema.ConfiguredList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ListConfigProperty<T> extends AbstractConfigProperty<List<T>> implements ConfiguredList<T> {
   private final Class<T> nestedType;
   private final List<T> defaultValue;
   private final Codec<List<T>> codec;
   private final StreamCodec<ByteBuf, List<T>> streamCodec;

   public ListConfigProperty(ConfigPropertyBuilder parent, Class<T> nestedType, List<T> defaultValue) {
      super(parent);
      this.nestedType = nestedType;
      this.defaultValue = defaultValue;
      this.codec = PrimitiveConfigCodecs.codec(nestedType).listOf();
      this.streamCodec = ByteBufCodecs.collection(ArrayList::new, PrimitiveConfigCodecs.streamCodec(nestedType));
   }

   @Override
   public Class<?> type() {
      return List.class;
   }

   @Override
   public Codec<List<T>> codec() {
      return this.codec;
   }

   @Override
   public StreamCodec<ByteBuf, List<T>> streamCodec() {
      return this.streamCodec;
   }

   @Override
   public Class<T> nestedType() {
      return this.nestedType;
   }

   public List<T> defaultValue() {
      return this.defaultValue;
   }
}
