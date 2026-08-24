package com.iafenvoy.origins.data.power;

import com.google.common.collect.ImmutableSet.Builder;
import com.iafenvoy.origins.data.badge.Badge;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.util.codec.AnyMapCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MultiplePower extends Power {
   public static final List<String> KNOWN_KEYS = List.of("type", "name", "description", "condition", "hidden", "loading_priority", "badges");
   public static final MapCodec<MultiplePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings), AnyMapCodec.create(KNOWN_KEYS, Power.DIRECT_CODEC).forGetter(MultiplePower::getPowers)
         )
         .apply(i, MultiplePower::new)
   );
   private final Map<String, Power> powers;

   public MultiplePower(Power.BaseSettings settings, Map<String, Power> powers) {
      super(settings);
      this.powers = powers;
   }

   public Map<String, Power> getPowers() {
      return this.powers;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void collectBadges(Builder<Badge> builder) {
      super.collectBadges(builder);
      this.powers.values().forEach(p -> p.collectBadges(builder));
   }

   public Stream<PowerHolder> getPowers(ResourceLocation parent) {
      return this.powers.entrySet().stream().map(e -> new PowerHolder(parent.withSuffix("_" + e.getKey()), e.getValue()));
   }
}
