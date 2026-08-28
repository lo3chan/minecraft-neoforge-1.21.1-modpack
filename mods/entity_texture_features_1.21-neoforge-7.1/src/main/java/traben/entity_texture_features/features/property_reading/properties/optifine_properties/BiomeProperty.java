/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import com.google.common.base.CaseFormat;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class BiomeProperty
extends StringArrayOrRegexProperty {
    protected BiomeProperty(String data) throws RandomProperty.RandomPropertyException {
        super(data);
    }

    public static BiomeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            String dataFromProperty = RandomProperty.readPropertiesOrThrow(properties, propertyNum, "biomes", "biome");
            if (dataFromProperty.startsWith("regex:") || dataFromProperty.startsWith("pattern:")) {
                return new BiomeProperty(dataFromProperty);
            }
            boolean prints = dataFromProperty.startsWith("print:");
            String[] biomeList = (prints ? dataFromProperty.substring(6) : dataFromProperty).split("\\s+");
            if (biomeList.length > 0) {
                block35: for (int currentIndex = 0; currentIndex < biomeList.length; ++currentIndex) {
                    biomeList[currentIndex] = biomeList[currentIndex].strip().replaceAll("^minecraft:", "");
                    switch (biomeList[currentIndex]) {
                        case "ExtremeHills": {
                            biomeList[currentIndex] = "stony_peaks";
                            continue block35;
                        }
                        case "Forest": 
                        case "ForestHills": {
                            biomeList[currentIndex] = "forest";
                            continue block35;
                        }
                        case "Taiga": 
                        case "TaigaHills": {
                            biomeList[currentIndex] = "taiga";
                            continue block35;
                        }
                        case "Swampland": {
                            biomeList[currentIndex] = "swamp";
                            continue block35;
                        }
                        case "Hell": {
                            biomeList[currentIndex] = "nether_wastes";
                            continue block35;
                        }
                        case "Sky": {
                            biomeList[currentIndex] = "the_end";
                            continue block35;
                        }
                        case "IcePlains": {
                            biomeList[currentIndex] = "snowy_plains";
                            continue block35;
                        }
                        case "IceMountains": {
                            biomeList[currentIndex] = "snowy_slopes";
                            continue block35;
                        }
                        case "MushroomIsland": 
                        case "MushroomIslandShore": {
                            biomeList[currentIndex] = "mushroom_fields";
                            continue block35;
                        }
                        case "DesertHills": 
                        case "Desert": {
                            biomeList[currentIndex] = "desert";
                            continue block35;
                        }
                        case "ExtremeHillsEdge": {
                            biomeList[currentIndex] = "meadow";
                            continue block35;
                        }
                        case "Jungle": 
                        case "JungleHills": {
                            biomeList[currentIndex] = "jungle";
                            continue block35;
                        }
                        default: {
                            String currentBiome = biomeList[currentIndex];
                            if (currentBiome.contains("_") || currentBiome.equals(currentBiome.toLowerCase())) continue block35;
                            biomeList[currentIndex] = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, currentBiome);
                        }
                    }
                }
                StringBuilder builder = new StringBuilder();
                if (prints) {
                    builder.append("print:");
                }
                for (String str : biomeList) {
                    builder.append(str).append(" ");
                }
                return new BiomeProperty(builder.toString().trim().toLowerCase());
            }
            return null;
        }
        catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    public String getValueFromEntity(ETFEntityRenderState etfEntity) {
        if (etfEntity.world() != null && etfEntity.blockPos() != null) {
            String biome = ETF.getBiomeString(etfEntity.world(), etfEntity.blockPos());
            return biome == null ? null : biome.replace("minecraft:", "");
        }
        return null;
    }

    @Override
    @NotNull
    public String[] getPropertyIds() {
        return new String[]{"biomes", "biome"};
    }
}

