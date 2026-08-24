package io.wispforest.owo.serialization;

import com.mojang.serialization.MapCodec;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.SerializationAttribute.Instance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class EndecRecipeSerializer<R extends Recipe<?>> implements RecipeSerializer<R> {
   private final StreamCodec<FriendlyByteBuf, R> packetCodec;
   private final MapCodec<R> codec;

   public EndecRecipeSerializer(StructEndec<R> endec, Endec<R> networkEndec) {
      this.packetCodec = CodecUtils.toPacketCodec(networkEndec);
      this.codec = CodecUtils.toMapCodec(endec, SerializationContext.attributes(new Instance[]{SerializationAttributes.HUMAN_READABLE}));
   }

   public EndecRecipeSerializer(StructEndec<R> endec) {
      this(endec, endec);
   }

   public MapCodec<R> codec() {
      return this.codec;
   }

   public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
      return this.packetCodec.cast();
   }
}
