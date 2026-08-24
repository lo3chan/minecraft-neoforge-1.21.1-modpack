package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ModifySlipperinessPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifySlipperinessPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            BlockCondition.optionalCodec("block_condition").forGetter(ModifySlipperinessPower::getBlockCondition),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifySlipperinessPower::getModifier)
         )
         .apply(i, ModifySlipperinessPower::new)
   );
   private final BlockCondition blockCondition;
   private final List<Modifier> modifier;

   public ModifySlipperinessPower(Power.BaseSettings settings, BlockCondition blockCondition, List<Modifier> modifier) {
      super(settings);
      this.blockCondition = blockCondition;
      this.modifier = modifier;
   }

   public BlockCondition getBlockCondition() {
      return this.blockCondition;
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
