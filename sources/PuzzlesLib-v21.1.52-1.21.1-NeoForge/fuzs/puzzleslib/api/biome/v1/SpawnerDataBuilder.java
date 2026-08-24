package fuzs.puzzleslib.api.biome.v1;

import java.util.Objects;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import org.apache.commons.lang3.math.Fraction;

public final class SpawnerDataBuilder {
   private final MobSpawnSettingsContext context;
   private final EntityType<?> entityType;
   private IntUnaryOperator weightMapper = IntUnaryOperator.identity();
   private ToIntFunction<SpawnerData> minCountMapper = spawnerData -> spawnerData.minCount;
   private ToIntFunction<SpawnerData> maxCountMapper = spawnerData -> spawnerData.maxCount;

   private SpawnerDataBuilder(MobSpawnSettingsContext context, EntityType<?> entityType) {
      Objects.requireNonNull(context, "context is null");
      Objects.requireNonNull(entityType, "entity type is null");
      this.context = context;
      this.entityType = entityType;
   }

   public static SpawnerDataBuilder create(MobSpawnSettingsContext context, EntityType<?> entityType) {
      return new SpawnerDataBuilder(context, entityType);
   }

   public SpawnerDataBuilder setWeight(int weight) {
      return this.setWeight((IntUnaryOperator)(oldWeight -> weight));
   }

   public SpawnerDataBuilder setWeight(Fraction weight) {
      Objects.requireNonNull(weight, "weight is null");
      return this.setWeight((IntUnaryOperator)(oldWeight -> weight.multiplyBy(Fraction.getFraction(oldWeight, 1)).intValue()));
   }

   public SpawnerDataBuilder setWeight(IntUnaryOperator weight) {
      Objects.requireNonNull(weight, "weight is null");
      this.weightMapper = oldWeight -> Math.max(1, weight.applyAsInt(oldWeight));
      return this;
   }

   public SpawnerDataBuilder setMinCount(int minCount) {
      return this.setMinCount((ToIntFunction<SpawnerData>)(spawnerData -> minCount));
   }

   public SpawnerDataBuilder setMinCount(Fraction minCount) {
      Objects.requireNonNull(minCount, "min count is null");
      return this.setMinCount((ToIntFunction<SpawnerData>)(spawnerData -> minCount.multiplyBy(Fraction.getFraction(spawnerData.minCount, 1)).intValue()));
   }

   public SpawnerDataBuilder setMinCount(ToIntFunction<SpawnerData> minCount) {
      Objects.requireNonNull(minCount, "min count is null");
      this.minCountMapper = spawnerData -> Math.max(1, minCount.applyAsInt(spawnerData));
      return this;
   }

   public SpawnerDataBuilder setMaxCount(int maxCount) {
      return this.setMaxCount((ToIntFunction<SpawnerData>)(spawnerData -> maxCount));
   }

   public SpawnerDataBuilder setMaxCount(Fraction maxCount) {
      Objects.requireNonNull(maxCount, "max count is null");
      return this.setMaxCount((ToIntFunction<SpawnerData>)(spawnerData -> maxCount.multiplyBy(Fraction.getFraction(spawnerData.maxCount, 1)).intValue()));
   }

   public SpawnerDataBuilder setMaxCount(ToIntFunction<SpawnerData> maxCount) {
      Objects.requireNonNull(maxCount, "max count is null");
      this.maxCountMapper = spawnerData -> Math.max(1, maxCount.applyAsInt(spawnerData));
      return this;
   }

   public void apply(EntityType<?> entityType) {
      for (MobCategory mobCategory : this.context.getMobCategoriesWithSpawns()) {
         this.getSpawnerDataForType(this.context, mobCategory, this.entityType).ifPresent(spawnerData -> {
            int weight = this.weightMapper.applyAsInt(spawnerData.getWeight().asInt());
            int minCount = this.minCountMapper.applyAsInt(spawnerData);
            int maxCount = this.maxCountMapper.applyAsInt(spawnerData);
            this.context.addSpawn(mobCategory, new SpawnerData(entityType, weight, Math.min(minCount, maxCount), maxCount));
         });
      }
   }

   private Optional<SpawnerData> getSpawnerDataForType(MobSpawnSettingsContext context, MobCategory mobCategory, EntityType<?> entityType) {
      return context.getSpawnerData(mobCategory).stream().filter(spawnerData -> spawnerData.type == entityType).findAny();
   }
}
