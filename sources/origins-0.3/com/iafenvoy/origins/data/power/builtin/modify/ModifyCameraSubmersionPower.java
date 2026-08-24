package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.ExtraEnumCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.NotNull;

public class ModifyCameraSubmersionPower extends Power {
   public static final MapCodec<ModifyCameraSubmersionPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            ExtraEnumCodecs.FOG_TYPE.fieldOf("to").forGetter(ModifyCameraSubmersionPower::getTo),
            ExtraEnumCodecs.FOG_TYPE.optionalFieldOf("from").forGetter(ModifyCameraSubmersionPower::getFrom)
         )
         .apply(i, ModifyCameraSubmersionPower::new)
   );
   private final FogType to;
   private final Optional<FogType> from;

   public ModifyCameraSubmersionPower(Power.BaseSettings settings, FogType to, Optional<FogType> from) {
      super(settings);
      this.to = to;
      this.from = from;
   }

   public FogType getTo() {
      return this.to;
   }

   public Optional<FogType> getFrom() {
      return this.from;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public static Optional<FogType> tryReplace(Entity entity, FogType original) {
      return PowerHelper.get(entity)
         .streamActive(ModifyCameraSubmersionPower.class)
         .map(x -> x.from.isEmpty() ? Optional.of(x.to) : x.from.filter(original::equals).map(k -> x.to))
         .flatMap(Optional::stream)
         .findFirst();
   }
}
