package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

public class ProfessionProperty extends StringArrayOrRegexProperty {
   protected ProfessionProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      super(readPropertiesOrThrow(properties, propertyNum, new String[]{"professions"}));
   }

   public static ProfessionProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new ProfessionProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   public boolean testEntityInternal(ETFEntityRenderState entity) {
      if (entity != null && entity.entity() instanceof VillagerDataHolder villagerEntity) {
         String entityProfession = villagerEntity.getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
         int entityProfessionLevel = villagerEntity.getVillagerData().getLevel();
         boolean check = false;

         for (String str : this.ARRAY) {
            if (str != null) {
               str = str.toLowerCase().replaceAll("\\s*", "").replace("minecraft:", "");
               if (!str.contains(":")) {
                  if (entityProfession.contains(str) || str.contains(entityProfession)) {
                     check = true;
                     break;
                  }
               } else {
                  String[] data = str.split(":\\d");
                  if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
                     if (data.length != 2) {
                        check = true;
                        break;
                     }

                     String[] levels = data[1].split(",");
                     ArrayList<Integer> levelData = new ArrayList<>();

                     for (String lvls : levels) {
                        if (lvls.contains("-")) {
                           levelData.addAll(Arrays.asList(SimpleIntegerArrayProperty.getIntRange(lvls).getAllWithinRangeAsList()));
                        } else {
                           levelData.add(Integer.parseInt(lvls.replaceAll("\\D", "")));
                        }
                     }

                     for (Integer i : levelData) {
                        if (i == entityProfessionLevel) {
                           check = true;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return check;
      } else {
         return false;
      }
   }

   @Override
   protected boolean shouldForceLowerCaseCheck() {
      return false;
   }

   @Override
   protected String getValueFromEntity(ETFEntityRenderState entity) {
      return null;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"professions"};
   }
}
