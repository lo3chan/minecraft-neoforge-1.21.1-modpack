package com.iafenvoy.origins.data.power.component.builtin;

import com.iafenvoy.origins.data.power.component.PowerComponent;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public final class EmptyComponent extends PowerComponent {
   public static final MapCodec<EmptyComponent> CODEC = MapCodec.unit(EmptyComponent::new);

   @NotNull
   @Override
   public MapCodec<? extends PowerComponent> codec() {
      return CODEC;
   }
}
