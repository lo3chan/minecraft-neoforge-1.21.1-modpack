package org.dimdev.limlib.post;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;

public interface PostEffect {
   Codec<PostEffect> CODEC = PostEffect.PostEffectType.CODEC.dispatch(PostEffect::type, PostEffect.PostEffectType::codec);

   PostEffect.PostEffectType<? extends PostEffect> type();

   boolean shouldRender();

   ResourceLocation getShaderLocation();

   public record PostEffectType<T extends PostEffect>(MapCodec<T> codec) {
      public static final Codec<PostEffect.PostEffectType<?>> CODEC = LimLibRegistires.POST_EFFECT_TYPE.byNameCodec();
      public static final PostEffect.PostEffectType<StaticPostEffect> STATIC = register("static", StaticPostEffect.CODEC);
      public static final PostEffect.PostEffectType<EmptyPostEffect> EMPTY = register("empty", EmptyPostEffect.CODEC);

      public static void register() {
      }

      static <U extends PostEffect> PostEffect.PostEffectType<U> register(String id, MapCodec<U> codec) {
         return Limlib.getSided().register(LimLibRegistryKeys.POST_EFFECT_TYPE, id, new PostEffect.PostEffectType<>(codec));
      }
   }
}
