package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.AttributeEntry;
import com.iafenvoy.origins.data._common.helper.AttributePowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AttributePower extends Power implements AttributePowerHelper {
   public static final MapCodec<AttributePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.ATTRIBUTE.optionalFieldOf("modifier", List.of()).forGetter(AttributePower::getModifier),
            Codec.BOOL.optionalFieldOf("update_health", true).forGetter(AttributePower::shouldUpdateHealth)
         )
         .apply(i, AttributePower::new)
   );
   private final List<AttributeEntry> modifier;
   private final boolean updateHealth;

   public AttributePower(Power.BaseSettings settings, List<AttributeEntry> modifier, boolean updateHealth) {
      super(settings);
      this.modifier = modifier;
      this.updateHealth = updateHealth;
   }

   @Override
   public List<AttributeEntry> getModifier() {
      return this.modifier;
   }

   @Override
   public boolean shouldUpdateHealth() {
      return this.updateHealth;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void active(@NotNull OriginDataHolder holder) {
      this.modify(holder, true);
   }

   @Override
   public void inactive(@NotNull OriginDataHolder holder) {
      this.modify(holder, false);
   }

   @Override
   public void respawn(OriginDataHolder holder, boolean backFromEnd) {
      this.modify(holder, true);
   }

   @Override
   public Power self() {
      return this;
   }
}
