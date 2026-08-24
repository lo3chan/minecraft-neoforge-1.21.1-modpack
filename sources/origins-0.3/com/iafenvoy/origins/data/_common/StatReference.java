package com.iafenvoy.origins.data._common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;

public record StatReference(Either<ResourceLocation, StatReference.TypedStat> stat) {
   public static final Codec<StatReference> CODEC = Codec.either(ResourceLocation.CODEC, StatReference.TypedStat.CODEC)
      .xmap(StatReference::new, StatReference::stat);

   public Stat<?> resolve() {
      return (Stat<?>)this.stat.map(id -> {
         ResourceLocation registered = (ResourceLocation)BuiltInRegistries.CUSTOM_STAT.get(id);
         return registered != null ? Stats.CUSTOM.get(registered) : null;
      }, ts -> {
         StatType statType = (StatType)BuiltInRegistries.STAT_TYPE.get(ts.statType);
         if (statType == null) {
            return null;
         } else {
            Object value = statType.getRegistry().get(ts.id);
            return value == null ? null : statType.get(value);
         }
      });
   }

   public record TypedStat(ResourceLocation statType, ResourceLocation id) {
      public static final Codec<StatReference.TypedStat> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               ResourceLocation.CODEC.fieldOf("stat_type").forGetter(StatReference.TypedStat::statType),
               ResourceLocation.CODEC.fieldOf("id").forGetter(StatReference.TypedStat::id)
            )
            .apply(i, StatReference.TypedStat::new)
      );
   }
}
