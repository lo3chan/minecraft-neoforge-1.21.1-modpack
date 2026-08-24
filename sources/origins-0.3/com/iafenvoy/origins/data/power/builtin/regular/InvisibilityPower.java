package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class InvisibilityPower extends Power {
   public static final MapCodec<InvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("render_armor", false).forGetter(InvisibilityPower::shouldRenderArmor),
            Codec.BOOL.optionalFieldOf("render_outline", false).forGetter(InvisibilityPower::shouldRenderOutline)
         )
         .apply(i, InvisibilityPower::new)
   );
   private final boolean renderArmor;
   private final boolean renderOutline;

   public InvisibilityPower(Power.BaseSettings settings, boolean renderArmor, boolean renderOutline) {
      super(settings);
      this.renderArmor = renderArmor;
      this.renderOutline = renderOutline;
   }

   public boolean shouldRenderArmor() {
      return this.renderArmor;
   }

   public boolean shouldRenderOutline() {
      return this.renderOutline;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}
