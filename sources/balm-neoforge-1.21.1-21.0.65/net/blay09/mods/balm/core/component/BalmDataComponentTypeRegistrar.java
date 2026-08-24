package net.blay09.mods.balm.core.component;

import com.mojang.serialization.Codec;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface BalmDataComponentTypeRegistrar {
   default <T> BalmDataComponentTypeRegistration<T> register(String name, Codec<T> codec) {
      return this.register(name, (BiFunction<ResourceLocation, Builder<T>, Builder<T>>)((id, builder) -> this.createBuilder().persistent(codec)));
   }

   default <T> BalmDataComponentTypeRegistration<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
      return this.register(
         name, (BiFunction<ResourceLocation, Builder<T>, Builder<T>>)((id, builder) -> this.createBuilder().persistent(codec).networkSynchronized(streamCodec))
      );
   }

   default <T> BalmDataComponentTypeRegistration<T> register(String name, Function<Builder<T>, Builder<T>> builderConsumer) {
      return this.register(name, (BiFunction<ResourceLocation, Builder<T>, Builder<T>>)((id, builder) -> builderConsumer.apply(builder)));
   }

   <T> BalmDataComponentTypeRegistration<T> register(String var1, BiFunction<ResourceLocation, Builder<T>, Builder<T>> var2);

   void addAlias(ResourceLocation var1, ResourceLocation var2);

   void addAlias(String var1, String var2);

   <T> Builder<T> createBuilder();
}
