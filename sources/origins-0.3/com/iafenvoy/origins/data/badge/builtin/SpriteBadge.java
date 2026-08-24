package com.iafenvoy.origins.data.badge.builtin;

import com.iafenvoy.origins.data.badge.Badge;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SpriteBadge(ResourceLocation sprite) implements Badge {
   public static final MapCodec<SpriteBadge> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(ResourceLocation.CODEC.fieldOf("sprite").forGetter(SpriteBadge::sprite)).apply(i, SpriteBadge::new)
   );

   @NotNull
   @Override
   public MapCodec<? extends Badge> codec() {
      return CODEC;
   }
}
