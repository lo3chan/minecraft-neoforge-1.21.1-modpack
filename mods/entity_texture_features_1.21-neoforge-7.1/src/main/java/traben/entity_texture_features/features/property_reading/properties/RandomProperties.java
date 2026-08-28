/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.demonwav.mcdev.annotations.Translatable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
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
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
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
    private static final Set<RandomPropertyFactory> REGISTERED_PROPERTIES = new HashSet<RandomPropertyFactory>();

    public static void forEachProperty(@NotNull Consumer<RandomPropertyFactory> consumer) {
        REGISTERED_PROPERTIES.stream().sorted(Comparator.comparing(RandomPropertyFactory::getPropertyId)).forEach(consumer);
    }

    public static void register(RandomPropertyFactory ... properties) {
        for (RandomPropertyFactory factory : properties) {
            if (factory == null) continue;
            REGISTERED_PROPERTIES.add(factory);
        }
    }

    public static RandomProperty[] getAllRegisteredRandomPropertiesOfIndex(Properties properties, int propertyNum) {
        ArrayList<RandomProperty> randomProperties = new ArrayList<RandomProperty>();
        for (RandomPropertyFactory factory : REGISTERED_PROPERTIES) {
            RandomProperty property;
            if (factory == null || (property = factory.getPropertyOrNull(properties, propertyNum)) == null) continue;
            randomProperties.add(property);
        }
        return randomProperties.toArray(new RandomProperty[0]);
    }

    static {
        RandomProperties.register(RandomPropertyFactory.of("angry", "config.entity_texture_features.property_explanation.angry", AngryProperty::getPropertyOrNull), RandomPropertyFactory.of("creeperCharged", "config.entity_texture_features.property_explanation.creeper", ChargedCreeperProperty::getPropertyOrNull), RandomPropertyFactory.of("distance", "config.entity_texture_features.property_explanation.distance", DistanceToPlayerProperty::getPropertyOrNull), RandomPropertyFactory.of("items", "config.entity_texture_features.property_explanation.items", ItemProperty::getPropertyOrNull), RandomPropertyFactory.of("jumpStrength", "config.entity_texture_features.property_explanation.jump", JumpProperty::getPropertyOrNull, true), RandomPropertyFactory.of("llamaInventory", "config.entity_texture_features.property_explanation.llama", LlamaInventoryProperty::getPropertyOrNull, true), RandomPropertyFactory.of("maxHealth", "config.entity_texture_features.property_explanation.max_health", MaxHealthProperty::getPropertyOrNull, true), RandomPropertyFactory.of("moving", "config.entity_texture_features.property_explanation.moving", MovingProperty::getPropertyOrNull), RandomPropertyFactory.of("hiddenGene", "config.entity_texture_features.property_explanation.gene", PandaGeneProperty::getPropertyOrNull, true), RandomPropertyFactory.of("playerCreated", "config.entity_texture_features.property_explanation.created", PlayerCreatedProperty::getPropertyOrNull, true), RandomPropertyFactory.of("screamingGoat", "config.entity_texture_features.property_explanation.goat", ScreamingGoatProperty::getPropertyOrNull, true), RandomPropertyFactory.of("maxSpeed", "config.entity_texture_features.property_explanation.speed", SpeedProperty::getPropertyOrNull, true), RandomPropertyFactory.of("isSpawner", "config.entity_texture_features.property_explanation.spawner", SpawnerProperty::getPropertyOrNull, true), RandomPropertyFactory.of("dimension", "config.entity_texture_features.property_explanation.dimension", DimensionProperty::getPropertyOrNull, true), RandomPropertyFactory.of("light", "config.entity_texture_features.property_explanation.light", LightProperty::getPropertyOrNull), RandomPropertyFactory.of("variant", "config.entity_texture_features.property_explanation.variant", VariantProperty::getPropertyOrNull, true), RandomPropertyFactory.of("isCreative", "config.entity_texture_features.property_explanation.creative", CreativeProperty::getPropertyOrNull), RandomPropertyFactory.of("isTeammate", "config.entity_texture_features.property_explanation.teammate", TeammateProperty::getPropertyOrNull), RandomPropertyFactory.of("isClientPlayer", "config.entity_texture_features.property_explanation.client", ClientPlayerProperty::getPropertyOrNull, true), RandomPropertyFactory.of("teams", "config.entity_texture_features.property_explanation.team", TeamProperty::getPropertyOrNull), RandomPropertyFactory.of("blockSpawned", "config.entity_texture_features.property_explanation.block_spawned", BlockSpawnedProperty::getPropertyOrNull, true), RandomPropertyFactory.of("nbtVehicle", "config.entity_texture_features.property_explanation.nbt_vehicle", NBTVehicleProperty::getPropertyOrNull), RandomPropertyFactory.of("blockAbove", "config.entity_texture_features.property_explanation.block_above", BlockAboveProperty::getPropertyOrNull), RandomPropertyFactory.of("blockAboveSolid", "config.entity_texture_features.property_explanation.block_above_solid", BlockAboveSolidProperty::getPropertyOrNull), RandomPropertyFactory.of("blockBelow", "config.entity_texture_features.property_explanation.block_below", BlockBelowProperty::getPropertyOrNull), RandomPropertyFactory.of("blockBelowSolid", "config.entity_texture_features.property_explanation.block_below_solid", BlockBelowSolidProperty::getPropertyOrNull), RandomPropertyFactory.of("biomeTag", "config.entity_texture_features.property_explanation.biome_tag", BiomeTagProperty::getPropertyOrNull, true), RandomPropertyFactory.of("temperature", "config.entity_texture_features.property_explanation.temperature", TemperatureProperty::getPropertyOrNull, true), RandomPropertyFactory.of("hour", "config.entity_texture_features.property_explanation.hour", HourProperty::getPropertyOrNull), RandomPropertyFactory.of("minute", "config.entity_texture_features.property_explanation.min", MinuteProperty::getPropertyOrNull), RandomPropertyFactory.of("monthDay", "config.entity_texture_features.property_explanation.month_day", MonthDayProperty::getPropertyOrNull, true), RandomPropertyFactory.of("month", "config.entity_texture_features.property_explanation.month", MonthProperty::getPropertyOrNull, true), RandomPropertyFactory.of("second", "config.entity_texture_features.property_explanation.sec", SecondProperty::getPropertyOrNull), RandomPropertyFactory.of("weekDay", "config.entity_texture_features.property_explanation.week_day", WeekDayProperty::getPropertyOrNull, true), RandomPropertyFactory.of("yearDay", "config.entity_texture_features.property_explanation.year_day", YearDayProperty::getPropertyOrNull, true), RandomPropertyFactory.of("year", "config.entity_texture_features.property_explanation.year", YearProperty::getPropertyOrNull, true), RandomPropertyFactory.of("language", "config.entity_texture_features.property_explanation.lang", LanguageProperty::getPropertyOrNull, true), RandomPropertyFactory.of("textureSuffix", "config.entity_texture_features.property_explanation.texture_suffix", TextureSuffixProperty::getPropertyOrNull), RandomPropertyFactory.of("textureRule", "config.entity_texture_features.property_explanation.texture_rule", TextureRuleIndexProperty::getPropertyOrNull), RandomPropertyFactory.of("modLoaded", "config.entity_texture_features.property_explanation.mod_rule", ModLoadedProperty::getPropertyOrNull), RandomPropertyFactory.of("nbtClient", "config.entity_texture_features.property_explanation.nbt_client", NBTClientProperty::getPropertyOrNull), RandomPropertyFactory.of("minecraftVersion", "config.entity_texture_features.property_explanation.mc_version", MinecraftVersionProperty::getPropertyOrNull), RandomPropertyFactory.of("Difficulty", "config.entity_texture_features.property_explanation.difficulty", DifficultyProperty::getPropertyOrNull), RandomPropertyFactory.of("regionalDifficulty", "config.entity_texture_features.property_explanation.regional_difficulty", RegionalDifficultyProperty::getPropertyOrNull), RandomPropertyFactory.of("clientGameMode", "config.entity_texture_features.property_explanation.client_game_mode", ClientGameModeProperty::getPropertyOrNull), RandomPropertyFactory.of("hardcore", "config.entity_texture_features.property_explanation.hardcore", HardcoreProperty::getPropertyOrNull), RandomPropertyFactory.of("baby", "config.entity_texture_features.property_explanation.baby", BabyProperty::getPropertyOrNull), RandomPropertyFactory.of("biomes", "config.entity_texture_features.property_explanation.biome", BiomeProperty::getPropertyOrNull, true), RandomPropertyFactory.of("blocks", "config.entity_texture_features.property_explanation.block", BlocksProperty::getPropertyOrNull), RandomPropertyFactory.of("colors", "config.entity_texture_features.property_explanation.color", ColorProperty::getPropertyOrNull), RandomPropertyFactory.of("health", "config.entity_texture_features.property_explanation.health", HealthProperty::getPropertyOrNull), RandomPropertyFactory.of("heights", "config.entity_texture_features.property_explanation.height", HeightProperty::getPropertyOrNull, true), RandomPropertyFactory.of("moonPhase", "config.entity_texture_features.property_explanation.moon", MoonPhaseProperty::getPropertyOrNull, true), RandomPropertyFactory.of("name", "config.entity_texture_features.property_explanation.name", NameProperty::getPropertyOrNull), RandomPropertyFactory.of("nbt", "config.entity_texture_features.property_explanation.nbt", NBTProperty::getPropertyOrNull), RandomPropertyFactory.of("professions", "config.entity_texture_features.property_explanation.profession", ProfessionProperty::getPropertyOrNull), RandomPropertyFactory.of("sizes", "config.entity_texture_features.property_explanation.size", SizeProperty::getPropertyOrNull), RandomPropertyFactory.of("dayTime", "config.entity_texture_features.property_explanation.day_time", TimeOfDayProperty::getPropertyOrNull), RandomPropertyFactory.of("weather", "config.entity_texture_features.property_explanation.weather", WeatherProperty::getPropertyOrNull, true));
    }

    public static interface RandomPropertyFactory {
        @NotNull
        public static RandomPropertyFactory of(@NotNull String id, @NotNull @Translatable String explanationKey, @NotNull BiFunction<Properties, Integer, RandomProperty> factory) {
            return RandomPropertyFactory.of(id, explanationKey, factory, false);
        }

        @NotNull
        public static RandomPropertyFactory of(final @NotNull String id, final @NotNull @Translatable String explanationKey, final @NotNull BiFunction<Properties, Integer, @Nullable RandomProperty> factory, final boolean isSpawnLocked) {
            return new RandomPropertyFactory(){

                @Override
                public RandomProperty getPropertyOrNull(Properties properties, int propertyNum) {
                    if (ETF.config().getConfig().isPropertyDisabled(this)) {
                        return null;
                    }
                    if (properties == null) {
                        return null;
                    }
                    RandomProperty property = (RandomProperty)factory.apply(properties, propertyNum);
                    if (property == null) {
                        return null;
                    }
                    property.setCanUpdate(ETF.config().getConfig().canPropertyUpdate(this));
                    return property;
                }

                @Override
                @NotNull
                public String getPropertyId() {
                    return id;
                }

                public boolean equals(Object obj) {
                    return obj instanceof RandomPropertyFactory && ((RandomPropertyFactory)obj).getPropertyId().equals(this.getPropertyId());
                }

                public int hashCode() {
                    return this.getPropertyId().hashCode();
                }

                @Override
                public boolean updatesOverTime() {
                    return !isSpawnLocked;
                }

                @Override
                @NotNull
                public String getExplanationTranslationKey() {
                    return explanationKey;
                }
            };
        }

        @Nullable
        public RandomProperty getPropertyOrNull(Properties var1, int var2);

        @NotNull
        public String getPropertyId();

        public boolean updatesOverTime();

        @NotNull
        public String getExplanationTranslationKey();
    }
}

