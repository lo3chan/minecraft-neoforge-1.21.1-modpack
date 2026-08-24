package dev.shadowsoffire.placebo.systems.mixes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import net.minecraft.world.item.crafting.Ingredient;

public record JsonMix<T>(JsonMix.Type type, Mix<T> mix) implements CodecProvider<JsonMix<?>> {
   public static Codec<JsonMix<?>> CODEC = PlaceboCodecs.enumCodec(JsonMix.Type.class).dispatch("mix_type", JsonMix::type, JsonMix.Type::codec);

   public JsonMix(Holder<T> pFrom, Ingredient pIngredient, Holder<T> pTo, JsonMix.Type mixType) {
      this(mixType, new Mix(pFrom, pIngredient, pTo));
   }

   @Override
   public Codec<? extends JsonMix<?>> getCodec() {
      return CODEC;
   }

   public static enum Type {
      CONTAINER(BuiltInRegistries.ITEM.holderByNameCodec()),
      POTION(BuiltInRegistries.POTION.holderByNameCodec());

      private final MapCodec<JsonMix<?>> codec;

      private <T> Type(Codec<Holder<T>> elementCodec) {
         this.codec = forType(this, elementCodec);
      }

      public MapCodec<JsonMix<?>> codec() {
         return this.codec;
      }

      private static <T> MapCodec<JsonMix<T>> forType(JsonMix.Type type, Codec<Holder<T>> elementCodec) {
         return RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                  elementCodec.fieldOf("from").forGetter(m -> m.mix.from()),
                  Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(m -> m.mix.ingredient()),
                  elementCodec.fieldOf("to").forGetter(m -> m.mix.to())
               )
               .apply(inst, (from, ingredient, to) -> new JsonMix(from, ingredient, to, type))
         );
      }
   }
}
