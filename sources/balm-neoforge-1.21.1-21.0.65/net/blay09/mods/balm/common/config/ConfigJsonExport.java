package net.blay09.mods.balm.common.config;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredEnum;
import net.blay09.mods.balm.api.config.schema.ConfiguredList;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.ConfiguredSet;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import org.jetbrains.annotations.Nullable;

public class ConfigJsonExport {
   public static ConfigJsonExport.ExportedConfig mapToExportData(Collection<BalmConfigSchema> schemas) {
      ArrayList<ConfigJsonExport.ConfigProperty> properties = new ArrayList<>();

      for (BalmConfigSchema schema : schemas) {
         for (ConfiguredProperty<?> property : schema.rootProperties()) {
            properties.add(new ConfigJsonExport.ConfigProperty(property));
         }

         for (ConfigCategory category : schema.categories()) {
            for (ConfiguredProperty<?> property : category.properties()) {
               properties.add(new ConfigJsonExport.ConfigProperty(property));
            }
         }
      }

      return new ConfigJsonExport.ExportedConfig(properties);
   }

   public static void exportToFile(Collection<BalmConfigSchema> schemas, File file) throws IOException {
      File parentFile = file.getParentFile();
      if (!parentFile.exists() && !parentFile.mkdirs()) {
         throw new IOException("Failed to create parent directories for file: " + file);
      } else {
         Files.writeString(file.toPath(), new Gson().toJson(mapToExportData(schemas)));
      }
   }

   @Nullable
   private static String[] getValidValues(ConfiguredProperty<?> property) {
      Class<?> enumType = null;
      if (property instanceof ConfiguredEnum<?> enumProperty) {
         enumType = enumProperty.type();
      } else if (property instanceof ConfiguredList<?> listProperty && listProperty.nestedType().isEnum()) {
         enumType = listProperty.nestedType();
      } else if (property instanceof ConfiguredSet<?> setProperty && setProperty.nestedType().isEnum()) {
         enumType = setProperty.nestedType();
      }

      return enumType != null ? Arrays.stream(enumType.getEnumConstants()).map(Object::toString).toArray(String[]::new) : null;
   }

   public record ConfigProperty(
      String configType, String category, String name, String type, String description, String defaultValue, @Nullable String[] validValues
   ) {
      public ConfigProperty(ConfiguredProperty<?> property) {
         this(
            property.parentSchema().identifier().getPath(),
            property.category(),
            property.name(),
            property.type().getSimpleName(),
            property.comment(),
            Objects.toString(property.defaultValue()),
            ConfigJsonExport.getValidValues(property)
         );
      }
   }

   public record ExportedConfig(List<ConfigJsonExport.ConfigProperty> properties) {
   }
}
