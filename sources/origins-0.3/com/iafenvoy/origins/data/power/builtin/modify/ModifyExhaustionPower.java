package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ModifyExhaustionPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyExhaustionPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings), CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyExhaustionPower::getModifier)
         )
         .apply(i, ModifyExhaustionPower::new)
   );
   private final List<Modifier> modifier;

   public ModifyExhaustionPower(Power.BaseSettings settings, List<Modifier> modifier) {
      super(settings);
      this.modifier = modifier;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }
}
