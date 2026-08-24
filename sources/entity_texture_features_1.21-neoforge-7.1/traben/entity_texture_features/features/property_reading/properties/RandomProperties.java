package traben.entity_texture_features.features.property_reading.properties;

import com.demonwav.mcdev.annotations.Translatable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.AngryProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BiomeTagProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BlockAboveProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BlockAboveSolidProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BlockBelowProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BlockBelowSolidProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.BlockSpawnedProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.ChargedCreeperProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.ClientPlayerProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.CreativeProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.DimensionProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.DistanceToPlayerProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.ItemProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.JumpProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.LightProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.LlamaInventoryProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.MaxHealthProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.MovingProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.NBTClientProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.PandaGeneProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.PlayerCreatedProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.ScreamingGoatProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.SpawnerProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.SpeedProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.TeamProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.TeammateProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.TemperatureProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.TextureRuleIndexProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.TextureSuffixProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.VariantProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.ClientGameModeProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.DifficultyProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.HardcoreProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.HourProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.LanguageProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.MinecraftVersionProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.MinuteProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.ModLoadedProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.MonthDayProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.MonthProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.NBTVehicleProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.RegionalDifficultyProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.SecondProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.WeekDayProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.YearDayProperty;
import traben.entity_texture_features.features.property_reading.properties.etf_properties.external.YearProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BabyProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BiomeProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BlocksProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.ColorProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.HealthProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.HeightProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.MoonPhaseProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NBTProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.NameProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.ProfessionProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.SizeProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.TimeOfDayProperty;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.WeatherProperty;

public class RandomProperties {
   private static final Set<RandomProperties.RandomPropertyFactory> REGISTERED_PROPERTIES = new HashSet<>();

   public static void forEachProperty(@NotNull Consumer<RandomProperties.RandomPropertyFactory> consumer) {
      REGISTERED_PROPERTIES.stream().sorted(Comparator.comparing(RandomProperties.RandomPropertyFactory::getPropertyId)).forEach(consumer);
   }

   public static void register(RandomProperties.RandomPropertyFactory... properties) {
      for (RandomProperties.RandomPropertyFactory factory : properties) {
         if (factory != null) {
            REGISTERED_PROPERTIES.add(factory);
         }
      }
   }

   public static RandomProperty[] getAllRegisteredRandomPropertiesOfIndex(Properties properties, int propertyNum) {
      ArrayList<RandomProperty> randomProperties = new ArrayList<>();

      for (RandomProperties.RandomPropertyFactory factory : REGISTERED_PROPERTIES) {
         if (factory != null) {
            RandomProperty property = factory.getPropertyOrNull(properties, propertyNum);
            if (property != null) {
               randomProperties.add(property);
            }
         }
      }

      return randomProperties.toArray(new RandomProperty[0]);
   }

