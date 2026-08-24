package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

public record WeightedSpawnerData(EntityType<?> type, int weight, int minCount, int maxCount) {
   public static final Codec<WeightedSpawnerData> CODEC = RecordCodecBuilder.create(
         i -> i.group(
               BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(WeightedSpawnerData::type),
               Codec.INT.fieldOf("weight").forGetter(WeightedSpawnerData::weight),
               ExtraCodecs.POSITIVE_INT.fieldOf("minCount").forGetter(WeightedSpawnerData::minCount),
               ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter(WeightedSpawnerData::maxCount)
            )
            .apply(i, WeightedSpawnerData::new)
      )
      .validate(
         spawnerData -> spawnerData.minCount > spawnerData.maxCount
            ? DataResult.error(() -> "minCount needs to be smaller or equal to maxCount")
            : DataResult.success(spawnerData)
      );

   public WeightedSpawnerData(EntityType<?> type, int weight, int minCount, int maxCount) {
      type = type.getCategory() == MobCategory.MISC ? EntityType.PIG : type;
      this.type = type;
      this.weight = weight;
      this.minCount = minCount;
      this.maxCount = maxCount;
   }

   @NotNull
   @Override
   public String toString() {
      return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + ")";
   }
}
