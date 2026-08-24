package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModelColorPower extends Power {
   public static final MapCodec<ModelColorPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(Power.BaseSettings.CODEC.forGetter(Power::getSettings), ColorSettings.CODEC.forGetter(ModelColorPower::getColor))
         .apply(i, ModelColorPower::new)
   );
   private final ColorSettings color;

   public ModelColorPower(Power.BaseSettings settings, ColorSettings color) {
      super(settings);
      this.color = color;
   }

   public ColorSettings getColor() {
      return this.color;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public static Optional<ColorSettings> getColor(Entity entity) {
      return PowerHelper.get(entity).streamActive(ModelColorPower.class).map(ModelColorPower::getColor).reduce(ColorSettings::merge);
   }
}