   static {
      register(
         RandomProperties.RandomPropertyFactory.of("angry", "config.entity_texture_features.property_explanation.angry", AngryProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "creeperCharged", "config.entity_texture_features.property_explanation.creeper", ChargedCreeperProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "distance", "config.entity_texture_features.property_explanation.distance", DistanceToPlayerProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of("items", "config.entity_texture_features.property_explanation.items", ItemProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "jumpStrength", "config.entity_texture_features.property_explanation.jump", JumpProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "llamaInventory", "config.entity_texture_features.property_explanation.llama", LlamaInventoryProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "maxHealth", "config.entity_texture_features.property_explanation.max_health", MaxHealthProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("moving", "config.entity_texture_features.property_explanation.moving", MovingProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "hiddenGene", "config.entity_texture_features.property_explanation.gene", PandaGeneProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "playerCreated", "config.entity_texture_features.property_explanation.created", PlayerCreatedProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "screamingGoat", "config.entity_texture_features.property_explanation.goat", ScreamingGoatProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "maxSpeed", "config.entity_texture_features.property_explanation.speed", SpeedProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "isSpawner", "config.entity_texture_features.property_explanation.spawner", SpawnerProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "dimension", "config.entity_texture_features.property_explanation.dimension", DimensionProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("light", "config.entity_texture_features.property_explanation.light", LightProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "variant", "config.entity_texture_features.property_explanation.variant", VariantProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "isCreative", "config.entity_texture_features.property_explanation.creative", CreativeProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "isTeammate", "config.entity_texture_features.property_explanation.teammate", TeammateProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "isClientPlayer", "config.entity_texture_features.property_explanation.client", ClientPlayerProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("teams", "config.entity_texture_features.property_explanation.team", TeamProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "blockSpawned", "config.entity_texture_features.property_explanation.block_spawned", BlockSpawnedProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "nbtVehicle", "config.entity_texture_features.property_explanation.nbt_vehicle", NBTVehicleProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "blockAbove", "config.entity_texture_features.property_explanation.block_above", BlockAboveProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "blockAboveSolid", "config.entity_texture_features.property_explanation.block_above_solid", BlockAboveSolidProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "blockBelow", "config.entity_texture_features.property_explanation.block_below", BlockBelowProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "blockBelowSolid", "config.entity_texture_features.property_explanation.block_below_solid", BlockBelowSolidProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "biomeTag", "config.entity_texture_features.property_explanation.biome_tag", BiomeTagProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "temperature", "config.entity_texture_features.property_explanation.temperature", TemperatureProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("hour", "config.entity_texture_features.property_explanation.hour", HourProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of("minute", "config.entity_texture_features.property_explanation.min", MinuteProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "monthDay", "config.entity_texture_features.property_explanation.month_day", MonthDayProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("month", "config.entity_texture_features.property_explanation.month", MonthProperty::getPropertyOrNull, true),
         RandomProperties.RandomPropertyFactory.of("second", "config.entity_texture_features.property_explanation.sec", SecondProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "weekDay", "config.entity_texture_features.property_explanation.week_day", WeekDayProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "yearDay", "config.entity_texture_features.property_explanation.year_day", YearDayProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("year", "config.entity_texture_features.property_explanation.year", YearProperty::getPropertyOrNull, true),
         RandomProperties.RandomPropertyFactory.of(
            "language", "config.entity_texture_features.property_explanation.lang", LanguageProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "textureSuffix", "config.entity_texture_features.property_explanation.texture_suffix", TextureSuffixProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "textureRule", "config.entity_texture_features.property_explanation.texture_rule", TextureRuleIndexProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "modLoaded", "config.entity_texture_features.property_explanation.mod_rule", ModLoadedProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "nbtClient", "config.entity_texture_features.property_explanation.nbt_client", NBTClientProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "minecraftVersion", "config.entity_texture_features.property_explanation.mc_version", MinecraftVersionProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "Difficulty", "config.entity_texture_features.property_explanation.difficulty", DifficultyProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "regionalDifficulty", "config.entity_texture_features.property_explanation.regional_difficulty", RegionalDifficultyProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "clientGameMode", "config.entity_texture_features.property_explanation.client_game_mode", ClientGameModeProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "hardcore", "config.entity_texture_features.property_explanation.hardcore", HardcoreProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of("baby", "config.entity_texture_features.property_explanation.baby", BabyProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "biomes", "config.entity_texture_features.property_explanation.biome", BiomeProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("blocks", "config.entity_texture_features.property_explanation.block", BlocksProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of("colors", "config.entity_texture_features.property_explanation.color", ColorProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of("health", "config.entity_texture_features.property_explanation.health", HealthProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "heights", "config.entity_texture_features.property_explanation.height", HeightProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of(
            "moonPhase", "config.entity_texture_features.property_explanation.moon", MoonPhaseProperty::getPropertyOrNull, true
         ),
         RandomProperties.RandomPropertyFactory.of("name", "config.entity_texture_features.property_explanation.name", NameProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of("nbt", "config.entity_texture_features.property_explanation.nbt", NBTProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "professions", "config.entity_texture_features.property_explanation.profession", ProfessionProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of("sizes", "config.entity_texture_features.property_explanation.size", SizeProperty::getPropertyOrNull),
         RandomProperties.RandomPropertyFactory.of(
            "dayTime", "config.entity_texture_features.property_explanation.day_time", TimeOfDayProperty::getPropertyOrNull
         ),
         RandomProperties.RandomPropertyFactory.of(
            "weather", "config.entity_texture_features.property_explanation.weather", WeatherProperty::getPropertyOrNull, true
         )
      );
   }

   public interface RandomPropertyFactory {
      @NotNull
      static RandomProperties.RandomPropertyFactory of(
         @NotNull String id, @NotNull @Translatable String explanationKey, @NotNull BiFunction<Properties, Integer, RandomProperty> factory
      ) {
         return of(id, explanationKey, factory, false);
      }

      @NotNull
      static RandomProperties.RandomPropertyFactory of(
         @NotNull String id,
         @NotNull @Translatable String explanationKey,
         @NotNull BiFunction<Properties, Integer, RandomProperty> factory,
         boolean isSpawnLocked
      ) {
         return new RandomProperties.RandomPropertyFactory() {
            @Override
            public RandomProperty getPropertyOrNull(Properties properties, int propertyNum) {
               if (ETF.config().getConfig().isPropertyDisabled(this)) {
                  return null;
               } else if (properties == null) {
                  return null;
               } else {
                  RandomProperty property = factory.apply(properties, propertyNum);
                  if (property == null) {
                     return null;
                  } else {
                     property.setCanUpdate(ETF.config().getConfig().canPropertyUpdate(this));
                     return property;
                  }
               }
            }

            @NotNull
            @Override
            public String getPropertyId() {
               return id;
            }

            @Override
            public boolean equals(Object obj) {
               return obj instanceof RandomProperties.RandomPropertyFactory
                  && ((RandomProperties.RandomPropertyFactory)obj).getPropertyId().equals(this.getPropertyId());
            }

            @Override
            public int hashCode() {
               return this.getPropertyId().hashCode();
            }

            @Override
            public boolean updatesOverTime() {
               return !isSpawnLocked;
            }

            @NotNull
            @Override
            public String getExplanationTranslationKey() {
               return explanationKey;
            }
         };
      }

      @Nullable
      RandomProperty getPropertyOrNull(Properties var1, int var2);

      @NotNull
      String getPropertyId();

      boolean updatesOverTime();

      @NotNull
      String getExplanationTranslationKey();
   }
}
