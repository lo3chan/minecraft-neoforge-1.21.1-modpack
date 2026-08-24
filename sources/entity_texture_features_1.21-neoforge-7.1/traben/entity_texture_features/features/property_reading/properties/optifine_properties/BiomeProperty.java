package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import com.google.common.base.CaseFormat;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class BiomeProperty extends StringArrayOrRegexProperty {
   protected BiomeProperty(String data) throws RandomProperty.RandomPropertyException {
      super(data);
   }

   public static BiomeProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         String dataFromProperty = RandomProperty.readPropertiesOrThrow(properties, propertyNum, "biomes", "biome");
         if (!dataFromProperty.startsWith("regex:") && !dataFromProperty.startsWith("pattern:")) {
            boolean prints = dataFromProperty.startsWith("print:");
            String[] biomeList = (prints ? dataFromProperty.substring(6) : dataFromProperty).split("\\s+");
            if (biomeList.length <= 0) {
               return null;
            } else {
               for (int currentIndex = 0; currentIndex < biomeList.length; currentIndex++) {
                  biomeList[currentIndex] = biomeList[currentIndex].strip().replaceAll("^minecraft:", "");
                  String var6 = biomeList[currentIndex];
                  switch (var6) {
                     case "ExtremeHills":
                        biomeList[currentIndex] = "stony_peaks";
                        break;
                     case "Forest":
                     case "ForestHills":
                        biomeList[currentIndex] = "forest";
                        break;
                     case "Taiga":
                     case "TaigaHills":
                        biomeList[currentIndex] = "taiga";
                        break;
                     case "Swampland":
                        biomeList[currentIndex] = "swamp";
                        break;
                     case "Hell":
                        biomeList[currentIndex] = "nether_wastes";
                        break;
                     case "Sky":
                        biomeList[currentIndex] = "the_end";
                        break;
                     case "IcePlains":
                        biomeList[currentIndex] = "snowy_plains";
                        break;
                     case "IceMountains":
                        biomeList[currentIndex] = "snowy_slopes";
                        break;
                     case "MushroomIsland":
                     case "MushroomIslandShore":
                        biomeList[currentIndex] = "mushroom_fields";
                        break;
                     case "DesertHills":
                     case "Desert":
                        biomeList[currentIndex] = "desert";
                        break;
                     case "ExtremeHillsEdge":
                        biomeList[currentIndex] = "meadow";
                        break;
                     case "Jungle":
                     case "JungleHills":
                        biomeList[currentIndex] = "jungle";
                        break;
                     default:
                        String currentBiome = biomeList[currentIndex];
                        if (!currentBiome.contains("_") && !currentBiome.equals(currentBiome.toLowerCase())) {
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
         } else {
            return new BiomeProperty(dataFromProperty);
         }
      } catch (RandomProperty.RandomPropertyException var10) {
         return null;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return true;
   }

   @Nullable
   @Override
   public String getValueFromEntity(ETFEntityRenderState etfEntity) {
      if (etfEntity.world() != null && etfEntity.blockPos() != null) {
         String biome = ETF.getBiomeString(etfEntity.world(), etfEntity.blockPos());
         return biome == null ? null : biome.replace("minecraft:", "");
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"biomes", "biome"};
   }
}
